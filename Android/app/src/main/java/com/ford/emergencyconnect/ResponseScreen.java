package com.ford.emergencyconnect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Date;

public class ResponseScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_screen);

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://emergencyconnect.firebaseio.com/");

        ref.child("distress").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Ignore old messages
                Log.i("Debug", "Distress Child Added");
                long time = (long) dataSnapshot.child("timestamp").getValue();
                Log.i("Debug", new Date().getTime() - time + " ms");
                if(new Date().getTime() - time <= 30000){
                    TextView tv = (TextView) findViewById(R.id.displayMessage);
                    DistressMessage message = new DistressMessage((double) dataSnapshot.child("lat").getValue(),
                            (double) dataSnapshot.child("lng").getValue(),
                            dataSnapshot.child("name").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("age").getValue().toString()),
                            dataSnapshot.child("preConditions").getValue().toString(),
                            dataSnapshot.child("phoneNumber").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("numPassengers").getValue().toString()));
                    tv.setText(message.toString());
                    //Determine if GPS location is close enought
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
