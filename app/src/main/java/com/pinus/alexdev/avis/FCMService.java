package com.pinus.alexdev.avis;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.ReviewsApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationActivity;
import com.pinus.alexdev.avis.view.conversation_activity.ConversationListReviewsActivity;
import com.pinus.alexdev.avis.view.home_activity.HomeActivity;
import com.pinus.alexdev.avis.view.review_list_activity.InfoReviewActivity;
import com.pinus.alexdev.avis.view.review_list_activity.ReviewsListActivity;
import com.pinus.alexdev.avis.view.settings_activity.NotificationsActivity;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = FCMService.class.getSimpleName();

    private SaveLoadData saveLoadData;

    public FCMService() {

    }

    @SuppressLint("CheckResult")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        String channelId = "channel_01";
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Log.d("MYTAG", "ITS WORKING");

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        saveLoadData = new SaveLoadData(getApplicationContext());

        //При первом запуске приложения все уведомления по дефолту будут включены.
        //Точно такой же код находится в классе FCMService, чтобы уведомления включались сразу в двух случаях:
        // • Когда пришло первое уведомление и все уведомления сразу устанавливаются на включенные, то есть на true (В этом классе)
        // • Когда пользователь открыл это активити и при открытии вызывается этот метод, в котором все уведомления сразу устанавливаются на включенные, то есть на true (В классе NotificationsActivity)
        if (saveLoadData.isApplicationFirstRun("firstRunREVIEW", true)) {
            saveLoadData.saveBoolean(NotificationsActivity.REVIEW_NOTIFICATION_KEY, true);
            saveLoadData.saveBoolean("firstRunREVIEW", false);
        }

        if (saveLoadData.isApplicationFirstRun("firstRunCHAT", true)) {
            saveLoadData.saveBoolean(NotificationsActivity.CHAT_NOTIFICATION_KEY, true);
            saveLoadData.saveBoolean("firstRunCHAT", false);
        }

        if (saveLoadData.isApplicationFirstRun("firstRunCTA", true)) {
            saveLoadData.saveBoolean(NotificationsActivity.CTA_NOTIFICATION_KEY, true);
            saveLoadData.saveBoolean("firstRunCTA", false);
        }

//        if (saveLoadData.isApplicationFirstRun("firstRunSURVEY", true)) {
//            saveLoadData.saveBoolean(NotificationsActivity.SURVEY_NOTIFICATION_KEY, true);
//            saveLoadData.saveBoolean("firstRunSURVEY", false);
//        }

//        if (Objects.equals(remoteMessage.getData().get("notificationType"), "REVIEW")) {
//            if (!saveLoadData.loadBoolean(NotificationsActivity.REVIEW_NOTIFICATION_KEY))
//                return;
//        } else if (Objects.equals(remoteMessage.getData().get("notificationType"), "MESSAGE")) {
//            if (!saveLoadData.loadBoolean(NotificationsActivity.CHAT_NOTIFICATION_KEY))
//                return;
//        } else if (Objects.equals(remoteMessage.getData().get("notificationType"), "CTA")) {
//            if (!saveLoadData.loadBoolean(NotificationsActivity.CTA_NOTIFICATION_KEY))
//                return;
//        }
        //Раздел SURVEY ещё не реализован, поэтому на данный момент его код описан, но закомментирован
//        else if (Objects.equals(remoteMessage.getData().get("notificationType"), "SURVEY")) {
//            if (!saveLoadData.loadBoolean(NotificationsActivity.SURVEY_NOTIFICATION_KEY))
//                return;
//        }

        //НЕ УДАЛЯТЬ! НУЖНО ОТРЕДАКТИРОВАТЬ
        switch (Objects.requireNonNull(remoteMessage.getData().get("notificationType"))) {
            case "REVIEW": {
                Intent intent = new Intent(this, ReviewsListActivity.class)
//                        .putExtra("revieInfo", t)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(intent);

                Notification notification = getNotification(getString(R.string.newReview), remoteMessage, stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT), channelId);
                makeNotification(notification, channelId);
                break;
            }
            case "MESSAGE": {
                Intent intent = new Intent(this, ConversationListReviewsActivity.class)
//                        .putExtra("revieInfo", t)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(intent);

                Notification notification = getNotification(getString(R.string.newMessage), remoteMessage, stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT), channelId);
                makeNotification(notification, channelId);
                break;
            }
            case "CTA": {
                Intent intent = new Intent(this, HomeActivity.class)
//                        .putExtra("revieInfo", t)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(intent);

                Notification notification = getNotification(getString(R.string.newCTA), remoteMessage, stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT), channelId);
                makeNotification(notification, channelId);
                break;
            }
            default: {
                PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = getNotification(getString(R.string.newNotif), remoteMessage, intent, channelId);
                makeNotification(notification, channelId);
                break;
            }
        }
    }

    private Notification getNotification(String notificationText, RemoteMessage remoteMessage, PendingIntent contentIntent, String channelId) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            return new NotificationCompat.Builder(this, "MyNotification")
                    .setContentTitle(notificationText)
                    .setContentText(remoteMessage.getData().get("body"))
                    .setSmallIcon(getNotificationIcon())
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setVibrate(new long[]{200, 200, 200, 200})
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .build();

        } else return new NotificationCompat.Builder(this, "MyNotification")
                .setContentTitle(notificationText)
                .setContentText(remoteMessage.getData().get("body"))
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setChannelId(channelId)
                .build();
    }

    private void makeNotification(Notification notification, String channelId) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        String channelName = "Channel Name";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            manager.notify(123, notification);

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(mChannel);
        }
        manager.notify(123, notification);
    }

    @Override
    public void onNewToken(String token) {
        Log.v("TokenTAG", "Refreshed token: " + token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification : R.drawable.notifications;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }
}
