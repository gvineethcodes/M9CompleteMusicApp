package com.example.m9;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class NotifAlarm extends BroadcastReceiver {
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    @Override
    public void onReceive(Context context, Intent intent) {
        sharedpreferences = context.getSharedPreferences("MyM8PREFERECES_M9", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        try{
            if(intent.getAction() != null){
                switch (intent.getAction()){
                    case "play":
                        MainActivity.getInstance().playPause();
                        break;
                    case "prev":
                        MainActivity.getInstance().previous();
                        break;
                    case "next":
                        MainActivity.getInstance().next();
                        break;
                    case "alarm":
                        editor.putBoolean("alarm", true);
                        editor.commit();
                        context.startActivity(new Intent(context,MainActivity.class));
                        break;
                    case "android.intent.action.BOOT_COMPLETED":

                        Intent notifyIntent = new Intent(context, NotifAlarm.class).setAction("alarm");

                        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, 11, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        //long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                        long repeatInterval = AlarmManager.INTERVAL_HOUR;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY,(calendar.get(Calendar.HOUR_OF_DAY)+1));
                        calendar.set(Calendar.MINUTE,0);
                        calendar.set(Calendar.SECOND,0);

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, notifyPendingIntent);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
