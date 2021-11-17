package com.highresults.NotificationFeatures.servises;


import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.highresults.NotificationFeatures.DetailsActivity;

import java.util.concurrent.TimeUnit;

/**
 * Asynchronously handles snooze and dismiss actions for reminder app (and active Notification).
 * Notification for for reminder app uses BigTextStyle.
 */
public class BigTextIntentService extends JobIntentService {

    private static final String TAG = "BigTextService";

    public static final String ACTION_DISMISS =
            "com.highresults.android.NotificationFeatures.services.action.DISMISS";
    public static final String ACTION_SNOOZE =
            "com.highresults.android.NotificationFeatures.services.action.SNOOZE";

    private static final long SNOOZE_TIME = TimeUnit.SECONDS.toMillis(5);

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork(): " + intent);
        final String action = intent.getAction();
        if (ACTION_DISMISS.equals(action)) {
            handleActionDismiss();
        } else if (ACTION_SNOOZE.equals(action)) {
            handleActionSnooze();
        }
    }

    /**
     * Handles action Dismiss in the provided background thread.
     */
    private void handleActionDismiss() {
        Log.d(TAG, "handleActionDismiss()");

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.cancel(DetailsActivity.BIG_TEXT_NOTIFICATION_ID);
    }

    /**
     * Handles action Snooze in the provided background thread.
     */
    private void handleActionSnooze() {
        Log.d(TAG, "handleActionSnooze()");

        // You could use NotificationManager.getActiveNotifications() if you are targeting SDK 23
        // and above, but we are targeting devices with lower SDK API numbers, so we saved the
        // builder globally and get the notification back to recreate it later.


        NotificationCompat.Builder notificationCompatBuilder =
                GlobalNotificationBuilder.getTextBuilderInstance();

        // Recreate builder from persistent state if app process is killed
        if (notificationCompatBuilder == null) {
            // Note: New builder set globally in the method
            notificationCompatBuilder = DetailsActivity.createBigTextNotification(this);
        }

        Notification notification;
        notification = notificationCompatBuilder.build();


        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(getApplicationContext());

        notificationManagerCompat.cancel(DetailsActivity.BIG_TEXT_NOTIFICATION_ID);

        try {
            Thread.sleep(SNOOZE_TIME);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        notificationManagerCompat.notify(DetailsActivity.BIG_TEXT_NOTIFICATION_ID, notification);

    }

    /*
     * This recreates the notification from the persistent state in case the app process was killed.
     * It is basically the same code for creating the Notification from MainActivity.
     */

}
