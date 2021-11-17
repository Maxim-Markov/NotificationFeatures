package com.highresults.NotificationFeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class MessagingActivity extends AppCompatActivity {

    TextView textView = null;
    NotificationManagerCompat notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        notificationManager =
                NotificationManagerCompat.from(MessagingActivity.this);

        notificationManager.cancel(DetailsActivity.MESSAGE_ID);

        textView = findViewById(R.id.messagingTextView);
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            ArrayList<String> list = (ArrayList<String>) getIntent().getSerializableExtra("EXTRA_MESSAGE_LIST");
            for(String message : list){
                textView.append(message+"\n");
            }
        }

    }
}