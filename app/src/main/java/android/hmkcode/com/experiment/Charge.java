package android.hmkcode.com.experiment;

/**
 * Created by akhilprasad97 on 18/1/18.
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Charge extends AppCompatActivity {

    TextView currentCharge;
    Button request, start, abort;
    ProgressBar progress;

    BluetoothDevice myDevice;
    BluetoothAdapter myAdapter;
    BluetoothSocket mySocket = null;
    InputStream mmIn = null;
    OutputStream mmOut = null;
    boolean isBtConnected = false;
    boolean requested = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charge);

        myAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myAdapter==null) {
            Toast.makeText(getBaseContext(), "Device Does Not Support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(!myAdapter.isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT,1);
        }

        Set<BluetoothDevice> paired = myAdapter.getBondedDevices();
        if(paired.size()>0){
            for (BluetoothDevice device : paired ){
                myDevice = device;
            }
        }

        Toast.makeText(getBaseContext(), myDevice.getName() + " Connected", Toast.LENGTH_SHORT).show();

        currentCharge = findViewById(R.id.currentCharge);
        request = findViewById(R.id.request);
        start = findViewById(R.id.start);
        abort = findViewById(R.id.abort);
        progress = findViewById(R.id.progress);

        new Transfer().execute();

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Do Nothing", Toast.LENGTH_SHORT).show();
                show(view, "?");
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show(view, "D0100");
            }
        });

        abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show(view, "$");
            }
        });
    }

    private void send_charge(String s)
    {
        if(mySocket!=null)
        {
            try
            {
                mmOut.write(s.getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        //Toast.makeText(getApplicationContext(), "Finish", Toast.LENGTH_SHORT).show();
    }

    public void show(View v, final String num) {
        //Toast.makeText(getApplicationContext(), "show() Entered", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Charge.this);

        alertDialog.setTitle("Confirmation")
                .setMessage("Want to go ahead?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        send_charge(num);
                        if(!num.equals("$"))
                            Toast.makeText(getApplicationContext(), "Charge Transfer : " + num + " mAh", Toast.LENGTH_SHORT).show();
                        if(num.equals("?")) {
                            //Toast.makeText(getApplicationContext(), mySocket.toString(), Toast.LENGTH_SHORT).show();
                            if(!requested) {
                                new request_charge().execute();
                                requested = true;
                            }
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });

        alertDialog.show();
    }

    private class request_charge extends AsyncTask<String, Void, String>
    {
        byte  buffer[] = new byte[1024];
        int n;
        @Override
        protected void onPreExecute()
        {
            try {
                mmIn = mySocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                n = mmIn.read(buffer, 0, 6);
                Log.d("Bytes read",String.valueOf(n));
                buffer[n] = '\0';
                return new String(buffer);
            }
            catch (IOException e)
            {
                Log.d("error","Reached catch block");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            //Toast.makeText(getApplicationContext(), "Entered PostExecute()", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), result + " received", Toast.LENGTH_SHORT).show();
            try {
                mmIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String str = "Current Charge:" + String.valueOf(result);
            currentCharge.setText(str);
        }
    }

    private class Transfer extends AsyncTask<String, Void, String>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            //nothing done here
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                if (mySocket == null || !isBtConnected)
                {
                    mySocket = myDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mySocket.connect();
                    mmOut = mySocket.getOutputStream();
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            //Toast.makeText(getApplicationContext(), "Socket Closed", Toast.LENGTH_SHORT).show();
            if (!ConnectSuccess)
            {
                //do nothing
            }
            else
            {
                isBtConnected = true;
            }
        }
    }
}

