package com.ford.emergencyconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sregmi1 on 3/31/16.
 */
public class ResponderListFragment extends Fragment {

    ListView lv;
    Activity activity;
    View rootView;
    public static final String FRAGMENT_TAG = "ResponderListFragment";
    private IResponderListFragmentCallback callback;
    String distressKey;
    private Firebase ref = null;
    private ArrayList<ResponseMessage> responseList = new ArrayList<ResponseMessage>();

    /* Following section of Code is only hack for for Video Screencast */
    private static final long ARRIVAL_NOTIFICATION_DELAY = 5000L;
    private static int arrivalTime1 = 4;
    private static int arrivalTime2 = 3;
    private final Handler arrivalHandler = new Handler();
    private ResponseMessage savedResponseMessage;

    private Runnable notifyArrivalTime1 = new Runnable() {
        @Override
        public void run() {
            Log.i(FRAGMENT_TAG, "notifyArrivalTime1() : Notify Arrival Time:responseList.size() = " + responseList.size());
            View v = lv.getChildAt(0);
            if(v == null )
                return;
            TextView etaText = (TextView)v.findViewById(R.id.tvETAValue);
            LinearLayout etaConatainer = (LinearLayout) v.findViewById(R.id.containerETA);

            if( arrivalTime1-- > 0 ) {
                etaText.setText("" + arrivalTime1 + " Mins");
                arrivalHandler.postDelayed(notifyArrivalTime1, ARRIVAL_NOTIFICATION_DELAY);
            } else {
                etaText.setTextColor(Color.WHITE);
                etaConatainer.setBackgroundColor(Color.RED);
                etaText.setText("ARRIVED");
                callback.onResponderListFragmentListener(1);
            }


        }
    };

    private Runnable notifyArrivalTime2 = new Runnable() {
        @Override
        public void run() {
            Log.i(FRAGMENT_TAG, "notifyArrivalTime2() : Notify Arrival Time:responseList.size() = " + responseList.size());
            View v = lv.getChildAt(1);
            if(v == null)
                return;
            TextView etaText = (TextView)v.findViewById(R.id.tvETAValue);
            LinearLayout etaConatainer = (LinearLayout) v.findViewById(R.id.containerETA);
            if( arrivalTime2-- > 0 ) {
                etaText.setText("" + arrivalTime2 + " Mins");
                arrivalHandler.postDelayed(notifyArrivalTime2, ARRIVAL_NOTIFICATION_DELAY);
            } else {
                etaText.setTextColor(Color.WHITE);
                etaConatainer.setBackgroundColor(Color.RED);
                etaText.setText("ARRIVED");
                callback.onResponderListFragmentListener(2);
            }
        }
    };
    /* Above section of Code is only hack for for Video Screencast */

    public interface IResponderListFragmentCallback {
        void onResponderListFragmentListener(int responder);
    }

    @Override
    public void onAttach(Context context) {
        Log.i(FRAGMENT_TAG, "onAttach Enter");
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
            Log.i(FRAGMENT_TAG, "activity =" + activity.toString());
            try {
                callback = (IResponderListFragmentCallback) activity;
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
        rootView = inflater.inflate(R.layout.fragment_reponder_list, container, false);
        return rootView;
    }


    @Override
    public void onStart() {
        Log.i(FRAGMENT_TAG, "onStart Enter");
        super.onStart();
        Firebase.setAndroidContext(activity);
        ref = new Firebase("https://emergencyconnect.firebaseio.com/");

        lv=(ListView) rootView.findViewById(R.id.listViewResponder);

        lv.setAdapter(new CustomListViewAdapter((ResponseScreen)activity, responseList));

        ref.child("response").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(FRAGMENT_TAG, "onStart onChildAdded Enter");
                long time = (long) dataSnapshot.child("timestamp").getValue();
                Log.i(FRAGMENT_TAG, "Difference in ms " + (new Date().getTime() - time));
                if (new Date().getTime() - time <= 30000) {
                    Log.i("Debug", dataSnapshot.toString());
                    Log.i(FRAGMENT_TAG, "onStart onChildAdded Creating New ResponseMessage");
                    ResponseMessage message = new ResponseMessage(dataSnapshot.child("name").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("age").getValue().toString()),
                            dataSnapshot.child("skills").getValue().toString(),
                            dataSnapshot.child("phoneNumber").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("ETA").getValue().toString()) / 60,
                            dataSnapshot.child("distressKey").getValue().toString());
                    Log.i(FRAGMENT_TAG, message.toString());
                    if (message.distressKey.equals(distressKey)) {
                        Log.i(FRAGMENT_TAG, "onStart onChildAdded Adding Response message to the list");
                        responseList.add(message);
                        Log.i(FRAGMENT_TAG, "onStart responseList.size() = " + responseList.size());
                        lv.invalidateViews();
                        arrivalHandler.postDelayed(notifyArrivalTime1, ARRIVAL_NOTIFICATION_DELAY);
                        arrivalHandler.postDelayed(notifyArrivalTime2, ARRIVAL_NOTIFICATION_DELAY*2);
                    } else {
                        Log.i(FRAGMENT_TAG, "onChildAdded message.distressKey not equal to distressKey: " + distressKey);
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

    public void setDistressKey(String distressKey) {
        Log.i(FRAGMENT_TAG, "setDistressKey Enter");
        this.distressKey = distressKey;

    }
}
