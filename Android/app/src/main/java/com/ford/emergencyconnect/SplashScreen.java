package com.ford.emergencyconnect;

/**
 * Created by sregmi1 on 3/14/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ford.emergencyconnect.R;

public class SplashScreen extends Activity {

    // splash screen timer
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // start the app main activity
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}