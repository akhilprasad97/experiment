package android.hmkcode.com.experiment;

import android.app.Activity;
import android.content.Intent;
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

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

/**
 * Created by akhilprasad97 on 16/1/18.
 */

public class Register extends Activity{

    Button submit;
    TextView name;
    TextView username;
    TextView password;
    TextView conf;
    TextView model;
    TextView regId;
    LocationManager manager;
    OutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        submit = (Button) findViewById(R.id.button2);
        name = (TextView) findViewById(R.id.editText6);
        username = (TextView) findViewById(R.id.editText3);
        password = (TextView) findViewById(R.id.editText4);
        conf = (TextView) findViewById(R.id.editText5);
        model = (TextView) findViewById(R.id.editText2);
        regId = (TextView) findViewById(R.id.editText);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conf.getText().toString().equals(password.getText().toString())) {
                    new JSONTask().execute("http://10.1.85.42:8000/register");
                } else {
                    conf.setText(null);
                    conf.setHint("Passwords dont match");
                }
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
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
            } finally {
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
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("YOYO", buffer.toString());
            return buffer.toString();
        }

        private void writeStream(OutputStream out) {
            String output_user = username.getText().toString();
            String output_pass = password.getText().toString();
            String output_name = name.getText().toString();
            String output_model = model.getText().toString();
            String output_reg = regId.getText().toString();
            String output = "";

            String str = FirebaseInstanceId.getInstance().getToken();

            JSONObject user = new JSONObject();
            try {
                user.accumulate("username",username.getText().toString());
                user.accumulate("password",output_pass);
                user.accumulate("name",output_name);
                user.accumulate("id", output_reg);
                user.accumulate("model", output_model);
                user.accumulate("token", str);
                user.accumulate("latitude",0);
                user.accumulate("longitude",0);
                output = user.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                out.write(output.getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d("SERIOUS",s);
            //Log.d("ENTERD","HELLO");
            Log.d("Peace Mama",s);
            if(s=="Fail")
                Log.d("Fail","Fail");
            else {
                Intent i = new Intent(Register.this, MainActivity.class);
                startActivity(i);
            }
        }
    }
}

/*connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "text/plain");

                String input = "YO MAMA, WHATS UP";

                OutputStream out = connection.getOutputStream();
                out.write(input.getBytes());
                out.flush();
                /*InputStream stream = connection.getInputStream();

                reader = new BufferedReader((new InputStreamReader(stream)));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null)
                    buffer.append(line);
                Log.d("YOYO",buffer.toString());
                return buffer.toString();*/
