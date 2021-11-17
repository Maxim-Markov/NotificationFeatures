package com.highresults.NotificationFeatures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import com.highresults.NotificationFeatures.servises.ListStyleWorker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ListStyleWorkerReceiver extends BroadcastReceiver {
    public static final String ACTION_REPLY =
            "com.highresults.android.NotificationFeatures.services.action.MESSAGE_REPLY";
    public static final String EXTRA_COMMENT =
            "com.highresults.android.NotificationFeatures.services.extra_messaging_comment";
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (ACTION_REPLY.equals(action)) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                String comment = remoteInput.getCharSequence(EXTRA_COMMENT).toString();
                if (!comment.equals("")) {

                    Data myData = new Data.Builder()
                            .putString("Comment",comment)
                .build();

                    Constraints constraints = new Constraints.Builder()
                            .setRequiresCharging(true)
                            .build();

                    OneTimeWorkRequest myWorkRequest1 = new OneTimeWorkRequest.Builder(ListStyleWorker.class)
                            .setInputData(myData)
                            .addTag("mytag")
                            .setInitialDelay(1, TimeUnit.SECONDS)
                           // .setConstraints(constraints)
                            .build();
                    OneTimeWorkRequest myWorkRequest2 = new OneTimeWorkRequest.Builder(ListStyleWorker.class)
                            .setInputData(myData).build();
                    OneTimeWorkRequest myWorkRequest3 = new OneTimeWorkRequest.Builder(ListStyleWorker.class).build();
                    OneTimeWorkRequest myWorkRequest4 = new OneTimeWorkRequest.Builder(ListStyleWorker.class).build();
                    OneTimeWorkRequest myWorkRequest5 = new OneTimeWorkRequest.Builder(ListStyleWorker.class).build();


                    WorkContinuation chain12 = WorkManager.getInstance()
                            .beginUniqueWork("work123", ExistingWorkPolicy.REPLACE,myWorkRequest1)
                            .then(myWorkRequest2);

                    WorkContinuation chain34 = WorkManager.getInstance()
                            .beginWith(myWorkRequest3)
                            .then(myWorkRequest4);
                    ArrayList<WorkContinuation> list = new ArrayList<>();
                    list.add(chain12);
                    list.add(chain34);

                    WorkContinuation.combine(list)
                            .then(myWorkRequest5).enqueue();

                }
            }
        }

    }
}