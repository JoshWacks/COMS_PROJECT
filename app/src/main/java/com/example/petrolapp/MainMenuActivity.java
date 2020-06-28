package com.example.petrolapp;

//TODO Videos code ,app,DBF and put videos together
//TODO Project document
//TODO compiled app

//TODO Business rules
//TODO Problems with Database
//TODO Implementation

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */


//Todo make sure it does not crash when not connected to the internet
public class MainMenuActivity extends AppCompatActivity {
    private TextView pTextView;
    private TextView dTextView;
    private String petrolPrice = "";
    private String dieselPrice = "";
    private Intent i ;
    private Intent gpsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        gpsIntent = new Intent(getApplicationContext(), GPS_Service.class);
        startService(gpsIntent);//Starts the GPS service here

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        pTextView = findViewById(R.id.txtViewPPrice);
        dTextView = findViewById(R.id.txtViewDPrice);

        final Thread thD = new Thread(new Runnable() {
            @Override
            public void run() {
                fetchDieselPrice();
            }
        });

        final Thread thP = new Thread(new Runnable() {
            @Override
            public void run() {
                fetchPetrolPrice();
            }
        });
        thD.start();
        thP.start();



    }

    public void atStation(View view) {
        i = new Intent(getApplicationContext(), AtStationActivity.class);
        startActivity(i);
    }

    public void logOut(View view) {
        stopService(gpsIntent);//Ends the GPS service when they logout before the app closes

        System.exit(0);
        //check if i can go back to login screen
//        i = new Intent(getApplicationContext(),Login.class);
//        startActivity(i);



    }

    public void viewFillups(View view) {
        i = new Intent(getApplicationContext(), ViewFillUpsActivity.class);
        startActivity(i);
    }

    public void viewStationEfficiency(View view) {
        i = new Intent(getApplicationContext(), StationsEfficiencyActivity.class);

        startActivity(i);
    }

    public void viewCarEfficiency(View view) {
        i = new Intent(getApplicationContext(), CarEfficiencyActivity.class);
        startActivity(i);
    }

    public void goToCars(View view){
        i = new Intent(getApplicationContext(), CarDetails.class);
        startActivity(i);
    }

    public void goToCards(View view){
        i = new Intent(getApplicationContext(), CardDetails.class);
        startActivity(i);
    }

    public void goToAccount(View view){
        i = new Intent(getApplicationContext(), AccDetails.class);
        startActivity(i);
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
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        petrolPrice = fuelPrices.get(fuelPrices.size() - 1);//we only want the most recent price
        appInformation.setPetrolPrice(petrolPrice);
        pTextView.setText("Current Petrol Price: " + petrolPrice);

    }

    public void fetchDieselPrice() {
        final String url = "https://www.globalpetrolprices.com/South-Africa/diesel_prices/";//the website we are getting the current diesel price from

        try {
            final Document document = Jsoup.connect(url).get();//returns a type document

            Element row = document.selectFirst("tbody tr");//the name of the element we need is tbody and we only want it to return the first(ZAR) row from that tables
            dieselPrice = row.text().substring(4, 9);//we use substring as we don't want all the text, only the price

            dTextView.setText("Current Diesel Price: R " + dieselPrice);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


