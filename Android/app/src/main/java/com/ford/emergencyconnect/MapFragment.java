package com.ford.emergencyconnect;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sregmi1 on 3/31/16.
 */
public class MapFragment extends Fragment implements View.OnClickListener {

    public static final String FRAGMENT_TAG = "ResponderListFragment";
    private IMapFragmentCallback callback;
    Button sendDistress;
    private Activity activity;

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
        View rootView = inflater.inflate(R.layout.fragment_map_msg, container, false);

//        Button sendDistress = (Button) rootView.findViewById(R.id.btnCrash);
//        sendDistress.setOnClickListener(this);

        return rootView;
    }

    public void createDistressMessageUI(DistressMessage distressMessage){

        if (distressMessage == null) return;

        //TextView tv = (TextView) findViewById(R.id.distress_message);
        //tv.setText(distressMessage.toString());
        Log.i(FRAGMENT_TAG, "createDistressMessageUI Enter");
        TextView distressName = (TextView) activity.findViewById(R.id.distress_name);
        TextView distressAge = (TextView) activity.findViewById(R.id.distress_age);
        TextView distressPreExisting = (TextView) activity.findViewById(R.id.distress_preexisting);
        TextView distressPhone = (TextView) activity.findViewById(R.id.distress_phone);

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
}
