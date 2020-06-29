package com.example.petrolapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AddCards extends AppCompatActivity {
    static String username;
    String company,card_number,exp_date,cardid;
    String allTheCardNos;
    ArrayList<String> Card_Nos;

    private Spinner spinner1,spinner2,spinner3;
    private static final String[] allottedCompanys = {"edgars", "woolworths", "shell","discovery","dis_chem","clicks"};
    private static final String[] allottedMonths = {"01", "02", "03","04","05","06","07","08","09","10","11","12"};
    private static final String[] allottedYears = {"2021", "2022", "2023","2024","2025","2026","2027","2028","2029","2030"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cards);

        username=appInformation.getUsername();

        ContentValues cv=new ContentValues();
        cv.put("USERNAME",username);
        Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        c.fetchInfo(AddCards.this, "get_ALL_CARDS",cv, new RequestHandler() {
            @Override
            public void processResponse(String response)  {
                try {
                    processJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        //determining Comapny
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(AddCards.this,
                android.R.layout.simple_spinner_item, allottedCompanys);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        //determining month
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AddCards.this,
                android.R.layout.simple_spinner_item, allottedMonths);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        //determining year
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(AddCards.this,
                android.R.layout.simple_spinner_item, allottedYears);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);
    }

    private void processJSON (String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String card_number ="";
        String temp ="";

        for (int i = 0 ;i<jsonArray.length()-1; i++ ){
            JSONObject item = jsonArray.getJSONObject(i);
            card_number = card_number +item.getString("CARD_NUMBER")+",";

        }

        //puts the liscence plates in the list
        JSONObject last = jsonArray.getJSONObject(jsonArray.length()-1);
        temp = temp + last.getString("CARD_NUMBER");
        allTheCardNos =card_number + temp;
        Card_Nos = new ArrayList<>(Arrays.asList(allTheCardNos.split(",")));

    }

    public void doAdd(View view) {

        GetData();
    }
    private void GetData() {
        String month,year;
        EditText Card_number = (EditText) findViewById(R.id.editCard_Number);
        card_number = Card_number.getText().toString();

        company= (String) spinner1.getSelectedItem();
        month= (String) spinner2.getSelectedItem();
        year= (String) spinner3.getSelectedItem();
        exp_date = year+"-"+month+"-01";


        InsertData(company,card_number,exp_date);

    }

    public void InsertData(final String company, final String card_number, final String exp_date){

        ContentValues params = new ContentValues();
        params.put("COMPANY", company);
        params.put("USERNAME", username);
        params.put("CARD_NUMBER", card_number);
        params.put("EXPIRY_DATE", exp_date);


        if(!checkCardNo(card_number) &&!isempty(company,card_number,exp_date) && validateCN(card_number)){

            Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
            c.fetchInfo(AddCards.this, "NewCard",params, new RequestHandler() {
                @Override
                public void processResponse(String response) {
                    Toast.makeText(AddCards.this, "Data Submitted Successfully", Toast.LENGTH_LONG).show();

                }
            });
        }



    }
    private boolean isempty(String company, String card_number, String exp_date) {
        if(card_number.isEmpty()) {
            Toast.makeText(AddCards.this, "card number is empty", Toast.LENGTH_LONG).show();
            Toast.makeText(AddCards.this, "please enter valid inputs", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean checkCardNo(String input) {
        if(Card_Nos.contains(input)){
            Toast.makeText(AddCards.this, "card number already exists", Toast.LENGTH_LONG).show();
            return true;
        } else{
            return false;
        }
    }

    public boolean validateCN(String input){
        String [] spl=input.split(" ");
        if(spl.length==4 && (spl[0].matches("^[0-9]*$") && spl[0].length() == 4) && (spl[1].matches("^[0-9]*$") && spl[1].length() == 4) &&(spl[2].matches("^[0-9]*$") && spl[2].length() == 4) && (spl[3].matches("^[0-9]*$") && spl[3].length() == 4)){
            return true;
        }
        Toast.makeText(AddCards.this, "Enter Valid Card number", Toast.LENGTH_LONG).show();
        return false;
    }

    public void bttnBack(View view) {
        Intent myIntent = new Intent(AddCards.this, MainMenuActivity.class);        //go back to main menu

        startActivity(myIntent);
    }

}