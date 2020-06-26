package com.example.petrolapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Forgot_password extends AppCompatActivity {
    //below is the Password regex pattern created and used to take sure the users' password meets basic password strength requirements
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    // global variables are declared
    private TextInputLayout textinputPetName;
    private TextInputLayout textinputMaidenName;
    private TextInputLayout textinputStreetName;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private ArrayList<String> list;
    private String SecInfo ="";
    private ArrayList<String> SecIn;
    public String allTheUsers= "";

    Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();//hides the name of the activity at the top

        textinputPetName = findViewById(R.id.text_input_PetName);
        textinputMaidenName = findViewById(R.id.text_input_MaidenName);
        textinputStreetName = findViewById(R.id.text_input_StreetName);
        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_Password);
        textInputConfirmPassword = findViewById(R.id.text_input_ConfirmPassword);
        SecInfo ="";
        getAllUsers();

    }

    /*this method gets a list of all the usernames of other users from the database and this list will
     * be used when validating the username entered by the user is taken by someone else or not
     * the basic okhttp request was used instead of using the Connection and RequestHandler classes
     * because there are no post parameters included for requesting all of the usernames
     * and felt that the basic okhttp request was more relevant and effective for this purpose*/
    public void getAllUsers(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2143116/getAllUsers.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String responseData = response.body().string();
                    try {
                        processUserNames(responseData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /*this method processes the json response when the request for all the usernames is made */
    public void processUserNames(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String users ="";
        String temp = "";

        for (int i = 0 ;i<jsonArray.length()-1; i++ ){
            JSONObject item = jsonArray.getJSONObject(i);
            users = users +item.getString("USERNAME")+",";
        }

        JSONObject last = jsonArray.getJSONObject(jsonArray.length()-1);
        temp = temp + last.getString("USERNAME");

        allTheUsers =users + temp;
        list = new ArrayList<>(Arrays.asList(allTheUsers.split(","))); // converts the single string of usernames into an Arraylist

    }

    //this method below checks whether or not the username the user has entered is taken by another user


    /* this method validates the username entered by the user
     * it will check if the field entered by the user is empty
     * it will check if the username entered does not succeed 15 characters, this was done to ensure that users
     * do not use overly complicated and long usernames that are hard for them to remember
     * Then it will also check if the username the user entered is already taken by another user.
     * If any of the requirements are not met a relevant error message will be displayed to the user
     * and a false value will be returned, but if they are all met a true value will be returned*/
    private boolean validateUsername() {
        String username = textInputUsername.getEditText().getText().toString().trim();

        if (username.isEmpty()) {
            textInputUsername.setError("Field can't be empty");
            return false;
        } else if (username.length() > 15) {
            textInputUsername.setError("Username is to long");
            return false;
        } else {
            textInputUsername.setError(null);
            return true;
        }
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

    /*this method validates the password
     * it will first check if the the field entered by the user is empty,
     * then it will check if the password meets the requirements of the regex PASSWORD_PATTERN
     * that was created. If either of the these points are not met a relevant error message will be
     * displayed and the method will return a false value, but if all the conditions are met then a
     * true boolean value will be returned*/
    private boolean validatePassword(){
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if(passwordInput.isEmpty()){
            textInputPassword.setError("Field can't be empty");
            return false;
        }else if(!PASSWORD_PATTERN.matcher(passwordInput).matches()){
            textInputPassword.setError("Password is to weak");
            return false;
        }else{
            textInputPassword.setError(null);
            return true;
        }

    }

    /* this method is used to confirm a users password and to ensure that the user did not make a
     * mistake the first time they entered their password. this method checks if the password
     * entered and the confirmed password entered matches, it also checks if the field is empty*/
    private boolean validateConfirmPassword(){
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        String confirmPasswordInput = textInputConfirmPassword.getEditText().getText().toString().trim();





        if (confirmPasswordInput.isEmpty()){
            textInputConfirmPassword.setError("Field can't be empty");
            return false;
        }else if(!confirmPasswordInput.equals(passwordInput)){
            textInputConfirmPassword.setError("Passwords do not match");
            return false;
        }else{
            textInputConfirmPassword.setError(null);
            return true;
        }

    }

    /*this method takes in the users password(after its been salted)
     * and hashes/encrypts it using md5 encryption and it will return a 32 character hash string
     * that will stored in the database*/
    public String hashPassword(byte[] md5Input){
        BigInteger md5Data = null;
        try {
            md5Data = new BigInteger(1,md5.encriptMD5(md5Input));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String md5Str = md5Data.toString(16);

        return md5Str;

    }

    /*this method generates a random integer that will used to help add a salt to the password so when
     * the password is encrypted the salt will be encrypted along with the password entered to add an
     * extra layer of security and uniqueness to the passwords' encryption.
     * The salt generated will also be stored in the database as it will be used when the user logs into
     * the app*/
    public String saltbae(){
        Random rand = new Random();
        int n = rand.nextInt();
        n += 1000;
        String salt = Integer.toString(n);
        return salt;
    }

    /*this method make the okhttp request to get the security questions answers for a specific user*/
    private void getSecQuestions(){
        String username = textInputUsername.getEditText().getText().toString().trim();
        ContentValues params = new ContentValues();
        params.put("USERNAME", username);

        c.fetchInfo(Forgot_password.this, "ForgotPassword",params, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                try {
                    processSecQuestions(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //processes th security questions requested by the okhttp request
    private void processSecQuestions(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String username = "";
        String answer1 = "";
        String answer2 = "";
        String answer3 ="";

        for (int i = 0 ;i<jsonArray.length(); i++ ){
            JSONObject item = jsonArray.getJSONObject(i);

            username = item.getString("USERNAME");
            answer1 = item.getString("Q1_ANSWER");
            answer2 = item.getString("Q2_ANSWER");
            answer3 = item.getString("Q3_ANSWER");
        }

        SecInfo = SecInfo + username+","+answer1+","+answer2+","+answer3;
        SecIn = new ArrayList<>(Arrays.asList(SecInfo.split(",")));

        checkSecQuestions();


    }

    //this method check is the security questions entered maths the security questions in the database for that user
    private void checkSecQuestions(){
        String maidenName = textinputMaidenName.getEditText().getText().toString().trim().toLowerCase();
        String petName = textinputPetName.getEditText().getText().toString().trim().toLowerCase();
        String streetName = textinputStreetName.getEditText().getText().toString().trim().toLowerCase();

        String username = textInputUsername.getEditText().getText().toString().trim();

        if(!list.contains(username)){
            textInputUsername.setError("This username is taken, please use a different one");
            return;
        }else if(list.contains(username)){
            if(!SecIn.contains(petName)){
                textinputPetName.setError("Pet Name is Incorrect");
                return;
            }else if(!SecIn.contains(maidenName)){
                textinputMaidenName.setError("Maiden Name is Incorrect");
                return;
            }else if(!SecIn.contains(streetName)){
                textinputStreetName.setError("Street Name is Incorrect");
                return;
            }else{
                UpdatePassword(); // only if the security questions are correct for the username provided, will the that users password and salt be updated
            }

        }


    }

    /*when all the security questions are corred for the username, will this method be run to update and change that usenames' password*/
    private void UpdatePassword(){

        //gets the user inputs
        String username = textInputUsername.getEditText().getText().toString().trim();
        String password =textInputPassword.getEditText().getText().toString().trim();


        String salt = "ABC"+saltbae();// the characters ABC is added to the front of the salt generated to add a small extra bit of security to the salt and thus the password
        String passwordTBH = password + salt;// the salt is added to the password the user entered so they can both be hashed and stored

        byte[] md5Input = passwordTBH.getBytes(); // the password+salt is converted in the byte array of the string(password+salt) which is required when hashing using md5
        String hashedInput = hashPassword(md5Input); // the password+salt gets hashed using md5

        //below updates the users password and salt
        ContentValues params = new ContentValues();
        params.put("USERNAME", username);
        params.put("PASSWORD",hashedInput);
        params.put("SALT",salt);




        c.fetchInfo(Forgot_password.this, "UpdatePassword",params, new RequestHandler() {
            @Override
            public void processResponse(String response) {
                goToLogin();
            }
        });

    }

    public void confirm(View v){
        if(!validateMaidenName() | !validatePetName() | !validateStreetName() | !validateUsername()| !validatePassword() | !validateConfirmPassword()){
            return;
        }else{
            getSecQuestions();
        }
    }

    private void goToLogin(){
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }

    public void gotoLogin(View view) {
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }
}
