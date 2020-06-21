package com.example.petrolapp;

import android.annotation.SuppressLint;
import android.content.*;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class AtStationActivity extends AppCompatActivity {

    private double x_co;
    private double y_co;
    private String user="JoshW";//TODO get the correct username from the main menu first via intent
    private String stationAt="";
    LocalDate d= LocalDate.now();//saves it for the query
    TextView txtStation;
    TextView txtDate;
    TextView txtCar;
    private Button btnDone;

    Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");//We can use the same connection throughout as file path will be the same


    private BroadcastReceiver broadcastReceiver;

    public void goBack(View view){
        Intent i=new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(i);
    }

    public void configure(){
        txtStation=findViewById(R.id.txtViewStation);
        txtDate=findViewById(R.id.txtViewDate);

        Toast toast=Toast.makeText(getApplicationContext(),"Remember it is the mileage for your last trip",Toast.LENGTH_LONG);
        toast.show();

        //their current petrol station is found in processStation
        //Todo check if they have 2 cars and if so, let them choose which car they are filling up

        txtDate.append(d+" ");//sets the current date
        getDesc();
        getStations();

    }

    public void getDesc(){
        ContentValues cv=new ContentValues();
        cv.put("USERNAME",user);
        final String[] desc = {""};


        c.fetchInfo(AtStationActivity.this, "get_CAR_DESC",cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                try {
                    desc[0] =processJson(response);
                    txtCar=findViewById(R.id.txtViewCar);
                    txtCar.append(desc[0]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public String processJson(String json) throws JSONException {
        JSONArray jsonArray=new JSONArray(json);
        String brand ="";
        String model="";
        for (int i = 0; i <jsonArray.length() ; i++) {//check if they have 2 cars
            JSONObject item=jsonArray.getJSONObject(i);

            brand=item.getString("CAR_BRAND");
            model=item.getString("CAR_MODEL");

        }

        return brand+" "+model;
    }

    private void getStations(){

        ContentValues cv=new ContentValues();

        c.fetchInfo(AtStationActivity.this, "get_STATIONS",cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {
                processStation(response);
            }
        });
    }


    private  void processStation(String json) {
        String name="";
        double x=0;
        double y=0;

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item=jsonArray.getJSONObject(i);
                name=item.getString("PETROL_STATION_NAME");
                x=item.getDouble("X_CO");
                y=item.getDouble("Y_CO");
                //here we find the petrol station the user is at. the 0.01 is added on either side as it is very
                //unlikely they will be at the exact coordinates
                if((x_co-0.01)<x&&x<(x_co+0.01)&&(y_co-0.01)<y&&y<(y_co+0.01)){
                    stationAt=name;
                    txtStation.append(stationAt);

                }





            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insert(){
        String strPrice= getIntent().getStringExtra("price");//gets the price from the main menu
        double price=Double.parseDouble(strPrice.substring(2));//we use substring to remove the R in front of the price

        TextView txtLitres=findViewById(R.id.txtInLitres);
        CharSequence charLitres=  txtLitres.getText();
        String strLitres=charLitres.toString();
        double litres=Double.parseDouble(strLitres);
        txtLitres.setText(" ");//we clear the text to ensure they don't enter the same record twice

        double cost=litres*price;//cost is calculated via multiplying litres by price

        TextView txtMileage=findViewById(R.id.txtInMileage);
        CharSequence charMileage=  txtMileage.getText();
        String strMileage=charMileage.toString();
        double mileage=Double.parseDouble(strMileage);
        txtMileage.setText(" ");//we clear the text to ensure they don't enter the same record twice


        ContentValues cv=new ContentValues();
        cv.put("USERNAME",user);
        cv.put("STATION",stationAt);
        cv.put("COST",cost);
        cv.put("MILEAGE",mileage);
        cv.put("DATE", String.valueOf(d));//the date is found earlier
        cv.put("LITRES",litres);




        c.fetchInfo(AtStationActivity.this, "insert_CAR_LOG",cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

            }
        });


    }



    private boolean runtime_permissions(){

        if(Build.VERSION.SDK_INT>=23&& ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission
                    .ACCESS_COARSE_LOCATION},100);
            return true;

        }
        return false;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==100){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else{
                runtime_permissions();
            }
        }
    }

    private void enable_buttons() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();// we do the insert here or the onclick is overwritten
                Toast toast=Toast.makeText(getApplicationContext(),"Successfully Added Full Up",Toast.LENGTH_LONG);
                toast.show();
                Intent i=new Intent(getApplicationContext(),GPS_Service.class);
                stopService(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver==null){
            broadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras=intent.getExtras();//gets the co-ord's here
                    x_co=extras.getDouble("x_co");
                    y_co=extras.getDouble("y_co");
                    System.out.println("x "+x_co+" y "+y_co);

                }
            };
            registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
        }
    }








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

        setContentView(R.layout.activity_at_station);
        View decorView=getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getSupportActionBar();//configures the navigation bar and the name of the app at the top
        assert actionBar != null;
        actionBar.hide();

        configure();


        btnDone=findViewById(R.id.btnDone);


        if(!runtime_permissions()){
            enable_buttons();
        }




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
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);

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
