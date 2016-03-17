package com.ford.emergencyconnect;

import android.app.Application;

/**
 * Created by sregmi1 on 3/16/16.
 */
public class EmergencyConnectApplication extends Application{

    private static final int ROLE_DISTRESSER = 1;
    private static final int ROLE_RESPONDER = 2;
    private int role = ROLE_DISTRESSER; // Default set it to Distresser

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
