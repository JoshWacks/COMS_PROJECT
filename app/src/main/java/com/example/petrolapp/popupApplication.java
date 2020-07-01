package com.example.petrolapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import com.mrntlu.toastie.Toastie;

import java.util.ArrayList;


public class popupApplication extends Activity {
    //Class to handle all the popups in the app
    private TextView txtH1;
    private TextView txtH2;
    private TextView txtH3;
    private TextView txtH4;

    private TextView txtData1;
    private TextView txtData2;
    private TextView txtData3;

    private ScrollView scrollView;
    private LinearLayout linearLayout;

    private String activity;

    private TextView txtStationName ;

    private Button btnPopDone;

    Double x_co;
    Double y_co;

    private static StationsEfficiencyActivity stationsEfficiencyActivity;
    private static ViewFillUpsActivity viewFillUpsActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        configure();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        activity = appInformation.getActivity();//checks which activity is calling it

        double hDim;
        double wDim;
        switch (activity) {
            case "FillUps":
                fillUpsPopUp(bundle);
                hDim = 0.16;
                wDim = 0.92;
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));

                break;
            case "AtStation":
                hDim = 0.18;
                wDim = 1;
                atStationPopup(bundle);
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));

                break;
            case "CarEff":
                hDim = 0.2;
                wDim = 0.7;
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
                carEffPopUp(bundle);

                break;
            case "selectCarEff":
                stationsEfficiencyActivity=new StationsEfficiencyActivity();
                hDim = 0.25;
                wDim = 1;
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
                selectCarPopup();

                break;
            case "selectCarFillUps":
                viewFillUpsActivity=new ViewFillUpsActivity();
                hDim = 0.25;
                wDim = 1;
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
                selectCarPopup();

                break;
        }


    }

    private void configure(){


        txtH1 = findViewById(R.id.txtPopHeading1);
        txtH2 = findViewById(R.id.txtPopHeading2);
        txtH3 = findViewById(R.id.txtPopHeading3);
        txtH4 = findViewById(R.id.txtPopHeading4);

        txtData1 = findViewById(R.id.txtPopData1);
        txtData2 = findViewById(R.id.txtPopData2);
        txtData3 = findViewById(R.id.txtPopData3);

        linearLayout=findViewById(R.id.scrollLinearLayout);
        scrollView=findViewById(R.id.scrollCarChoice);

        txtStationName = findViewById(R.id.etxtName);

        btnPopDone=findViewById(R.id.btnPopDone);
    }

    public void fillUpsPopUp(Bundle bundle) {
        //Method for when the fillups activity is calling the popup window
        txtData3.setVisibility(View.GONE);
        btnPopDone.setVisibility(View.GONE);
        txtH4.setVisibility(View.GONE);
        txtH3.setVisibility(View.GONE);
        txtStationName.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);

        txtH1.setText("Station: ");
        txtH2.setText("Efficiency: ");


        txtData1.setVisibility(View.VISIBLE);
        txtData2.setVisibility(View.VISIBLE);
        txtH2.setVisibility(View.VISIBLE);
        txtH1.setVisibility(View.VISIBLE);

        String name = bundle.getString("name");
        double eff = bundle.getDouble("eff");

        txtData1.setText(name);

        txtData2.setText(eff + "   (Mileage/Litres)");
        if(eff==-1){
            Toastie.topInfo(getApplicationContext(),"The Efficiency is -1 as this trip is not yet completed",Toast.LENGTH_SHORT).show();
        }






    }

    public void atStationPopup(Bundle bundle) {

        x_co = bundle.getDouble("x_co");
        y_co = bundle.getDouble("y_co");

        txtData1.setVisibility(View.GONE);
        txtData2.setVisibility(View.GONE);
        txtData3.setVisibility(View.GONE);

        txtH1.setVisibility(View.GONE);
        txtH2.setVisibility(View.GONE);
        txtH3.setVisibility(View.GONE);

        linearLayout.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);


        btnPopDone.setVisibility(View.VISIBLE);
        txtStationName.setVisibility(View.VISIBLE);
        txtH4.setVisibility(View.VISIBLE);

    }

    private void selectCarPopup(){
        txtH1.setVisibility(View.GONE);
        txtH2.setVisibility(View.GONE);
        txtH3.setVisibility(View.GONE);
        txtData1.setVisibility(View.GONE);
        txtData2.setVisibility(View.GONE);
        txtData3.setVisibility(View.GONE);
        txtStationName.setVisibility(View.GONE);
        btnPopDone.setVisibility(View.GONE);


        txtH4.setText("Please select a car below first");
        txtH4.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        ArrayList<CarType>userCars;

        if(activity.equals("selectCarEff")){
            userCars=stationsEfficiencyActivity.getUserCars();
        }
        else{
            userCars=viewFillUpsActivity.getUserCars();
        }



        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        for(int i=0;i<userCars.size();i++){
            CarType ct=userCars.get(i);
            TextView textView=new TextView(this);
            textView.setLayoutParams(layoutParams);
            textView.setTextAppearance(R.style.fontForTextViews2);
            textView.setText(ct.getBrand()+"\t  "+ct.getModel()+" \t\t:"+ct.getLiscence_plate());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCarSelected(v);
                }
            });
            TextView blankLine=new TextView(this);
            linearLayout.addView(textView);
            linearLayout.addView(blankLine);
        }

    }

    public void onCarSelected(View view){

        TextView selectedTxtView= (TextView) view;
        selectedTxtView.setBackgroundColor(Color.CYAN);
        String txt= (String) selectedTxtView.getText();
        int pos=txt.indexOf(":");
        appInformation.setLiscence_plate(txt.substring(pos+1));
        finish();
        Intent intent;
        if(activity.equals("selectCarEff")){
            intent=new Intent(getApplicationContext(),StationsEfficiencyActivity.class);
        }
        else{
            intent=new Intent(getApplicationContext(),ViewFillUpsActivity.class);
        }

        startActivity(intent);
    }


    public void carEffPopUp(Bundle bundle) {
        btnPopDone.setVisibility(View.GONE);
        txtStationName.setVisibility(View.GONE);
        txtH4.setVisibility(View.GONE);

        linearLayout.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        txtH1.setText("BRAND:     ");
        txtH2.setText("MODEL:    ");
        txtH3.setText("YEAR:    ");

        String brand = bundle.getString("brand");
        String model = bundle.getString("model");
        String year = bundle.getString("year");

        txtData1.setText(brand);
        txtData2.setText(model);
        txtData3.setText(year);


    }

    public void btnDone(View view) {


        String stationName="";
        stationName =txtStationName.getEditableText().toString().trim();
        if (stationName.equals("")) {
            Toastie.error(getApplicationContext(),"Please enter a station name first",Toast.LENGTH_SHORT).show();
        }
        else {

            Connection connection = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
            ContentValues cv = new ContentValues();
            double add1=x_co*10000 % 1000;
            double add2=y_co*10000 % 1000;
            int a1= (int)add1;
            int a2= (int)add2;
            stationName=stationName+"_"+a1+"_"+a2;//We ensure that station name is unique by using the x an y co-ords
            cv.put("STATION", stationName);
            cv.put("X_CO", x_co);
            cv.put("Y_CO", y_co);


            String finalStationName = stationName;
            connection.fetchInfo(popupApplication.this, "insert_STATION", cv, new RequestHandler() {
                @Override
                public void processResponse(String response) {
                    Toastie.success(getApplicationContext(),"Successfully Added Station \nPress anywhere else to close this",Toast.LENGTH_LONG).show();

                    appInformation.setNewStationName(finalStationName);
                    AtStationActivity.txtStation.append(appInformation.getNewStationName());
                    AtStationActivity.stationAt=appInformation.getNewStationName();


                }
            });
        }
    }
}
