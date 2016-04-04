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

    public void createDistressMessageUI(DistressMessage distressMessage){

        if (distressMessage == null) return;

        //TextView tv = (TextView) findViewById(R.id.distress_message);
        //tv.setText(distressMessage.toString());
        Log.i(FRAGMENT_TAG, "createDistressMessageUI Enter distressMessage = " + distressMessage.toString());
        TextView distressName = (TextView) rootView.findViewById(R.id.distress_name);
        TextView distressAge = (TextView) rootView.findViewById(R.id.distress_age);
        TextView distressPreExisting = (TextView) rootView.findViewById(R.id.distress_preexisting);
        TextView distressPhone = (TextView) rootView.findViewById(R.id.distress_phone);

        Log.i(FRAGMENT_TAG, "distressName  = " + distressName);
        distressName.setText(distressMessage.name);
        distressAge.setText("" + distressMessage.age);
        distressPreExisting.setText(distressMessage.preConditions);
        distressPhone.setText(distressMessage.phoneNumber);
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
