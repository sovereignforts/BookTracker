package com.booktracker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.booktracker.data.repository.ReadingRepository;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Close any open session that might have been left from before reboot
            ReadingRepository repo = new ReadingRepository(context);
            repo.closeOpenSession(System.currentTimeMillis());
        }
    }
}
