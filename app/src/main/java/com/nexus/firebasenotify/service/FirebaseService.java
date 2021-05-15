package com.nexus.firebasenotify.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nexus.firebasenotify.MainActivity;
import com.nexus.firebasenotify.R;

import org.json.JSONArray;

import java.util.Map;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";
    private static final String N_ACTION_FILTER = "NOTIFICATION_DISMISS";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().toString());
            Map<String,String> receivedMessage = remoteMessage.getData();
            sendNotification(receivedMessage.get("text"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Intent dismiss = new Intent(N_ACTION_FILTER);
        PendingIntent deleteIntent = PendingIntent.getBroadcast(this,2, dismiss, PendingIntent.FLAG_CANCEL_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deleteIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,200,100,500};
        vibrator.vibrate(VibrationEffect.createWaveform(pattern,1));

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Lambda Activity",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
