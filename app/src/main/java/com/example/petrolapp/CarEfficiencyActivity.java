package com.example.petrolapp;

import android.accessibilityservice.GestureDescription;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CarEfficiencyActivity extends AppCompatActivity {
    String username;
    BarChart barChart;

    Button btnBack;
    private boolean backBtnVisible =true;

    private HashMap<String, Integer> CarTypeMap=new HashMap<String, Integer>() ;//A Map is used to see if we have encountered that car type before,
    //We use a HashMap to avoid implementing all the map methods
    private ArrayList<CarType> CarTypes=new ArrayList<CarType>();//An arraylist to keep track of all our car types

    Thread thread;

    //Todo find their specific car and show it specifically for them
    //Todo to get more info on the car select on a bar
    //TODO give them options for which cars they would like to see

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_efficiency);

        Intent intent=getIntent();
        username=intent.getStringExtra("username");

        configureScreen();
        fetchData();

        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                createGraph();//We can only create the graph confidently once we know the data has been fetched
                barInfo();
            }
        });


    }

    public void configureScreen(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();//hides the name of the activity at the top
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);//hides the navigation bar at the bottom

        btnBack=findViewById(R.id.btnCarEffBack);

        ConstraintLayout mContentView=findViewById(R.id.content);
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
    }

    private void fetchData(){//directly fetches the raw data to be processed
        Connection connection=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        ContentValues cv=new ContentValues();
       // cv.put("USERNAME",username);

        connection.fetchInfo(CarEfficiencyActivity.this, "get_CARS_EFFICIENCY",cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                processJson(response);
            }
        });
    }

    private void processJson(String data){
        String brand;
        String model;
        String year;
        String type;
        Double eff;

        int CarTypeNum=0;//each car type will be assigned a unique integer

        try {
            JSONArray jsonArray=new JSONArray(data);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item= (JSONObject) jsonArray.get(i);
                brand=item.getString("CAR_BRAND");
                model=item.getString("CAR_MODEL");
                year=item.getString("CAR_YEAR");
                eff=item.getDouble("EFFICIENCY");

                type=brand+model+year;//Type is a combination of brand,model and year

                //System.out.println(brand+"  "+model+"  "+year+" "+eff);

                if(!CarTypeMap.containsKey(type)){//Checks to see if we have not come across that car type before
                    CarTypeMap.put(type,CarTypeNum);
                    CarTypeNum++;

                    CarType carType=new CarType(brand,model,year);
                    CarTypes.add(carType);
                }

                for(CarType c:CarTypes){//finds the correct CarType for which that entry belongs
                    if(type.equals(c.getType())){
                        c.addEntry(eff);
                        break;
                    }
                }



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thread.start();


    }

    private void createGraph(){

        barChart=findViewById(R.id.CarBarGraph);


        ArrayList<BarEntry>entries=new ArrayList<>();//the data values
        final String[]names=new String[CarTypes.size()];//the names on the x-axis

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
        for(Integer i=0;i<CarTypes.size();i++){
            CarType ct=CarTypes.get(i);

            float x=i.floatValue();//the default position of that bar
            float y=ct.getAverage().floatValue();//the y value of that bar
            // x and y must be of type float
            entries.add(new BarEntry(x,y));

            names[i]=ct.getModel();

        }
    }

    public void formatDefaultBarGraph(BarDataSet set,BarData data,ValueFormatter formatter){
        //method to do all the layout associated code for the bargraph

        final Thread th = new Thread(new Runnable() {
            //network calls must be done on their own threads
            @Override
            public void run() {
//TODO check if the background should actually be black and if I should draw grid lines

                CarEfficiencyActivity.this.runOnUiThread(new Runnable() {//To change the background colour we must run a thread on the UI Thread
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
        set.setValueTextSize(18f);//the text size for the inner labels
        set.setValueTextColor(Color.CYAN);
        set.setColors(colorArr);

        Legend legend=barChart.getLegend();
        legend.setEnabled(false);//no need for a legend on a bar graph

        barChart.setExtraBottomOffset(10f);//Prevents the bottom of the x-axis labels from being cut off
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(formatter);//sets the values on the x-axis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(CarTypes.size());//The total number of labels that must appear
        xAxis.setTextSize(16f);
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
//TODO check if this barwidth will always be alright
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

    public void barInfo(){
        //Method to show extra info on the type of car when that bar is selected

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener(){

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                System.out.println(e);
                System.out.println(h);
            }

            @Override
            public void onNothingSelected() {

            }
        } );
    }
}
