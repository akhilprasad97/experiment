package android.hmkcode.com.experiment;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static android.hmkcode.com.experiment.MainActivity.*;

/**
 * Created by akhilprasad97 on 17/1/18.
 */

public class Http_Background extends Service implements LocationListener{

    public int counter = 4;
    HttpURLConnection connection = null;
    LocationManager location;
    Location coordinates;
    OutputStream out;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new HttpCall().execute();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connection != null)
            connection.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        JSONObject json = new JSONObject();
        SharedPreferences session = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        try {
            json.accumulate("longitude",coordinates.getLongitude());
            json.accumulate("latitude",coordinates.getLatitude());
            json.accumulate("user",session.getString(Username,"PEACE"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String output = json.toString();
        String fail = "fail";
        counter++;
        try {
            out.write(output.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class HttpCall extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://10.1.85.42:8000/backgroundLocation");
                connection = (HttpURLConnection) url.openConnection();

                Log.d("Hello", "Wrold");

                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                out = new BufferedOutputStream(connection.getOutputStream());
                try {
                    writeStream(out);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                InputStream in = new BufferedInputStream(connection.getInputStream());
                return readStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = new BufferedReader((new InputStreamReader(in)));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            try {
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("YOYO", buffer.toString());
            return buffer.toString();
        }

        private void writeStream(OutputStream out) throws JSONException {
            location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(Http_Background.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(Http_Background.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            coordinates = location.getLastKnownLocation(location.NETWORK_PROVIDER);
            onLocationChanged(coordinates);
        }
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }
}
