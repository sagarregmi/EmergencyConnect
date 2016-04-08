package com.ford.emergencyconnect;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sregmi1 on 3/31/16.
 */
public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    public static final String FRAGMENT_TAG = "ResponderListFragment";
    private IMapFragmentCallback callback;
    Button sendDistress;
    private Activity activity;
    View rootView;

    public interface IMapFragmentCallback {
        void onMapFragmentListener();
    }

    @Override
    public void onAttach(Context context) {
        Log.i(FRAGMENT_TAG, "onAttach Enter");
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
            try {
                callback = (IMapFragmentCallback) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement IGetStartedCallback");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(FRAGMENT_TAG, "onCreateView Enter");
        rootView = inflater.inflate(R.layout.fragment_map_msg, container, false);

        TextView currentUserName = (TextView) rootView.findViewById(R.id.distress_name);
        TextView currentUserAge = (TextView) rootView.findViewById(R.id.distress_age);
        TextView currentUserPreExisting = (TextView) rootView.findViewById(R.id.distress_preexisting);
        TextView currentUserPhone = (TextView) rootView.findViewById(R.id.distress_phone);

        EmergencyConnectApplication ecApp = (EmergencyConnectApplication)activity.getApplicationContext();
        User currentUser = ecApp.getCurrentUser();
        Log.i(FRAGMENT_TAG, "currentUserName  = " + currentUserName);
        currentUserName.setText("Name: " + currentUser.name);
        currentUserAge.setText("Age: " + currentUser.age);
        currentUserPreExisting.setText("Pre-existing Conditions: " + currentUser.skills);
        currentUserPhone.setText("Phone Number: " + currentUser.phone);
        ((TextView) rootView.findViewById(R.id.responseScreenMsg)).setText("Distress Message");

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        /*
        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) getApplicationContext();
        if(ecApp.getRole() == ecApp.ROLE_REGULAR_USER) {
            updateResponderInfoUI();
        }*/
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //LatLng here = new LatLng(lat, lng);
        LatLng hardcoded = new LatLng(37.40, -122.1420958);
        //googleMap.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hardcoded, 13));
        map.addMarker(new MarkerOptions()
                .title("You")
                .position(hardcoded));
    }

    public void createDistressMessageUI(DistressMessage distressMessage) {

        if (distressMessage == null) return;

        Log.i(FRAGMENT_TAG, "createDistressMessageUI Enter distressMessage = " + distressMessage.toString());
        TextView distressName = (TextView) rootView.findViewById(R.id.distress_name);
        TextView distressAge = (TextView) rootView.findViewById(R.id.distress_age);
        TextView distressPreExisting = (TextView) rootView.findViewById(R.id.distress_preexisting);
        TextView distressPhone = (TextView) rootView.findViewById(R.id.distress_phone);

        Log.i(FRAGMENT_TAG, "distressName  = " + distressName);
        distressName.setText("Name: " + distressMessage.name);
        distressAge.setText("Age: " + distressMessage.age);
        distressPreExisting.setText("Pre-existing Conditions: " + distressMessage.preConditions);
        distressPhone.setText("Phone Number: " + distressMessage.phoneNumber);
        ((TextView) rootView.findViewById(R.id.responseScreenMsg)).setText("Distress Message:");
    }

    public void updateResponderInfoUI() {
        Log.i(FRAGMENT_TAG, "updateResponderInfo Enter");

        TextView currentUserName = (TextView) rootView.findViewById(R.id.distress_name);
        TextView currentUserAge = (TextView) rootView.findViewById(R.id.distress_age);
        TextView currentUserSkills = (TextView) rootView.findViewById(R.id.distress_preexisting);
        TextView currentUserPhone = (TextView) rootView.findViewById(R.id.distress_phone);

        EmergencyConnectApplication ecApp = (EmergencyConnectApplication)activity.getApplicationContext();
        User currentUser = ecApp.getCurrentUser();
        Log.i(FRAGMENT_TAG, "currentUserName  = " + currentUserName);
        currentUserName.setText("Name: " + currentUser.name);
        currentUserAge.setText("Age: " + currentUser.age);
        currentUserSkills.setText("Skills: " + currentUser.skills);
        currentUserPhone.setText("Phone Number: " + currentUser.phone);
        ((TextView) rootView.findViewById(R.id.responseScreenMsg)).setText("My Profile");

    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        Log.i(FRAGMENT_TAG, "onClick Enter");
        if (v.equals(sendDistress)) {
            callback.onMapFragmentListener();
        }
    }

    public void addMarker(DistressMessage distressMessage){
        Log.i(FRAGMENT_TAG, "addMarker Enter");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        LatLng distressLocation = new LatLng(distressMessage.lat, distressMessage.lng);
        GoogleMap map = mapFragment.getMap();
        if(map != null) {
            Log.i(FRAGMENT_TAG, "Adding Marker to the Map lat = " + distressMessage.lat + " long = " + distressMessage.lng);
            map.addMarker(new MarkerOptions()
                    .title("DISTRESS")
                    .position(distressLocation));
        }
    }
}
