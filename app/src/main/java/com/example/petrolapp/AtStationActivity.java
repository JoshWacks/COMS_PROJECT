package com.example.petrolapp;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
//TODO check they are at a petrol station before entering data
    //TODO check if they have a car first if not take them to the new car page
@RequiresApi(api = Build.VERSION_CODES.O)

public class AtStationActivity extends AppCompatActivity {
    private static final String username=appInformation.getUsername() ;


    LocalDate d = LocalDate.now();//saves it for the query
    TextView txtStation;
    TextView txtDate;
    TextView txtCar;
    Button btnBack;
    LinearLayout fullScreenContentControls;
    private double x_co = 0;
    private double y_co = 0;

    private String stationAt = "";
    private Button btnDone;
    private boolean backBtnVisible = true;
    private BroadcastReceiver broadcastReceiver;

    private boolean nameSet=false;

    private String strPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_at_station);

        strPrice=appInformation.getPetrolPrice();

        configure();
        configureScreen();

        btnDone = findViewById(R.id.btnDone);

        if (!runtime_permissions()) {
            enable_buttons();
        }

    }

    public void goBack(View view) {
        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(i);
    }

    public void configure() {
        txtStation = findViewById(R.id.txtViewStation);
        txtDate = findViewById(R.id.txtViewDate);

        Toast toast = Toast.makeText(getApplicationContext(), "Remember it is the mileage for your last trip", Toast.LENGTH_LONG);
        toast.show();

        //their current petrol station is found in processStation
        //Todo check if they have 2 cars and if so, let them choose which car they are filling up

        txtDate.append(d + " ");//sets the current date

        getDesc();


    }

    public void getDesc() {
        ContentValues cv = new ContentValues();
        cv.put("USERNAME", username);
        final String[] desc = {""};
        Connection c = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

        c.fetchInfo(AtStationActivity.this, "get_CAR_DESC", cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                try {
                    desc[0] = processJson(response);
                    txtCar = findViewById(R.id.txtViewCar);
                    txtCar.append(desc[0]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public String processJson(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String brand = "";
        String model = "";
        for (int i = 0; i < jsonArray.length(); i++) {//check if they have 2 cars
            JSONObject item = jsonArray.getJSONObject(i);

            brand = item.getString("CAR_BRAND");
            model = item.getString("CAR_MODEL");

        }

        return brand + " " + model;
    }

    private void getStations() {

        ContentValues cv = new ContentValues();
        Connection c = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

        c.fetchInfo(AtStationActivity.this, "get_STATIONS", cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {
                processStation(response);
            }
        });
    }


    private void processStation(String json) {
        String name = "";
        double x = 0;
        double y = 0;

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                name = item.getString("PETROL_STATION_NAME");
                x = item.getDouble("X_CO");
                y = item.getDouble("Y_CO");
                //here we find the petrol station the user is at. the 0.01 is added on either side as it is very
                //unlikely they will be at the exact coordinates
                if ((x_co - 0.01) < x && x < (x_co + 0.01) && (y_co - 0.01) < y && y < (y_co + 0.01)) {
                    stationAt = name;
                    txtStation.append(stationAt);


                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insert() {

        double price = Double.parseDouble(strPrice.substring(2));//we use substring to remove the R in front of the price

        TextView txtLitres = findViewById(R.id.txtInLitres);
        CharSequence charLitres = txtLitres.getText();
        String strLitres = charLitres.toString();
        double litres = Double.parseDouble(strLitres);
        txtLitres.setText(" ");//we clear the text to ensure they don't enter the same record twice

        double cost = litres * price;//cost is calculated via multiplying litres by price

        TextView txtMileage = findViewById(R.id.txtInMileage);
        CharSequence charMileage = txtMileage.getText();
        String strMileage = charMileage.toString();
        double mileage = Double.parseDouble(strMileage);
        txtMileage.setText(" ");//we clear the text to ensure they don't enter the same record twice


        ContentValues cv = new ContentValues();
        cv.put("USERNAME", username);
        cv.put("STATION", stationAt);
        cv.put("COST", cost);
        cv.put("MILEAGE", mileage);
        cv.put("DATE", String.valueOf(d));//the date is found earlier
        cv.put("LITRES", litres);


        Connection c = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

        c.fetchInfo(AtStationActivity.this, "insert_CAR_LOG", cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

            }
        });


    }


    private boolean runtime_permissions() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                    .ACCESS_COARSE_LOCATION}, 100);
            return true;

        }
        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enable_buttons();
            } else {
                runtime_permissions();
            }
        }
    }

    private void enable_buttons() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();// we do the insert here or the onclick is overwritten
                Toast toast = Toast.makeText(getApplicationContext(), "Successfully Added Full Up", Toast.LENGTH_LONG);
                toast.show();
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();//gets the co-ord's here
                    x_co = extras.getDouble("x_co");
                    y_co = extras.getDouble("y_co");
                    if(!nameSet){
                        getStations();
                        nameSet=true;
                    }





                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }


    public void configureScreen() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();//hides the name of the activity at the top
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);//hides the navigation bar at the bottom
        fullScreenContentControls = findViewById(R.id.fullscreen_content_controls);
        btnBack = findViewById(R.id.btnBackAtStation);

        ConstraintLayout mContentView = findViewById(R.id.AtStationContent);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    private void toggle() {//sets the navigation bar at the bottom visible or not when the user touches the screen
        if (backBtnVisible) {

            btnBack.setVisibility(View.INVISIBLE);
            fullScreenContentControls.setVisibility(View.INVISIBLE);
            backBtnVisible = false;

        } else {
            btnBack.setVisibility(View.VISIBLE);
            fullScreenContentControls.setVisibility(View.VISIBLE);
            backBtnVisible = true;

        }
    }


}
