package com.highresults.NotificationFeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;
import androidx.core.graphics.drawable.IconCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.highresults.NotificationFeatures.servises.BigPictureIntentService;
import com.highresults.NotificationFeatures.servises.BigTextIntentService;
import com.highresults.NotificationFeatures.servises.GlobalNotificationBuilder;
import com.highresults.NotificationFeatures.servises.ListStyleWorker;
import com.highresults.NotificationFeatures.servises.StartJobIntentServiceReceiver;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    public final static int BIG_TEXT_NOTIFICATION_ID = 103;
    public final static int BIG_PICTURE_NOTIFICATION_ID = 104;
    public final static int LIST_NOTIFICATION_ID = 105;
    public static final int MESSAGE_ID = 106;

    private static final int jobID = 1;
    private static final int jobID_picture = 2;

    NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        notificationManager =
                NotificationManagerCompat.from(DetailsActivity.this);

        int itemId = getIntent().getIntExtra("NOTIFY_ID2", 0);
        notificationManager.cancel(itemId);


    }

    public void longTextClick(View view) {
        NotificationCompat.Builder builder = createBigTextNotification(this);
        GlobalNotificationBuilder.setTextBuilderInstance(builder);

        notificationManager.notify(BIG_TEXT_NOTIFICATION_ID, builder.build());
    }

    public static NotificationCompat.Builder createBigTextNotification(Context context) {
        String longText = "Чтобы создать длинный текст, создай обычное уведомление, а затем " +
                "добавь к нему стиль BigTextStyle и передай ему bigText";

// Доп. действия по кнопкам в уведомлении
        Intent snoozeIntent = StartJobIntentServiceReceiver.getIntent(context, new Intent(context, BigTextIntentService.class), jobID);
        snoozeIntent.setAction(BigTextIntentService.ACTION_SNOOZE);

//Пересоздать через 5 сек в фоновом потоке(сервис)
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);
        NotificationCompat.Action snoozeAction =
                new NotificationCompat.Action.Builder(
                        android.R.drawable.ic_menu_send,
                        "Snooze",
                        snoozePendingIntent)
                        .build();

// Удалить в фоновом потоке(JobIntentService)
        Intent dismissIntent = StartJobIntentServiceReceiver.getIntent(context, new Intent(context, BigTextIntentService.class), jobID);
        dismissIntent.setAction(BigTextIntentService.ACTION_DISMISS);

        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, 0);
        NotificationCompat.Action dismissAction =
                new NotificationCompat.Action.Builder(
                        android.R.drawable.ic_menu_close_clear_cancel,
                        "Dismiss",
                        dismissPendingIntent)
                        .build();

        return new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentTitle("ExpandedNotification")//Заголовок свёрнутого
                .setContentText("Notification text")//Текст свёрнутого
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.getResources(),
                        R.drawable.ic_alarm_white_48dp))
                .setDefaults(NotificationCompat.DEFAULT_ALL)//все настройки стандартные(для устройств до android 8)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)//извещает систему о категории для сортировки системой
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)//Уровень приоритета для устройств до android 8
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)//На экране блокировки
                .addAction(snoozeAction)
                .addAction(dismissAction)

                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(longText)
                        .setBigContentTitle("Title Expanded")//Заголовок развёрнутого
                        .setSummaryText("Summary"));
    }

    public void bigPictureClick(View view) {
        NotificationCompat.Builder builder = createBigPictureNotification(this);
        GlobalNotificationBuilder.setPictureBuilderInstance(builder);
        notificationManager.notify(BIG_PICTURE_NOTIFICATION_ID, builder.build());
    }

    public static NotificationCompat.Builder createBigPictureNotification(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dahu, options);

        Intent mainIntent = new Intent(context, BigTextReplyActivity.class);
        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
// Create the RemoteInput.
        String replyLabel = " Enter reply text";
        RemoteInput remoteInput =
                new RemoteInput.Builder(BigPictureIntentService.EXTRA_COMMENT)
                        .setLabel(replyLabel)
// List of quick response choices for any wearables paired with the phone
                        .setChoices(new CharSequence[]{"Yes", "No", "Maybe?"})
                        .build();

// Pending intent =
// API <24 (M and below): activity so the lock-screen presents the auth challenge
// API 24+ (N and above): this should be a Service or BroadcastReceiver
        PendingIntent replyActionPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = StartJobIntentServiceReceiver.getIntent(context, new Intent(context, BigPictureIntentService.class), jobID_picture);
            intent.setAction(BigPictureIntentService.ACTION_REPLY);
            replyActionPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        } else {//переход в активити для ответа

            replyActionPendingIntent = mainPendingIntent;
        }

        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply_white_18dp,
                        replyLabel,
                        replyActionPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
// Because we want this to be a new notification (not updating a previous notification), we
// create a new Builder. Later, we use the same global builder to get back the notification
// we built here for a comment on the post.
        return new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle("Title Expanded")//Заголовок развёрнутого
                        .setSummaryText("Summary")
                        .bigPicture(bitmap)
                        .bigLargeIcon(bitmap))
// Title for API <16 (4.0 and below) devices.
                .setSmallIcon(android.R.drawable.star_big_off)
                .setContentTitle("ExpandedNotification")
// Content for API <24 (7.0 and below) devices.
                .setContentText("Notification Picture")
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.getResources(),
                        R.drawable.ic_person_black_48dp))
                .setContentIntent(mainPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

// SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
// devices and all Wear devices. If you have more than one notification and
// you prefer a different summary notification, set a group key and create a
// summary notification via
// .setGroupSummary(true)
// .setGroup(GROUP_KEY_YOUR_NAME_HERE)
                .setSubText(Integer.toString(1))
                .addAction(replyAction)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
// Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
// 'importance' which is set in the NotificationChannel. The integers representing
// 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(NotificationCompat.PRIORITY_HIGH)
// Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
// visibility is set in the NotificationChannel.
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
    }

    public void listStyleClick(View view) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ListStyleWorker.class).build();
        WorkManager.getInstance().beginUniqueWork("listNotificationWork", ExistingWorkPolicy.REPLACE, request).enqueue();

    }

    public void messageStyleClick(View view) {
        NotificationCompat.Builder builder = createMessageNotification(this);
        GlobalNotificationBuilder.setMessageBuilderInstance(builder);
        notificationManager.notify(MESSAGE_ID, builder.build());
    }

    public static NotificationCompat.Builder createMessageNotification(Context context) {
        Person me = new Person.Builder()
                .setIcon(IconCompat.createWithResource(context, android.R.drawable.alert_light_frame))
                .setName("Me")
                .setKey("1234567890")//id
                .setUri("tel:1234567890")//uri на телефон
                .setImportant(true)
                .build();

        Person Ivan = new Person.Builder()
                .setIcon(IconCompat.createWithResource(context, android.R.drawable.btn_minus))
                .setName("Ivan")
                .setImportant(false)
                .build();

        Person Andrey = new Person.Builder()
                .setIcon(IconCompat.createWithResource(context, android.R.drawable.btn_star_big_on))
                .setName("Andrey")
                .setImportant(false)
                .build();

        ArrayList<NotificationCompat.MessagingStyle.Message> messages = new ArrayList<>();

        // For each message, you need the timestamp. In this case, we are using arbitrary longs
        // representing time in milliseconds.
        messages.add(
                // When you are setting an image for a message, text does not display.
                new NotificationCompat.MessagingStyle.Message("", System.currentTimeMillis(), Ivan)
                        .setData("image/png", resourceToUri(context, R.drawable.earth)));

        messages.add(
                new NotificationCompat.MessagingStyle.Message(
                        "Visiting the moon again? :P", System.currentTimeMillis(), me));

        messages.add(
                new NotificationCompat.MessagingStyle.Message("HEY, I see my house!", System.currentTimeMillis(), Andrey));
        ArrayList<String> passMessages = new ArrayList<>();
        for (NotificationCompat.MessagingStyle.Message message : messages) {
            if (message.getText() != null) {
                passMessages.add(message.getText().toString());
            }
        }
        // Responses based on the last messages of the conversation. You would use
        // Machine Learning to get these (https://developers.google.com/ml-kit/).
        CharSequence[] replyChoicesBasedOnLastMessages =
                new CharSequence[]{"Me too!", "How's the weather?", "You have good eyesight."};

        Intent notifyIntent = new Intent(context, MessagingActivity.class);

        // When creating your Intent, you need to take into account the back state, i.e., what
        // happens after your Activity launches and the user presses the back button.
        notifyIntent.putExtra("EXTRA_MESSAGE_LIST", passMessages);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addParentStack(MessagingActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(notifyIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        RemoteInput remoteInput = new RemoteInput.Builder(ListStyleWorkerReceiver.EXTRA_COMMENT)
                .setLabel("Enter reply")
                // Use machine learning to create responses based on previous messages.
                .setChoices(replyChoicesBasedOnLastMessages)
                .build();
        PendingIntent replyActionPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(context, ListStyleWorkerReceiver.class);//Worker
            intent.setAction(ListStyleWorkerReceiver.ACTION_REPLY);
            replyActionPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        } else {
            replyActionPendingIntent = mainPendingIntent;
        }
        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply_white_18dp,
                        "Reply!!",
                        replyActionPendingIntent)
                        .addRemoteInput(remoteInput)
                        // Informs system we aren't bringing up our own custom UI for a reply
                        // action.
                        .setShowsUserInterface(false)
                        // Allows system to generate replies by context of conversation.
                        .setAllowGeneratedReplies(true)
                        .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
                        .build();
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(me);
        messagingStyle.setConversationTitle("Android chat")
                .setGroupConversation(true);

        for (NotificationCompat.MessagingStyle.Message message : messages) {
            messagingStyle.addMessage(message);
        }
        return new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.star_big_off)
                .setContentTitle("ExpandedNotification")
                .setContentText("Notification Picture")
                .setGroup("GROUP_KEY")
                .setContentIntent(mainPendingIntent)
                .addAction(replyAction)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addPerson(me)//необязательно добавлять
                .addPerson(Ivan)//необязательно добавлять
                .addPerson(Andrey)//необязательно добавлять
                .setStyle(messagingStyle);
    }

    public static Uri resourceToUri(Context context, int resId) {
        return Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://"
                        + context.getResources().getResourcePackageName(resId)
                        + "/"
                        + context.getResources().getResourceTypeName(resId)
                        + "/"
                        + context.getResources().getResourceEntryName(resId));
    }

    public void customClick(View view) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.textView, "Custom notification text");

        RemoteViews remoteViewsExtended = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViewsExtended.setTextViewText(R.id.textView, "Extended custom notification text");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(DetailsActivity.this, MainActivity.CHANNEL_ID)
                        .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                        .setCustomContentView(remoteViews)
                        .setCustomBigContentView(remoteViewsExtended)
                        .setGroup("GROUP_KEY")
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
//создание группы. В группу будут помещеы все уведомления с таким же ключом для setGroup . Группа будет создана только если было вызвано уведомление с setGroupSummary и оно не было скинуто
        NotificationCompat.Builder builder1 =
                new NotificationCompat.Builder(DetailsActivity.this, MainActivity.CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.btn_radio)
                        .setContentTitle("Content title")
//set content text to support devices running API level < 24
                        .setContentText("Two new messages")
                        .setContentInfo("contentInfo")
                        .setStyle(new NotificationCompat.InboxStyle()
                                .setSummaryText("SummaryText"))
                        .setGroup("GROUP_KEY")
                        .setGroupSummary(true);//создание группы

        notificationManager.notify(107, builder.build());
        notificationManager.notify(-100, builder1.build());
    }
}