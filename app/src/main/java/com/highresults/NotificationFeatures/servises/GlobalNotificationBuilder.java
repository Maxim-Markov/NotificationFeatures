package com.highresults.NotificationFeatures.servises;

import android.annotation.SuppressLint;

import androidx.core.app.NotificationCompat;

public final class GlobalNotificationBuilder {
    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder sGlobalBigTextBuilder = null;

    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder sGlobalBigPictureBuilder = null;

    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder sGlobalMessageBuilder = null;
    /*
     * Empty constructor - We don't initialize builder because we rely on a null state to let us
     * know the Application's process was killed.
     */
    private GlobalNotificationBuilder() { }

    public static void setTextBuilderInstance (NotificationCompat.Builder builder) {
        sGlobalBigTextBuilder = builder;
    }

    public static NotificationCompat.Builder getTextBuilderInstance() {
        return sGlobalBigTextBuilder;
    }

    public static void setPictureBuilderInstance (NotificationCompat.Builder builder) {
        sGlobalBigPictureBuilder = builder;
    }

    public static NotificationCompat.Builder getPictureBuilderInstance() {
        return sGlobalBigPictureBuilder;
    }

    public static void setMessageBuilderInstance (NotificationCompat.Builder builder) {
        sGlobalMessageBuilder = builder;
    }

    public static NotificationCompat.Builder getMessageBuilderInstance() {
        return sGlobalMessageBuilder;
    }
}
