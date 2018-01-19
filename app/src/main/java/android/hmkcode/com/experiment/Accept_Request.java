package android.hmkcode.com.experiment;

import android.app.Activity;
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
import android.widget.Toast;

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

import static android.hmkcode.com.experiment.MainActivity.*;

/**
 * Created by akhilprasad97 on 19/1/18.
 */


public class Accept_Request extends Activity{

    TextView user;
    Button accept, reject;
    TextView chargeper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept);

        user = (TextView)findViewById(R.id.recepient);
        accept = (Button)findViewById(R.id.accept);
        reject =(Button)findViewById(R.id.reject);
        chargeper = (TextView) findViewById(R.id.charge);

        Intent receiever = getIntent();

        String recepient_user = receiever.getStringExtra("recepient");

        //Log.d("Recepient",recepient);
        Log.d("charge",recepient_user);


        user.setText(recepient_user);
        chargeper.setText(String.valueOf(0));

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new acceptRequest().execute("http://10.1.85.42:8000/acceptRequest");
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Accept_Request.this, Login.class);
            }
        });
    }

    public class acceptRequest extends AsyncTask<String, String, String> {
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

            SharedPreferences session = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            JSONObject json = new JSONObject();
            try {
                json.accumulate("donor",session.getString(Username,"PEACE"));
                json.accumulate("id",session.getString(RegID,"PEACE"));
                json.accumulate("recepient_latitude",0);
                json.accumulate("recepient_longitude",0);
                json.accumulate("recepient",session.getString(Recepient,"PEACE"));
                out.write(json.toString().getBytes());
                out.flush();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
