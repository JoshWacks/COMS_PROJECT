package com.example.petrolapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

        String name=bundle.getString("name");
        double eff=bundle.getDouble("eff");

        TextView txtStation=findViewById(R.id.txtStation);
        txtStation.setText(name);

        TextView txtEff=findViewById(R.id.txtEfficiency);
        txtEff.setText(eff+"");




    }
}
