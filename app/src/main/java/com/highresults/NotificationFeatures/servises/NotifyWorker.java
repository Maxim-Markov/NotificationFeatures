package com.highresults.NotificationFeatures.servises;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.highresults.NotificationFeatures.DetailsActivity;
import com.highresults.NotificationFeatures.MainActivity;

public class NotifyWorker extends Worker {
    final static String INPUT_DATA_LIST_ID = "com.highresults.android.NotificationFeatures.INPUT_DATA_LIST_ID";
    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int id = getInputData().getInt(INPUT_DATA_LIST_ID,0);
        sendNotification(id);
        return Result.success();
    }

    private void sendNotification(int id) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), MainActivity.CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.star_big_off)
                        .setContentTitle("ExpandedNotification")
                        .setContentText("Notification List")
                        .setCategory(NotificationCompat.CATEGORY_EMAIL)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Line 1")
                                .addLine("Line 2")
                                .addLine("Line 3")
                                .setBigContentTitle("NEW TEXT")
                                .setSummaryText("+ more"));
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(DetailsActivity.LIST_NOTIFICATION_ID, builder.build());
    }
}
