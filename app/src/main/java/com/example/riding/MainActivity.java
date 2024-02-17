package com.example.riding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private CardView cardViewCardInfo;
    private TextView textViewCardId, textViewCardBalance,name;
    private RecyclerView recyclerViewRidingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardViewCardInfo = findViewById(R.id.cardViewCardInfo);
        textViewCardId = findViewById(R.id.textViewCardId);
        textViewCardBalance = findViewById(R.id.textViewCardBalance);
        recyclerViewRidingData = findViewById(R.id.recyclerViewRidingData);
        name=findViewById(R.id.username);

        Intent intent = getIntent();
        if (intent != null) {
            int userId = intent.getIntExtra("userid", -1); // -1 is the default value if the key is not found
            String userEmail = intent.getStringExtra("email");
//            Toast.makeText(this, "id : "+userId, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "email : "+userEmail, Toast.LENGTH_SHORT).show();
            fetchData(userEmail);
        }


        cardViewCardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alertDialog.show();
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });

        }

    private void fetchData(String email) {
        String url = "https://trippay.in/getcardinfo.php?email="+email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract card information
                            String cardId = response.getString("card_id");
                            String cardBalance = response.getString("card_balance");
                            String uname=response.getString("name");

                            // Set card information in the views
                            textViewCardId.setText("Card ID: " + cardId);
                            name.setText("Account Holder : " + uname);
                            textViewCardBalance.setText("Card Balance: " + cardBalance+"$");


                            // Extract riding data array
                            JSONArray ridingDataArray = response.getJSONArray("riding_data");

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
                                String startep=ridingDataObject.getString("startep");
                                String endep=ridingDataObject.getString("endep");

                                //    Toast.makeText(MainActivity.this, "start ep"+startep.toString(), Toast.LENGTH_SHORT).show();
                                // Create a RidingData object and add it to the list
                                MainActivity.RidingData ridingData = new MainActivity.RidingData(startPoint, endPoint, totalFare, totalDistance, logtime, drivername,startep,endep);
                                ridingDataList.add(ridingData);
                            }

                            // Set up the RecyclerView with the adapter
                            MainActivity.RidingDataAdapter adapter = new MainActivity.RidingDataAdapter(ridingDataList);
                            recyclerViewRidingData.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            recyclerViewRidingData.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
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
    private class RidingDataAdapter extends RecyclerView.Adapter<MainActivity.RidingDataAdapter.ViewHolder> {

        private final List<MainActivity.RidingData> ridingDataList;

        public RidingDataAdapter(List<MainActivity.RidingData> ridingDataList) {
            this.ridingDataList = ridingDataList;
        }

        @NonNull
        @Override
        public MainActivity.RidingDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riding_data, parent, false);
            return new MainActivity.RidingDataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MainActivity.RidingDataAdapter.ViewHolder holder, int position) {
            MainActivity.RidingData ridingData = ridingDataList.get(position);
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
    private void navigateToDetailsActivity(MainActivity.RidingData ridingData) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
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
    }}