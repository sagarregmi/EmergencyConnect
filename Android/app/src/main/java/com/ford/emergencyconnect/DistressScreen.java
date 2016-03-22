package com.ford.emergencyconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.Calendar;

/**
 * Created by sregmi1 on 3/15/16.
 */
public class DistressScreen extends AppCompatActivity implements LocationListener {
    double lat;
    double lng;
    LocationManager locationManager;
    private String provider;
    private SeekBar seekBar;
    private Switch mDisableAppSwitch;
    private TextView textViewTotalPassengersNum;
    private int totalPassengers = 0;

    private static final String TAG = DistressScreen.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distress_screen);

        textViewTotalPassengersNum = (TextView)findViewById(R.id.tvTotalPassengersNum);
        seekBar = (SeekBar) findViewById(R.id.distressScreenSeekbar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                Log.d(TAG, "onProgressChanged: " + progress);
                textViewTotalPassengersNum.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
                totalPassengers = progressChanged;
                Log.d(TAG, "onStopTrackingTouch: " + totalPassengers);
                textViewTotalPassengersNum.setText("" + totalPassengers);
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
        final Firebase ref = new Firebase("https://emergencyconnect.firebaseio.com/");

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

        Button sendDistress = (Button) findViewById(R.id.crashButton);
        sendDistress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistressMessage message = new DistressMessage(lat, lng,
                        "Owen", 26, "None", "555-555-5555", 0);
                ref.child("distress").push().setValue(message);
            }
        });

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
}
