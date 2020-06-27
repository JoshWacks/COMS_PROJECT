package com.example.petrolapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AddCars extends AppCompatActivity {

    private static final String username=appInformation.getUsername();

    private String lisc_plate,brand,model,year;
    private String allTheLiscPlates;
    private ArrayList<String> Lisc_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        ContentValues cv=new ContentValues();
        cv.put("USERNAME",username);
        Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        c.fetchInfo(AddCars.this, "get_ALL_CARS",cv, new RequestHandler() {
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

    private void processJSON (String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String lisc ="";
        String temp ="";

        for (int i = 0 ;i<jsonArray.length()-1; i++ ){
            JSONObject item = jsonArray.getJSONObject(i);
            lisc = lisc +item.getString("LISCENCE_PLATE")+",";

        }

        //puts the liscence plates in the list
        JSONObject last = jsonArray.getJSONObject(jsonArray.length()-1);
        temp = temp + last.getString("LISCENCE_PLATE");
        allTheLiscPlates =lisc + temp;
        Lisc_list = new ArrayList<>(Arrays.asList(allTheLiscPlates.split(",")));
    }

    public void doAdd(View view) {

        GetData();
        InsertData(lisc_plate, brand, model, year);
    }

    private void GetData() {

        EditText editLisc = (EditText) findViewById(R.id.editLiscPlate);
        EditText editBrand = (EditText) findViewById(R.id.editBrand);
        EditText editModel = (EditText) findViewById(R.id.editModel);
        EditText editYear = (EditText) findViewById(R.id.editYear);

        lisc_plate = editLisc.getText().toString();
        brand = editBrand.getText().toString();
        model = editModel.getText().toString();
        year = editYear.getText().toString();
    }


    public void InsertData(final String lisc_plate, final String brand, final String model, final String year){

        ContentValues params = new ContentValues();
        params.put("LISCENCE_PLATE", lisc_plate);
        params.put("USERNAME", username);
        params.put("CAR_BRAND", brand);
        params.put("CAR_MODEL", model);
        params.put("CAR_YEAR", year);

        if(checkLisc(lisc_plate)==0 ){
            System.out.println("this is complete bull");
            Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
            c.fetchInfo(AddCars.this, "NewCar",params, new RequestHandler() {
                @Override
                public void processResponse(String response) {
                    Toast.makeText(AddCars.this, "Data Submit Successfully", Toast.LENGTH_LONG).show();

                }
            });
        }
        else{
            System.out.println("this is huge bull");
            Toast.makeText(AddCars.this, "Liscence plate already exists", Toast.LENGTH_LONG).show();

        }

    }

    private int checkLisc(String input) {
        System.out.println("this is bull");
        if(Lisc_list.contains(input)){
            System.out.println("this is absolute bull");
            //Toast.makeText(AddCars.this, "Liscence plate already exists", Toast.LENGTH_LONG).show();
            return 1;
        } else{
            return 0;
        }
    }


    public void bttnBack(View view) {
        Intent myIntent = new Intent(AddCars.this, MainMenuActivity.class);        //go back to main menu

        startActivity(myIntent);
    }
}