package com.example.m9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.loader.content.Loader;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private static MainActivity ins;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    MediaPlayer mediaPlayer = null;
    MediaSessionCompat mediaSessionCompat;
    Spinner spinner, spinner2;
    ImageButton imageButton, imageButton2, imageButton3;
    TextView textView;
    StorageReference mStorageRef;
    ArrayAdapter<String> arrayAdapter, arrayAdapter2;
    NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ins = this;

        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        imageButton = findViewById(R.id.imageButton);
        imageButton2 = findViewById(R.id.imageButton2);
        imageButton3 = findViewById(R.id.imageButton3);
        textView = findViewById(R.id.textview);
        //sendBroadcast(new Intent(getApplicationContext(),NotifAlarm.class));
        mediaSessionCompat = new MediaSessionCompat(this, "mytagmediam9");


        sharedpreferences = getSharedPreferences("MyM8PREFERECES_M9", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        imageButton.setEnabled(false);
        imageButton2.setEnabled(false);
        imageButton3.setEnabled(false);
        updateData();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });

        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        createNotificationChannel();

    }

    public static MainActivity getInstance() {
        return ins;
    }

    public void updateTheTextView(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                textView.setText(t);
            }
        });
    }

    private void keepInSharedPreferences(String keyStr, int valueInt) {
        editor.putInt(keyStr, valueInt);
        editor.apply();
    }

    private void keepStringSharedPreferences(String keyStr1, String valueStr1) {
        editor.putString(keyStr1, valueStr1);
        editor.apply();
    }

    public void updateData() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        //ArrayList<String> list = new ArrayList<String>();
                        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
//                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(arrayAdapter);

                        for (StorageReference prefix : listResult.getPrefixes()) {
                            //Log.d("my", prefix.getName());
                            arrayAdapter.add(prefix.getName());
                        }

                        spinner.setSelection(sharedpreferences.getInt("SubjectPosition", 0));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        textView.setText(e.toString());
                    }
                });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mStorageRef.child(arrayAdapter.getItem(i)).listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
//                                ArrayList<String> list = new ArrayList<String>();
                                arrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
//                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner2.setAdapter(arrayAdapter2);

                                for (StorageReference item : listResult.getItems()) {
                                    arrayAdapter2.add(item.getName());
//                                    Log.d("my", item.getName());
                                }
                                spinner2.setSelection(sharedpreferences.getInt("TopicPosition", 0));
//                                if(!textView.getText().toString().contains("preparing")) {
//                                    imageButton.setEnabled(true);
//                                    imageButton2.setEnabled(true);
//                                    imageButton3.setEnabled(true);
//                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Uh-oh, an error occurred!
                                textView.setText(e.toString());
                            }
                        });
                keepInSharedPreferences("SubjectPosition", i);
                keepStringSharedPreferences("subject", arrayAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                keepInSharedPreferences("TopicPosition", i);
                keepStringSharedPreferences("topic", arrayAdapter2.getItem(i));

                if(sharedpreferences.getBoolean("alarm",false)){
                    playPause();
                    editor.putBoolean("one", false);
                    editor.commit();
                }else {
                    imageButton.setEnabled(true);
                    imageButton2.setEnabled(true);
                    imageButton3.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void playPause() {
        if (mediaPlayer != null && sharedpreferences.getString("topic", "").equals(sharedpreferences.getString("playingTopic", ""))) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                showNotification(R.drawable.ic_baseline_play_arrow_24);
                imageButton2.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
            else {
                mediaPlayer.start();
                showNotification( R.drawable.ic_baseline_pause_24);
                imageButton2.setImageResource(R.drawable.ic_baseline_pause_24);
            }

        } else play();

    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        imageButton.setEnabled(false);
        imageButton2.setEnabled(false);
        imageButton3.setEnabled(false);
        String topic = sharedpreferences.getString("topic", "");
        keepStringSharedPreferences("notify", "preparing " + topic);
        textView.setText("preparing "+topic);
        showNotification( R.drawable.ic_baseline_play_arrow_24);
        imageButton2.setImageResource(R.drawable.ic_baseline_play_arrow_24);


        mStorageRef.child(sharedpreferences.getString("subject", ""))
                .child(sharedpreferences.getString("topic", ""))
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                if (sharedpreferences.getInt("play", 0) == 1) {
                    spinner2.setSelection(sharedpreferences.getInt("TopicPosition", 0));
                    keepInSharedPreferences("play", 0);
                }

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.prepareAsync();

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            keepStringSharedPreferences("playingTopic", topic);
                            mediaPlayer.start();
                            imageButton2.setImageResource(R.drawable.ic_baseline_pause_24);
                            imageButton.setEnabled(true);
                            imageButton2.setEnabled(true);
                            imageButton3.setEnabled(true);
                            keepStringSharedPreferences("notify", topic);
                            textView.setText(topic);
                            showNotification( R.drawable.ic_baseline_pause_24);


                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            showNotification( R.drawable.ic_baseline_play_arrow_24);
                            imageButton2.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        }
                    });
                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            textView.setText("MediaError");
                            mediaPlayer.reset();
                            play();
                            return false;
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        mStorageRef.child(sharedpreferences.getString("subject", ""))
                .child(sharedpreferences.getString("topic", ""))
                .getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textView.setText(e.getMessage());
            }
        });

    }

    public void previous() {
        int prev = arrayAdapter2.getPosition(sharedpreferences.getString("topic", "")) - 1;
        if (prev > -1) {
            keepStringSharedPreferences("topic", arrayAdapter2.getItem(prev));
            keepInSharedPreferences("TopicPosition", prev);
            keepInSharedPreferences("play", 1);
            play();
        }
    }

    public void next() {
        int next = arrayAdapter2.getPosition(sharedpreferences.getString("topic", "")) + 1;
        if (next < arrayAdapter2.getCount()) {
            keepStringSharedPreferences("topic", arrayAdapter2.getItem(next));
            keepInSharedPreferences("TopicPosition", next);
            keepInSharedPreferences("play", 1);
            play();
        }
    }

    public void showNotification(int playPause) {
        if(imageButton.isEnabled()) {
            Intent playI = new Intent(this, NotifAlarm.class).setAction("play");
            PendingIntent playPI = PendingIntent.getBroadcast(this, 90, playI, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent prevI = new Intent(this, NotifAlarm.class).setAction("prev");
            PendingIntent prevPI = PendingIntent.getBroadcast(this, 9, prevI, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent nextI = new Intent(this, NotifAlarm.class).setAction("next");
            PendingIntent nextPI = PendingIntent.getBroadcast(this, 80, nextI, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notification = new NotificationCompat.Builder(this, "11")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(sharedpreferences.getString("subject", ""))
                    .setContentText(sharedpreferences.getString("notify", ""))
                    .setContentIntent(contentIntent)
                    .addAction(R.drawable.ic_baseline_skip_previous_24, "prev", prevPI)
                    .addAction(playPause, "play", playPI)
                    .addAction(R.drawable.ic_baseline_skip_next_24, "next", nextPI)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOnlyAlertOnce(true)
                    .build();

            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(12, notification);
        }else{
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification notification = new NotificationCompat.Builder(this, "11")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(sharedpreferences.getString("subject", ""))
                    .setContentText(sharedpreferences.getString("notify", ""))
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOnlyAlertOnce(true)
                    .build();

            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(12, notification);
        }



    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "play";
            String description = "click to play";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("11", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sharedpreferences.getBoolean("one",true)){

            Intent notifyIntent = new Intent(this, NotifAlarm.class).setAction("alarm");

            final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, 1111, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            //long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            long repeatInterval = AlarmManager.INTERVAL_HOUR;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,(calendar.get(Calendar.HOUR_OF_DAY)+1));
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);

            //long triggerTime = calendar.get(Calendar.MINUTE)+(59 - calendar.get(Calendar.MINUTE));
            //calendar.getTimeInMillis(triggerTime)
            //textView.setText(""+(60 - calendar.get(Calendar.MINUTE))+"   "+repeatInterval);
//            long repeatInterval = 3*60000;
//
//            long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

            /*if (alarmManager != null) {
                alarmManager.cancel(notifyPendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);
            }*/

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, notifyPendingIntent);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);
            editor.putBoolean("one", false);
            editor.commit();
        }

        //showNotification(R.drawable.ic_baseline_play_arrow_24);

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            if (mediaPlayer != null){
//                if (mediaPlayer.isPlaying()) {
//                    keepInSharedPreferences("playPause", R.drawable.ic_baseline_pause_24);
//                    imageButton2.setImageResource(R.drawable.ic_baseline_pause_24);
//                }
//                else {
//                    keepInSharedPreferences("playPause",R.drawable.ic_baseline_play_arrow_24);
//                    imageButton2.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//
//                }
//            }
//                handler.postDelayed(this,1000);
//            }
//        },0);

    }

    @Override
    protected void onStop() {
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(12);
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            //showNotification( R.drawable.ic_baseline_play_arrow_24);
            imageButton2.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
        startService(new Intent(this,myservice.class));

        super.onStop();

    }

    @Override
    protected void onRestart() {
        stopService(new Intent(this,myservice.class));

        super.onRestart();
    }

    //    public void finishAndRemoveTask (){
//        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(12);
//    }
//
//    public void onRemoveFromRecents(View view) {
//        // The document is no longer needed; remove its task.
//
//        finishAndRemoveTask();
//    }
//    @Override
//    public void finish(){
//        //super.finish();
//
//        if (mediaPlayer != null){
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//
//    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this,myservice.class));
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer=null;
            //showNotification( R.drawable.ic_baseline_play_arrow_24);
            imageButton2.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
        super.onDestroy();

    }
}