package com.example.riding;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class UserDetails extends AppCompatActivity {

    private TextView name,ssid,cardId,balance,carrier,kmrr,last_update;
    private String email;
    private ImageView back,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //initialize
        name = findViewById(R.id.name);
        ssid = findViewById(R.id.ssid);
        cardId = findViewById(R.id.cardid);
        balance = findViewById(R.id.detail_balance);
        carrier = findViewById(R.id.detail_carrier);
        kmrr = findViewById(R.id.detail_kmrr);
        last_update = findViewById(R.id.detail_lastUpdate);
        back = findViewById(R.id.back);
        logout = findViewById(R.id.logout);

        //add activity to images
        addAcitivity_to_Images();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        fetchLatestData(email);
    }

    //add action listener to top 2 images
    private  void addAcitivity_to_Images() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_dashboard = new Intent(UserDetails.this, driverdash.class);
                intent_dashboard.putExtra("email",email);
                startActivity(intent_dashboard);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_login = new Intent(UserDetails.this, login.class);
                intent_login.putExtra("email",email);
                startActivity(intent_login);
            }
        });

    }

    //fetch the data for the page
    private void fetchLatestData(String email) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String ENDPOINT_URL="https://trippay.in/latestData.php?email="+email;

        //   Toast.makeText(this, ""+ENDPOINT_URL, Toast.LENGTH_SHORT).show();

        // Create a JsonObjectRequest to fetch JSON response
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                ENDPOINT_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the JSON response
                        // Toast.makeText(driverdash.this, "response : ", Toast.LENGTH_SHORT).show();
                        try {
                            String hname = response.getString("name");
                            String carrierS = response.getString("carrier");
                            String balanceS = response.getString("card_balance");
                            String kmrate = response.getString("kmrate");
                            String cardIdS = response.getString("card_id");
                            String ssiddS = response.getString("ssid");

                            //manage lastupdate
                            int lp = response.getInt("logtime");
                            long currentTime = System.currentTimeMillis() / 1000;
                            // Calculate time difference
                            long differenceInSeconds = currentTime - lp;
                            last_update.setText(timemanager(differenceInSeconds));

                            name.setText(hname);
                            carrier.setText(carrierS);
                            kmrr.setText(kmrate);
                            cardId.setText(cardIdS);
                            ssid.setText(ssiddS);
                            balance.setText(balanceS);
                            } catch (JSONException e) {

                            e.printStackTrace();
                            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e(TAG, "Error fetching JSON response: " + error.getMessage());
                        //Toast.makeText(driverdash.this, "ERROR "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
    private String timemanager(long differenceInSeconds) {
        String message="";
        if (differenceInSeconds < 60) {
            // Few seconds ago
            message = differenceInSeconds + "Few seconds ago";
        } else if (differenceInSeconds < 3600) {
            // X minutes ago
            long minutes = TimeUnit.SECONDS.toMinutes(differenceInSeconds);
            message = minutes + " minutes ago";
        } else if (differenceInSeconds < 86400) {
            // X hours ago
            long hours = TimeUnit.SECONDS.toHours(differenceInSeconds);
            message =  hours + " hours ago";
        } else {
            // X days ago
            long days = TimeUnit.SECONDS.toDays(differenceInSeconds);
            message = days + " days ago";
        }
        return message;
    }
}