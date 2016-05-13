package com.ford.emergencyconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
    private Button btnLogout;
    Marker distressMarker, Responder1Marker, Responder2Marker;

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

        TextView distressName_title = (TextView) rootView.findViewById(R.id.distress_name_title);
        TextView distressAge_title = (TextView) rootView.findViewById(R.id.distress_age_title);
        TextView distressPreExisting_title = (TextView) rootView.findViewById(R.id.distress_preexisting_title);
        TextView distressPhone_title = (TextView) rootView.findViewById(R.id.distress_phone_title);
        distressName_title.setVisibility(View.INVISIBLE);
        distressAge_title.setVisibility(View.INVISIBLE);
        distressPreExisting_title.setVisibility(View.INVISIBLE);
        distressPhone_title.setVisibility(View.INVISIBLE);

        View v = (View)rootView.findViewById(R.id.distressMessageUIViewSeparator);
        v.setVisibility(View.INVISIBLE);

        btnLogout = (Button)rootView.findViewById(R.id.btnResponseLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finishAffinity();
                Intent i = new Intent(activity, LoginActivity.class);
                startActivity(i);
            }
        });


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

        EmergencyConnectApplication ecApp = (EmergencyConnectApplication) activity.getApplication();
        if( ecApp.getRole() != EmergencyConnectApplication.ROLE_REGULAR_USER) {
            ecApp = (EmergencyConnectApplication) activity.getApplicationContext();
            double lat = ecApp.getMyLocation().getLat();
            double lng = ecApp.getMyLocation().getLong();
            LatLng hardcoded = new LatLng(lat, lng);
            //googleMap.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(hardcoded, 13));
            Responder1Marker = map.addMarker(new MarkerOptions()
                    .title("You")
                    .position(hardcoded));
        }
    }

    public void createDistressMessageUI(DistressMessage distressMessage) {

        if (distressMessage == null) return;

        Log.i(FRAGMENT_TAG, "createDistressMessageUI Enter distressMessage = " + distressMessage.toString());
        TextView distressName = (TextView) rootView.findViewById(R.id.distress_name);
        TextView distressAge = (TextView) rootView.findViewById(R.id.distress_age);
        TextView distressPreExisting = (TextView) rootView.findViewById(R.id.distress_preexisting);
        TextView distressPhone = (TextView) rootView.findViewById(R.id.distress_phone);

        Log.i(FRAGMENT_TAG, "distressName  = " + distressName);
        distressName.setText("" + distressMessage.name);
        distressAge.setText(""+distressMessage.age);
        distressPreExisting.setText(""+distressMessage.preConditions);
        distressPhone.setText(""+distressMessage.phoneNumber);
        ((TextView) rootView.findViewById(R.id.responseScreenMsg)).setText("Distress Message");

        TextView distressName_title = (TextView) rootView.findViewById(R.id.distress_name_title);
        TextView distressAge_title = (TextView) rootView.findViewById(R.id.distress_age_title);
        TextView distressPreExisting_title = (TextView) rootView.findViewById(R.id.distress_preexisting_title);
        TextView distressPhone_title = (TextView) rootView.findViewById(R.id.distress_phone_title);
        distressName_title.setVisibility(View.VISIBLE);
        distressAge_title.setVisibility(View.VISIBLE);
        distressPreExisting_title.setVisibility(View.VISIBLE);
        distressPhone_title.setVisibility(View.VISIBLE);

        View v = (View)rootView.findViewById(R.id.distressMessageUIViewSeparator);
        v.setVisibility(View.VISIBLE);
    }

    /*
    public void updateResponderInfoUI() {
        Log.i(FRAGMENT_TAG, "updateResponderInfo Enter");

        TextView currentUserName = (TextView) rootView.findViewById(R.id.distress_name);
        TextView currentUserAge = (TextView) rootView.findViewById(R.id.distress_age);
        TextView currentUserSkills = (TextView) rootView.findViewById(R.id.distress_preexisting);
        TextView currentUserPhone = (TextView) rootView.findViewById(R.id.distress_phone);

        EmergencyConnectApplication ecApp = (EmergencyConnectApplication)activity.getApplicationContext();
        User currentUser = ecApp.getCurrentUser();
        Log.i(FRAGMENT_TAG, "currentUserName  = " + currentUserName);
        currentUserName.setText(currentUser.name);
        currentUserAge.setText(currentUser.age);
        currentUserSkills.setText(currentUser.skills);
        currentUserPhone.setText(currentUser.phone);
        ((TextView) rootView.findViewById(R.id.responseScreenMsg)).setText("My Profile");
    }
*/
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
            distressMarker = map.addMarker(new MarkerOptions()
                    .title("DISTRESS")
                    .position(distressLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            EmergencyConnectApplication ecApp = (EmergencyConnectApplication) activity.getApplicationContext();
            double lat = ecApp.getMyLocation().getLat();
            double lng = ecApp.getMyLocation().getLong();
            builder.include(new LatLng(lat, lng));
            builder.include(distressLocation);
            LatLngBounds bounds = builder.build();
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
        }
    }

    public void updateMarkerResponder1() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        GoogleMap map = mapFragment.getMap();
        LatLng hardcoded = new LatLng(distressMarker.getPosition().latitude+0.0005, distressMarker.getPosition().longitude+0.0001);
        map.clear();

        map.addMarker(new MarkerOptions()
                .title("DISTRESS")
                .position(distressMarker.getPosition())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //Responder1Marker.remove();
        //distressMarker.remove();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hardcoded, 13));
        Marker responder1Marker = map.addMarker(new MarkerOptions()
                .title("1st Responder Arrived")
                .position(hardcoded)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        responder1Marker.showInfoWindow();
    }

    public void updateMarkerResponder2() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        GoogleMap map = mapFragment.getMap();
        LatLng hardcoded = new LatLng(distressMarker.getPosition().latitude - 0.0005, distressMarker.getPosition().longitude - 0.0001);
        map.clear();
        map.addMarker(new MarkerOptions()
                .title("DISTRESS")
                .position(distressMarker.getPosition())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hardcoded, 13));
        Marker responder2Marker = map.addMarker(new MarkerOptions()
                .title("2nd Responder Arrived")
                .position(hardcoded)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        responder2Marker.showInfoWindow();
    }
}
