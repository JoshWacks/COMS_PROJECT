package com.example.petrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    //below is the Password regex pattern created and used to take sure the users' password meets basic password strength requirements
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //minimum of 8 characters
                    "$");

    //global variables are declared
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private TextView hashTextOutput;
    private String userInfo="";
    private String userPassHash = "";
    private String userSalt ="";
    private String allTheUsers ="";
    private ArrayList<String> list;


    Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/"); // the url connection was declared globally as it is used several times

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEmail = findViewById(R.id.text_input_email);
        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_Password);

        userInfo = "";
        userSalt ="";
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



    public void getSecurityInfo(){
        String usernameInput = textInputUsername.getEditText().getText().toString().trim();
        ContentValues params = new ContentValues();
        params.put("USERNAME",usernameInput);

        c.fetchInfo(Login.this, "CheckLogin",params, new RequestHandler() {
            @Override
            public void processResponse(String response) {

                try {
                    processUserInfo(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /*processes the user info of a certain user (their email, hashed salted password and their salt)*/
    public String processUserInfo(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);

        String email = "";
        String password= "";
        String salt="";
        ArrayList<String> userNames= new ArrayList<String>();
        for (int i = 0 ;i<jsonArray.length(); i++ ){
            JSONObject item = jsonArray.getJSONObject(i);
            email = item.getString("EMAIL_ADDRESS");
            password = item.getString("PASSWORD");
            salt = item.getString("SALT");
        }

        userInfo = userInfo+ email+","+ password+ " "+ salt;
        userPassHash =userPassHash+ password;
        userSalt = userSalt +salt;

        securityCheck(email, password, salt);
        return password + " "+ salt;
    }

    /*this method validates the email address entered by the user
     * it will check if the field entered by the user is empty
     * it will also check if the email entered by the user meets all the requirements defined by the built in
     * email regex(i.e. has an @ symbol, a domain name and a domain type).
     * If any of the requirements are not met a relevant error message will be displayed to the user
     *  and a false value will be returned, but if they are all met a true value will be returned*/
    private boolean validateEmail(){
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if(emailInput.isEmpty()){
            textInputEmail.setError("Field cant'be empty");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            textInputEmail.setError("Please enter a valid email address");
            return false;
        } else{
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
    private boolean validateUsername(){
        String username = textInputUsername.getEditText().getText().toString().trim();

        if(username.isEmpty()){
            textInputUsername.setError("Field cant'be empty");
            return false;
        }else if(username.length()>15){
            textInputUsername.setError("Username is to long");
            return false;
        }else {
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
            textInputPassword.setError("Field cant'be empty");
            return false;

        }else if(!PASSWORD_PATTERN.matcher(passwordInput).matches()){
            textInputPassword.setError("Password is to weak");
            return false;
        }else{
            textInputPassword.setError(null);
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

    /*this function does the "security checking", if a username is valid and exists, it checks
     * whether or not the email address, and if the password matches according to the username entered.
     * the parameters of this method are the email address, hashed salted password */
    private void securityCheck(String email, String dbPassword, String salt){
        //user info is fetched for security comparison
        String usernameInput = textInputUsername.getEditText().getText().toString().trim();
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        boolean correct = false;

        String toHash = passwordInput +salt; // the salt is added to the password the user entered so they can both be hashed and stored

        byte[] md5Input = toHash.getBytes(); // the password+salt is converted in the byte array of the string(password+salt) which is required when hashing using md5
        String hashtag =  hashPassword(md5Input);// the password+salt gets hashed using mb5

        //below, is the comparison of the user input with the security info according to the username provided
        if(!list.contains(usernameInput)) {
            textInputUsername.setError("Invalid user"); // the error message given is the username does no exists in the database
        }else if(list.contains(usernameInput)) {
            if (!email.equals(emailInput)) {
                textInputEmail.setError("Incorrect email");
                correct = false;
            }
            if (!dbPassword.equals(hashtag)) {
                textInputPassword.setError("Incorrect password");
                correct = false;
            }
            if(dbPassword.equals(hashtag) && email.equals(emailInput) ) {
                correct = true;
            }
        }
        //if all the user inputs are correct,valid and match according to the security info with respect to the username provided,
        //the user will gain access to the account in the app
        if(correct){
            gotoSuccessPage();
        }

    }

    /* then this the confirm button is clicked it will run the okhttp request methods and thus the security checking as well as do basic user input validation */
    public void confirmInput(View v){
        getAllUsers();
        if(!validateEmail() | !validateUsername() | !validatePassword()){
            return;
        }else{
            getSecurityInfo();
        }
    }

    public void gotoSuccessPage(){
        String username = textInputUsername.getEditText().getText().toString().trim();

        Intent intent = new Intent(this,MainMenuActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    public void gotoforgotpassword(View V){
        Intent intent = new Intent(this,Forgot_password.class);
        startActivity(intent);
    }

    public void gotosignupPage(View V){
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }


}
