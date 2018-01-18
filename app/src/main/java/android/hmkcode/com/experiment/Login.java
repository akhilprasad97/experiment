package android.hmkcode.com.experiment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class Login extends Activity{

    TextView welcome;
    Button user_details;
    Button sharing_details;
    Button start;
    Button req;
    Button stop;

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

        GPS_timer.schedule(timerTaskObj, 1000, 1500);

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Log.d("Login", "WORKING");
        //SharedPreferences.Editor editor = sharedpreferences.edit();

        welcome = (TextView)findViewById(R.id.textView);
        //start = (Button)findViewById(R.id.button5);
        //stop = (Button)findViewById(R.id.button8);
        user_details = (Button)findViewById(R.id.button6);
        sharing_details = (Button)findViewById(R.id.button7);
        req = (Button)findViewById(R.id.button5);

        String name = sharedpreferences.getString(Name, "YO");

        welcome.setText(name);

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Charge.class);
                startActivity(i);
            }
        });

        //start.setOnClickListener(this);
        //stop.setOnClickListener(this);
    }

    /*@Override
    public void onClick(View view) {
        //ctx = this;

        //if(view == start){
            //if (!isMyServiceRunning(background.getClass()))

            /*startService(new Intent(this, Http_Background.class));
             {
                startService(background_intent);
            }
        }
        /*else if(view == stop){
            stopService(new Intent(this,Http_Background.class));
            timerObj.cancel();
        }
    }*/



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
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
