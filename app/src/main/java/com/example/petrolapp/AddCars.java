package com.example.petrolapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class AddCars extends AppCompatActivity {
    static String username;
    String lisc_plate,brand,model,year;
    String allTheLiscPlates;
    ArrayList<String> Lisc_list;

    private Spinner spinner1,spinner2,spinner3;
    private static final String[] allottedBrands = {"Nissan", "Toyota", "VolksWagen","Ford","BMW","Mercedes","Audi"};
    private static final String[] allottedModelsNissan={"Coupe 370Z","GT-R","Navara","NP300 HardBody","Micra","Micra Active","Qashqai","NV 350","Almera","NP200","NP200 HardBody","Patrol","XTrail"};
    private static final String[] allottedModelsToyota={"86","Avanza" ,"Aygo","Corolla","Corolla Hatch","Corolla Quest","Etios HAtch","Etios Sedan","Prius","Supra","Yaris","C_HR","FJ Cruiser","Fortuner","Land Cruiser 200","Land Cruiser Prado","Rav4","Rush","Avanza Panel Van","Hiace","Hiace Ses'fikile","Hilux Double Cab","Hilux single Cab","Hilux Extra Cab","Land Cruiser 76","Land Cruiser 79","Quantum Bus","Quatum Panel Van","Aygo",};
    private static final String[] allottedModelsVolkswagen={"Amarok","Golf","Polo","Polo Vivo","Up!","T Cross","Caddy","California","Caravelle","Kombi","Transporter","Tiguan","Tiguan AllSpace","Touareg"};
    private static final String[] allottedModelsFord={"Figo","Fiesta","Mustang","EcoSport","Everest","Kuga","Ranger","Trasit Custom","Transit single Chassis Cab","Transit Van","Tourneo Custom","Raptor"};
    private static final String[] allottedModelsBMW={"220i","M240i","M2","218i","220d","M235i","318i ","320i","330I","320d","330d","420i","440i","420d","m4","540i","520d","530d"};
    private static final String[] allottedModelsMercedes={"A Class","B Class","C Class","CLA","CLS","E Class","G Class","GLA","GLB","GLC","GLE","GLS","S Class","SL","V Class","Vito","X Class"};
    private static final String[] allottedModelsAudi={"A1","S1","A3","S3","RS3","A4","S4","RS4","A5","S5","RS5","A6","S6","RS6","A7","S7","RS7","Q2","Q3","RS Q3","Q5","SQ5","Q7","Q8","TT",};
    private static final String[] allottedYears = {"2010", "2011", "2012","2013","2014","2015","2016","2017","2018","2019","2020",};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

       username=appInformation.getUsername();

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


        //determining year
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(AddCars.this,
                android.R.layout.simple_spinner_item, allottedYears);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        //determining brand
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(AddCars.this,
                android.R.layout.simple_spinner_item, allottedBrands);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedBrand= (String) spinner1.getSelectedItem();
                switch (selectedBrand){
                    case "Nissan":        creatingDropBox(allottedModelsNissan); break;
                    case "Toyota":        creatingDropBox(allottedModelsToyota); break;
                    case "Volkswagen":        creatingDropBox(allottedModelsVolkswagen); break;
                    case "Ford":        creatingDropBox(allottedModelsFord); break;
                    case "BMW":        creatingDropBox(allottedModelsBMW); break;
                    case "Mercedes":        creatingDropBox(allottedModelsMercedes); break;
                    case "Audi":        creatingDropBox(allottedModelsAudi); break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    public void creatingDropBox(String [] input) {
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AddCars.this,
                android.R.layout.simple_spinner_item, input);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

    }
    public void doAdd(View view) {
        EditText editLisc = (EditText) findViewById(R.id.editLiscPlate);
        lisc_plate = editLisc.getText().toString().toUpperCase();
        brand= (String) spinner1.getSelectedItem();
        model= (String) spinner2.getSelectedItem();
        year= (String) spinner3.getSelectedItem();

        InsertData(lisc_plate, brand, model, year);
    }

    public void InsertData(final String lisc_plate, final String brand, final String model, final String year){

        ContentValues params = new ContentValues();
        params.put("LISCENCE_PLATE", lisc_plate);
        params.put("USERNAME", username);
        params.put("CAR_BRAND", brand);
        params.put("CAR_MODEL", model);
        params.put("CAR_YEAR", year);

        if(!checkLisc(lisc_plate) && !isempty(lisc_plate,model,brand,year) && validateL(lisc_plate) ){
            Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
            c.fetchInfo(AddCars.this, "NewCar",params, new RequestHandler() {
                @Override
                public void processResponse(String response) {
                    Toast.makeText(AddCars.this, "Data Submitted Successfully", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    private boolean isempty(String lisc_plate, String model, String brand, String year) {
        if(lisc_plate.isEmpty()){
            Toast.makeText(AddCars.this, "Liscence plate is empty", Toast.LENGTH_LONG).show();
            Toast.makeText(AddCars.this, "please enter valid inputs", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean checkLisc(String input) {
        if(Lisc_list.contains(input)){
            Toast.makeText(AddCars.this, "Liscence plate already exists", Toast.LENGTH_LONG).show();
            return true;
        } return false;
    }

    public boolean validateL(String input){
        String [] spl=input.split(" ");   //spl=split liscence plate
        boolean temp=false;
        switch(spl[spl.length-1]){
            case "WP": temp=checkTypes(spl,"WP");  break;
            case "ZN": temp=checkTypes(spl,"ZN");  break;
            case "MP": temp=checkTypes(spl,"MP");  break;
            case "EC": temp=checkTypes(spl,"EC");  break;
            case "L":  temp=checkTypes(spl,"L");  break;
            case "GP": temp=checkTypes(spl,"GP");  break;
            case "FS": temp=checkTypes(spl,"FS");  break;
            case "NW": temp=checkTypes(spl,"NW");  break;
            default: Toast.makeText(AddCars.this, "Enter Valid Liscence Plate number", Toast.LENGTH_LONG).show();

        }
        return temp;


    }
    public boolean checkTypes(String[] spl,String input){

        if(Objects.equals(spl[spl.length-1], input)) {
            System.out.println(input);

            if ( ( (Pattern.matches("[a-zA-Z]+", spl[0]) && spl[0].length() == 3) && (spl[1].matches("^[0-9]*$") && spl[1].length() == 3) ) || (Pattern.matches("[a-zA-Z]+", spl[0]) && spl[0].length() < 7)) {

                return true;            // validating for types xxx 000 GP AND custom
            } else if ((Pattern.matches("[a-zA-Z]+", spl[0]) && spl[0].length() == 2) && (spl[1].matches("^[0-9]*$") && spl[1].length() == 2) && (Pattern.matches("[a-zA-Z]+", spl[2]) && spl[2].length() == 2)) {

                return true;
            }
            else{
                Toast.makeText(AddCars.this, "Enter Valid Liscence Plate number", Toast.LENGTH_LONG).show();
            }
        }

        return false;
    }






    public void bttnBack(View view) {
        Intent myIntent = new Intent(AddCars.this, MainMenuActivity.class);
        finish();//go back to main menu
        startActivity(myIntent);
    }
}