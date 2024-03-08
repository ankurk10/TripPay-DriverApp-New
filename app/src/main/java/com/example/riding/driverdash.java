package com.example.riding;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class driverdash extends AppCompatActivity {
//    private CardView cardViewCardInfo;
    private SwipeRefreshLayout swipeRefreshLayout;
    String rate;
    private  TextView name,balance,battery,signal,lastupdated,km;
    private ImageView imageView_logout,imageView_reload;
    private ConstraintLayout rideHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverdash);

        //get intent
        Intent intent = getIntent();

        //initialize and add reference to variables
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        name = findViewById(R.id.name);
        balance=findViewById(R.id.balance);
        lastupdated=findViewById(R.id.lastupdated);
        signal=findViewById(R.id.signal);
        battery=findViewById(R.id.battery);
        km=findViewById(R.id.kmrr);

        //Transfers to the RideHistory Activity
        {
            rideHistory = findViewById(R.id.rideHistory);
            rideHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = intent.getStringExtra("email");
                    Intent intent_goToRideHistory = new Intent(driverdash.this, RideHistory.class);
                    intent_goToRideHistory.putExtra("email", email);
                    startActivity(intent_goToRideHistory);
                }
            });
        }

        //Performs Logout operation
        {
            imageView_logout = (ImageView) findViewById(R.id.logout);
            imageView_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLogoutConfirmationDialog();
                }
            });
        }

        //Fetch and update data when swiped from top
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String userEmail = intent.getStringExtra("email");
                fetchLatestData(userEmail);
                Toast.makeText(driverdash.this, "Data upated sucessfully", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Performs reload and fetch data from server
        {
            imageView_reload = (ImageView) findViewById(R.id.reload);
            imageView_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userEmail = intent.getStringExtra("email");
                    fetchLatestData(userEmail);
                    Toast.makeText(driverdash.this, "Data upated sucessfully", Toast.LENGTH_SHORT).show();
                }
            });
        }


        //Fetch data while loading the activity

        if (intent != null) {
            int userId = intent.getIntExtra("userid", -1); // -1 is the default value if the key is not found
            String userEmail = intent.getStringExtra("email");
            //            Toast.makeText(this, "id : "+userId, Toast.LENGTH_SHORT).show();
            //            Toast.makeText(this, "email : "+userEmail, Toast.LENGTH_SHORT).show();
            fetchLatestData(userEmail);
        }
    }

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
                            String cardBalance = response.getString("card_balance");
                            String signall = response.getString("signal");
                            String batteryy = response.getString("bat");
                            String kmrate = response.getString("kmrate");
                            rate = kmrate;
                            int lp = response.getInt("logtime");
                            name.setText(hname);
                            long currentTime = System.currentTimeMillis() / 1000;

                            // Calculate time difference
                            long differenceInSeconds = currentTime - lp;
                            lastupdated.setText(timemanager(differenceInSeconds));

                            signal.setText("Signal\n" + signall + "%");
                            battery.setText("Battery\n" + batteryy + "%");
                            km.setText("Price\n" + kmrate + "/km");
                            balance.setText("₹"+cardBalance);

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

    @Override
    public void onBackPressed() {

        showLogoutConfirmationDialog();
        super.onBackPressed();
    }

    //provides dialogue when logout icon is pressed
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Okay, perform logout and move to login activity
                        performLogout();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel, dismiss the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //completes the logout operation
    private void performLogout() {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        finish();
    }
    private String timemanager(long differenceInSeconds) {
        String message="";
        if (differenceInSeconds < 60) {
            // Few seconds ago
            message = "Last Updated : " + differenceInSeconds + "Few seconds ago";
        } else if (differenceInSeconds < 3600) {
            // X minutes ago
            long minutes = TimeUnit.SECONDS.toMinutes(differenceInSeconds);
            message = "Last Updated : " + minutes + " minutes ago";
        } else if (differenceInSeconds < 86400) {
            // X hours ago
            long hours = TimeUnit.SECONDS.toHours(differenceInSeconds);
            message = "Last Updated : " + hours + " hours ago";
        } else {
            // X days ago
            long days = TimeUnit.SECONDS.toDays(differenceInSeconds);
            message = "Last Updated : " + days + " days ago";
        }
        return message;
    }
}