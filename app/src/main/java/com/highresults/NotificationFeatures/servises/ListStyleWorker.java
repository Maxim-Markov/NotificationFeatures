package com.highresults.NotificationFeatures.servises;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.highresults.NotificationFeatures.DetailsActivity;
import com.highresults.NotificationFeatures.MainActivity;

public class ListStyleWorker extends Worker {

    public ListStyleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private static final String TAG = "Worker";

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "onHandleWork");


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), MainActivity.CHANNEL_ID)
                        .setContentTitle("Title")
                        .setSmallIcon(android.R.drawable.dark_header)
                        .setContentText("Notification text")
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Line 1")
                                .addLine("Line 2")
                                .addLine("Line 3"));


        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(DetailsActivity.LIST_NOTIFICATION_ID, builder.build());

        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.d(TAG, "onStopped");
    }
}


