package android.hmkcode.com.experiment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.hmkcode.com.experiment.MainActivity.*;
import static android.hmkcode.com.experiment.Accept_Request.*;

/**
 * Created by akhilprasad97 on 17/1/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d("data ",remoteMessage.getData().toString());
        if(remoteMessage.getData().size()>0) {
            if (remoteMessage.getNotification() != null) {
                JSONObject json = new JSONObject(remoteMessage.getData());
                try {
                    String click_action = remoteMessage.getNotification().getClickAction();
                    if(remoteMessage.getNotification().getClickAction().equals("Accept_Request"))
                        sendNotification(click_action, String.valueOf(json.getDouble("longitude")), String.valueOf(json.getDouble("latitude")), json.getString("user"), remoteMessage.getData().get("charge"));
                    if(remoteMessage.getNotification().getClickAction().equals("Request_Accepted"))
                        sendNotification(click_action, String.valueOf(json.getDouble("longitude")), String.valueOf(json.getDouble("latitude")), json.getString("user"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String click_action, String latitude, String longitude, String recepient, String charge){
        Intent i = new Intent(click_action);
            i.putExtra("recepient",recepient);
            i.putExtra("charge_of_recepient",charge);
            i.putExtra("recepient_longitude",longitude);
            i.putExtra("recepient_latitude",latitude);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i,
                PendingIntent.FLAG_ONE_SHOT);

        //PendingIntent pi = PendingIntent.getActivity(this, 0,i,PendingIntent.FLAG_ONE_SHOT);
    }

    private void sendNotification(String click_action, String latitude, String longitude, String donor){
        Intent i = new Intent(click_action);
        i.putExtra("recepient",donor);
        i.putExtra("recepient_longitude",longitude);
        i.putExtra("recepient_latitude",latitude);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i,
                PendingIntent.FLAG_ONE_SHOT);

        //PendingIntent pi = PendingIntent.getActivity(this, 0,i,PendingIntent.FLAG_ONE_SHOT);
    }
}

