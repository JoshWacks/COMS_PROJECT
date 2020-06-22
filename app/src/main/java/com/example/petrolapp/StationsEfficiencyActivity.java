package com.example.petrolapp;

import android.content.ContentValues;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
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

    BarChart barChart;

    Thread thread;


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





        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                createGraph();

            }
        });


    }

    private void toggle(){//sets the navigation bar at the bottom visible or not when the user touches the screen
        View decorView=getWindow().getDecorView();
        if(actionBarVisible){

            int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
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
        thread.start();//we are done fetching the data and we can now run the Graph Thread

    }

    private void viewData(){
        for(Station s:Stations){
            System.out.println(s.getName()+"  : "+s.getAverage());
        }

    }

    private void createGraph(){

        barChart=findViewById(R.id.bargraph);

        ArrayList<BarEntry>entries=new ArrayList<>();
        final String[]names=new String[Stations.size()];


        for(Integer i=0;i<Stations.size();i++){
            Station s=Stations.get(i);

            float x=i.floatValue();//the default position of that bar
            float y=s.getAverage().floatValue();//the y value of that bar
            // x and y must be of type float
            entries.add(new BarEntry(x,y));
            String[] arr =s.getName().split(" ",2);//To only get the first word of the station name

            names[i]=arr[0];

        }
        ValueFormatter formatter=new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return names[(int) value];
            }
        };
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(formatter);//sets the values on the x-axis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(Stations.size());//The total number of labels that must appear

        BarDataSet set = new BarDataSet(entries, "Station Efficiencies");

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        barChart.setData(data);

        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.postInvalidate(); // refresh

    }


}
