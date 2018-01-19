package android.hmkcode.com.experiment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

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

import static android.hmkcode.com.experiment.MainActivity.*;

/**
 * Created by akhilprasad97 on 18/1/18.
 */

public class Request_Charge extends Activity {

    TextView success;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_charge);

        success = (TextView)findViewById(R.id.textView4);

        new sendRequest().execute("http://10.1.85.42:8000/requestForCharge");

    }

    public class sendRequest extends AsyncTask<String, String, String> {
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
            Log.d("REQUEST_SUCCESS",buffer.toString());
            //success.setText(buffer.toString());
            return buffer.toString();
        }

        private void writeStream(OutputStream out) {
            SharedPreferences session = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            try {
                JSONObject json = new JSONObject();
                json.accumulate("user",session.getString(Username,"PEACE"));
                json.accumulate("id",session.getString(RegID,"PEACE"));
                json.accumulate("charge",6);
                out.write(json.toString().getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
