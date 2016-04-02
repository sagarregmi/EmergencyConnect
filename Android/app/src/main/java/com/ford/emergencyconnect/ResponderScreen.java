package com.ford.emergencyconnect;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sregmi1 on 4/1/16.
 */
public class ResponderScreen extends AppCompatActivity implements ResponderListFragment.IResponderListFragmentCallback,
        MapFragment.IMapFragmentCallback, ResponderCallToActionFragment.IResponderCallToActionCallback{

    private static final String TAG = ResponderScreen.class.getSimpleName();
    User user;
    Firebase ref;
    DistressMessage distressMessage;
    String distressKey;
    int ETA;
    boolean inForeground = false;
    ResponderCallToActionFragment responderCallToActionFragment;
    ResponderListFragment responderListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate Enter");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_screen);

        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
        //User user  = ecApp.getCurrentUser();
        //if( ecApp.getRole() == ecApp.ROLE_REGULAR_USER) {
        getSupportFragmentManager().beginTransaction().add(R.id.responder_screen_map_msg_fragment_container,
                new MapFragment(), MapFragment.FRAGMENT_TAG).commit();

        Intent intent = getIntent();
        int fragmentid = intent.getIntExtra(ecApp.INTENT_FRAGMENT_ID, ecApp.FRAGMENT_ID_CALL_TO_ACTION);
        responderCallToActionFragment = new ResponderCallToActionFragment();
        responderListFragment = new ResponderListFragment();

        if( fragmentid == ecApp.FRAGMENT_ID_CALL_TO_ACTION ) {
            getSupportFragmentManager().beginTransaction().add(R.id.responder_screen_calltoaction_fragment_container,
                    responderCallToActionFragment, ResponderCallToActionFragment.FRAGMENT_TAG).commit();
        } else {
            Log.i(TAG, "onCreate begining transaction for ResponderListFragment");
            getSupportFragmentManager().beginTransaction().add(R.id.responder_screen_calltoaction_fragment_container,
                    responderListFragment, ResponderListFragment.FRAGMENT_TAG).commit();
        }

        android.support.v7.widget.Toolbar
                myToolbar = (android.support.v7.widget.Toolbar
                ) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.LightRed));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.ec_app_icon);
        getSupportActionBar().setTitle("Responders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Get User
        ecApp = (EmergencyConnectApplication) getApplicationContext();
        user = ecApp.getCurrentUser();

        Firebase.setAndroidContext(this);
        ref = new Firebase("https://emergencyconnect.firebaseio.com/");
        //initLocation();
        initFirebase();

        inForeground = true;
    }

    private void initFirebase(){
        ref.child("distress").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Ignore old messages
                Log.i("Debug", "Distress Child Added");
                long time = (long) dataSnapshot.child("timestamp").getValue();
                Log.i("Debug", new Date().getTime() - time + " ms");

                if (new Date().getTime() - time <= 30000) {
                    distressMessage = new DistressMessage((double) dataSnapshot.child("lat").getValue(),
                            (double) dataSnapshot.child("lng").getValue(),
                            dataSnapshot.child("name").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("age").getValue().toString()),
                            dataSnapshot.child("preConditions").getValue().toString(),
                            dataSnapshot.child("phoneNumber").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("numPassengers").getValue().toString()));

                    FragmentManager manager = getSupportFragmentManager();
                    MapFragment fragment = (MapFragment)manager.findFragmentById(R.id.responder_screen_map_msg_fragment_container);
                    fragment.createDistressMessageUI(distressMessage);

                    distressKey = dataSnapshot.getKey();
                    isInRange();
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

    private void isInRange(){
        Log.i(TAG, "isInRange Enter");
        final boolean inRange = false;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + distressMessage.lat + "," + distressMessage.lng +
                "&destination=" + "37.4234775,-122.1420958" + "&key=AIzaSyD2drkOw0W2n6ZAjlkgUCfNc12z0O7E4Jk";
        Log.i("Debug", url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Got directions, determine if within 10 minute drive.
                        Log.i("Debug", response);
                        boolean sentNotif = false;
                        try {
                            JSONObject directions = new JSONObject(response);
                            JSONArray routes = directions.getJSONArray("routes");
                            for(int i=0; i<routes.length(); i++){
                                ETA = 0;
                                JSONObject route = routes.getJSONObject(i);
                                JSONArray legs = route.getJSONArray("legs");
                                for(int j=0; j<legs.length(); j++){
                                    JSONObject leg = legs.getJSONObject(j);
                                    JSONObject duration = leg.getJSONObject("duration");
                                    int legTime = duration.getInt("value");
                                    ETA += legTime;
                                }
                                Log.i("Debug", "Travel Time = " + ETA);
                                if(ETA <= 600){
                                    //Within 10 minutes
                                    if(!sentNotif){
                                        //send Notification
                                        sentNotif = true;
                                        if(!inForeground) {
                                            sendNotification();
                                        }else{
                                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                                    .findFragmentById(R.id.map);
                                            LatLng distressLocation = new LatLng(distressMessage.lat, distressMessage.lng);
                                            GoogleMap map = mapFragment.getMap();
                                            if(map != null) {
                                                map.addMarker(new MarkerOptions()
                                                        .title("DISTRESS")
                                                        .position(distressLocation));
                                            }
                                        }
                                        //Enable response button
                                        Button yesResponse = (Button) findViewById(R.id.yesResponse);
                                        yesResponse.setEnabled(true);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Debug", "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    protected void onResume() {
        super.onResume();
        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
        ecApp.getMyLocation().requestLocationUpdates();
        inForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
        ecApp.getMyLocation().removeUpdates();
        inForeground = false;
    }

    private void sendNotification(){
        Log.i("Debug", "Notification Sent");

        Intent i = new Intent(this, ResponseScreen.class);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ec_app_logo);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(getApplicationContext(),
                        0,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ec_app_logo)
                        .setAutoCancel(true)
                        .setContentTitle("Emergency reported " + ETA/60 + " minutes away!")
                        .setVibrate(new long[]{1000, 1000, 1000})
                        .setContentIntent(resultPendingIntent)
                        .setContentText("Can you help?");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());

    }

    private void sendResponseMessage(){
        ResponseMessage message = new ResponseMessage("Owen", 26, "CPR", "555-555-5555", ETA, distressKey);
        ref.child("response").push().setValue(message);
    }

    @Override
    public void onResponderListFragmentListener() {
        Log.i(TAG, "onGetStarted Enter");
    }

    @Override
    public void onMapFragmentListener() {
        Log.i(TAG, "onGetStarted Enter");
    }


    @Override
    public void onResponderCallToActionFragmentListener() {
        Log.i(TAG, "onGetStarted Enter");
        ResponderListFragment responderListFragment = new ResponderListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .hide(responderCallToActionFragment)
                .add(R.id.responder_screen_calltoaction_fragment_container, responderListFragment)
                .commit();

    }
}
