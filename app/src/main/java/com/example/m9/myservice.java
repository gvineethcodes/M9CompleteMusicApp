package com.example.m9;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class myservice extends Service {
//    SharedPreferences sharedpreferences;
//    SharedPreferences.Editor editor;
//    NotificationManager notificationManager;
//    MediaSessionCompat mediaSessionCompat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
////        mediaSessionCompat = new MediaSessionCompat(this, "mytagmediam9");
////
////        sharedpreferences = getSharedPreferences("MyM8PREFERECES_M9", Context.MODE_PRIVATE);
////        editor = sharedpreferences.edit();
//
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         super.onStartCommand(intent, flags, startId);

         MainActivity.getInstance().showNotification(R.drawable.ic_baseline_play_arrow_24);

//            Intent playI = new Intent(this, NotifAlarm.class).setAction("play");
//            PendingIntent playPI = PendingIntent.getBroadcast(this, 90, playI, PendingIntent.FLAG_UPDATE_CURRENT);
//            Intent prevI = new Intent(this, NotifAlarm.class).setAction("prev");
//            PendingIntent prevPI = PendingIntent.getBroadcast(this, 9, prevI, PendingIntent.FLAG_UPDATE_CURRENT);
//            Intent nextI = new Intent(this, NotifAlarm.class).setAction("next");
//            PendingIntent nextPI = PendingIntent.getBroadcast(this, 80, nextI, PendingIntent.FLAG_UPDATE_CURRENT);
//            Intent notificationIntent = new Intent(this, MainActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//            Notification notification = new NotificationCompat.Builder(this, "11")
//                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .setContentTitle(sharedpreferences.getString("subject", ""))
//                    .setContentText(sharedpreferences.getString("notify", ""))
//                    .setContentIntent(contentIntent)
//                    .addAction(R.drawable.ic_baseline_skip_previous_24, "prev", prevPI)
//                    .addAction(R.drawable.ic_baseline_play_arrow_24, "play", playPI)
//                    .addAction(R.drawable.ic_baseline_skip_next_24, "next", nextPI)
//                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                            .setMediaSession(mediaSessionCompat.getSessionToken()))
//                    .setPriority(NotificationCompat.PRIORITY_LOW)
//                    .setOnlyAlertOnce(true)
//                    .build();
//
//            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.notify(12, notification);
        //stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(12);
        super.onDestroy();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(12);
        super.onTaskRemoved(rootIntent);

    }
}
