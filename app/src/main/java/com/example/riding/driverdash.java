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
import android.widget.Button;
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
    private String rate;
    private TextView name,balance,battery,signal,lastupdated,km,carrier,totaldistance;
    private ImageView imageView_logout,imageView_reload,imageView_photo,imageView_signal,imageView_battery;
    private Button withdraw;
    private ConstraintLayout rideHistory,rewards,chatSupport,withdrawal_history,refer,help,settings,share;

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
        carrier = findViewById(R.id.carrier);
        totaldistance = findViewById(R.id.distanceTravelled);
        imageView_signal = findViewById(R.id.signalimage);
        imageView_battery = findViewById(R.id.batteryImage);
        String email  =  intent.getStringExtra("email");

        //Makes the bottom 8 buttons alive and adds clicklistener to move to new activity
        updateButtonsActivity(email);

        //Transfer to the WithdrawBalanceActivity ( on clicking the withdraw button)
        {
            withdraw = findViewById(R.id.withdraw);
            withdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_gotoWithdrawBalanceActivity = new Intent(driverdash.this, WithdrawBalanceActivity.class);
                    intent_gotoWithdrawBalanceActivity.putExtra("email", email);
                    startActivity(intent_gotoWithdrawBalanceActivity);
                }
            });
        }
        //Transfers to userDetails Activity (on tapping the user photo icon)
        {
            imageView_photo =  findViewById(R.id.photo);
            imageView_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_gotoUserDetails = new Intent(driverdash.this, UserDetails.class);
                    intent_gotoUserDetails.putExtra("email", email);
                    startActivity(intent_gotoUserDetails);
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
    private void updateButtonsActivity(String email) {
        //Share the App Activity
        {
            share = findViewById(R.id.shareLayout);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    //Here the URL to the app needs to be added
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "URL");
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                }
            });
        }

        //Transfers to the RideHistory Activity (on clicking the ridehistory icon)
        {
            rideHistory = findViewById(R.id.rideHistory);
            rideHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_goToRideHistory = new Intent(driverdash.this, RideHistory.class);
                    intent_goToRideHistory.putExtra("email", email);
                    startActivity(intent_goToRideHistory);
                }
            });
        }

        //Transfers to the ChatSupport Activity (on clicking the chat Support icon)
        {
            chatSupport = findViewById(R.id.chatSupportLayout);
            chatSupport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_gotoChatSupport = new Intent(driverdash.this, ChatSupport.class);
                    intent_gotoChatSupport.putExtra("email", email);
                    startActivity(intent_gotoChatSupport);
                }
            });
        }
        //Transfers to the Help Activity (on clicking the help icon)
        {
            help = findViewById(R.id.helpLayout);
            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_gotoHelp = new Intent(driverdash.this, Help.class);
                    intent_gotoHelp.putExtra("email", email);
                    startActivity(intent_gotoHelp);
                }
            });
        }
        //Transfers to the ReferandEarn Activity (on clicking the refer and earn icon)
        {
            refer = findViewById(R.id.referLayout);
            refer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_gotoReferAndEarn = new Intent(driverdash.this, ReferandEarn.class);
                    intent_gotoReferAndEarn.putExtra("email", email);
                    startActivity(intent_gotoReferAndEarn);
                }
            });
        }
        //Transfers to the Rewards Activity (on clicking the chat rewards icon)
        {
            rewards = findViewById(R.id.rewardLayout);
            rewards.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_gotoRewards = new Intent(driverdash.this, Rewards.class);
                    intent_gotoRewards.putExtra("email", email);
                    startActivity(intent_gotoRewards);
                }
            });
        }
        //Transfers to the Settings Activity (on clicking the settings icon)
        {
            settings = findViewById(R.id.settingLayout);
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_gotoSettings = new Intent(driverdash.this, Settings.class);
                    intent_gotoSettings.putExtra("email", email);
                    startActivity(intent_gotoSettings);
                }
            });
        }
        //Transfers to the WithdrawalHistory Activity (on clicking the withdrawal history icon)
        {
            withdrawal_history = findViewById(R.id.WithdrawalLayout);
            withdrawal_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_gotoWithdrawalHistory = new Intent(driverdash.this, WithdrawalHistory.class);
                    intent_gotoWithdrawalHistory.putExtra("email", email);
                    startActivity(intent_gotoWithdrawalHistory);
                }
            });
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
                            String carrierS = response.getString("carrier");
                            //here card balance is being used as daily earning add daily earning to json response and update the string
                            String todaysEarning = response.getString("card_balance");
                            String signall = response.getString("signal");
                            String batteryy = response.getString("bat");
                            String kmrate = response.getString("kmrate");

                            String distanceS = "20";
                            //total distance not defined in server response, define it and update the below commented line and remove the old definition of distanceS
                            // String distanceS = response.getString("");
                            rate = kmrate;
                            int lp = response.getInt("logtime");
                            name.setText(hname);
                            long currentTime = System.currentTimeMillis() / 1000;



                            // Calculate time difference
                            long differenceInSeconds = currentTime - lp;
                            lastupdated.setText(timemanager(differenceInSeconds));
                            totaldistance.setText(distanceS + "km");
                            carrier.setText("Carrier : "+carrierS);
                            signal.setText("Signal\n" + signall + "%");
                            battery.setText("Battery\n" + batteryy + "%");
                            km.setText("Price\n" + kmrate + "/km");
                            balance.setText("₹"+todaysEarning);

                            //dynamically updates the signal icon and battery icon based on signal strength and battery available
                            try {
                                updateSignalImage(Integer.valueOf(signall));
                                updateBatteryImage(Integer.valueOf(batteryy));
                            } catch (Exception e) {

                            }
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

    //Updates the signal Image
    private void updateSignalImage(int signal) {
        if(signal < 15 )
            imageView_signal.setImageResource(R.drawable.baseline_signal_cellular_0_bar_24);
        else if (signal <40)
            imageView_signal.setImageResource(R.drawable.baseline_signal_cellular_alt_1_bar_24);
        else if (signal <70)
            imageView_signal.setImageResource(R.drawable.baseline_signal_cellular_alt_2_bar_24);
        else
            imageView_signal.setImageResource(R.drawable.baseline_signal_cellular_alt_24);
    }

    //updates the battery image
    private void updateBatteryImage(int battery) {
        if ( battery < 10)
            imageView_battery.setImageResource(R.drawable.baseline_battery_0_bar_24);
        else if(battery < 37)
            imageView_battery.setImageResource(R.drawable.baseline_battery_2_bar_24);
        else if(battery < 64)
            imageView_battery.setImageResource(R.drawable.baseline_battery_4_bar_24);
        else if(battery < 90)
            imageView_battery.setImageResource(R.drawable.baseline_battery_6_bar_24);
        else
            imageView_battery.setImageResource(R.drawable.baseline_battery_full_24);


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

    //Converts time from second to suitable time scale
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