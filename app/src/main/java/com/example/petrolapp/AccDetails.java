package com.example.petrolapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccDetails extends AppCompatActivity {
    private static final String username=appInformation.getUsername();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_details);

        ContentValues cv=new ContentValues();
        cv.put("USERNAME",username);
        Connection c=new Connection("https://lamp.ms.wits.ac.za/home/s2143116/");
        c.fetchInfo(AccDetails.this, "fetching_username",cv, new RequestHandler() {
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
        JSONArray all = new JSONArray(json);
        for (int i = 0; i < all.length(); i++) {
            JSONObject item = all.getJSONObject(i);

            TextView username = (TextView) findViewById(R.id.textView1);
            TextView f_name = (TextView) findViewById(R.id.textView2);
            TextView l_name = (TextView) findViewById(R.id.textView3);
            TextView e_add = (TextView) findViewById(R.id.textView4);

            String s_username = item.getString("USERNAME");
            String s_f_name = item.getString("FIRST_NAME");
            String s_l_name = item.getString("LAST_NAME");
            String s_e_add = item.getString("EMAIL_ADDRESS");

            username.setText(s_username);
            f_name.setText(s_f_name);
            l_name.setText(s_l_name);
            e_add.setText(s_e_add);
        }
    }



    public void goBack1(View view) {

        Intent myIntent = new Intent(AccDetails.this, MainMenuActivity.class);        //go back to main menu
        startActivity(myIntent);

    }
}
