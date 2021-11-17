package com.highresults.NotificationFeatures.servises;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.highresults.NotificationFeatures.DetailsActivity;

public class BigPictureIntentService extends JobIntentService {
    private static final String TAG = "BigPictureService";
    public static final String ACTION_REPLY =
            "com.highresults.android.NotificationFeatures.services.action.PICTURE_REPLY";
    public static final String EXTRA_COMMENT =
            "com.example.android.wearable.wear.wearnotifications.handlers.extra_comment";
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork(): " + intent);
        final String action = intent.getAction();
        if (ACTION_REPLY.equals(action)) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                CharSequence comment = remoteInput.getCharSequence(EXTRA_COMMENT);
                if (comment != null) {

                    // Retrieves NotificationCompat.Builder used to create initial Notification
                    NotificationCompat.Builder notificationCompatBuilder =
                            GlobalNotificationBuilder.getPictureBuilderInstance();

                    // Recreate builder from persistent state if app process is killed
                    if (notificationCompatBuilder == null) {
                        // Note: New builder set globally in the method
                 notificationCompatBuilder = DetailsActivity.createBigPictureNotification(this);
                    }

                    // Updates active Notification
                    Notification updatedNotification = notificationCompatBuilder
                            // Adds a line and comment below content in Notification
                            .setRemoteInputHistory(new CharSequence[]{comment})
                            .build();

                    // Pushes out the updated Notification
                    NotificationManagerCompat notificationManagerCompat =
                            NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(DetailsActivity.BIG_PICTURE_NOTIFICATION_ID, updatedNotification);
                }
            }
        }
    }
}
