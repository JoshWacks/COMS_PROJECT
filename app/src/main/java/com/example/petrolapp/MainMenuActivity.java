package com.example.petrolapp;
//To-do
//Get Current Diesal Price
//Re-do Main menu layout
//consider 2 screens and simple layout with paint backround


import android.content.Intent;

import android.widget.TextView;

import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
//TODO get username from login and send it to all other activities via intent
//TODO optimise fetchPetrol and fetchDiesel code so we don't wait long for main menu
    //Todo make sure it does not crash when not connected to the internet
public class MainMenuActivity extends AppCompatActivity {
    private String petrolPrice ="";
    private String dieselPrice="";
    private String username="JoshW";
    TextView pTextView ;

    TextView dTextView;


    public void atStation(View view){
        Intent i=new Intent(getApplicationContext(),AtStationActivity.class);

        i.putExtra("price", petrolPrice);
        i.putExtra("username",username);
        startActivity(i);
    }
    public void logOut(View view){
        Intent i=new Intent(getApplicationContext(),GPS_Service.class);
        stopService(i);//Ends the GPS service when the logout before the app closes
        System.exit(0);
    }
    public void viewFillups(View view){
        Intent i=new Intent(getApplicationContext(),ViewFillUpsActivity.class);
        i.putExtra("username",username);
        startActivity(i);
    }
    public void viewStationEfficiency(View view){
        Intent i=new Intent(getApplicationContext(),StationsEfficiencyActivity.class);
        i.putExtra("username",username);
        startActivity(i);
    }
    public void viewCarEfficiency(View view){
        Intent i=new Intent(getApplicationContext(),CarEfficiencyActivity.class);
        i.putExtra("username",username);
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
        pTextView = findViewById(R.id.txtViewPPrice);
        dTextView= findViewById(R.id.txtViewDPrice);

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

        //Network calls must be done on their own threads




//        final Thread thread = new Thread(new Runnable() {
//            //network calls must be done on their own threads
//            @Override
//            public void run() {
//                try  {
//                    MainMenuActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//
//                            pTextView.setText("Current Petrol Price: "+ petrolPrice);
//                            dTextView.setText("Current Diesel Price: R "+ dieselPrice);
//
//                        }
//                    });
//
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        try {
//
//            thP.join(4000);
//            thD.join(4000);//Waits max 3s to get the price first before displaying
//            if(petrolPrice.equals("")&&dieselPrice.equals(""))
//            {
//                Toast toast = Toast.makeText(getApplicationContext(), "You don't seem to be connected to the internet", Toast.LENGTH_LONG);//If takes too long to access website
//                toast.show();
//            }
//
//            thread.start();
//
//        }
//
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }


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
        }

        catch (IOException ex) {

            ex.printStackTrace();
        }

        petrolPrice= fuelPrices.get(fuelPrices.size() - 1);//we only want the most recent price

        pTextView.setText("Current Petrol Price: "+ petrolPrice);

    }

    public void fetchDieselPrice(){
        final String url = "https://www.globalpetrolprices.com/South-Africa/diesel_prices/";//the website we are getting the current diesel price from

        try {
            final Document document = Jsoup.connect(url).get();//returns a type document

            Element row =document.selectFirst("tbody tr");//the name of the element we need is tbody and we only want it to return the first(ZAR) row from that tables
            dieselPrice=row.text().substring(4,9);//we use substring as we don't want all the text, only the price

            dTextView.setText("Current Diesel Price: R "+ dieselPrice);


        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    }


