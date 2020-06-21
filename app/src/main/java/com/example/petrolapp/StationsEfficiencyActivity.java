package com.example.petrolapp;

import android.content.ContentValues;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class StationsEfficiencyActivity extends AppCompatActivity {
    private boolean actionBarVisible =true;
    private HashMap<String, Integer> stationsMap=new HashMap<String, Integer>() ;//A dictionary is used to see if we have encountered that station before,
                                                                                    //We use a HashMap to avoid implementing all the map methods
    private ArrayList<Station>Stations=new ArrayList<Station>();//An arraylist to keep track of all our stations

//    Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_efficiency);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();//hides the name of the activity at the top


        ConstraintLayout mContentView=findViewById(R.id.content);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        fetchData();
//        thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                viewData();
//            }
//        });


    }

    private void toggle(){//sets the navigation bar at the bottom visible or not when the user touches the screen
        View decorView=getWindow().getDecorView();
        if(actionBarVisible){

            int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            actionBarVisible =false;
        }
        else{
            int uiOptions=View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            decorView.setSystemUiVisibility(uiOptions);
            actionBarVisible =true;
        }
    }

    private void fetchData(){//directly fetches the raw data to be processed
        Connection connection=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        ContentValues cv=new ContentValues();

        connection.fetchInfo(StationsEfficiencyActivity.this, "get_STATIONS_EFFICIENCY",cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                processJson(response);
            }
        });
    }

    private void processJson(String data) {
        int stationNum=0;
        String name;
        Double eff;

        try {
            JSONArray jsonArray=new JSONArray(data);

            for (int i=0;i<jsonArray.length();i++) {
                JSONObject item= (JSONObject) jsonArray.get(i);
                name=item.getString("PETROL_STATION_NAME");
                eff=item.getDouble("EFFICIENCY");


                if(!stationsMap.containsKey(name)){//checks to see if this is the first time we are seeing this station, if so creates a station object
                    stationsMap.put(name,stationNum);
                    stationNum++;//to ensure each station has a unique number assigned to it

                    Station station=new Station(name);//creates the station with that name
                    Stations.add(station);
                }

                for (Station s:Stations) {// checks to find the station that entry belongs to

                    if(name.equals(s.getName())){
                        s.addEntry(eff);
                        break;
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void viewData(){
        for(Station s:Stations){
            System.out.println(s.getName()+"  : "+s.getAverage());
        }

    }


}
