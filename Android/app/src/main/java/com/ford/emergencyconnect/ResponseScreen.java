package com.ford.emergencyconnect;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ResponseScreen extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    double lat;
    double lng;
    LocationManager locationManager;
    private String provider;
    final String API_KEY = "AIzaSyBbLd62uXZrIQygltPle2l3l88CoT8C8oo";
    DistressMessage distressMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_screen);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Debug", "Permissions error");
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.i("Debug", "Last known location null");
        }

        //Initialize Map
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
                    distressMessage = new DistressMessage((double) dataSnapshot.child("lat").getValue(),
                            (double) dataSnapshot.child("lng").getValue(),
                            dataSnapshot.child("name").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("age").getValue().toString()),
                            dataSnapshot.child("preConditions").getValue().toString(),
                            dataSnapshot.child("phoneNumber").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("numPassengers").getValue().toString()));
                    tv.setText(distressMessage.toString());
                    //Determine if GPS location is close enough
                    Log.i("Debug", "DistressGPS " + distressMessage.lat + ", " + distressMessage.lng);
                    Log.i("Debug", "ResponseGPS " + lat + ", " + lng);
                    LatLng responseLocation = new LatLng(lat, lng);
                    LatLng distressLocation = new LatLng(distressMessage.lat, distressMessage.lng);
                    GoogleMap map = mapFragment.getMap();
                    if(map != null) {
                        map.addMarker(new MarkerOptions()
                                .title("DISTRESS")
                                .position(distressLocation));
                    }
                    isInRange();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    private void isInRange(){
        final boolean inRange = false;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + distressMessage.lat + "," + distressMessage.lng +
                "&destination=" + "37.4234775,-119.1420958" + "&key=AIzaSyD2drkOw0W2n6ZAjlkgUCfNc12z0O7E4Jk";
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
                                int travelTime = 0;
                                JSONObject route = routes.getJSONObject(i);
                                JSONArray legs = route.getJSONArray("legs");
                                for(int j=0; j<legs.length(); j++){
                                    JSONObject leg = legs.getJSONObject(j);
                                    JSONObject duration = leg.getJSONObject("duration");
                                    int legTime = duration.getInt("value");
                                    travelTime += legTime;
                                }
                                Log.i("Debug", "Travel Time = " + travelTime);
                                if(travelTime <= 600){
                                    //Within 10 minutes
                                    if(!sentNotif){
                                        //send Notification
                                        sentNotif = true;
                                        sendNotification();
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

    private void sendNotification(){
        Log.i("Debug", "Notification Sent");
    }

    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Debug", "Permissions Error");
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Debug", "Permissions Error");
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        //LatLng here = new LatLng(lat, lng);
        LatLng hardcoded = new LatLng(37.4234775, -122.1420958);
        //googleMap.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hardcoded, 13));
        map.addMarker(new MarkerOptions()
                .title("You")
                .position(hardcoded));
    }
}
