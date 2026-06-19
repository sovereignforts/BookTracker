package com.booktracker.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.booktracker.R;
import com.booktracker.data.model.ReadingSession;
import com.booktracker.data.repository.ReadingRepository;
import com.booktracker.ui.home.MainActivity;
import com.booktracker.util.DateUtil;

public class ReadingSessionService extends Service {

    public static final String ACTION_START = "com.booktracker.START_SESSION";
    public static final String ACTION_STOP  = "com.booktracker.STOP_SESSION";
    public static final String EXTRA_FILE_NAME = "file_name";
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_FILE_TYPE = "file_type";

    private static final String CHANNEL_ID = "reading_session_channel";
    private static final int NOTIF_ID = 1001;

    private ReadingRepository repository;
    private long currentSessionId = -1;
    private long sessionStart = 0;
    private String currentFileName = "";

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new ReadingRepository(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
            String filePath = intent.getStringExtra(EXTRA_FILE_PATH);
            String fileType = intent.getStringExtra(EXTRA_FILE_TYPE);
            startSession(fileName, filePath, fileType);
        } else if (ACTION_STOP.equals(action)) {
            stopSession();
        }

        return START_NOT_STICKY;
    }

    private void startSession(String fileName, String filePath, String fileType) {
        // Close any existing open session first
        repository.closeOpenSession(System.currentTimeMillis());

        currentFileName = fileName != null ? fileName : "Unknown";
        sessionStart = System.currentTimeMillis();

        ReadingSession session = new ReadingSession(
                currentFileName,
                filePath != null ? filePath : "",
                fileType != null ? fileType : "PDF",
                sessionStart,
                DateUtil.todayKey()
        );

        repository.insertSession(session, id -> {
            currentSessionId = id;
        });

        startForeground(NOTIF_ID, buildNotification("Reading: " + currentFileName));
    }

    private void stopSession() {
        if (sessionStart > 0) {
            repository.closeOpenSession(System.currentTimeMillis());
        }
        currentSessionId = -1;
        sessionStart = 0;
        stopForeground(true);
        stopSelf();
    }

    private Notification buildNotification(String content) {
        Intent openApp = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                this, 0, openApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle("BookTracker")
                .setContentText(content)
                .setContentIntent(pi)
                .setOngoing(true)
                .setSilent(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reading Session",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Tracks your active reading session");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sessionStart > 0) {
            repository.closeOpenSession(System.currentTimeMillis());
        }
    }

    // Static helpers to start/stop the service
    public static void startTracking(Context ctx, String name, String path, String type) {
        Intent i = new Intent(ctx, ReadingSessionService.class);
        i.setAction(ACTION_START);
        i.putExtra(EXTRA_FILE_NAME, name);
        i.putExtra(EXTRA_FILE_PATH, path);
        i.putExtra(EXTRA_FILE_TYPE, type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(i);
        } else {
            ctx.startService(i);
        }
    }

    public static void stopTracking(Context ctx) {
        Intent i = new Intent(ctx, ReadingSessionService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }
}
