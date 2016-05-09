package com.ford.emergencyconnect;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
public class ResponseScreen extends AppCompatActivity implements ResponderListFragment.IResponderListFragmentCallback,
        MapFragment.IMapFragmentCallback, ResponderCallToActionFragment.IResponderCallToActionCallback{

    private static final String TAG = ResponseScreen.class.getSimpleName();
    User user;
    Firebase ref;
    DistressMessage distressMessage;
    String distressKey;
    int ETA;
    boolean inForeground = false;
    private ResponderCallToActionFragment responderCallToActionFragment;
    private ResponderListFragment responderListFragment;
    private MapFragment mapFragment;
    private static boolean initFB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate Enter initFB = " + initFB);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_screen);

        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.responder_screen_map_msg_fragment_container,
                mapFragment, MapFragment.FRAGMENT_TAG).commit();

        //Intent intent = getIntent();
        //int fragmentid = intent.getIntExtra(ecApp.INTENT_FRAGMENT_ID, ecApp.FRAGMENT_ID_CALL_TO_ACTION);
        if(ecApp.getRole() == ecApp.ROLE_REGULAR_USER) {
            Intent intent = getIntent();
            distressKey = intent.getStringExtra("distressKey");
            responderListFragment = new ResponderListFragment();
            responderListFragment.setDistressKey(distressKey);
            getSupportFragmentManager().beginTransaction().add(R.id.responder_screen_calltoaction_fragment_container,
                    responderListFragment, ResponderListFragment.FRAGMENT_TAG).commit();
            //LinearLayout ll = (LinearLayout) findViewById(R.id.responderInfoContainer);
            //ll.removeAllViews();


        } else if (ecApp.getRole() == ecApp.ROLE_RESPONDER) {
            responderCallToActionFragment = new ResponderCallToActionFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.responder_screen_calltoaction_fragment_container,
                    responderCallToActionFragment, ResponderCallToActionFragment.FRAGMENT_TAG).commit();

        }


        android.support.v7.widget.Toolbar
                myToolbar = (android.support.v7.widget.Toolbar
                ) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.LightRed));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.ec_app_icon);
        if(ecApp.getRole() == ecApp.ROLE_REGULAR_USER) {
            getSupportActionBar().setTitle("Driver");
        } else {
            getSupportActionBar().setTitle("Responders");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Get User
        ecApp = (EmergencyConnectApplication) getApplicationContext();
        user = ecApp.getCurrentUser();

        if( !initFB ) {
            Firebase.setAndroidContext(this);
            ref = new Firebase("https://emergencyconnect.firebaseio.com/");
            //initLocation();
            initFirebase();
        }

        Button directionsButton = (Button) findViewById(R.id.directionsButton);
        directionsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
                double lat = ecApp.getMyLocation().getLat();
                double lng = ecApp.getMyLocation().getLong();
                if(lat==0.0 || lng==0.0 || distressMessage==null) return;

                String startLatLng = lat + "," + lng;
                String endLatLng = distressMessage.lat + "," + distressMessage.lng;

                Intent directionsIntent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + startLatLng + "&daddr=" + endLatLng));
                startActivity(directionsIntent);
            }

        });

        inForeground = true;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy Enter");
        super.onDestroy();
        initFB = false;
        ref = null;
    }

    private void initFirebase(){
        ref.child("distress").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Ignore old messages
                Log.i(TAG, "Distress Child Added");
                long time = (long) dataSnapshot.child("timestamp").getValue();
                Log.i(TAG, new Date().getTime() - time + " ms");

                if (new Date().getTime() - time <= 30000) {
                    distressMessage = new DistressMessage((double) dataSnapshot.child("lat").getValue(),
                            (double) dataSnapshot.child("lng").getValue(),
                            dataSnapshot.child("name").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("age").getValue().toString()),
                            dataSnapshot.child("preConditions").getValue().toString(),
                            dataSnapshot.child("phoneNumber").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("numPassengers").getValue().toString()));

                    if (mapFragment != null) {
                        mapFragment.createDistressMessageUI(distressMessage);
                    }

                    distressKey = dataSnapshot.getKey();
                    isInRange();
                }
                initFB = true;
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
        Log.i(TAG, url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Got directions, determine if within 10 minute drive.
                        Log.i(TAG, response);
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
                                Log.i(TAG, "Travel Time = " + ETA);
                                if(ETA <= 600){
                                    //Within 10 minutes
                                    if(!sentNotif){
                                        //send Notification
                                        sentNotif = true;
                                        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
                                        if(!inForeground && ecApp.getRole() == ecApp.ROLE_RESPONDER) {
                                            Log.i(TAG, "isInRange() Activity is not in foreground");
                                            sendNotification();
                                        }else{
                                            MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.responder_screen_map_msg_fragment_container);
                                            if( mapFragment != null ) {
                                                mapFragment.addMarker(distressMessage);
                                            }

                                        }
                                        //Enable response button
                                        if( responderCallToActionFragment != null) {
                                            responderCallToActionFragment.enableYesButton();
                                        }
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
                Log.i(TAG, "That didn't work!");
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
        Log.i(TAG, "Notification Sent");

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
        initFB = false;
    }

    private void sendResponseMessage(){
        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
        ResponseMessage message = new ResponseMessage(ecApp.getCurrentUser().name,
                ecApp.getCurrentUser().age, ecApp.getCurrentUser().skills,
                ecApp.getCurrentUser().phone, ETA, distressKey);
        ref.child("response").push().setValue(message);
    }

    @Override
    public void onResponderListFragmentListener(int responder) {
        Log.i(TAG, "onGetStarted Enter");
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.responder_screen_map_msg_fragment_container);
        if( mapFragment != null ) {
            if(responder == 1) {
                mapFragment.updateMarkerResponder1();
            } else {
                mapFragment.updateMarkerResponder2();
            }

        }
    }

    @Override
    public void onMapFragmentListener() {
        Log.i(TAG, "onGetStarted Enter");
    }

    @Override
    public void onResponderCallToActionFragmentListener() {
        Log.i(TAG, "onGetStarted Enter");
        ResponderListFragment responderListFragment = new ResponderListFragment();
        responderListFragment.setDistressKey(distressKey);
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .hide(responderCallToActionFragment)
                .add(R.id.responder_screen_calltoaction_fragment_container, responderListFragment)
                .commit();
        sendResponseMessage();
    }

    @Override
    public void onBackPressed() {
    }
}
