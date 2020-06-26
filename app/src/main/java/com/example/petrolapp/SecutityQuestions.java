package com.example.petrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class SecutityQuestions extends AppCompatActivity {
    private TextInputLayout textinputPetName;
    private TextInputLayout textinputMaidenName;
    private TextInputLayout textinputStreetName;
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secutity_questions);

        textinputPetName = findViewById(R.id.text_input_PetName);
        textinputMaidenName = findViewById(R.id.text_input_MaidenName);
        textinputStreetName = findViewById(R.id.text_input_StreetName);

        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");


    }

    //this method validates the pet name and ensures the field is not empty
    public boolean validatePetName(){
        String petName = textinputPetName.getEditText().getText().toString().trim();

        if(petName.isEmpty()){
            textinputPetName.setError("Field can't be empty");
            return false;
        }else{
            textinputPetName.setError(null);
            return true;
        }

    }

    //this method validates the maiden name and ensures the field is not empty
    public boolean validateMaidenName(){
        String maidenName = textinputMaidenName.getEditText().getText().toString().trim();

        if(maidenName.isEmpty()){
            textinputMaidenName.setError("Field can't be empty");
            return false;
        }else{
            textinputMaidenName.setError(null);
            return true;
        }

    }

    //this method validates the street name and ensures the field is not empty
    public boolean validateStreetName(){
        String streetName = textinputStreetName.getEditText().getText().toString().trim();

        if(streetName.isEmpty()){
            textinputStreetName.setError("Field can't be empty");
            return false;
        }else{
            textinputStreetName.setError(null);
            return true;
        }

    }

    public void confirm(View v){

        String maidenName = textinputMaidenName.getEditText().getText().toString().trim().toLowerCase();
        String petName = textinputPetName.getEditText().getText().toString().trim().toLowerCase();
        String streetName = textinputStreetName.getEditText().getText().toString().trim().toLowerCase();

        if(!validateMaidenName() | !validatePetName() | !validateStreetName()){
            return;
        }else{
            ContentValues params = new ContentValues();
            params.put("USERNAME", username);
            params.put("Q1_ANSWER", petName);
            params.put("Q2_ANSWER", maidenName);
            params.put("Q3_ANSWER",streetName);

            Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

            c.fetchInfo(SecutityQuestions.this, "SecQuestions",params, new RequestHandler() {
                @Override
                public void processResponse(String response) {

                    gotoLogin();
                }
            });

        }

    }

    public void gotoLogin(){
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }

}
