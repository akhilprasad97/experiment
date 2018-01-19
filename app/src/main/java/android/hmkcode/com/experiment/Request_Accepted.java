package android.hmkcode.com.experiment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by akhilprasad97 on 19/1/18.
 */

public class Request_Accepted extends Activity{

    TextView accepted;

    String donor_user;
    String donor_latitude;
    String donor_longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donor_accept);

        accepted = (TextView)findViewById(R.id.textView7);

        if (getIntent().getExtras() != null) {
            donor_user = getIntent().getExtras().getString("user");
            donor_latitude = getIntent().getExtras().getString("latitude");
            donor_longitude = getIntent().getExtras().getString("longitude");
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("USER IS REQUEST", "Key: " + key + " Value: " + value);
            }
            accepted.setText(donor_user+" accepted your request");
        }

    }
}
