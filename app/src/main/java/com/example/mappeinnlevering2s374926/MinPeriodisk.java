package com.example.mappeinnlevering2s374926;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class MinPeriodisk extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Sets AlarmManager to trigger at the user-selected time
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String time = preferences.getString("sms_time", "08:00");
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent alarmIntent = new Intent(this, MinSendService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
