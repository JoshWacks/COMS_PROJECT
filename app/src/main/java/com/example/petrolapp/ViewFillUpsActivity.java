package com.example.petrolapp;
//To-DO
//Finish Sort and Search Methods
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewFillUpsActivity extends AppCompatActivity {
    private static String user="JoshW";


    private TableLayout tbl;

    public void fillTable(){

        ContentValues cv=new ContentValues();
        cv.put("USERNAME",user);

        Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

        c.fetchInfo(ViewFillUpsActivity.this, "get_CAR_LOG",cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                procsesJson(response);
            }
        });

//        for (int i = 0; i < 250; i++) {
//
//        }



    }
    public void procsesJson(String json){
        tbl=findViewById(R.id.tblLayout);
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
                tr.setLayoutParams(tableRowParams);
                tr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle extra=new Bundle();
                        extra.putString("name",stationFullName);
                        extra.putDouble("eff",eff);
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



    public void goBack(View view){
        Intent i=new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(i);
    }




    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_fill_ups);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        View decorView=getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        fillTable();

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.btnBack).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
//        View decorView=getWindow().getDecorView();
//        int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);


        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        mVisible = true;

    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
