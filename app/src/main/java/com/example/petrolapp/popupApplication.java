package com.example.petrolapp;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;


public class popupApplication extends Activity {
    //Class to handle all the popups in the app
    private TextView txtH1;
    private TextView txtH2;
    private TextView txtH3;
    private TextView txtH4;

    private TextView txtData1;
    private TextView txtData2;
    private TextView txtData3;

    private String activity;

    private TextView txtBottomBorder ;
    private TextView txtStationName ;

    private Button btnPopDone;

    Double x_co;
    Double y_co;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        txtH1 = findViewById(R.id.txtPopHeading1);
        txtH2 = findViewById(R.id.txtPopHeading2);
        txtH3 = findViewById(R.id.txtPopHeading3);
        txtH4 = findViewById(R.id.txtPopHeading4);


        txtData1 = findViewById(R.id.txtPopData1);
        txtData2 = findViewById(R.id.txtPopData2);
        txtData3 = findViewById(R.id.txtPopData3);

        txtBottomBorder = findViewById(R.id.txtBottomBorder);
        txtStationName = findViewById(R.id.etxtName);

        btnPopDone=findViewById(R.id.btnPopDone);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        activity = appInformation.getActivity();//checks which activity is calling it

        double hDim;
        double wDim;
        if (activity.equals("FillUps")) {
            fillUpsPopUp(bundle);
            hDim = 0.16;
            wDim = 0.9;
            getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
        } else if (activity.equals("AtStation")) {
            hDim = 0.18;
            wDim = 0.9;
            atStationPopup(bundle);
            getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
        } else if(activity.equals("CarEff")) {
            hDim = 0.2;
            wDim = 0.7;
            getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
            carEffPopUp(bundle);
        }


    }

    public void fillUpsPopUp(Bundle bundle) {
        //Method for when the fillups activity is calling the popup window
        TextView txtFound = findViewById(R.id.txtNotFound);

        btnPopDone.setVisibility(View.GONE);
        txtH4.setVisibility(View.GONE);
        txtStationName.setVisibility(View.GONE);
        txtBottomBorder.setVisibility(View.VISIBLE);
        txtH1.setText("Station: ");
        txtH2.setText("Efficiency: ");

        boolean f = bundle.getBoolean("found");
        if (!f) {
            txtData1.setVisibility(View.GONE);
            txtData2.setVisibility(View.GONE);
            txtH2.setVisibility(View.GONE);
            txtH1.setVisibility(View.GONE);

            txtFound.setText("We were unable to find any records on that date \n \nPlease re-enter your search query");
            txtFound.setVisibility(View.VISIBLE);
        } else {
            txtData1.setVisibility(View.VISIBLE);
            txtData2.setVisibility(View.VISIBLE);
            txtH2.setVisibility(View.VISIBLE);
            txtH1.setVisibility(View.VISIBLE);
            txtFound.setVisibility(View.GONE);

            String name = bundle.getString("name");
            double eff = bundle.getDouble("eff");

            txtData1.setText(name);
            txtData2.setText(eff + "   (Mileage/Litres)");


        }
    }

    public void atStationPopup(Bundle bundle) {

        x_co = bundle.getDouble("x_co");
        y_co = bundle.getDouble("y_co");


        txtData1.setVisibility(View.GONE);
        txtH2.setVisibility(View.GONE);
        txtData2.setVisibility(View.GONE);
        btnPopDone.setVisibility(View.VISIBLE);
        txtStationName.setVisibility(View.VISIBLE);
        txtH4.setVisibility(View.VISIBLE);
        txtBottomBorder.setVisibility(View.VISIBLE);



    }


    public void carEffPopUp(Bundle bundle) {
        btnPopDone.setVisibility(View.GONE);
        txtStationName.setVisibility(View.GONE);
        txtBottomBorder.setVisibility(View.GONE);
        txtH4.setVisibility(View.GONE);
        txtH1.setText("BRAND:     ");
        txtH2.setText("MODEL:    ");
        txtH3.setText("YEAR:    ");

        String brand = bundle.getString("brand");
        String model = bundle.getString("model");
        String year = bundle.getString("year");

        txtData1.setText(brand);
        txtData2.setText(model);
        txtData3.setText(year);


    }

    public void btnDone(View view) {


        String stationName="";
        stationName =txtStationName.getEditableText().toString().trim();
        if (stationName.equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a station name first", Toast.LENGTH_LONG);
            toast.show();
        }
        else {

            Connection connection = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
            ContentValues cv = new ContentValues();
            cv.put("STATION", stationName);
            cv.put("X_CO", x_co);
            cv.put("Y_CO", y_co);

            String finalStationName = stationName;

            connection.fetchInfo(popupApplication.this, "insert_STATION", cv, new RequestHandler() {
                @Override
                public void processResponse(String response) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Successfully Added Station \n Press anywhere else to close this", Toast.LENGTH_LONG);
                    toast.show();
                    appInformation.setNewStationName(finalStationName);
                    AtStationActivity.txtStation.append(appInformation.getNewStationName());
                    AtStationActivity.stationAt=appInformation.getNewStationName();


                }
            });
        }
    }
}
