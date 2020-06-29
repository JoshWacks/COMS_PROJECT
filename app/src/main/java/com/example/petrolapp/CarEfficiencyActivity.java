package com.example.petrolapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.mrntlu.toastie.Toastie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CarEfficiencyActivity extends AppCompatActivity {

    private static final ArrayList<CarType> CarTypes = new ArrayList<CarType>();//An arraylist to keep track of all our car types
    private final HashMap<String, Integer> CarTypeMap = new HashMap<String, Integer>();//A Map is used to see if we have encountered that car type before,
    //We use a HashMap to avoid implementing all the map methods
    private static BarChart barChart;
    private static ArrayList<BarEntry> entries;
    private static BarDataSet set;
    private static BarData data;
    private static String[] names;

    private Button btnBack;
    private boolean backBtnVisible = true;

    private Spinner spnrBrand;
    private Spinner spnrModel;
    private Spinner spnrYear;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_efficiency);

        fetchData();
        configureScreen();


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                createGraph();//We can only create the graph confidently once we know the data has been fetched
                barInfo();

            }
        });


    }

    public void goBack(View view) {
        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(i);
        barChart.clear();
        CarTypes.clear();
        CarTypeMap.clear();
        entries.clear();
        data.clearValues();
        set.clear();
        names=new String[names.length];//Clears all the data from the previous bargraph first
        finish();


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(i);
        barChart.clear();
        CarTypes.clear();
        CarTypeMap.clear();
        entries.clear();
        data.clearValues();
        set.clear();
        names=new String[names.length];//Clears all the data from the previous bargraph first
        finish();
    }

    private void configureScreen() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();//hides the name of the activity at the top
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);//hides the navigation bar at the bottom

        Toastie.topSuccess(getApplicationContext(),"Click on a bar for more info",Toast.LENGTH_LONG).show();

        btnBack = findViewById(R.id.btnCarEffBack);

        ConstraintLayout mContentView = findViewById(R.id.FillUpsContent);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    private void toggle() {//sets the navigation bar at the bottom visible or not when the user touches the screen
        if (backBtnVisible) {

            btnBack.setVisibility(View.INVISIBLE);
            backBtnVisible = false;
        } else {
            btnBack.setVisibility(View.VISIBLE);
            backBtnVisible = true;
        }
    }


    private void fetchData() {//directly fetches the raw data to be processed
        Connection connection = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        ContentValues cv = new ContentValues();


        connection.fetchInfo(CarEfficiencyActivity.this, "get_CARS_EFFICIENCY", cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                processJson(response);
            }
        });
    }

    private void processJson(String data) {
        String brand;
        String model;
        String year;
        String type;
        Double eff;

        int CarTypeNum = 0;//each car type will be assigned a unique integer

        try {
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = (JSONObject) jsonArray.get(i);
                brand = item.getString("CAR_BRAND");
                model = item.getString("CAR_MODEL");
                year = item.getString("CAR_YEAR");
                eff = item.getDouble("EFFICIENCY");

                type = brand + model + year;//Type is a combination of brand,model and year

                //System.out.println(brand+"  "+model+"  "+year+" "+eff);

                if (!CarTypeMap.containsKey(type)) {//Checks to see if we have not come across that car type before
                    CarTypeMap.put(type, CarTypeNum);
                    CarTypeNum++;

                    CarType carType = new CarType(brand, model, year);
                    CarTypes.add(carType);
                }

                for (CarType c : CarTypes) {//finds the correct CarType for which that entry belongs
                    if (type.equals(c.getType())) {
                        c.addEntry(eff);
                        break;
                    }
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thread.start();
        fillDropDowns();//Only once all the data has been collected should we create the dropdowns

    }

    private void createGraph() {//Boolean to check if w we are using it for the default data or not

        barChart = findViewById(R.id.CarBarGraph);
        entries = new ArrayList<>();//the data values
        names = new String[CarTypes.size()];//the names on the x-axis

        addDefaultData(entries, names);//method to add the default data to the entries of the graph


        set = new BarDataSet(entries, "Station Efficiencies");
        data = new BarData(set);


        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return names[(int) value];//sets the names on the X-Axis to our stations
            }
        };
        formatDefaultBarGraph(set, data, formatter);


    }

    private void addDefaultData(ArrayList<BarEntry> entries, String[] names) {
        for (Integer i = 0; i < CarTypes.size(); i++) {
            CarType ct = CarTypes.get(i);

            float x = i.floatValue();//the default position of that bar
            float y = ct.getAverage().floatValue();//the y value of that bar
            // x and y must be of type float
            entries.add(new BarEntry(x, y));

            names[i] = ct.getModel();

        }
    }

    private void formatDefaultBarGraph(BarDataSet set, BarData data, ValueFormatter formatter) {
        //method to do all the layout associated code for the bargraph

        final Thread th = new Thread(new Runnable() {
            //network calls must be done on their own threads
            @Override
            public void run() {


                CarEfficiencyActivity.this.runOnUiThread(new Runnable() {//To change the background colour we must run a thread on the UI Thread
                    public void run() {

                        barChart.setBackgroundColor(Color.BLACK);

                    }
                });
            }
        });
        th.start();
        ArrayList<Integer> colorArr = new ArrayList<>();
        colorArr.add(Color.GREEN);
        colorArr.add(Color.MAGENTA);
        colorArr.add(Color.BLUE);
        colorArr.add(Color.YELLOW);
        colorArr.add(Color.RED);
        colorArr.add(Color.WHITE);//The colours that will represent the bars, will repeat if run out of colours
        set.setValueTextSize(18f);//the text size for the inner labels
        set.setValueTextColor(Color.CYAN);
        set.setColors(colorArr);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);//no need for a legend on a bar graph

        barChart.setExtraBottomOffset(10f);//Prevents the bottom of the x-axis labels from being cut off
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(formatter);//sets the values on the x-axis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(CarTypes.size());//The total number of labels that must appear
        xAxis.setTextSize(16f);
        xAxis.setTextColor(Color.CYAN);
        xAxis.setDrawGridLines(true);

        YAxis yAxisL = barChart.getAxisLeft();
        YAxis yAxisR = barChart.getAxisRight();
        yAxisL.setTextSize(18f);
        yAxisR.setTextSize(18f);
        yAxisL.setTextColor(Color.CYAN);
        yAxisR.setTextColor(Color.CYAN);
        yAxisL.setDrawGridLines(true);
        yAxisR.setDrawGridLines(true);

        float barWidth = 0.3f;
        data.setBarWidth(barWidth); // set custom bar width
        barChart.setData(data);

        barChart.getDescription().setText("");//we don't want an inner descriptor as it is clumsy
        barChart.setNoDataText("WOOPS it looks like you have no fill ups yet");//What it displays when no data is found
        barChart.setDrawBorders(true);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

        barChart.postInvalidate();


    }

    private void barInfo() {
        //Method to show extra info on the type of car when that bar is selected

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos = (int) e.getX();
                CarType ct = CarTypes.get(pos);
                String brand = ct.getBrand();
                String model = ct.getModel();
                String year = ct.getYear();
                appInformation.setActivity("CarEff");

                Bundle extra = new Bundle();
                extra.putString("brand", brand);
                extra.putString("model", model);
                extra.putString("year", year);
                Intent i = new Intent(getApplicationContext(), popupApplication.class);
                i.putExtras(extra);
                startActivity(i);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void fillDropDowns() {
        spnrBrand = findViewById(R.id.spnrBrand);
        spnrModel = findViewById(R.id.spnrModel);
        spnrYear = findViewById(R.id.spnrYear);

        ArrayList<String> arrayListBrand = new ArrayList<>();
        arrayListBrand.add("Brand");
        ArrayList<String> arrayListModel = new ArrayList<>();
        arrayListModel.add("Model");
        ArrayList<String> arrayListYear = new ArrayList<>();
        arrayListYear.add("Year");

        for (CarType ct : CarTypes) {
            if(!arrayListBrand.contains(ct.getBrand())){
                arrayListBrand.add(ct.getBrand());
            }
            if(!arrayListModel.contains(ct.getModel())){
                arrayListModel.add(ct.getModel());
            }
            if(!arrayListYear.contains(ct.getYear())){
                arrayListYear.add(ct.getYear());
            }

        }


        ArrayAdapter<String> arrayAdapterBrand = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListBrand);
        ArrayAdapter<String> arrayAdapterModel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListModel);
        ArrayAdapter<String> arrayAdapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListYear);

        spnrBrand.setAdapter(arrayAdapterBrand);
        spnrModel.setAdapter(arrayAdapterModel);
        spnrYear.setAdapter(arrayAdapterYear);

        spnrBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                onBrandSelected(arrayListModel);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spnrModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                onModelSelected(arrayListYear);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


    }

    private void onBrandSelected(ArrayList<String>arr) {
        String brand = (String) spnrBrand.getSelectedItem();
        ArrayList<String> arrayListModel = new ArrayList<>();
        if (brand.equals("Brand")) {
            arrayListModel=arr;
        }
       else {
            for (CarType ct : CarTypes) {
                if (ct.getBrand().equals(brand)) {
                    arrayListModel.add(ct.getModel());
                }
            }

        }
        ArrayAdapter<String> arrayAdapterModel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListModel);
        spnrModel.setAdapter(arrayAdapterModel);

    }

    private void onModelSelected(ArrayList<String>arr){
        String model=(String)spnrModel.getSelectedItem();
        ArrayList<String> arrayListYear = new ArrayList<>();

        if(model.equals("Model")){
            arrayListYear=arr;
        }
        else {
            for (CarType ct : CarTypes) {
                if (ct.getModel().equals(model)) {
                    arrayListYear.add(ct.getYear());
                }
            }
        }
        ArrayAdapter<String> arrayAdapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListYear);
        spnrYear.setAdapter(arrayAdapterYear);
    }


    private boolean checkValidChoice() {
        String brandSelected = (String) spnrBrand.getSelectedItem();

        if (brandSelected.equals("Brand")) {

            Toastie.centerError(this,"Please select a brand first", Toast.LENGTH_LONG).show();
            return false;
        }

        String modelSelected = (String) spnrModel.getSelectedItem();
        if (modelSelected.equals("Model")) {
            Toastie.centerError(this,"Please select a model first", Toast.LENGTH_LONG).show();
            return false;
        }

        String yearSelected = (String) spnrYear.getSelectedItem();
        if (yearSelected.equals("Year")) {
            Toastie.centerError(this,"Please select a year first", Toast.LENGTH_LONG).show();
            return false;
        }

        String typeSelected = brandSelected + modelSelected + yearSelected;
        if (!CarTypeMap.containsKey(typeSelected)) {
            Toastie.centerError(this,"Please select a valid car first", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }


    public void carEffSearch(View view) {
        boolean validChoice = checkValidChoice();

        if (validChoice) {
            String brandSelected = (String) spnrBrand.getSelectedItem();
            String modelSelected = (String) spnrModel.getSelectedItem();
            String yearSelected = (String) spnrYear.getSelectedItem();

            String typeSelected = brandSelected + modelSelected + yearSelected;

            int pos = 0;
            for (CarType ct : CarTypes) {
                if (ct.getType().equals(typeSelected)) {
                    pos = CarTypes.indexOf(ct);

                    break;
                }
            }
            barChart.highlightValue(pos, 0, -1);

            Bundle extra = new Bundle();
            appInformation.setActivity("CarEff");
            extra.putString("brand", brandSelected);
            extra.putString("model", modelSelected);
            extra.putString("year", yearSelected);
            Intent i = new Intent(getApplicationContext(), popupApplication.class);//We show that popup Activity
            i.putExtras(extra);
            startActivity(i);


        }

    }
}
