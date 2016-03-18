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
    public int role = ROLE_REGULAR_USER; // Default set it to Distresser

    private String TAG = EmergencyConnectApplication.class.getSimpleName();
    private static EmergencyConnectApplication singleton;
    private static Context mContext;

    public EmergencyConnectApplication getInstance(){
        return singleton;
    }
    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
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
