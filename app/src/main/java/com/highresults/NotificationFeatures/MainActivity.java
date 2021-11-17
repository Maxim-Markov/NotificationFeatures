package com.highresults.NotificationFeatures;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // Идентификатор уведомления
    private static final int NOTIFY_ID = 101;
    private static final int NOTIFY_ID2 = 102;
    // Идентификатор канала
    public static String CHANNEL_ID = "Cat channel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.but_simple_notif);
        Button button_update = findViewById(R.id.but_update);
        Button button_del = findViewById(R.id.but_delAll);
        Button button_throught = findViewById(R.id.but_throw_activity);

        createNotificationChannel();

        button.setOnClickListener(v -> {
            // Create PendingIntent
            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                            .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                            .setContentTitle("Напоминание")
                            .setContentText("Пора покормить кота")
                            .setUsesChronometer(true)
                            .setTimeoutAfter(6000)
                            .setVibrate(new long[]{500, 200, 200, 500})
                            .setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6"))
                            .setLights(Color.GREEN, 1000, 1000)
                            .setColor(Color.GREEN)
                            .setContentIntent(resultPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(MainActivity.this);
            notificationManager.notify(NOTIFY_ID, builder.build());
        });

        button_update.setOnClickListener(v -> {
            int max = 100;
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setContentTitle("Напоминание обновлённое")
                            .setContentText("Пора покормить Лекси")
                            .setProgress(max, 0, true)
                            .setOnlyAlertOnce(true)
                            .setOngoing(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(MainActivity.this);
            notificationManager.notify(NOTIFY_ID, builder.build());

            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int progress = 0;
                while (progress < max) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress += 10;

                    // show notification with current progress
                    builder.setProgress(max, progress, false)
                            .setContentText(progress + " of " + max);
                    notificationManager.notify(NOTIFY_ID, builder.build());

                }
                Intent resultIntent = new Intent(MainActivity.this, WhatsNewActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.this, 1, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                // show notification without progressbar
                builder.setProgress(0, 10, false)
                        .setContentText("Completed")
                        .setOngoing(false)
                        .setOnlyAlertOnce(false)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent);
                notificationManager.notify(NOTIFY_ID, builder.build());
            }).start();
        });

        button_del.setOnClickListener(v -> {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
            notificationManager.cancelAll();
        });

        button_throught.setOnClickListener(v -> {
            // Create PendingIntent
            Intent resultIntent = new Intent(this, DetailsActivity.class);
            resultIntent.putExtra("NOTIFY_ID2", NOTIFY_ID2);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(DetailsActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.alert_dark_frame)
                            .setContentTitle("Перескочим через Activity")
                            .setContentText("Кнопкой назад вернёмся к базовому")
                            .setColor(Color.BLACK)
                            .setContentIntent(resultPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
            notificationManager.notify(NOTIFY_ID2, builder.build());
        });

    }


    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Cat channel";
            String description = "des";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build();
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            channel.setVibrationPattern(new long[]{100, 30, 100, 30});
            channel.setLightColor(Color.BLUE);
            //channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),audioAttributes);
            ArrayList<String> Uris = getNotificationSounds();
            channel.setSound(Uri.parse(Uris.get(1)), audioAttributes);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public ArrayList<String> getNotificationSounds() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            list.add(uri + "/" + id);
        }
        return list;
    }

    public void toExtandClick(View view) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        startActivity(intent);
    }
}