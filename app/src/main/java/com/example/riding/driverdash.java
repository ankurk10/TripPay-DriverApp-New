package com.example.riding;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class driverdash extends AppCompatActivity {
    private CardView cardViewCardInfo;
    private SwipeRefreshLayout swipeRefreshLayout;
String rate;
    private  TextView name,cardid,balance,ssid,passwrd,battery,signal,carrier,lastupdated,km;
    private RecyclerView recyclerViewRidingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverdash);
        cardViewCardInfo=findViewById(R.id.cardViewCardInfo);
        recyclerViewRidingData=findViewById(R.id.recyclerViewRidingData);
        cardViewCardInfo = findViewById(R.id.cardViewCardInfo);
       // swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        name = findViewById(R.id.name);
        cardid = findViewById(R.id.card_id);
        balance=findViewById(R.id.balance);
        ssid=findViewById(R.id.ssid);
        passwrd=findViewById(R.id.passwrd);
        lastupdated=findViewById(R.id.lastupdated);
        signal=findViewById(R.id.signal);
        battery=findViewById(R.id.battery);
        carrier=findViewById(R.id.carrier);
        km=findViewById(R.id.kmrr);
        Intent intent = getIntent();
        ImageView im=(ImageView)findViewById(R.id.logout);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
            }
        });

        ImageView ir=(ImageView)findViewById(R.id.reload);
        ir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = intent.getStringExtra("email");
                fetchData(userEmail);
                Toast.makeText(driverdash.this, "Data upated sucessfully", Toast.LENGTH_SHORT).show();
            }
        });


        if (intent != null) {
            int userId = intent.getIntExtra("userid", -1); // -1 is the default value if the key is not found
            String userEmail = intent.getStringExtra("email");
//            Toast.makeText(this, "id : "+userId, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "email : "+userEmail, Toast.LENGTH_SHORT).show();
            fetchData(userEmail);
        }

        //alert Dialog box
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.payment_dialog, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Enter Your New Rate")
                .setView(dialogView)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle payment logic here
                        EditText rate = dialogView.findViewById(R.id.editTextrate);
                        //cardNumberEditText.setText("hello");
                        String newRate = rate.getText().toString();
                        String userEmail = intent.getStringExtra("email");
                        updateRateWithVolley(newRate,userEmail);

                      }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        cardViewCardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              alertDialog.show();

            }
        });

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
                            String cardId = response.getString("card_id");
                            String hname = response.getString("name");
                            String cardBalance = response.getString("card_balance");
                            String signall = response.getString("signal");
                            String batteryy = response.getString("bat");
                            String car = response.getString("carrier");
                            String ssidd = response.getString("ssid");
                            String pass = response.getString("pass");
                            String kmrate = response.getString("kmrate");
                            rate = kmrate;
                            int lp = response.getInt("logtime");
                            name.setText("Driver Name : " + hname);
                            ssid.setText("SSID : " + ssidd);
                            passwrd.setText("Password : " + pass);
                            // Get current time in epoch format
                            long currentTime = System.currentTimeMillis() / 1000;

                            // Calculate time difference
                            long differenceInSeconds = currentTime - lp;

                            if (differenceInSeconds < 60) {
                                // Few seconds ago
                                lastupdated.setText("Last Updated : " + differenceInSeconds + "Few seconds ago");
                            } else if (differenceInSeconds < 3600) {
                                // X minutes ago
                                long minutes = TimeUnit.SECONDS.toMinutes(differenceInSeconds);
                                lastupdated.setText("Last Updated : " + minutes + " minutes ago");
                            } else if (differenceInSeconds < 86400) {
                                // X hours ago
                                long hours = TimeUnit.SECONDS.toHours(differenceInSeconds);
                                lastupdated.setText("Last Updated : " + hours + " minutes ago");
                            } else {
                                // X days ago
                                long days = TimeUnit.SECONDS.toDays(differenceInSeconds);
                                lastupdated.setText("Last Updated : " + days + " minutes ago");
                            }

                            signal.setText("Signal Strength : " + signall + "%");
                            battery.setText("Battery : " + batteryy + "%");
                            carrier.setText("Carrier : " + car);
                            // Set card information in the views
                            cardid.setText("Device ID : " + cardId);
                            km.setText("Price/Km : " + kmrate + "$");
                            balance.setText("Earnings : " + cardBalance + "$");
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



    private void fetchData(String email) {
        String url = "https://trippay.in/getdriverinfo.php?email="+email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract card information
                            //Toast.makeText(driverdash.this, "res : "+response , Toast.LENGTH_SHORT).show();
                            String cardId = response.getString("card_id");
                            String hname = response.getString("name");
                            String cardBalance = response.getString("card_balance");
                            String signall = response.getString("signal");
                            String batteryy = response.getString("bat");
                            String car = response.getString("carrier");
                            String ssidd = response.getString("ssid");
                            String pass = response.getString("pass");
                            String kmrate = response.getString("kmrate");
                            rate = kmrate;
                            int lp = response.getInt("logtime");
                            name.setText("Driver Name : " + hname);
                            ssid.setText("SSID : " + ssidd);
                            passwrd.setText("Password : " + pass);
                            // Get current time in epoch format
                            long currentTime = System.currentTimeMillis() / 1000;

                            // Calculate time difference
                            long differenceInSeconds = currentTime - lp;

                            if (differenceInSeconds < 60) {
                                // Few seconds ago
                                lastupdated.setText("Last Updated : " + differenceInSeconds + "Few seconds ago");
                            } else if (differenceInSeconds < 3600) {
                                // X minutes ago
                                long minutes = TimeUnit.SECONDS.toMinutes(differenceInSeconds);
                                lastupdated.setText("Last Updated : " + minutes + " minutes ago");
                            } else if (differenceInSeconds < 86400) {
                                // X hours ago
                                long hours = TimeUnit.SECONDS.toHours(differenceInSeconds);
                                lastupdated.setText("Last Updated : " + hours + " minutes ago");
                            } else {
                                // X days ago
                                long days = TimeUnit.SECONDS.toDays(differenceInSeconds);
                                lastupdated.setText("Last Updated : " + days + " minutes ago");
                            }

                            signal.setText("Signal Strength : " + signall + "%");
                            battery.setText("Battery : " + batteryy + "%");
                            carrier.setText("Carrier : " + car);
                            // Set card information in the views
                            cardid.setText("Device ID : " + cardId);
                            km.setText("Price/Km : " + kmrate + "$");
                            balance.setText("Earnings : " + cardBalance + "$");

                            // Extract riding data array
                            // Extract riding data array
                            JSONObject jsonResponse = new JSONObject(String.valueOf(response));
                            if (jsonResponse.has("riding_data")) {
                            JSONArray ridingDataArray = response.getJSONArray("riding_data");
                                //Toast.makeText(driverdash.this, "rdidngdata rec", Toast.LENGTH_SHORT).show();
                            // Create a list to hold riding data objects
                            List<RidingData> ridingDataList = new ArrayList<>();

                            // Iterate through the riding data array
                            for (int i = 0; i < ridingDataArray.length(); i++) {
                                JSONObject ridingDataObject = ridingDataArray.getJSONObject(i);

                                // Extract data from the JSON object
                                String startPoint = ridingDataObject.getString("startpoint");
                                String endPoint = ridingDataObject.getString("endpoint");
                                String totalFare = ridingDataObject.getString("fair");
                                String totalDistance = ridingDataObject.getString("distance");
                                String logtime = ridingDataObject.getString("logtime");
                                String drivername = ridingDataObject.getString("full_name");
                                String startep = ridingDataObject.getString("startep");
                                String endep = ridingDataObject.getString("endep");

                                //    Toast.makeText(driverdash.this, "start ep"+startep.toString(), Toast.LENGTH_SHORT).show();
                                // Create a RidingData object and add it to the list
                                driverdash.RidingData ridingData = new driverdash.RidingData(startPoint, endPoint, totalFare, totalDistance, logtime, drivername, startep, endep);
                                ridingDataList.add(ridingData);
                            }

                            // Set up the RecyclerView with the adapter
                            driverdash.RidingDataAdapter adapter = new driverdash.RidingDataAdapter(ridingDataList);
                            recyclerViewRidingData.setLayoutManager(new LinearLayoutManager(driverdash.this));
                            recyclerViewRidingData.setAdapter(adapter);
                        }
                        else{
                                //Toast.makeText(driverdash.this, "No Ride Completed Yet", Toast.LENGTH_SHORT).show();
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        //Toast.makeText(driverdash.this, "Calling other endpoint", Toast.LENGTH_SHORT).show();
                        fetchLatestData(email);
                    }
                });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    // Define a data model for riding data
    private static class RidingData {
        private final String startPoint;
        private final String endPoint;
        private final String totalFare;
        private final String totalDistance;
        private final String logtime;
        private final String drivername;
        private final String startep;
        private final String endep;

        public RidingData(String startPoint, String endPoint, String totalFare, String totalDistance, String logtime, String drivername,String sep,String eep) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.totalFare = totalFare;
            this.totalDistance = totalDistance;
            this.logtime = logtime;
            this.drivername = drivername;
            this.startep=sep;
            this.endep=eep;
        }
    }

    // Adapter for the RecyclerView
    private class RidingDataAdapter extends RecyclerView.Adapter<driverdash.RidingDataAdapter.ViewHolder> {

        private final List<driverdash.RidingData> ridingDataList;

        public RidingDataAdapter(List<driverdash.RidingData> ridingDataList) {
            this.ridingDataList = ridingDataList;
        }

        @NonNull
        @Override
        public driverdash.RidingDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riding_data, parent, false);
            return new driverdash.RidingDataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull driverdash.RidingDataAdapter.ViewHolder holder, int position) {
            driverdash.RidingData ridingData = ridingDataList.get(position);
            holder.textViewDriverName.setText("Rider Name: " + ridingData.drivername);
            holder.textViewStartPoint.setText("Start Point: " + ridingData.startPoint);
            holder.textViewEndPoint.setText("End Point: " + ridingData.endPoint);
            holder.textViewTotalFare.setText("Total Fare: " + ridingData.totalFare+"$");
            holder.textViewTotalDistance.setText("Total Distance: " + ridingData.totalDistance+"Km");
            holder.textViewTotalDistance.setText("Total Distance: " + ridingData.totalDistance+"Km");
            holder.textViewstep.setText("Ride Start at: " + ridingData.startep);
            holder.textVieweep.setText("Ride Start at: " + ridingData.endep);


            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

            try {
                long logtimeEpoch = Long.parseLong(ridingData.logtime);
                Date date = new Date(logtimeEpoch * 1000); // Convert epoch time to milliseconds
                String formattedLogTime = outputFormat.format(date);
                holder.textViewLogTime.setText("Log Time: " + formattedLogTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.textViewLogTime.setText("Log Time: Error formatting date");
            }

            try {
                long logtimeEpoch = Long.parseLong(ridingData.startep);
                Date date = new Date(logtimeEpoch * 1000); // Convert epoch time to milliseconds
                String formattedLogTime = outputFormat.format(date);
                holder.textViewstep.setText("Ride start at: " + formattedLogTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.textViewstep.setText("Log Time: Error formatting date");
            }
            try {
                long logtimeEpoch = Long.parseLong(ridingData.endep);
                Date date = new Date(logtimeEpoch * 1000); // Convert epoch time to milliseconds
                String formattedLogTime = outputFormat.format(date);
                holder.textVieweep.setText("Ride end at: " + formattedLogTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.textVieweep.setText("Log Time: Error formatting date");
            }




            // Set a click listener for the RecyclerView item
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click, e.g., navigate to another activity
                    navigateToDetailsActivity(ridingData);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ridingDataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewStartPoint, textViewEndPoint, textViewTotalFare, textViewTotalDistance,textViewLogTime,textViewDriverName,textViewstep,textVieweep;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewStartPoint = itemView.findViewById(R.id.textViewStartPoint);
                textViewEndPoint = itemView.findViewById(R.id.textViewEndPoint);
                textViewTotalFare = itemView.findViewById(R.id.textViewTotalFare);
                textViewTotalDistance = itemView.findViewById(R.id.textViewTotalDistance);
                textViewDriverName=itemView.findViewById(R.id.textViewDriverName);
                textViewLogTime=itemView.findViewById(R.id.textViewLogTime);
                textViewstep=itemView.findViewById(R.id.sep);
                textVieweep=itemView.findViewById(R.id.eep);

            }
        }
    }

    // Method to navigate to the details activity
    private void navigateToDetailsActivity(driverdash.RidingData ridingData) {
        Intent intent = new Intent(driverdash.this, DetailsActivity.class);
        intent.putExtra("startPoint", ridingData.startPoint);
        intent.putExtra("endPoint", ridingData.endPoint);
        intent.putExtra("totalFare", ridingData.totalFare);
        intent.putExtra("totalDistance", ridingData.totalDistance);
        startActivity(intent);
    }

    private String formatEpochTime(long epochTime) {
        Date date = new Date(epochTime * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private void updateRateWithVolley(String newRate,String email) {
        String url = "https://trippay.in/updatekm.php"; // Replace with your actual endpoint URL
//        Toast.makeText(this, "rate "+newRate, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "email "+email, Toast.LENGTH_SHORT).show();
        // Prepare the request parameters
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("rate", newRate);

        // Create a new JsonObjectRequest
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from the server
                        km.setText("Price/Km : " + newRate+"$");
                        Toast.makeText(driverdash.this, "Rate updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        km.setText("Price/Km : " + newRate+"$");
                        Toast.makeText(driverdash.this, "Rate updated successfully", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    @Override
    public void onBackPressed() {

        showLogoutConfirmationDialog();
    }

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

    private void performLogout() {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        finish();
    }
}