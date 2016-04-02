package com.ford.emergencyconnect;

import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by sregmi1 on 3/31/16.
 */
public class FireBase {

    private com.firebase.client.Firebase ref = null;
    private Context context;

    EmergencyConnectApplication ecApp = null;

    FireBase(Context context) {
        this.context = context;
        ecApp = (EmergencyConnectApplication)context;
    }

    public void init() {
        com.firebase.client.Firebase.setAndroidContext(context);
        ref = new Firebase("https://emergencyconnect.firebaseio.com/");
    }

    public com.firebase.client.Firebase getRef() {
        return ref;
    }

    public String sendDistressMessage() {
        String distressKey = null;
        DistressMessage message = new DistressMessage(ecApp.getMyLocation().getLat(), ecApp.getMyLocation().getLong(),
                "Owen", 26, "None", "555-555-5555", ecApp.getTotalPassengers());

        if( null != ref) {
            Firebase newChildref = ref.child("distress").push();
            distressKey = newChildref.getKey();
            newChildref.setValue(message);
        }
        return distressKey;
    }
}
