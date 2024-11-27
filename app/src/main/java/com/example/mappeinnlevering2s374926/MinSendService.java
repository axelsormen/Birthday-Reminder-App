package com.example.mappeinnlevering2s374926;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.SmsManager;
import androidx.preference.PreferenceManager;
import java.sql.SQLException;
import java.util.List;

public class MinSendService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean smsEnabled = preferences.getBoolean("sms_service", false);
        String defaultMessage = preferences.getString("default_sms_message", "Happy Birthday!");

        if (smsEnabled) {
            PersonDataKilde dataKilde = new PersonDataKilde(this);
            try {
                dataKilde.open();
                List<Person> birthdayPeople = dataKilde.finnPersonerMedFoedselsdag();

                for (Person person : birthdayPeople) {
                    sendSms(person.getTelefonnummer(), defaultMessage);
                }

                dataKilde.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return START_NOT_STICKY;
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
