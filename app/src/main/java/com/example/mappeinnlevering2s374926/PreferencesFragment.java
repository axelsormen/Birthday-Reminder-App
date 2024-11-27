package com.example.mappeinnlevering2s374926;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class PreferencesFragment extends Fragment {

    private Switch serviceToggle;
    private TimePicker sendTimePicker;
    private EditText defaultMessage;
    private Button backButton;
    private Button buttonSavePreferences;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_SERVICE_ENABLED = "service_enabled";
    private static final String KEY_SEND_HOUR = "send_hour";
    private static final String KEY_SEND_MINUTE = "send_minute";
    private static final String KEY_DEFAULT_MESSAGE = "default_message";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        // Initializes UI elements
        serviceToggle = view.findViewById(R.id.serviceToggle);
        sendTimePicker = view.findViewById(R.id.sendTimePicker);
        defaultMessage = view.findViewById(R.id.defaultMessage);
        backButton = view.findViewById(R.id.backButton);
        buttonSavePreferences = view.findViewById(R.id.save);

        // Loads preferences when the fragment is created
        loadPreferences();

        // Sets click listener for the back button
        backButton.setOnClickListener(v -> goBack());

        // Sets listener for the service toggle switch
        serviceToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setDailyAlarm();
                Toast.makeText(getActivity(), "SMS tjeneste skrudd på", Toast.LENGTH_SHORT).show();
            } else {
                cancelDailyAlarm();
                Toast.makeText(getActivity(), "SMS tjeneste skrudd av", Toast.LENGTH_SHORT).show();
            }
        });

        // Sets click listener for the save button
        buttonSavePreferences.setOnClickListener(v -> savePreferences());

        return view;
    }

    // Method to close the fragment
    private void goBack() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    // Method to load preferences from SharedPreferences
    private void loadPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Loads service toggle state
        boolean serviceEnabled = sharedPreferences.getBoolean(KEY_SERVICE_ENABLED, false);
        serviceToggle.setChecked(serviceEnabled);

        // Loads time picker values
        int hour = sharedPreferences.getInt(KEY_SEND_HOUR, 8);
        int minute = sharedPreferences.getInt(KEY_SEND_MINUTE, 0);
        sendTimePicker.setCurrentHour(hour);
        sendTimePicker.setCurrentMinute(minute);

        // Loads default message
        String message = sharedPreferences.getString(KEY_DEFAULT_MESSAGE, "Gratulerer med dagen!"); // Default message
        defaultMessage.setText(message);

        // Sets alarm based on loaded preferences
        if (serviceEnabled) {
            setDailyAlarm();
        }
    }

    // Method to save preferences to SharedPreferences
    private void savePreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Saves the toggle state
        editor.putBoolean(KEY_SERVICE_ENABLED, serviceToggle.isChecked());

        // Saves the time picker values
        editor.putInt(KEY_SEND_HOUR, sendTimePicker.getCurrentHour());
        editor.putInt(KEY_SEND_MINUTE, sendTimePicker.getCurrentMinute());

        // Saves the default message
        editor.putString(KEY_DEFAULT_MESSAGE, defaultMessage.getText().toString());

        // Commits the changes
        editor.apply();

        // Updates the alarm if the service is enabled
        if (serviceToggle.isChecked()) {
            setDailyAlarm();
            Toast.makeText(getActivity(), "Preferanser lagret og SMS tjeneste er skrudd på!", Toast.LENGTH_SHORT).show();
        } else {
            cancelDailyAlarm();
            Toast.makeText(getActivity(), "Preferanser lagret og SMS tjeneste er skrudd av!", Toast.LENGTH_SHORT).show();
        }
        goBack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Method to set a daily alarm
    private void setDailyAlarm() {
        Context context = getActivity();
        if (context == null) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MinBroadcastReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Sets the time based on the TimePicker
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, sendTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, sendTimePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);

        // If the time is in the past, add a day to it
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Sets the repeating alarm to trigger daily
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    // Method to cancel the daily alarm
    private void cancelDailyAlarm() {
        Context context = getActivity();
        if (context == null) return;

        Intent intent = new Intent(context, MinBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}