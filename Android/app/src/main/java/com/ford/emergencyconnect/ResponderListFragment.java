package com.ford.emergencyconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sregmi1 on 3/31/16.
 */
public class ResponderListFragment extends Fragment implements View.OnClickListener {

    ListView lv;
    Activity activity;
    public static final String FRAGMENT_TAG = "ResponderListFragment";
    private IResponderListFragmentCallback callback;
    Button sendDistress;
    String distressKey;
    private Firebase ref = null;
    private ArrayList<ResponseMessage> responseList = new ArrayList<ResponseMessage>();
    public interface IResponderListFragmentCallback {
        void onResponderListFragmentListener();
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
        View rootView = inflater.inflate(R.layout.fragment_reponder_list, container, false);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Intent intent = activity.getIntent();
        distressKey = intent.getStringExtra("distressKey");
        Firebase.setAndroidContext(activity);
        ref = new Firebase("https://emergencyconnect.firebaseio.com/");

        lv=(ListView) activity.findViewById(R.id.listViewResponder);
        responseList.add(new ResponseMessage("Sagar", 30, "Basic First Aid" , "214-738-5328", 5,
                "123"));

        responseList.add(new ResponseMessage("Regmi", 27, "First Aid", "847-209-5328", 4,
                "345"));

        lv.setAdapter(new CustomListViewAdapter((ResponderScreen)activity, responseList));

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
                            Integer.parseInt(dataSnapshot.child("ETA").getValue().toString()) / 60,
                            dataSnapshot.child("distressKey").getValue().toString());
                    Log.i("Debug", message.toString());
                    if (message.distressKey.equals(distressKey)) {
                        responseList.add(message);
                        lv.invalidateViews();
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
    @Override
    public void onClick(View v) {
        Log.i(FRAGMENT_TAG, "onClick Enter");
        if (v.equals(sendDistress)) {
            callback.onResponderListFragmentListener();
        }
    }
}
