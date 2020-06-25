package com.example.petrolapp;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class SpecifyCarActivity extends AppCompatActivity {
    private ArrayList<CarType> CarTypes=new ArrayList<CarType>();//An arraylist to keep track of all our car types
    ArrayList<BarEntry>entries;
    BarDataSet set;
    BarData data;
    String[]names;

    Button btnView1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_car);

        configureScreen();
        btnView1=findViewById(R.id.btnView1);
        getAllData();

        configureData();

    }


    private void configureScreen(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();//hides the name of the activity at the top

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);//hides the navigation bar at the bottom


    }

    private void getAllData(){
        CarEfficiencyActivity cea=new CarEfficiencyActivity();
        CarTypes=cea.getCarTypes();
        entries=cea.getEntries();
        set=cea.getSet();
        data=cea.getData();
        names=cea.getNames();//We use getters to get past having to use parsable
    }

    private void configureData(){
        Spinner spnrBrand=findViewById(R.id.spnrBrand);

        ArrayList<String>brandArr=new ArrayList<>();

        for(CarType ct:CarTypes){

            brandArr.add(ct.getBrand());
        }
        ArrayAdapter<String> brandArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,brandArr);
        brandArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrBrand.setAdapter(brandArrayAdapter);


    }


    private void viewByBrand(View view){//The method for btnView1

    }


}
