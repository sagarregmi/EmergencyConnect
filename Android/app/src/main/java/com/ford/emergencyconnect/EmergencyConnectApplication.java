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
    int appState = DRIVER_MODE_ENABLED; // Default set it to Enabled for the Driver

    private String TAG = EmergencyConnectApplication.class.getSimpleName();
    private static EmergencyConnectApplication singleton;
    private static Context mContext;

    private int totalPassengers = 0;

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

    public int getTotalPassengers() {
        return totalPassengers;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate()");
        singleton = this;
        this.mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
