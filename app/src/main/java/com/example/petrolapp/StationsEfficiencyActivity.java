package com.example.petrolapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.mrntlu.toastie.Toastie;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StationsEfficiencyActivity extends AppCompatActivity {
    private static String username;
    private Button btnBack;
    private boolean backBtnVisible =true;
    private final HashMap<String, Integer> stationsMap=new HashMap<String, Integer>() ;//A Map is used to see if we have encountered that station before,
                                                                                    //We use a HashMap to avoid implementing all the map methods
    private final ArrayList<Station>Stations=new ArrayList<Station>();//An arraylist to keep track of all our stations

    private BarChart barChart;

    private Thread thread;

    private int numCars;
    private static String selectedPlate;
    private static final ArrayList<CarType>userCars=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_efficiency);

        username=appInformation.getUsername();
        selectedPlate=appInformation.getLiscence_plate();
        checkNumCars();

        configureScreen();

        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                createGraph();//We can only create the graph confidently once we know the data has been fetched
            }
        });

    }
    public void checkNumCars(){
        ContentValues cv = new ContentValues();

        cv.put("USERNAME", username);

        Connection c = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

        c.fetchInfo(StationsEfficiencyActivity.this, "get_CAR_DESC", cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                try {
                    fillUserCars(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillUserCars(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);

        for(int i=0;i<jsonArray.length();i++){
            JSONObject item= (JSONObject) jsonArray.get(i);

            CarType ct=new CarType(item.getString("CAR_BRAND"),item.getString("CAR_MODEL"),item.getString("CAR_YEAR"));
            ct.setLiscence_plate(item.getString("LISCENCE_PLATE"));
            userCars.add(ct);

        }

        numCars=userCars.size();
        if(numCars==0){
            Toastie.centerInfo(getApplicationContext(),"Please add your car first", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getApplicationContext(),AddCars.class);
            finish();
            startActivity(intent);
        }
        else if(numCars==1){
            selectedPlate=userCars.get(0).getLiscence_plate();//If the use has only one car we can get the number plate from it
            fetchData();
        }
        else if(appInformation.getLiscence_plate().equals("")){

            appInformation.setActivity("selectCarEff");
            Intent intent=new Intent(getApplicationContext(),popupApplication.class);
            startActivity(intent);
        }
        else{
            fetchData();
        }

    }

    public ArrayList<CarType> getUserCars() {
        return userCars;
    }

    private  void configureScreen(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();//hides the name of the activity at the top
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);//hides the navigation bar at the bottom

        btnBack=findViewById(R.id.btnEffBack);

        ConstraintLayout mContentView=findViewById(R.id.stationEffContent);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    private void toggle(){//sets the navigation bar at the bottom visible or not when the user touches the screen
        if(backBtnVisible){

            btnBack.setVisibility(View.INVISIBLE);
            backBtnVisible =false;
        }
        else{
            btnBack.setVisibility(View.VISIBLE);
            backBtnVisible =true;
        }
    }

    public void goBack(View view){

        Intent i=new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(i);
        if(Stations.size()>0) {//Makes sure we don't have an empty barchart
            barChart.clear();
            Stations.clear();
            stationsMap.clear();
            //Clears all the data from the previous bargraph first
            userCars.clear();//Clears the users array first so we don't add to it again

        }
        appInformation.setLiscence_plate("");
        finish();

    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(i);
        if(Stations.size()>0) {//Makes sure we don't have an empty barchart
            barChart.clear();
            Stations.clear();
            stationsMap.clear();
            //Clears all the data from the previous bargraph first
            userCars.clear();//Clears the users array first so we don't add to it again

        }
        appInformation.setLiscence_plate("");
        finish();

    }


    private void fetchData(){
        Connection connection=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        ContentValues cv=new ContentValues();
        cv.put("LISCENCE_PLATE",selectedPlate);

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
            if(jsonArray.length()==0){
                Toastie.centerWarning(getApplicationContext(),"You have no full ups in this car yet",Toast.LENGTH_LONG).show();
                return;
            }

            for (int i=0;i<jsonArray.length();i++) {
                JSONObject item= (JSONObject) jsonArray.get(i);
                name=item.getString("PETROL_STATION_NAME");
                eff=item.getDouble("EFFICIENCY");
                String[] arr =name.split(" ",2);//To only get the first word of the station name as we want to group them by Station Brand

                if(!stationsMap.containsKey(arr[0])){//checks to see if this is the first time we are seeing this station, if so creates a station object
                    stationsMap.put(arr[0],stationNum);
                    stationNum++;//to ensure each station has a unique number assigned to it

                    Station station=new Station(arr[0]);//creates the station with that name
                    Stations.add(station);
                }

                for (Station s:Stations) {// checks to find the station that entry belongs to

                    if(arr[0].equals(s.getName())){
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

    private void createGraph(){
        barChart=findViewById(R.id.stationBarGraph);

        ArrayList<BarEntry>entries=new ArrayList<>();//the data values
        final String[]names=new String[Stations.size()];//the names on the x-axis

        addDefaultData(entries,names);//method to add the default data to the entries of the graph
        BarDataSet set = new BarDataSet(entries, "Station Efficiencies");
        BarData data = new BarData(set);

        ValueFormatter formatter=new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return names[(int) value];//sets the names on the X-Axis to our stations
            }
        };

        formatDefaultBarGraph(set,data,formatter);
    }

    public void addDefaultData(ArrayList<BarEntry>entries,String[]names){
        for(Integer i=0;i<Stations.size();i++){
            Station s=Stations.get(i);

            float x=i.floatValue();//the default position of that bar
            float y=s.getAverage().floatValue();//the y value of that bar
            // x and y must be of type float
            entries.add(new BarEntry(x,y));
            String[] arr =s.getName().split(" ",2);//To only get the first word of the station name

            names[i]=arr[0].toUpperCase();

        }
    }

    private void formatDefaultBarGraph(@NotNull BarDataSet set, BarData data, ValueFormatter formatter){
        //method to do all the layout associated code for the bargraph

        final Thread th = new Thread(new Runnable() {
            //network calls must be done on their own threads
            @Override
            public void run() {

                StationsEfficiencyActivity.this.runOnUiThread(new Runnable() {//To change the background colour we must run a thread on the UI Thread
                    public void run() {

                        barChart.setBackgroundColor(Color.BLACK);

                    }
                });
            }
            });
        th.start();
        ArrayList<Integer>colorArr=new ArrayList<>();
        colorArr.add(Color.GREEN);
        colorArr.add(Color.MAGENTA);
        colorArr.add(Color.BLUE);
        colorArr.add(Color.YELLOW);
        colorArr.add(Color.RED);
        colorArr.add(Color.WHITE);//The colours that will represent the bars, will repeat if run out of colours
        set.setValueTextSize(20f);//the text size for the inner labels
        set.setValueTextColor(Color.CYAN);
        set.setColors(colorArr);

        Legend legend=barChart.getLegend();
        legend.setEnabled(false);//no need for a legend on a bar graph

        barChart.setExtraBottomOffset(10f);//Prevents the bottom of the x-axis labels from being cut off
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(formatter);//sets the values on the x-axis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(Stations.size());//The total number of labels that must appear
        xAxis.setTextSize(20f);
        xAxis.setTextColor(Color.CYAN);
        xAxis.setDrawGridLines(true);

        YAxis yAxisL=barChart.getAxisLeft();
        YAxis yAxisR=barChart.getAxisRight();
        yAxisL.setTextSize(18f);
        yAxisR.setTextSize(18f);
        yAxisL.setTextColor(Color.CYAN);
        yAxisR.setTextColor(Color.CYAN);
        yAxisL.setDrawGridLines(true);
        yAxisR.setDrawGridLines(true);

        float barWidth=0.3f;
        if(Stations.size()<4){
            barWidth=0.6f;
        }
        else if(Stations.size()==4) {
            barWidth=0.5f;
        }
        else if(Stations.size()==5){
             barWidth=0.4f;
        }
        data.setBarWidth(barWidth); // set custom bar width
        barChart.setData(data);

        barChart.getDescription().setText("");//we don't want an inner descriptor as it is clumsy
        barChart.setNoDataText("WOOPS it looks like you have no fill ups yet");//What it displays when no data is found
        barChart.setDrawBorders(true);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.postInvalidate(); // refresh
    }
}
