package com.example.mappeinnlevering2s374926;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

public class BirthdayCheckReceiver extends BroadcastReceiver {

    private static final String PREFS_NAME = "BirthdayPrefs"; // Name of shared preferences
    private static final String SMS_SERVICE_KEY = "sms_service_enabled"; // Key for SMS service status
    private static final String SMS_MESSAGE_KEY = "sms_message"; // Key for SMS message
    private static final String DEFAULT_MESSAGE = "Gratulerer med dagen!"; // Default SMS message

    @Override
    public void onReceive(Context context, Intent intent) {
        // Checks if the SMS service is enabled
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isSmsServiceEnabled = sharedPreferences.getBoolean(SMS_SERVICE_KEY, false); // Default to false

        if (!isSmsServiceEnabled) {
            Toast.makeText(context, "SMS tjeneste er skrudd av", Toast.LENGTH_SHORT).show();
            return; // Exits if the SMS service is not enabled
        }

        // Retrievse the SMS message from shared preferences or use default
        String smsMessage = sharedPreferences.getString(SMS_MESSAGE_KEY, DEFAULT_MESSAGE);

        // Checks if today is someone's birthday
        PersonDataKilde dataKilde = new PersonDataKilde(context);
        try {
            dataKilde.open();
            List<Person> birthdayPeople = dataKilde.finnPersonerMedFoedselsdag();

            // Sends SMS if there are any birthdays
            if (!birthdayPeople.isEmpty()) {
                for (Person person : birthdayPeople) {
                    sendSms(person.getTelefonnummer(), smsMessage, context);
                }

                // Triggers notification
                NotificationUtils.showNotification(context, "Vellykket!", "SMS er sendt.");
            }

            dataKilde.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to send SMS
    private void sendSms(String phoneNumber, String message, Context context) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(context, "SMS sendt til " + phoneNumber, Toast.LENGTH_SHORT).show();
    }
}