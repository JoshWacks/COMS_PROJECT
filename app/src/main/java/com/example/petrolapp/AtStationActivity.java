package com.example.petrolapp;

import android.content.*;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mrntlu.toastie.Toastie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

/**
 The activity to control the insertion of full ups and the user at the station details.
 */
@RequiresApi(api = Build.VERSION_CODES.O)

public class AtStationActivity extends AppCompatActivity {
    public static TextView txtStation;
    public static String stationAt;
    private static String username;
    private final LocalDate d = LocalDate.now();//saves it for the query
    private TextView txtDate;
    private TextView txtLitres;
    private TextView txtMileage;
    private TextView txtInstructions;
    private Button btnBack;
    private LinearLayout fullScreenContentControls;
    private double x_co = 0;
    private double y_co = 0;
    private Button btnDone;
    private boolean backBtnVisible = true;
    private BroadcastReceiver broadcastReceiver;

    private boolean nameSet = false;

    private String strPrice;
    private String selected_liscencePlate = "";

    private LinearLayout linearLayout;

    private ImageView image;
    private Intent gpsIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_at_station);



        username = appInformation.getUsername();

        stationAt = appInformation.getNewStationName();
        strPrice = appInformation.getPetrolPrice();

        configure();




//        if (!runtime_permissions()) {
//            enable_buttons();
//
//        }

    }

    public void goBack(View view) {
        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
        stationAt = "";
        appInformation.setNewStationName("");//resets the station name
        finish();
        startActivity(i);
    }


    private void configure() {
        configureScreen();
        txtStation = findViewById(R.id.txtViewStation);
        txtLitres = findViewById(R.id.txtInLitres);
        txtMileage = findViewById(R.id.txtInMileage);
        txtInstructions = findViewById(R.id.txtInstructions);
        btnDone = findViewById(R.id.btnDone);
        txtDate = findViewById(R.id.txtViewDate);
        txtDate.append(d + " ");//sets the current date

        linearLayout=findViewById(R.id.selectCarLinearLayout);

        image = findViewById(R.id.imageAtStation);

        //their current petrol station is found in processStation
        getDesc();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();// we do the insert here or the onclick is overwritten

                Intent i = new Intent(getApplicationContext(), GPS_Service.class);//Stops the service, stops checking for x and y co-ords
                stopService(i);
            }
        });
    }

    public void selectCar(View view) {
        TextView textView;

        for(int i=0;i<linearLayout.getChildCount();i++){
            textView= (TextView) linearLayout.getChildAt(i);
            textView.setBackgroundColor(Color.WHITE);//reset all other to textviews to white first

        }
        TextView selectedTxtView= (TextView) view;
        selectedTxtView.setBackgroundColor(Color.CYAN);
        String txt= (String) selectedTxtView.getText();
        int pos=txt.indexOf(":");
        selected_liscencePlate=txt.substring(pos+1);

    }


    private void getDesc() {
        ContentValues cv = new ContentValues();

        cv.put("USERNAME", username);

        Connection c = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

        c.fetchInfo(AtStationActivity.this, "get_CAR_DESC", cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                try {

                    processCar(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void processCar(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);

        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        if (jsonArray.length() == 0) {
            Toastie.centerInfo(getApplicationContext(),"Please add your car first",Toast.LENGTH_LONG).show();

            finish();//makes sure they cannot go back to their old activity before adding a car
            Intent intent = new Intent(getApplicationContext(), AddCars.class);
            startActivity(intent);//takes them to add their car first before filling up for a car not on our system

        }
        else if (jsonArray.length() > 1) {//they have more than one car and we need to know which car they are filling up first

            txtInstructions.setVisibility(View.VISIBLE);
            Toastie.centerWarning(getApplicationContext(), "Remember it is the mileage for your last trip \nAnd to full up to the same point each time", Toast.LENGTH_SHORT).show();

            String brand, model, plate = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                brand = item.getString("CAR_BRAND");
                model = item.getString("CAR_MODEL");
                plate = item.getString("LISCENCE_PLATE");

                TextView textView=new TextView(this);
                textView.setLayoutParams(layoutParams);
                textView.setTextAppearance(R.style.fontForTextViews2);
                textView.setText(brand+" "+model+"\t:"+plate);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectCar(v);
                    }
                });

                TextView blankLine=new TextView(this);
                linearLayout.addView(textView);
                linearLayout.addView(blankLine);
            }


        } else {//only one car
            image.setVisibility(View.VISIBLE);
            txtInstructions.setVisibility(View.GONE);

            Toastie.centerWarning(getApplicationContext(), "Remember it is the mileage for your last trip \nAnd to full up to the same point each time", Toast.LENGTH_SHORT).show();

            JSONObject item = jsonArray.getJSONObject(0);

            String brand = item.getString("CAR_BRAND");
            String model = item.getString("CAR_MODEL");
            String plate = item.getString("LISCENCE_PLATE");

            selected_liscencePlate = plate;
            TextView textView=new TextView(this);
            textView.setLayoutParams(layoutParams);
            textView.setTextAppearance(R.style.fontForTextViews2);
            textView.setText(brand+" "+model+"\t:"+plate);

            linearLayout.addView(textView);

        }
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
                    appInformation.setNewStationName(name);
                    break;//found the station , we can exit


                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (stationAt.equals("")) {

            insertNewStation();

        }
    }

    private void insertNewStation() {
        appInformation.setActivity("AtStation");

        Intent intent = new Intent(getApplicationContext(), popupApplication.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("x_co", x_co);
        bundle.putDouble("y_co", y_co);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private void insert() {

        CharSequence charLitres = txtLitres.getText();//fetched their entered mileage and litres
        String strLitres = charLitres.toString();

        CharSequence charMileage = txtMileage.getText();
        String strMileage = charMileage.toString();

        //Validation Checks before entering are done here

        if (stationAt.equals("")) {

            insertNewStation();

        } else if (selected_liscencePlate.equals("")) {

            Toastie.centerWarning(getApplicationContext(), "Please select a car first", Toast.LENGTH_LONG).show();

        } else if (strLitres.equals("")) {

            Toastie.centerWarning(getApplicationContext(), "Please enter your litres first", Toast.LENGTH_LONG).show();

        } else if (strMileage.equals("")) {

            Toastie.centerWarning(getApplicationContext() , "Please enter your mileage first", Toast.LENGTH_LONG).show();

        } else {

            double price = Double.parseDouble(strPrice.substring(2));//we use substring to remove the R in front of the price
            double litres = Double.parseDouble(strLitres);

            txtLitres.setText(" ");//we clear the text to ensure they don't enter the same record twice
            double cost = litres * price;//cost is calculated via multiplying litres by price

            double mileage = Double.parseDouble(strMileage);
            txtMileage.setText(" ");//we clear the text to ensure they don't enter the same record twice

            ContentValues cv = new ContentValues();
            cv.put("LISCENCE_PLATE", selected_liscencePlate);
            cv.put("STATION", stationAt);
            cv.put("COST", cost);
            cv.put("MILEAGE", mileage);
            cv.put("DATE", String.valueOf(d));//the date is found earlier
            cv.put("LITRES", litres);


            Connection c = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

            c.fetchInfo(AtStationActivity.this, "insert_CAR_LOG", cv, new RequestHandler() {
                @Override
                public void processResponse(String response) {

                    Toastie.centerSuccess(getApplicationContext(),"Successfully Added Full Up",Toast.LENGTH_LONG).show();
                }
            });
        }

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

                    if (!nameSet) {//The name has not been set yet
                        getStations();//We only call get stations once we have the x and y co-ords
                        nameSet = true;
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

    private void configureScreen() {
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
