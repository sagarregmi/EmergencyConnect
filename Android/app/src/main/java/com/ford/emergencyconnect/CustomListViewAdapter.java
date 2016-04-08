package com.ford.emergencyconnect;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sregmi1 on 3/23/16.
 */
public class CustomListViewAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    int [] imageId;
    private static LayoutInflater inflater=null;
    ResponseMessage responseMessage = null;
    private ArrayList data;

    public CustomListViewAdapter(ResponseScreen responseScreen, ArrayList d) {

        data = d;
        activity = responseScreen;
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder
    {
        public TextView tvResponderName;
        public TextView tvResponderSkills;
        public TextView tvETAValue;
        public TextView tvPhoneNumber;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.responders_list_item, null);

            holder = new ViewHolder();
            holder.tvResponderName = (TextView) vi.findViewById(R.id.tvResponderName);
            holder.tvResponderSkills = (TextView) vi.findViewById(R.id.tvResponderSkills);
            holder.tvETAValue = (TextView) vi.findViewById(R.id.tvETAValue);
            holder.tvPhoneNumber = (TextView) vi.findViewById(R.id.tvResponderPhoneNumber);

            vi.setTag( holder );
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }

        if(data.size()<=0)
        {
            holder.tvResponderName.setText("0 Responder");
            holder.tvResponderSkills.setVisibility(View.INVISIBLE);
            holder.tvPhoneNumber.setVisibility(View.INVISIBLE);

        }
        else
        {
            responseMessage = null;
            responseMessage = ( ResponseMessage ) data.get( position );

            holder.tvResponderSkills.setVisibility(View.VISIBLE);
            holder.tvPhoneNumber.setVisibility(View.VISIBLE);
            holder.tvResponderName.setText("Name: " + responseMessage.getName());
            holder.tvResponderSkills.setText("Skills: " + responseMessage.getSkills() );
            holder.tvETAValue.setText( "" + responseMessage.getETA() + " Mins" );
            holder.tvPhoneNumber.setText( "Phone: " + responseMessage.getPhoneNumber() );
            vi.setOnClickListener(new OnItemClickListener( position ));
        }

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }


    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

        }
    }

}