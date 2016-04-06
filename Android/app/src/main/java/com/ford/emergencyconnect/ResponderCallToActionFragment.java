package com.ford.emergencyconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.client.Firebase;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by sregmi1 on 4/1/16.
 */
public class ResponderCallToActionFragment extends Fragment{

    public static final String FRAGMENT_TAG = "ResponderCTA";

    Context context;
    Activity activity;
    Firebase ref;
    View rootView;
    private IResponderCallToActionCallback callback;
    private ArrayList<ResponseMessage> responseList = new ArrayList<ResponseMessage>();

    public interface IResponderCallToActionCallback {
        void onResponderCallToActionFragmentListener();
    }

    @Override
    public void onAttach(Context context) {
        Log.i(FRAGMENT_TAG, "onAttach Enter");
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;

            try {
                callback = (IResponderCallToActionCallback) activity;
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
        rootView = inflater.inflate(R.layout.fragment_calltoaction, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(FRAGMENT_TAG, "onStart Enter");
        Button yesResponse = (Button) rootView.findViewById(R.id.yesResponse);
        //yesResponse.setEnabled(false);
        yesResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(FRAGMENT_TAG, "onClick() - Responder: YES");
                callback.onResponderCallToActionFragmentListener();
            }

        });

    }

    public void enableYesButton() {
        Log.i(FRAGMENT_TAG, "enableYesButton Enter");
        if( rootView != null) {
            Button yesResponse = (Button) rootView.findViewById(R.id.yesResponse);
            if (yesResponse != null) {
                yesResponse.setEnabled(true);
            }
        }
    }
}
