package com.ford.emergencyconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.client.Firebase;

/**
 * Created by sregmi1 on 3/15/16.
 */
public class DistressScreen extends AppCompatActivity implements View.OnClickListener {

    private TextView tvName;
    private TextView tvAddress;
    private Button btnLogout;
    private Switch mDisableAppSwitch;
    private NumberPicker numberPicker = null;
    private int totalPassengers = 0;
    private User user = null;
    private MyLocation map;
    private Firebase ref = null;
    private static final String TAG = DistressScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distress_screen);

        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();

        Button sendDistress = (Button) findViewById(R.id.btnCrash);
        sendDistress.setOnClickListener(this);

        android.support.v7.widget.Toolbar
                myToolbar = (android.support.v7.widget.Toolbar
                ) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.LightRed));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.ec_app_icon);
        getSupportActionBar().setTitle("Driver Profile");

        tvName = (TextView)findViewById(R.id.tvUserName);
        tvAddress = (TextView)findViewById(R.id.tvUserAddress);
        tvName.setText(ecApp.getCurrentUser().name);
        tvAddress.setText("Palo Alto, CA");
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
                if (null != ecApp) {
                    ecApp.setTotalPassengers(totalPassengers);
                }

            }
        });

        btnLogout = ( Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        ecApp = (EmergencyConnectApplication) getApplicationContext();
        map = ecApp.getMyLocation();

        //fireBase.getFirebase();
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://emergencyconnect.firebaseio.com/");
    }

    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.btnCrash)) {
            NumberPicker passengers = (NumberPicker) findViewById(R.id.numberPicker);
            EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
            if( null != ecApp) {
                ecApp.setTotalPassengers(passengers.getValue());
            }
            DistressMessage message = new DistressMessage(map.getLat(), map.getLong(),
                    ecApp.getCurrentUser().name,
                    ecApp.getCurrentUser().age,
                    ecApp.getCurrentUser().preConditions,
                    ecApp.getCurrentUser().phone,
                    passengers.getValue());
            Log.i(TAG, "Creating a Distress message: " + message.toString());
            if( null != ref) {
                Firebase newChildref = ref.child("distress").push();
                String distressKey = newChildref.getKey();
                newChildref.setValue(message);
                Intent i = new Intent(DistressScreen.this, ResponseScreen.class);
                i.putExtra("distressKey", distressKey);
                //i.putExtra(ecApp.INTENT_FRAGMENT_ID, ecApp.FRAGMENT_ID_RESPONDER_LIST);
                startActivity(i);
            }
        } else if( v == findViewById(R.id.btnLogout)) {
            finishAffinity();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if( map != null) {
            map.requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if( map != null) {
            map.removeUpdates();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
    }
}
