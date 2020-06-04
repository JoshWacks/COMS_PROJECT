package com.example.petrolapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class popupApplication extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int) (width*(0.9)),(int) (height*0.13));

        Bundle bundle = getIntent().getExtras();
        TextView txtStation = findViewById(R.id.txtStation);
        TextView txtEff = findViewById(R.id.txtEfficiency);
        TextView txtH1=findViewById(R.id.txtHeadingStation);
        TextView txtH2=findViewById(R.id.txtHeadingEfficiency);
        TextView txtFound=findViewById(R.id.txtNotFound);
        assert bundle != null;
        boolean f=bundle.getBoolean("found");
        if(!f) {
            txtStation.setVisibility(View.GONE);
            txtEff.setVisibility(View.GONE);
            txtH2.setVisibility(View.GONE);
            txtH1.setVisibility(View.GONE);

            txtFound.setText("We were unable to find any records on that date \n \nPlease re-enter your search query");
            txtFound.setVisibility(View.VISIBLE);
        }
        else{
            txtStation.setVisibility(View.VISIBLE);
            txtEff.setVisibility(View.VISIBLE);
            txtH2.setVisibility(View.VISIBLE);
            txtH1.setVisibility(View.VISIBLE);
            txtFound.setVisibility(View.GONE);

            String name = bundle.getString("name");
            double eff = bundle.getDouble("eff");

            txtStation.setText(name);
            txtEff.setText(eff + "");



        }


    }
}
