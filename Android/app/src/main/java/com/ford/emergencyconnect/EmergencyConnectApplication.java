package com.ford.emergencyconnect;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by sregmi1 on 3/16/16.
 */
public class EmergencyConnectApplication extends Application{

    public static final int ROLE_NONE = 0;
    public static final int ROLE_REGULAR_USER = 1;
    public static final int ROLE_RESPONDER = 2;
    public int role = ROLE_REGULAR_USER; // Default set it to Driver ( Distress-er)

    public static final int DRIVER_MODE_DISABLED = 0;
    public static final int DRIVER_MODE_ENABLED = 1;
    public static final int RESPONDER_MODE_DISABLED = 2;
    public static final int RESPONDER_MODE_ENABLED = 3;

    //public static final String INTENT_FRAGMENT_ID = "com.ford.emergencyconnect.fragmentid";
    //public static final int FRAGMENT_ID_CALL_TO_ACTION = 1;
    //public static final int FRAGMENT_ID_RESPONDER_LIST = 2;

    int appState = DRIVER_MODE_ENABLED; // Default set it to Enabled for the Driver

    private String TAG = EmergencyConnectApplication.class.getSimpleName();
    private static EmergencyConnectApplication singleton;
    private static Context mContext;

    private int totalPassengers = 0;
    private User currentUser;

    private MyLocation map;
    //private FireBase fireBase; // To-Do: Create separate Firebase class for all firebase operations

    public EmergencyConnectApplication getInstance(){

        return singleton;
    }

    public int getRole() {
        return role;

    }

    public void setRole(int role) {
        this.role = role;

    }

    public int getAppState() {
        Log.d(TAG, "App state = " + appState);
        return appState;
    }

    public void setAppState(int state) {
        appState = state;
        Log.d(TAG, "App state = " + appState);
    }

    public void setTotalPassengers(int totalPassengers) {

        this.totalPassengers = totalPassengers;
    }

    public void setCurrentUser(User setCurrentUser) {

        currentUser = setCurrentUser;
    }

    public User getCurrentUser() {

        return currentUser;
    }

    public int getTotalPassengers() {

        return totalPassengers;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate()");
        singleton = this;
        this.mContext = this;
        map = new MyLocation(this);
        map.init();
    }

    /*
    public FireBase getFireBase() {

        return fireBase;
    }
    */

    public MyLocation getMyLocation() {

        return map;
    }

    public static Context getContext(){

        return mContext;
    }
}
