package id.cnn.gomudik.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import id.cnn.gomudik.notification.model.Data;
import id.cnn.gomudik.notification.model.Notification;
import id.cnn.gomudik.util.Session;

public class GoMudikFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID ="GoMudik";
    private static final String CHANNEL_NAME = "GoMudik App";
    private static final String CHANNEL_DESC = "GoMudik Notification";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Session session = new Session(getApplicationContext());
        if(remoteMessage.getNotification() != null && session.login()){
            Notification notif = new Notification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            Data data = new Data(remoteMessage.getData().get("group_id"),remoteMessage.getData().get("group_name"),remoteMessage.getData().get("group_image_link"),remoteMessage.getData().get("group_member"),remoteMessage.getData().get("group_chat_room"));

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(CHANNEL_DESC);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            NotificationHelper.displayNotification(getApplicationContext(),notif,data);
        }
    }
}
