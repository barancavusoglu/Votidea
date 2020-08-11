package com.bcmobileappdevelopment.votidea;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.bcmobileappdevelopment.votidea.HelperClass.Author;
import com.bcmobileappdevelopment.votidea.HelperClass.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("tarja", "Refreshed token: " + token);
        Log.d("tarja","messageToken yenilendi");

        Intent intent = new Intent("NewToken");
        intent.putExtra("NewToken",token);
        sendBroadcast(intent);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    public static final String INTENT_FILTER = "INTENT_FILTER";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Message msg = new Message();
            msg.setAuthor(new Author());
            String dialogID = "";
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("messageID"))
                    msg.setId(value);
                else if (key.equals("senderUserProfilePic"))
                    msg.getUser().setAvatar(value);
                else if (key.equals("senderUserID"))
                    msg.getUser().setId(value);
                else if (key.equals("content"))
                    msg.setText(value);
                else if (key.equals("senderUserName"))
                    msg.getUser().setName(value);
                else if (key.equals("chatID"))
                    dialogID = value;
                Log.d("tarja", "key, " + key + " value " + value);
            }
            //Log.d("tarja", "Message data payload: " + remoteMessage.getData());
            //MediaPlayer mp = MediaPlayer.create(this, R.raw.sms);
            //mp.start();
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("chat");

            intent.putExtra("messageData",remoteMessage);
            intent.putExtra("messageID",msg.getId());
            intent.putExtra("senderUserProfilePic",msg.getUser().getAvatar());
            intent.putExtra("senderUserID",msg.getUser().getId());
            intent.putExtra("content",msg.getText());
            intent.putExtra("senderUserName", msg.getUser().getName());
            intent.putExtra("dialogID", dialogID);
            sendBroadcast(intent);
        }



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("tarja", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}
