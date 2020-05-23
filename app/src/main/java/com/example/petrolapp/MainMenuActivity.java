package com.example.petrolapp;

import android.annotation.SuppressLint;
import android.content.Context;

import android.os.Looper;
import android.widget.TextView;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        final String[] price = new String[1];


        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    price[0] =fetchPetrolPrice();
                    MainMenuActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TextView myTextView = findViewById(R.id.txtViewPrice);
                            myTextView.setText("Current Petrol Price: "+ price[0]);
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

