package com.example.riding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.concurrent.TimeUnit;

public class RideHistory extends AppCompatActivity {

    private TextView totalEarning;

    private RecyclerView recyclerViewRidingData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        totalEarning = findViewById(R.id.earning);

        recyclerViewRidingData = findViewById(R.id.RidingDataAdapter);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        fetchData(email);
    }

    private void fetchData(String email) {
        String url = "https://trippay.in/getdriverinfo.php?email="+email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract card information

                            //Update todays total earning
                            String cardBalance = response.getString("card_balance");
                            totalEarning.setText("Earnings : " + cardBalance + "$");

                            // Extract riding data array
                            // Extract riding data array
                            JSONObject jsonResponse = new JSONObject(String.valueOf(response));
                            if (jsonResponse.has("riding_data")) {
                                JSONArray ridingDataArray = response.getJSONArray("riding_data");
                                //Toast.makeText(RideHistory.this, "rdidngdata rec", Toast.LENGTH_SHORT).show();
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

                                    //    Toast.makeText(RideHistory.this, "start ep"+startep.toString(), Toast.LENGTH_SHORT).show();
                                    // Create a RidingData object and add it to the list
                                    RideHistory.RidingData ridingData = new RideHistory.RidingData(startPoint, endPoint, totalFare, totalDistance, logtime, drivername, startep, endep);
                                    ridingDataList.add(ridingData);
                                }

                                // Set up the RecyclerView with the adapter
                                RideHistory.RidingDataAdapter adapter = new RideHistory.RidingDataAdapter(ridingDataList);
                                recyclerViewRidingData.setLayoutManager(new LinearLayoutManager(RideHistory.this));
                                recyclerViewRidingData.setAdapter(adapter);
                            }
                            else{
                                //Toast.makeText(RideHistory.this, "No Ride Completed Yet", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RideHistory.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }



    //Riding Data Management

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
        private class RidingDataAdapter extends RecyclerView.Adapter<RideHistory.RidingDataAdapter.ViewHolder> {

            private final List<RideHistory.RidingData> ridingDataList;

            public RidingDataAdapter(List<RideHistory.RidingData> ridingDataList) {
                this.ridingDataList = ridingDataList;
            }

            @NonNull
            @Override
            public RideHistory.RidingDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riding_data, parent, false);
                return new RideHistory.RidingDataAdapter.ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RideHistory.RidingDataAdapter.ViewHolder holder, int position) {
                RideHistory.RidingData ridingData = ridingDataList.get(position);
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
        private void navigateToDetailsActivity(RideHistory.RidingData ridingData) {
        Intent intent = new Intent(RideHistory.this, DetailsActivity.class);
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

}