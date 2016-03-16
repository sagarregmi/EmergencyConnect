package com.ford.emergencyconnect;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

/**
 * Created by sregmi1 on 3/15/16.
 */
public class DistressScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distress_screen);
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://emergencyconnect.firebaseio.com/");

        Button sendDistress = (Button) findViewById(R.id.crashButton);
        sendDistress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistressMessage message = new DistressMessage(37.4108709, -122.1462192, "Owen", 26, "None", "555-555-5555", 0);
                ref.child("distress").push().setValue(message);
            }
        });
    }
}
