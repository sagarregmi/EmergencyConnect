package com.ford.emergencyconnect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toolbar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sregmi1 on 3/23/16.
 */
public class ResponderListActivity extends AppCompatActivity{

    ListView lv;
    Context context;

    private ArrayList<ResponseMessage> responseList = new ArrayList<ResponseMessage>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responders_list);

        /*
        android.support.v7.widget.Toolbar
                myToolbar = (android.support.v7.widget.Toolbar
                ) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        lv=(ListView) findViewById(R.id.listView);

        responseList.add(new ResponseMessage("Sagar", 30, "Basic First Aid" , "214-738-5328", 5,
                                              "123"));

        responseList.add(new ResponseMessage("Regmi", 27, "First Aid" , "847-209-5328", 4,
                "345"));
        lv.setAdapter(new CustomListViewAdapter(this, responseList));
    }
}
