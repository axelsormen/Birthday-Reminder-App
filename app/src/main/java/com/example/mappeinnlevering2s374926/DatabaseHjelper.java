package com.example.mappeinnlevering2s374926;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHjelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAVN = "mappe2.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABELL_PERSONER = "person";
    public static final String KOLONNE_ID = "id";
    public static final String KOLONNE_NAVN = "navn";
    public static final String KOLONNE_TELEFONNUMMER = "telefonnummer";
    public static final String KOLONNE_FOEDSELSDATO = "foedselsdato";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " +
            TABELL_PERSONER +
            "(" + KOLONNE_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KOLONNE_NAVN +
            " TEXT NOT NULL," +
            KOLONNE_TELEFONNUMMER +
            " INTEGER NOT NULL," +
            KOLONNE_FOEDSELSDATO +
            " DATE NOT NULL)";

    public DatabaseHjelper(Context context) {
        super(context, DATABASE_NAVN, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASKS); }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }
}