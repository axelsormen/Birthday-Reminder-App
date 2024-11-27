package com.example.mappeinnlevering2s374926;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MinBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean enabled = intent.getBooleanExtra("enabled", false);
        if (enabled) {
            // Starts the service to set the AlarmManager
            Intent serviceIntent = new Intent(context, MinPeriodisk.class);
            context.startService(serviceIntent);
        }
    }
}
