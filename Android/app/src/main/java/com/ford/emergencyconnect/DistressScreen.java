package com.ford.emergencyconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sregmi1 on 3/15/16.
 */
public class DistressScreen extends AppCompatActivity implements LocationListener, View.OnClickListener {
    double lat;
    double lng;
    LocationManager locationManager;
    private String provider;
    private Switch mDisableAppSwitch;
    private NumberPicker numberPicker = null;
    private int totalPassengers = 0;
    private Firebase ref = null;

    private static final String TAG = DistressScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distress_screen);

        Button sendDistress = (Button) findViewById(R.id.btnCrash);
        sendDistress.setOnClickListener(this);

        android.support.v7.widget.Toolbar
                myToolbar = (android.support.v7.widget.Toolbar
                ) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.ec_app_logo2);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        numberPicker = (NumberPicker)findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(true);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                totalPassengers = newVal;
                Log.d(TAG, "Total Passengers " + newVal);
                EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
                ecApp.setTotalPassengers(totalPassengers);

            }
        });

        mDisableAppSwitch = (Switch) findViewById(R.id.distressScreenSwitch);

        mDisableAppSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
                if (isChecked) {
                    mDisableAppSwitch.setChecked(true);
                    Log.d(TAG, "App is Disabled");
                    ecApp.setAppState(ecApp.DRIVER_MODE_DISABLED);
                } else {
                    mDisableAppSwitch.setChecked(false);
                    Log.d(TAG, "App is Enabled");
                    ecApp.setAppState(ecApp.DRIVER_MODE_ENABLED);
                }
            }
        });

        Firebase.setAndroidContext(this);
        ref = new Firebase("https://emergencyconnect.firebaseio.com/");

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
                            Integer.parseInt(dataSnapshot.child("ETA").getValue().toString()),
                            dataSnapshot.child("distressKey").getValue().toString());
                    Log.i("Debug", message.toString());
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

    @Override public void onClick(View v) {
        Log.i(TAG, "Creating a Distress message");
        DistressMessage message = new DistressMessage(lat, lng,
                "Owen", 26, "None", "555-555-5555", 0);
        if( null != ref) {
            ref.child("distress").push().setValue(message);
        }

        Intent i = new Intent(DistressScreen.this, ResponderListActivity.class);
        startActivity(i);
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
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar, menu);
        return true;
    }
}
