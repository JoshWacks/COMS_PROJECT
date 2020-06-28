package com.example.petrolapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarDetails extends AppCompatActivity {
    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        username=appInformation.getUsername();


        ContentValues cv=new ContentValues();
        cv.put("USERNAME",username);
        Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        c.fetchInfo(CarDetails.this, "get_USER_CARS",cv, new RequestHandler() {
            @Override
            public void processResponse(String response)  {
                try {
                    processJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void processJSON(String json) throws JSONException {
        JSONArray all = new JSONArray(json);

        ScrollView scroll=(ScrollView) findViewById(R.id.Scroll) ;

        LinearLayout l=new LinearLayout(CarDetails.this);
        l.setOrientation(LinearLayout.VERTICAL);

        String lisc_plate,brand,model,year ;

        for (int i = 0; i < all.length(); i++) {
            JSONObject item = all.getJSONObject(i);
            lisc_plate = item.getString("LISCENCE_PLATE");
            brand = item.getString("CAR_BRAND");
            model = item.getString("CAR_MODEL");
            year = item.getString("CAR_YEAR");

            LinearLayout lH1=new LinearLayout(CarDetails.this);
            lH1.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout lH2=new LinearLayout(CarDetails.this);
            lH2.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout lH3=new LinearLayout(CarDetails.this);
            lH3.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout lH4=new LinearLayout(CarDetails.this);
            lH4.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout lVertical=new LinearLayout(CarDetails.this);
            lVertical.setOrientation(LinearLayout.VERTICAL);

            TextView hCarIndex=new TextView(CarDetails.this);
            hCarIndex.setTypeface(null, Typeface.BOLD);
            TextView hLisc_Values=new TextView(CarDetails.this);
            TextView hBrand=new TextView(CarDetails.this);
            TextView hModel=new TextView(CarDetails.this);
            TextView hYear=new TextView(CarDetails.this);
            TextView VLisc_Values=new TextView(CarDetails.this);
            TextView VBrand=new TextView(CarDetails.this);
            TextView VModel=new TextView(CarDetails.this);
            TextView VYear=new TextView(CarDetails.this);
            TextView blank=new TextView(CarDetails.this);


            hCarIndex.setText("CAR "+(i+1) );
            hLisc_Values.setText("LISCENCE PLATE NUMBER : ");
            hBrand.setText("BRAND : ");
            hModel.setText("MODEL : ");
            hYear.setText("YEAR : ");
            VLisc_Values.setText(lisc_plate);
            VBrand.setText(brand);
            VModel.setText(model);
            VYear.setText(year);

            lVertical.addView(hCarIndex);


            lH1.addView(hLisc_Values);          //adding the lisc_plate in a horizontal layout
            lH1.addView(VLisc_Values);
            lH2.addView(hBrand);                 //adding the brand in a horizontal layout
            lH2.addView(VBrand);
            lH3.addView(hModel);                 //adding the model in a horizontal layout
            lH3.addView(VModel);
            lH4.addView(hYear);                  //adding the year in a horizontal layout
            lH4.addView(VYear);

            lVertical.addView(lH1);
            lVertical.addView(lH2);
            lVertical.addView(lH3);
            lVertical.addView(lH4);
            lVertical.addView(blank);
            l.addView(lVertical);

        }
        scroll.addView(l);

    }



    public void goBack2(View view) {
        Intent myIntent = new Intent(CarDetails.this, MainMenuActivity.class);        //go back to main menu

        startActivity(myIntent);

    }

    public void addNewCar(View view) {
        Intent myIntent = new Intent(CarDetails.this, AddCars.class);        //go to add a new car

        startActivity(myIntent);
    }
}