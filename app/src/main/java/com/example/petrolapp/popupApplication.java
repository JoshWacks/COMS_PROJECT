package com.example.petrolapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;


public class popupApplication extends Activity {
    private TextView txtH1;
    private TextView txtH2;
    private TextView txtH3;

    private TextView txtData1;
    private TextView txtData2;
    private TextView txtData3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;

        txtH1=findViewById(R.id.txtPopHeading1);
        txtH2=findViewById(R.id.txtPopHeading2);
        txtH3=findViewById(R.id.txtPopHeading3);

        txtData1=findViewById(R.id.txtPopData1);
        txtData2=findViewById(R.id.txtPopData2);
        txtData3=findViewById(R.id.txtPopData3);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String activity = bundle.getString("activity");//checks which activity is calling it

        assert activity != null;
        double hDim;
        double wDim;
        if(activity.equals("FillUps")){
            fillUpsPopUp(bundle);
            hDim =0.16;
            wDim =0.9;
            getWindow().setLayout((int) (width* wDim),(int) (height* hDim));
        }else{
            hDim =0.2;
            wDim =0.7;
            getWindow().setLayout((int) (width* wDim),(int) (height* hDim));
            carEffPopUp(bundle);
        }




    }

    public void fillUpsPopUp(Bundle bundle){
        //Method for when the fillups activity is calling the popup window
        TextView txtFound=findViewById(R.id.txtNotFound);

        TextView txtBottomBorder=findViewById(R.id.txtBottomBorder);
        txtBottomBorder.setVisibility(View.VISIBLE);
        txtH1.setText("Station: ");
        txtH2.setText("Efficiency: ");

        boolean f=bundle.getBoolean("found");
        if(!f) {
            txtData1.setVisibility(View.GONE);
            txtData2.setVisibility(View.GONE);
            txtH2.setVisibility(View.GONE);
            txtH1.setVisibility(View.GONE);

            txtFound.setText("We were unable to find any records on that date \n \nPlease re-enter your search query");
            txtFound.setVisibility(View.VISIBLE);
        }
        else{
            txtData1.setVisibility(View.VISIBLE);
            txtData2.setVisibility(View.VISIBLE);
            txtH2.setVisibility(View.VISIBLE);
            txtH1.setVisibility(View.VISIBLE);
            txtFound.setVisibility(View.GONE);

            String name = bundle.getString("name");
            double eff = bundle.getDouble("eff");

            txtData1.setText(name);
            txtData2.setText(eff + "   (Mileage/Litres)");



        }
    }

    public void carEffPopUp(Bundle bundle){

        TextView txtBottomBorder=findViewById(R.id.txtBottomBorder);
        txtBottomBorder.setVisibility(View.GONE);
        txtH1.setText("BRAND:     ");
        txtH2.setText("MODEL:    ");
        txtH3.setText("YEAR:    ");

        String brand=bundle.getString("brand");
        String model=bundle.getString("model");
        String year=bundle.getString("year");

        txtData1.setText(brand);
        txtData2.setText(model);
        txtData3.setText(year);


    }
}
