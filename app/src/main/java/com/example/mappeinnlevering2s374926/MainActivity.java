package com.example.mappeinnlevering2s374926;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PersonAdapter.OnItemClickListener, PersonDetailFragment.OnPersonActionListener {
    private PersonDataKilde dataKilde;
    private EditText nameText;
    private EditText phoneNumberText;
    private DatePicker birthdatePicker;
    private List<Person> personer;

    private RecyclerView recyclerView;
    private PersonAdapter personAdapter;

    private static final int PERMISSION_REQUEST_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializes database and RecyclerView setup
        dataKilde = new PersonDataKilde(this);
        try {
            dataKilde.open();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Initializes UI elements
        nameText = findViewById(R.id.nameText);
        phoneNumberText = findViewById(R.id.phoneNumberText);
        birthdatePicker = findViewById(R.id.birthdatePicker);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetches persons and sets to RecyclerView
        oppdaterRecyclerView();

        // Sets daily alarm for birthday check
        setDailyAlarm();

        // Checks SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_REQUEST_SEND_SMS);
        }

        // Sets OnClickListener for Preferences button
        Button preferancesButton = findViewById(R.id.preferances);
        preferancesButton.setOnClickListener(v -> preferances(v));
    }

    private void setDailyAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BirthdayCheckReceiver.class);

        // Adds the FLAG_IMMUTABLE flag to the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Sets the time for 08:00 AM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Sets the repeating alarm to trigger daily
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    // Handles SMS permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to update RecyclerView with current person data
    public void oppdaterRecyclerView() {
        personer = dataKilde.finnAllePersoner();
        personAdapter = new PersonAdapter(personer, this);
        recyclerView.setAdapter(personAdapter);
    }

    public void preferances(View v) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new PreferencesFragment())
                .addToBackStack(null)
                .commit();
    }

    // Adds a new person
    public void add(View v) {
        String navn = nameText.getText().toString();
        String telefonnummer = phoneNumberText.getText().toString();
        int day = birthdatePicker.getDayOfMonth();
        int month = birthdatePicker.getMonth() + 1;
        int year = birthdatePicker.getYear();
        String foedselsdato = day + "/" + month + "/" + year;

        if (!navn.isEmpty() && !telefonnummer.isEmpty() && !foedselsdato.isEmpty()) {
            Person person = dataKilde.leggInnPerson(navn, telefonnummer, foedselsdato);
            personer.add(person);  // Update the list
            personAdapter.notifyItemInserted(personer.size() - 1);

            // Clears inputs
            nameText.setText("");
            phoneNumberText.setText("");
            birthdatePicker.updateDate(2000, 1, 1);
        }
    }

    @Override
    public void onItemClick(Person person) {
        // Open the PersonDetailFragment with the selected person's data
        PersonDetailFragment personDetailFragment = new PersonDetailFragment(person);

        // Replace the current view with the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, personDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPersonUpdated() {
        oppdaterRecyclerView();  // Refresh the list after update
    }

    @Override
    public void onPersonDeleted() {
        oppdaterRecyclerView();  // Refresh the list after deletion
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        try {
            dataKilde.open();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        oppdaterRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        dataKilde.close();
    }
}