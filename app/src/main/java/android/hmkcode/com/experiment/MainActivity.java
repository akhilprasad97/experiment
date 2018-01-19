package android.hmkcode.com.experiment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
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

public class MainActivity extends Activity {

    Button login;
    Button register;
    TextView username;
    TextView password;
    TextView invalid;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    //public static final String  = "phoneKey";
    public static final String Username = "usernameKey";
    public static final String RegID = "regIDKey";
    public static final String Recepient = "recepientKey";
    public static final String Charge_of_recepient = "chargeofrecepient";
    public static final String Recepient_latitude = "RecepientlatitudeKey";
    public static final String Recepient_longitude = "RecepientlongitudeKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button)findViewById(R.id.button3);
        register = (Button)findViewById(R.id.button4);
        username = (TextView)findViewById(R.id.editText9);
        password = (TextView)findViewById(R.id.editText10);
        invalid = (TextView)findViewById(R.id.textView6);

        //Log.d("token", FirebaseInstanceId.getInstance().getToken());
        String str = FirebaseInstanceId.getInstance().getToken();
        Log.d("token","yo "+str);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTask().execute("http://10.1.85.42:8000/login");
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Register.class);
                startActivity(i);
            }
        });
    }

    public class JSONTask extends AsyncTask<String ,String,String>{

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                //connection.connect();

                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                writeStream(out);

                InputStream in = new BufferedInputStream(connection.getInputStream());
                return readStream(in);

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
            String output_user = username.getText().toString();
            String output_pass = password.getText().toString();
            String output = "";

            JSONObject user = new JSONObject();
            try {
                user.accumulate("username",output_user);
                user.accumulate("password",output_pass);
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
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if(s.equals("Fail")){
                invalid.setText("INVALID CREDENTIALS");
            }
            else {
                try {
                    JSONArray json = new JSONArray(s);
                    JSONObject indi = json.getJSONObject(0); // USE LATER WITHOUT FAIL
                    //Log.d("Name", json.getString("name"));
                    //JSONObject indi = new JSONObject(s);
                    editor.putString(Name, indi.getString("name"));
                    editor.putString(Username, indi.getString("username"));
                    editor.putString(RegID, indi.getString("id"));
                    editor.commit();
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


