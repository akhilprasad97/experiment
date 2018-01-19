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
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("test")
                .setContentText(remoteMessage.getData().get("message"));
        NotificationManager manager = (NotificationManager)     getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d("data ",remoteMessage.getData().toString());
        if(remoteMessage.getNotification() != null){
            JSONObject json = new JSONObject(remoteMessage.getData());
            try {
                String click_action = remoteMessage.getNotification().getClickAction();
                sendNotification(click_action,String.valueOf(json.getDouble("longitude")),String.valueOf(json.getDouble("latitude")),json.getString("user"),remoteMessage.getData().get("charge").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String click_action, String latitude, String longitude, String recepient, String charge){
        Intent i;
        if(click_action.equals("MainActivity")){
            i = new Intent(this, MainActivity.class);
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else if (click_action.equals("Register")){
            i = new Intent(this, Register.class);
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else if(click_action.equals("Login")){
            i = new Intent(this, Login.class);
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else if (click_action.equals("Accept_Request")){
            i = new Intent(this, Accept_Request.class);
            i.putExtra("recepient",recepient);
            i.putExtra("charge_of_recepient",charge);
            i.putExtra("recepient_longitude",longitude);
            i.putExtra("recepient_latitude",latitude);
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
        }

        //PendingIntent pi = PendingIntent.getActivity(this, 0,i,PendingIntent.FLAG_ONE_SHOT);
    }
}

