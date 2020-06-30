package com.example.petrolapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    //below is the Password regex pattern created and used to take sure the users' password meets basic password strength requirements
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter is allowed
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //minimum of 8 characters
                    "$");


    // global variables are declared
    private TextInputLayout textInputFirstName;
    private TextInputLayout textInputLastName;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private ArrayList<String> list;
    private String allTheUsers ="";

    private boolean usernameNotAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputFirstName =findViewById(R.id.text_input_FirstName);
        textInputLastName = findViewById(R.id.text_input_LastName);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_Password);
        textInputConfirmPassword = findViewById(R.id.text_input_ConfirmPassword);

        getAllUsers();

    }

    /*this method gets a list of all the usernames of other users from the database and this list will
     * be used when validating the username entered by the user is taken by someone else or not
     * the basic okhttp request was used instead of using the Connection and RequestHandler classes
     * because there are no post parameters included for requesting all of the usernames
     * and felt that the basic okhttp request was more relevant and effective for this purpose*/
    public void getAllUsers(){
        Connection c = new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        ContentValues cv = new ContentValues();
        final boolean[] value = new boolean[1];

        c.fetchInfo(SignUp.this, "getAllUsers", cv, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                try {
                    processUserNames(response);

                } catch (JSONException e) {
                    e.printStackTrace();
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
            System.out.println("Username:  "+item.getString("USERNAME"));
        }

        JSONObject last = jsonArray.getJSONObject(jsonArray.length()-1);
        temp = temp + last.getString("USERNAME");

        allTheUsers =users + temp;
        list = new ArrayList<>(Arrays.asList(allTheUsers.split(","))); // converts the single string of usernames into an Arraylist

    }
    //this method below checks whether or not the username the user has entered is taken by another user
    private boolean checkUsernameAvailability(){
        String username = textInputUsername.getEditText().getText().toString().trim();
        usernameNotAvailable=list.contains(username);
        System.out.println("Contains "+usernameNotAvailable);
        if(list.contains(username)){
            textInputUsername.setError("This username is taken, please use a different one");
            return true;
        }
        return false;
    }

    /*a basic validation check is done to just check if a first name is entered*/
    private boolean validFirstName(){
        String firstnameInput = textInputFirstName.getEditText().getText().toString().trim();

        if(firstnameInput.isEmpty()){
            textInputFirstName.setError("Please enter your first name, this field cannot be empty");
            return false;
        }else{
            textInputFirstName.setError(null);
            return true;
        }
    }

    /*a basic validation check is done to just check if a first name is entered*/
    private boolean validLastName(){
        String lastnameInput = textInputLastName.getEditText().getText().toString().trim();

        if(lastnameInput.isEmpty()){
            textInputLastName.setError("Please enter your last name, this field cannot be empty");
            return false;
        }else{
            textInputLastName.setError(null);
            return true;
        }
    }

    /*this method validates the email address entered by the user
     * it will check if the field entered by the user is empty
     * it will also check if the email entered by the user meets all the requirements defined by the built in
     * email regex(i.e. has an @ symbol, a domain name and a domain type).
     * If any of the requirements are not met a relevant error message will be displayed to the user
     *  and a false value will be returned, but if they are all met a true value will be returned*/
    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Please enter a valid email address");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    /* this method validates the username entered by the user
     * it will check if the field entered by the user is empty
     * it will check if the username entered does not succeed 15 characters, this was done to ensure that users
     * do not use overly complicated and long usernames that are hard for them to remember
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
            textInputPassword.setError("Password is too weak");
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

    /*this method is run when the sign up button is clicked by the user*/
    public void signup(View v){

        //the input entered by the user gets validated and if any input is invalid the function escapes

        System.out.println("Value" +usernameNotAvailable);
        if(!validFirstName() | !validLastName() | !validateEmail() | !validateUsername() | !validatePassword() | !validateConfirmPassword() | checkUsernameAvailability()){
            return;
        }else{

            //user inputs are fetched converted into strings and trimmed ot remove leading and trailing spaces
            String firstnameInput = textInputFirstName.getEditText().getText().toString().trim();
            String lastnameInput = textInputLastName.getEditText().getText().toString().trim();
            String emailInput = textInputEmail.getEditText().getText().toString().trim();
            String username = textInputUsername.getEditText().getText().toString().trim();
            String password =textInputPassword.getEditText().getText().toString().trim();

            String salt = "ABC"+saltbae(); // the characters ABC is added to the front of the salt generated to add a small extra bit of security to the salt and thus the password
            String passwordTBH = password + salt; // the salt is added to the password the user entered so they can both be hashed and stored

            byte[] md5Input = passwordTBH.getBytes();  // the password+salt is converted in the byte array of the string(password+salt) which is required when hashing using md5
            String hashedInput = hashPassword(md5Input); // the password+salt gets hashed using md5

            //below, the user inputs as well as the hashed and salted password as well as the salt gets inserted into the database

            ContentValues params = new ContentValues();
            params.put("USERNAME",username);
            params.put("FIRST_NAME", firstnameInput);
            params.put("LAST_NAME", lastnameInput);
            params.put("EMAIL_ADDRESS",emailInput);
            params.put("PASSWORD",hashedInput);
            params.put("SALT",salt);

            Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");

            c.fetchInfo(SignUp.this, "NewUserTest",params, new RequestHandler() {
                @Override
                public void processResponse(String response) {
                    openNextPage();
                }
            });
        }
    }




    private void openNextPage(){
        String username = textInputUsername.getEditText().getText().toString().trim();
        Intent intent = new Intent(this, SecurityQuestions.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }



}