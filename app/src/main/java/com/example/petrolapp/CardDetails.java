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

public class CardDetails extends AppCompatActivity {
    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        username=appInformation.getUsername() ;


        ContentValues cv=new ContentValues();
        cv.put("USERNAME",username);
        Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        c.fetchInfo(CardDetails.this, "get_USER_CARDS",cv, new RequestHandler() {
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

        ScrollView scroll=(ScrollView) findViewById(R.id.Scroll2) ;

        LinearLayout l=new LinearLayout(CardDetails.this);
        l.setOrientation(LinearLayout.VERTICAL);

        String company,card_no,exp_date ;

        JSONArray all = new JSONArray(json);
        for (int i = 0; i < all.length(); i++) {
            JSONObject item = all.getJSONObject(i);
            company = item.getString("COMPANY");
            card_no = item.getString("CARD_NUMBER");
            exp_date = item.getString("EXPIRY_DATE");

            LinearLayout lH1=new LinearLayout(CardDetails.this);
            lH1.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout lH2=new LinearLayout(CardDetails.this);
            lH2.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout lH3=new LinearLayout(CardDetails.this);
            lH3.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout lVertical=new LinearLayout(CardDetails.this);
            lVertical.setOrientation(LinearLayout.VERTICAL);

            TextView hCarIndex=new TextView(CardDetails.this);
            hCarIndex.setTypeface(null, Typeface.BOLD);
            TextView hCompany=new TextView(CardDetails.this);
            TextView hCard_Number=new TextView(CardDetails.this);
            TextView hExpiry_Date=new TextView(CardDetails.this);
            TextView VCompany=new TextView(CardDetails.this);
            TextView VCard_Number=new TextView(CardDetails.this);
            TextView VExpiry_Date=new TextView(CardDetails.this);
            TextView blank=new TextView(CardDetails.this);


            hCarIndex.setText("CARD "+(i+1) );
            hCompany.setText("COMPANY : ");
            hCard_Number.setText("CARD ID : ");
            hExpiry_Date.setText("EXPIRY DATE : ");
            VCompany.setText(company);
            VCard_Number.setText(card_no);
            VExpiry_Date.setText(exp_date);

            lVertical.addView(hCarIndex);

            lH1.addView(hCompany);                     //adding the company in a horizontal layout
            lH1.addView(VCompany);
            lH2.addView(hCard_Number);                 //adding the card no in a horizontal layout
            lH2.addView(VCard_Number);
            lH3.addView(hExpiry_Date);                 //adding the expiry date in a horizontal layout
            lH3.addView(VExpiry_Date);

            lVertical.addView(lH1);
            lVertical.addView(lH2);
            lVertical.addView(lH3);
            lVertical.addView(blank);
            l.addView(lVertical);

        }
        scroll.addView(l);

    }

    public void goBack3(View view) {

        Intent myIntent = new Intent(CardDetails.this, MainMenuActivity.class);        //go back to main menu
        startActivity(myIntent);
    }

    public void addNewCard(View view) {
        Intent myIntent = new Intent(CardDetails.this, AddCards.class);        //go back to main menu
        startActivity(myIntent);
    }
}