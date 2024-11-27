package com.example.mappeinnlevering2s374926;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mappeinnlevering2s374926.DatabaseHjelper;
import com.example.mappeinnlevering2s374926.Person;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PersonDataKilde {
    private SQLiteDatabase database;
    private DatabaseHjelper dbHelper;

    public PersonDataKilde(Context context) {
        dbHelper = new DatabaseHjelper(context);
    }

    // Opens the database connection
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Closes the database connection
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // Inserts a new person into the database
    public Person leggInnPerson(String name, String telefonnummer, String foedselsdato) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHjelper.KOLONNE_NAVN, name);
        values.put(DatabaseHjelper.KOLONNE_TELEFONNUMMER, telefonnummer);
        values.put(DatabaseHjelper.KOLONNE_FOEDSELSDATO, foedselsdato);

        long insertId = database.insert(DatabaseHjelper.TABELL_PERSONER, null, values);

        Cursor cursor = database.query(DatabaseHjelper.TABELL_PERSONER, null, DatabaseHjelper.KOLONNE_ID + " = ?",
                new String[]{String.valueOf(insertId)}, null, null, null);

        Person nyPerson = null;
        if (cursor != null && cursor.moveToFirst()) {
            nyPerson = cursorTilPerson(cursor);
            cursor.close();
        }

        return nyPerson;
    }

    public int oppdaterPerson(long id, String navn, String telefonnummer, String foedselsdato) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHjelper.KOLONNE_NAVN, navn);
        values.put(DatabaseHjelper.KOLONNE_TELEFONNUMMER, telefonnummer);
        values.put(DatabaseHjelper.KOLONNE_FOEDSELSDATO, foedselsdato);

        return database.update(DatabaseHjelper.TABELL_PERSONER, values,
                DatabaseHjelper.KOLONNE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Deletes a person from the database based on their ID
    public void slettPerson(long personId) {
        database.delete(DatabaseHjelper.TABELL_PERSONER,
                DatabaseHjelper.KOLONNE_ID + " =? ",
                new String[]{Long.toString(personId)});
    }

    // Converts a cursor into a Person object
    private Person cursorTilPerson(Cursor cursor) {
        Person person = new Person();
        person.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHjelper.KOLONNE_ID)));
        person.setNavn(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHjelper.KOLONNE_NAVN)));
        person.setTelefonnummer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHjelper.KOLONNE_TELEFONNUMMER)));
        person.setFoedselsdato(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHjelper.KOLONNE_FOEDSELSDATO)));
        return person;
    }

    // Retrieves all persons from the database
    public List<Person> finnAllePersoner() {
        List<Person> personer = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHjelper.TABELL_PERSONER, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Person person = cursorTilPerson(cursor);
                personer.add(person);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return personer;
    }

    // Method to find people with today's birthday
    public List<Person> finnPersonerMedFoedselsdag() {
        List<Person> personerMedBursdag = new ArrayList<>();

        // Get today's date in day/month format
        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_MONTH);
        int month = today.get(Calendar.MONTH) + 1;

        String dagOgMaaned = day + "/" + month;

        // Query for persons whose birthdate matches today's day and month
        Cursor cursor = database.query(DatabaseHjelper.TABELL_PERSONER, null,
                "substr(" + DatabaseHjelper.KOLONNE_FOEDSELSDATO + ", 1, 5) = ?",
                new String[]{dagOgMaaned}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Person person = cursorTilPerson(cursor);
                personerMedBursdag.add(person);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return personerMedBursdag;
    }
}