package android.hmkcode.com.experiment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
 * Created by akhilprasad97 on 16/1/18.
 */

public class Login extends Activity implements LocationListener {

    TextView welcome;
    Button user_details;
    Button sharing_details;
    Button start;
    Button req;
    Button stop;
    TextView gps;

    private LocationManager location;

    Intent background_intent;
    private Http_Background background;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public static Timer GPS_timer = new Timer();
    public TimerTask timerTaskObj = new TimerTask() {
        public void run() {
            //perform your action here
            startService(new Intent(Login.this, Http_Background.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ctx = this;

        background = new Http_Background();
        background_intent = new Intent(getCtx(), background.getClass());

        GPS_timer.schedule(timerTaskObj, 1000, 2500);

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Log.d("Login", "WORKING");
        //SharedPreferences.Editor editor = sharedpreferences.edit();

        welcome = (TextView) findViewById(R.id.textView);
        //start = (Button)findViewById(R.id.button5);
        //stop = (Button)findViewById(R.id.button8);
        user_details = (Button) findViewById(R.id.button6);
        sharing_details = (Button) findViewById(R.id.button7);
        req = (Button) findViewById(R.id.button5);
        gps = (TextView) findViewById(R.id.textView3);

        String name = sharedpreferences.getString(Name, "YO");

        welcome.setText(name);
        //gps.setText("HELLO");

        location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(Login.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Login.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
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

        //gps.setText("HELLO");
        Location coordinates = location.getLastKnownLocation(location.NETWORK_PROVIDER);
        onLocationChanged(coordinates);


        /*req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Charge.class);
                startActivity(i);
            }
        });*/

        //start.setOnClickListener(this);
        //stop.setOnClickListener(this);

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Request_Charge.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        Log.d("lonitude",String.valueOf(longitude));
        //gps.setText("Longitude"+longitude+"\n"+latitude);
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

    public class sendTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                //connection.connect();

                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                writeStream(out);

                InputStream in = new BufferedInputStream(connection.getInputStream());
                return readStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "Fail";
        }

        private String readStream(InputStream in) {
            BufferedReader reader = new BufferedReader((new InputStreamReader(in)));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            try {
                while((line = reader.readLine()) != null)
                    buffer.append(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("YOYO",buffer.toString());

            return buffer.toString();
        }

        private void writeStream(OutputStream out) {
            String output = "Sending";

            try {
                out.write(output.getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
