package com.example.petrolapp;
//To-do
//Get Current Diesal Price
//Re-do Main menu layout
//consider 2 screens and simple layout with paint backround


import android.content.Intent;

import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity {
    private String petrolPrice ="";
    private String dieselPrice="";

    public void atStation(View view){
        Intent i=new Intent(getApplicationContext(),AtStationActivity.class);

        i.putExtra("price", petrolPrice);
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
        fetchDieselPrice();

        View decorView=getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        setContentView(R.layout.activity_main_menu);



        final Thread thread = new Thread(new Runnable() {
            //network calls must be done on their own threads
            @Override
            public void run() {
                try  {
                    fetchPetrolPrice();
                    fetchDieselPrice();//These must be run at the same time as the thread
                    MainMenuActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TextView pTextView = findViewById(R.id.txtViewPPrice);
                            pTextView.setText("Current Petrol Price: "+ petrolPrice);
                            TextView dTextView= findViewById(R.id.txtViewDPrice);
                            dTextView.setText("Current Petrol Price: R "+ dieselPrice);

                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        final Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                fetchDieselPrice();
            }
        });
        th.start();



    }



    public void fetchPetrolPrice() {
        final String url = "https://www.aa.co.za/calculators-toolscol-1/fuel-pricing";
        ArrayList<String> fuelPrices = new ArrayList<String>();//have to use an arraylist as there is a lot of prices on this page

        try {
            final Document document = Jsoup.connect(url).get();

            for (Element row : document.select("table.active tr")) {//the name of the table with the prices
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
        petrolPrice= fuelPrices.get(fuelPrices.size() - 1);//we only want the most recent price

    }

    public void fetchDieselPrice(){
        final String url = "https://www.globalpetrolprices.com/South-Africa/diesel_prices/";//the website we are getting the current diesel price from

        try {
            final Document document = Jsoup.connect(url).get();//returns a type document

            Element row =document.selectFirst("tbody tr");//the name of the element we need is tbody and we only want it to return the first(ZAR) row from that tables
            dieselPrice=row.text().substring(4,9);//we use substring as we don't want all the text, only the price


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    }
    //TODO get the current Diesel Price

