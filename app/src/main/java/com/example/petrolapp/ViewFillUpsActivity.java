package com.example.petrolapp;
//To-DO
//Finish Sort and Search Methods
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.SQLOutput;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 */
//ToDo check when searching if they have two fill ups on the same day, must return both

public class ViewFillUpsActivity extends AppCompatActivity {
    private static String username;
    private TableLayout tbl;;
    private String JSON;

    Button btnBack;
    private boolean backBtnVisible =true;
    LinearLayout fullScreenContentControls;

    public void fillTable(){//method to full the table when it originally opens

        ContentValues cv=new ContentValues();
        cv.put("USERNAME",username);

        Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

        c.fetchInfo(ViewFillUpsActivity.this, "get_CAR_LOG",cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {
                JSON=response;
                processJson(JSON);
            }
        });


    }

    public void processJson(String json){

        ViewGroup.LayoutParams param = findViewById(R.id.txtHeading0).getLayoutParams();
        TableRow.LayoutParams tableRowParams=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);


        try {
            JSONArray jsonArray=new JSONArray(json);
            for (int i = 0; i <jsonArray.length() ; i++) {
                TableRow blankLine=new TableRow(this);//every record will have a blank line between them
                blankLine.setLayoutParams(tableRowParams);

                JSONObject item=jsonArray.getJSONObject(i);

                String stationFullName=item.getString("PETROL_STATION_NAME");
                String words[]=stationFullName.split(" ");
                String station=words[0];
                double cost=item.getDouble("COST");
                double mileage=item.getDouble("MILEAGE");
                String date=item.getString("DATE");
                double litres=item.getDouble("LITRES");
                double eff=item.getDouble("EFFICIENCY");

                TableRow tr = new TableRow(this);
                tr.setId(42);
                tr.setLayoutParams(tableRowParams);
                tr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle extra=new Bundle();
                        extra.putString("activity","FillUps");
                        extra.putString("name",stationFullName);
                        extra.putDouble("eff",eff);
                        extra.putBoolean("found",true);
                        Intent i=new Intent(getApplicationContext(),popupApplication.class);
                        i.putExtras(extra);
                        startActivity(i);
                    }
                });



                TextView stationView = new TextView(this);
                stationView.setTextAlignment(4);
                    stationView.setText(station);
                stationView.setLayoutParams(param);
                stationView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(stationView);

                TextView temp0=new TextView(this);
                temp0.setLayoutParams(param);
                blankLine.addView(temp0);

                TextView dateView = new TextView(this);
                dateView.setTextAlignment(4);
                dateView.setBackgroundColor(Color.rgb(0, 170, 240));
                    dateView.setText(date);
                dateView.setLayoutParams(param);
                dateView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(dateView);

                TextView temp1=new TextView(this);
                temp1.setLayoutParams(param);
                temp1.setBackgroundColor(Color.rgb(0, 170, 240));
                blankLine.addView(temp1);

                TextView litresView = new TextView(this);
                litresView.setBackgroundColor(Color.GREEN);
                litresView.setTextAlignment(4);
                    litresView.setText(litres+"");
                litresView.setLayoutParams(param);
                litresView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(litresView);

                TextView temp2=new TextView(this);
                temp2.setLayoutParams(param);
                temp2.setBackgroundColor(Color.GREEN);
                blankLine.addView(temp2);

                TextView costView = new TextView(this);
                costView.setBackgroundColor(Color.rgb(240, 100, 100));
                    costView.setText(cost+"");
                costView.setLayoutParams(param);
                costView.setTextAlignment(4);
                costView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(costView);

                TextView temp3=new TextView(this);
                temp3.setLayoutParams(param);
                temp3.setBackgroundColor(Color.rgb(240, 100, 100));
                blankLine.addView(temp3);

                TextView mileageView = new TextView(this);
                mileageView.setBackgroundColor(Color.rgb(255, 170, 0));
                mileageView.setTextAlignment(4);
                    mileageView.setText(mileage+"");
                mileageView.setLayoutParams(param);
                mileageView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(mileageView);


                TextView temp4=new TextView(this);
                temp4.setLayoutParams(param);
                temp4.setBackgroundColor(Color.rgb(255, 170, 0));
                blankLine.addView(temp4);

                tbl.addView(blankLine);
                tbl.addView(tr);


            }
        } catch ( JSONException e) {
            e.printStackTrace();
        }
    }

    public void showAll(View view) throws JSONException {
        JSONArray jsonArray=new JSONArray(JSON);
        int numViews=tbl.getChildCount();
        if(numViews<jsonArray.length()*2+1) {
            tbl.removeViews(1,numViews-1);
            processJson(JSON);
        }
    }

    public void search(View view) throws JSONException {
        ViewGroup.LayoutParams param = findViewById(R.id.txtHeading0).getLayoutParams();
        TableRow.LayoutParams tableRowParams=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        TextView txtDate=findViewById(R.id.txtDate);
        JSONArray jsonArray=new JSONArray(JSON);

        CharSequence req_date= txtDate.getText();
        txtDate.setText("");

        boolean found=false;//keeps track of if we found a record or not

        for (int i = 0; i <jsonArray.length() ; i++) {

            JSONObject item = jsonArray.getJSONObject(i);
            String date = item.getString("DATE");

            if(date.contentEquals(req_date)){
                try {
                    tbl.removeViews(1,tbl.getChildCount()-1);//removes all the rows except the heading row.
                }
                catch (NullPointerException | IndexOutOfBoundsException ex ){
                    processJson(JSON);
                }
                TableRow blankLine = new TableRow(this);//every record will have a blank line between them
                blankLine.setLayoutParams(tableRowParams);
                found=true;

                String stationFullName = item.getString("PETROL_STATION_NAME");
                String words[] = stationFullName.split(" ");
                String station = words[0];
                double cost = item.getDouble("COST");
                double mileage = item.getDouble("MILEAGE");

                double litres = item.getDouble("LITRES");
                double eff = item.getDouble("EFFICIENCY");

                TableRow tr = new TableRow(this);
                tr.setLayoutParams(tableRowParams);
                tr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle extra=new Bundle();
                        extra.putString("name",stationFullName);
                        extra.putDouble("eff",eff);
                        extra.putBoolean("found",true);
                        Intent i=new Intent(getApplicationContext(),popupApplication.class);
                        i.putExtras(extra);
                        startActivity(i);
                    }
                });



                TextView stationView = new TextView(this);
                stationView.setTextAlignment(4);
                stationView.setText(station);
                stationView.setLayoutParams(param);
                stationView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(stationView);

                TextView temp0=new TextView(this);
                temp0.setLayoutParams(param);
                blankLine.addView(temp0);

                TextView dateView = new TextView(this);
                dateView.setTextAlignment(4);
                dateView.setBackgroundColor(Color.rgb(0, 170, 240));
                dateView.setText(date);
                dateView.setLayoutParams(param);
                dateView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(dateView);

                TextView temp1=new TextView(this);
                temp1.setLayoutParams(param);
                temp1.setBackgroundColor(Color.rgb(0, 170, 240));
                blankLine.addView(temp1);

                TextView litresView = new TextView(this);
                litresView.setBackgroundColor(Color.GREEN);
                litresView.setTextAlignment(4);
                litresView.setText(litres+"");
                litresView.setLayoutParams(param);
                litresView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(litresView);

                TextView temp2=new TextView(this);
                temp2.setLayoutParams(param);
                temp2.setBackgroundColor(Color.GREEN);
                blankLine.addView(temp2);

                TextView costView = new TextView(this);
                costView.setBackgroundColor(Color.rgb(240, 100, 100));
                costView.setText(cost+"");
                costView.setLayoutParams(param);
                costView.setTextAlignment(4);
                costView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(costView);

                TextView temp3=new TextView(this);
                temp3.setLayoutParams(param);
                temp3.setBackgroundColor(Color.rgb(240, 100, 100));
                blankLine.addView(temp3);

                TextView mileageView = new TextView(this);
                mileageView.setBackgroundColor(Color.rgb(255, 170, 0));
                mileageView.setTextAlignment(4);
                mileageView.setText(mileage+"");
                mileageView.setLayoutParams(param);
                mileageView.setTextAppearance(R.style.fontForTextViews);
                tr.addView(mileageView);


                TextView temp4=new TextView(this);
                temp4.setLayoutParams(param);
                temp4.setBackgroundColor(Color.rgb(255, 170, 0));
                blankLine.addView(temp4);

                tbl.addView(blankLine);
                tbl.addView(tr);
            }


        }
        if(!found){
            Bundle extra=new Bundle();
            extra.putBoolean("found",found);
            Intent i=new Intent(getApplicationContext(),popupApplication.class);
            i.putExtras(extra);
            startActivity(i);
        }


    }

    public void sort(View view) throws JSONException {
        Spinner spnrSort=findViewById(R.id.spinnerSort);
        String sort=spnrSort.getSelectedItem().toString();

        Spinner spnrOrder=findViewById(R.id.spinnerOrder);
        String temp=spnrOrder.getSelectedItem().toString();
        String order="";
        if(temp.equals("Ascending")){
            order="ASC";
        }
        else{
            order="DESC";
        }

        int numViews=tbl.getChildCount();

        tbl.removeViews(1,numViews-1);//removes all the current records being shown first

        ContentValues cv=new ContentValues();
        cv.put("USERNAME",username);
        cv.put("sort",sort);
        cv.put("order",order);

        Connection connection=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

        connection.fetchInfo(ViewFillUpsActivity.this, "get_CAR_LOG_SORTED",cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {
                JSON=response;
                processJson(JSON);
            }
        });
    }

    public void goBack(View view){
        Intent i=new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(i);
    }

    public void configureScreen(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();//hides the name of the activity at the top
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);//hides the navigation bar at the bottom

        btnBack=findViewById(R.id.btnBackFillUps);
        fullScreenContentControls=findViewById(R.id.fullscreen_content_controls);

        ConstraintLayout mContentView=findViewById(R.id.FillUpsContent);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }
    private void toggle(){//sets the navigation bar at the bottom visible or not when the user touches the screen
        if(backBtnVisible){

            btnBack.setVisibility(View.INVISIBLE);
            fullScreenContentControls.setVisibility(View.INVISIBLE);
            backBtnVisible =false;
        }
        else{
            btnBack.setVisibility(View.VISIBLE);
            fullScreenContentControls.setVisibility(View.VISIBLE);
            backBtnVisible =true;
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fill_ups);

        Intent intent=getIntent();
        username=intent.getStringExtra("username");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        View decorView=getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        fillTable();

        tbl=findViewById(R.id.tblLayout);

        configureScreen();


    }


}
