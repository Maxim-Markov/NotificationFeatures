package com.highresults.NotificationFeatures.servises;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.JobIntentService;

public class StartJobIntentServiceReceiver extends BroadcastReceiver {
    public static final String EXTRA_SERVICE_CLASS = "com.highresults.NotificationFeatures.extra_service_class";
    public static final String EXTRA_JOB_ID = "com.highresults.NotificationFeatures.extra_job_id";

    public static Intent getIntent(Context context, Intent intent, int job_id) {
        ComponentName component = intent.getComponent();
        if (component == null)
            throw new RuntimeException("Missing intent component");

        Intent new_intent = new Intent(intent)
                .putExtra(EXTRA_SERVICE_CLASS, component.getClassName())
                .putExtra(EXTRA_JOB_ID, job_id);
        new_intent.setClass(context, StartJobIntentServiceReceiver.class);
        return new_intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getExtras() == null)
                throw new Exception("No extras found");

            // change intent's class to its intended service's class
            String service_class_name = intent.getStringExtra(EXTRA_SERVICE_CLASS);
            if (service_class_name == null)
                throw new Exception("No service class found in extras");

            if (!JobIntentService.class.isAssignableFrom(Class.forName(service_class_name)))
                throw new Exception("Service class found is not a JobIntentService: " + service_class_name);

            intent.setClass(context, Class.forName(service_class_name));

            // get job id
            if (!intent.getExtras().containsKey(EXTRA_JOB_ID))
                throw new Exception("No job ID found in extras");
            int job_id = intent.getIntExtra(EXTRA_JOB_ID, 0);

            // start the service
            JobIntentService.enqueueWork(context, Class.forName(service_class_name), job_id, intent);
        } catch (Exception e) {
            System.err.println("Error starting service from receiver: " + e.getMessage());
        }
    }
}