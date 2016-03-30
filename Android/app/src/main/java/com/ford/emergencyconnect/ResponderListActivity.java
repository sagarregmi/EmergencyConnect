package com.ford.emergencyconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toolbar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sregmi1 on 3/23/16.
 */
public class ResponderListActivity extends AppCompatActivity{

    ListView lv;
    Context context;
    String distressKey;
    private ArrayList<ResponseMessage> responseList = new ArrayList<ResponseMessage>();
    private Firebase ref = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responders_list);
        Intent intent = getIntent();
        distressKey = intent.getStringExtra("distressKey");
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://emergencyconnect.firebaseio.com/");

        android.support.v7.widget.Toolbar
                myToolbar = (android.support.v7.widget.Toolbar
                ) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.LightRed));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.ec_app_icon);
        getSupportActionBar().setTitle("Driver Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        lv=(ListView) findViewById(R.id.listView);

        responseList.add(new ResponseMessage("Sagar", 30, "Basic First Aid" , "214-738-5328", 5,
                                              "123"));

        responseList.add(new ResponseMessage("Regmi", 27, "First Aid" , "847-209-5328", 4,
                "345"));
        lv.setAdapter(new CustomListViewAdapter(this, responseList));

        ref.child("response").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                long time = (long) dataSnapshot.child("timestamp").getValue();
                if (new Date().getTime() - time <= 30000) {
                    Log.i("Debug", dataSnapshot.toString());
                    ResponseMessage message = new ResponseMessage(dataSnapshot.child("name").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("age").getValue().toString()),
                            dataSnapshot.child("skills").getValue().toString(),
                            dataSnapshot.child("phoneNumber").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("ETA").getValue().toString())/60,
                            dataSnapshot.child("distressKey").getValue().toString());
                    Log.i("Debug", message.toString());
                    if(message.distressKey.equals(distressKey)) {
                        responseList.add(message);
                        lv.invalidateViews();
                    }
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
