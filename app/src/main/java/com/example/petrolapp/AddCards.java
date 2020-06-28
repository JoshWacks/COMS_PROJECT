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

public class AddCards extends AppCompatActivity {

    private static String username;
    String company,card_number,exp_date,cardid;
    String allTheCardNos;
    ArrayList<String> Card_Nos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cards);
        username=appInformation.getUsername() ;

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
        InsertData(company,card_number,exp_date);
    }
    private void GetData() {

        EditText Comapny = (EditText) findViewById(R.id.editCompany);
        EditText Card_number = (EditText) findViewById(R.id.editCard_Number);
        EditText Expiry_date = (EditText) findViewById(R.id.editExpiry_Date);

        company = Comapny.getText().toString();
        company.toLowerCase();
        card_number = Card_number.getText().toString();
        exp_date = Expiry_date.getText().toString();
    }

    public void InsertData(final String company, final String card_number, final String exp_date){

        ContentValues params = new ContentValues();
        params.put("COMPANY", company);
        params.put("USERNAME", username);
        params.put("CARD_NUMBER", card_number);
        params.put("EXPIRY_DATE", exp_date);


        if(checkCardNo(card_number)==0 ){
            Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
            c.fetchInfo(AddCards.this, "NewCard",params, new RequestHandler() {
                @Override
                public void processResponse(String response) {
                    Toast.makeText(AddCards.this, "Data Submit Successfully", Toast.LENGTH_LONG).show();

                }
            });
        }
        else{
            Toast.makeText(AddCards.this, "Card Number already exists", Toast.LENGTH_LONG).show();

        }

    }
    private int checkCardNo(String input) {
        if(Card_Nos.contains(input)){
            return 1;
        } else{
            return 0;
        }
    }
    public void bttnBack(View view) {
        Intent myIntent = new Intent(AddCards.this, MainMenuActivity.class);        //go back to main menu

        startActivity(myIntent);
    }

}