package kesharpaudel.chatfree;


import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Friend Request")
                .setContentText("You've received a new Friend Request")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        //Set an ID for the notification
        int mNotificationId=(int) System.currentTimeMillis();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        notificationManager.notify(mNotificationId, mBuilder.build());

    }


}
