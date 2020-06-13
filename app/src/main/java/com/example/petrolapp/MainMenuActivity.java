package com.example.petrolapp;
//To-do
//Get Current Diesal Price
//Re-do Main menu layout
//consider 2 screens and simple layout with paint backround


import android.content.Intent;

import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity {
    private String price="";

    public void atStation(View view){
        Intent i=new Intent(getApplicationContext(),AtStationActivity.class);

        i.putExtra("price",price);
        startActivity(i);
    }
    public void logOut(View view){
        Intent i=new Intent(getApplicationContext(),GPS_Service.class);
        stopService(i);//Ends the GPS service when the logout before the app closes
        System.exit(0);
    }
    public void viewFillups(View view){
        Intent i=new Intent(getApplicationContext(),ViewFillUpsActivity.class);
        startActivity(i);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i=new Intent(getApplicationContext(),GPS_Service.class);
        startService(i);

        super.onCreate(savedInstanceState);

        View decorView=getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        setContentView(R.layout.activity_main_menu);



        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    price =fetchPetrolPrice();
                    MainMenuActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TextView myTextView = findViewById(R.id.txtViewPPrice);
                            myTextView.setText("Current Petrol Price: "+ price);
                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }



    public String fetchPetrolPrice() {
        final String url = "https://www.aa.co.za/calculators-toolscol-1/fuel-pricing";
        ArrayList<String> fuelPrices = new ArrayList<String>();

        try {
            final Document document = Jsoup.connect(url).get();
            //System.out.println(document.outerHtml());
            for (Element row : document.select("table.active tr")) {
                if (row.select(" td:nth-of-type(1) ").text().equals("")) {
                    continue;
                } else {
                    final String price = row.select(" td:nth-of-type(1) ").text();
                    fuelPrices.add(price);



                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      return fuelPrices.get(fuelPrices.size() - 1);

    }

    }
    //TODO get the current Diesel Price

