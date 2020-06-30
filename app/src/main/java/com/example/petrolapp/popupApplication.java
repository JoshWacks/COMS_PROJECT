package com.example.petrolapp;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.mrntlu.toastie.Toastie;

import java.util.ArrayList;


public class popupApplication extends Activity {
    //Class to handle all the popups in the app
    private TextView txtH1;
    private TextView txtH2;
    private TextView txtH3;
    private TextView txtH4;

    private TextView txtData1;
    private TextView txtData2;
    private TextView txtData3;

    private TextView txtCarChoice1;
    private TextView txtCarChoice2;
    private TextView txtCarChoice3;

    private String activity;

    private TextView txtBottomBorder ;
    private TextView txtStationName ;

    private Button btnPopDone;

    Double x_co;
    Double y_co;

    private static StationsEfficiencyActivity stationsEfficiencyActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        stationsEfficiencyActivity=new StationsEfficiencyActivity();

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        txtH1 = findViewById(R.id.txtPopHeading1);
        txtH2 = findViewById(R.id.txtPopHeading2);
        txtH3 = findViewById(R.id.txtPopHeading3);
        txtH4 = findViewById(R.id.txtPopHeading4);


        txtData1 = findViewById(R.id.txtPopData1);
        txtData2 = findViewById(R.id.txtPopData2);
        txtData3 = findViewById(R.id.txtPopData3);

        txtCarChoice1=findViewById(R.id.txtCarChoice1);
        txtCarChoice2=findViewById(R.id.txtCarChoice2);
        txtCarChoice3=findViewById(R.id.txtCarChoice3);

        txtBottomBorder = findViewById(R.id.txtBottomBorder);
        txtStationName = findViewById(R.id.etxtName);

        btnPopDone=findViewById(R.id.btnPopDone);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        activity = appInformation.getActivity();//checks which activity is calling it

        double hDim;
        double wDim;
        switch (activity) {
            case "FillUps":
                fillUpsPopUp(bundle);
                hDim = 0.16;
                wDim = 0.9;
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
                break;
            case "AtStation":
                hDim = 0.18;
                wDim = 1;
                atStationPopup(bundle);
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
                break;
            case "CarEff":
                hDim = 0.2;
                wDim = 0.7;
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
                carEffPopUp(bundle);
                break;
            case "selectCarEff":
                hDim = 0.25;
                wDim = 1;
                getWindow().setLayout((int) (width * wDim), (int) (height * hDim));
                selectCarPopup();

                break;
        }


    }

    public void fillUpsPopUp(Bundle bundle) {
        //Method for when the fillups activity is calling the popup window
        TextView txtFound = findViewById(R.id.txtNotFound);

        btnPopDone.setVisibility(View.GONE);
        txtH4.setVisibility(View.GONE);
        txtStationName.setVisibility(View.GONE);
        txtCarChoice1.setVisibility(View.GONE);
        txtCarChoice2.setVisibility(View.GONE);
        txtCarChoice3.setVisibility(View.GONE);

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
        txtCarChoice1.setVisibility(View.GONE);
        txtCarChoice2.setVisibility(View.GONE);
        txtCarChoice3.setVisibility(View.GONE);
        txtBottomBorder.setVisibility(View.GONE);

        btnPopDone.setVisibility(View.VISIBLE);
        txtStationName.setVisibility(View.VISIBLE);
        txtH4.setVisibility(View.VISIBLE);

    }

    private void selectCarPopup(){
        txtH1.setVisibility(View.GONE);
        txtH2.setVisibility(View.GONE);
        txtH3.setVisibility(View.GONE);
        txtData1.setVisibility(View.GONE);
        txtData2.setVisibility(View.GONE);
        txtData3.setVisibility(View.GONE);
        txtStationName.setVisibility(View.GONE);
        btnPopDone.setVisibility(View.GONE);
        txtBottomBorder.setVisibility(View.GONE);

        txtH4.setText("Please select a car below first");
        txtH4.setVisibility(View.VISIBLE);

        txtCarChoice1.setVisibility(View.VISIBLE);
        txtCarChoice2.setVisibility(View.VISIBLE);
        txtCarChoice3.setVisibility(View.VISIBLE);



        ArrayList<CarType>userCars=stationsEfficiencyActivity.getUserCars();


        for(int i=0;i<userCars.size();i++){
            CarType ct=userCars.get(i);
            if(i==0){
                txtCarChoice1.setText(ct.getBrand()+"  "+ct.getModel()+"  "+ct.getYear()+"\t \t ,"+ct.getLiscence_plate());
            }
            else if(i==1){
                txtCarChoice2.setText(ct.getBrand()+"  "+ct.getModel()+"  "+ct.getYear()+"\t \t ,"+ct.getLiscence_plate());
            }
            else if(i==2){
                txtCarChoice3.setText(ct.getBrand()+"  "+ct.getModel()+"  "+ct.getYear()+"\t \t ,"+ct.getLiscence_plate());
            }
        }

    }

    public void onCarSelected(View view){
        TextView temp= (TextView) view;
        temp.setBackgroundColor(Color.CYAN);



        String txt= (String) temp.getText();
        int pos=txt.indexOf(",");
        String plate=txt.substring(pos+1);
        appInformation.setLiscence_plate(plate);
        finish();


    }


    public void carEffPopUp(Bundle bundle) {
        btnPopDone.setVisibility(View.GONE);
        txtStationName.setVisibility(View.GONE);
        txtBottomBorder.setVisibility(View.GONE);
        txtH4.setVisibility(View.GONE);

        txtCarChoice1.setVisibility(View.GONE);
        txtCarChoice2.setVisibility(View.GONE);
        txtCarChoice3.setVisibility(View.GONE);
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
            Toastie.error(getApplicationContext(),"Please enter a station name first",Toast.LENGTH_SHORT).show();
        }
        else {

            Connection connection = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
            ContentValues cv = new ContentValues();
            stationName=stationName+"_"+x_co+"_"+y_co;//We ensure that station name is unique by using the x an u co-ords
            cv.put("STATION", stationName);
            cv.put("X_CO", x_co);
            cv.put("Y_CO", y_co);

            String finalStationName = stationName;

            connection.fetchInfo(popupApplication.this, "insert_STATION", cv, new RequestHandler() {
                @Override
                public void processResponse(String response) {
                    Toastie.success(getApplicationContext(),"Successfully Added Station \nPress anywhere else to close this",Toast.LENGTH_LONG).show();

                    appInformation.setNewStationName(finalStationName);
                    AtStationActivity.txtStation.append(appInformation.getNewStationName());
                    AtStationActivity.stationAt=appInformation.getNewStationName();


                }
            });
        }
    }
}
