package com.example.riding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView textViewTotalDistance, textViewTotalFare;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        textViewTotalDistance = findViewById(R.id.textViewTotalDistance);
        textViewTotalFare = findViewById(R.id.textViewTotalFare);
        mapView = findViewById(R.id.mapView);

        // Initialize the MapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Retrieve data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String totalDistance = intent.getStringExtra("totalDistance");
            String totalFare = intent.getStringExtra("totalFare");

            // Set values to TextViews
            textViewTotalDistance.setText("Total Distance: " + totalDistance+"Km");
            textViewTotalFare.setText("Total Fare: " + totalFare+"$");
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        // Retrieve start point and end point from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String startPoint = intent.getStringExtra("startPoint");
            String endPoint = intent.getStringExtra("endPoint");

            // Convert start point and end point strings to LatLng objects
            LatLng startLatLng = convertStringToLatLng(startPoint);
            LatLng endLatLng = convertStringToLatLng(endPoint);
            Toast.makeText(this, "st point "+convertStringToLatLng(startPoint), Toast.LENGTH_SHORT).show();

            // Add markers for start point and end point
            if (startLatLng != null && endLatLng != null) {
                googleMap.addMarker(new MarkerOptions().position(startLatLng).title("Start Point"));
                googleMap.addMarker(new MarkerOptions().position(endLatLng).title("End Point"));

                // Move camera to include both markers
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(startLatLng);
                builder.include(endLatLng);
                LatLngBounds bounds = builder.build();

                // Adjust camera position to include both markers
                int padding = 100; // Padding in pixels
               // googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            }
        }
    }

    // Helper method to convert a string with latitude and longitude to a LatLng object
    private LatLng convertStringToLatLng(String latLngString) {
        try {
            String[] latLngArray = latLngString.split(",");

            // Add logging to see the values
            Log.d("DetailsActivity", "latLngString: " + latLngString);
            Log.d("DetailsActivity", "latLngArray length: " + latLngArray.length);

            if (latLngArray.length >= 2) {
                double latitude = Double.parseDouble(latLngArray[0].trim());
                double longitude = Double.parseDouble(latLngArray[1].trim());
                return new LatLng(latitude, longitude);
            } else if (latLngArray.length == 1) {
                // Assume that the single value is either latitude or longitude
                double singleValue = Double.parseDouble(latLngArray[0].trim());
                return new LatLng(singleValue, 0.0); // Assuming 0.0 for longitude
            } else {
                Log.e("DetailsActivity", "Invalid latLngArray length for latLngString: " + latLngString);
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e("DetailsActivity", "Error converting string to LatLng. NumberFormatException", e);
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.e("DetailsActivity", "Error converting string to LatLng. ArrayIndexOutOfBoundsException", e);
            return null;
        }
    }

    // Lifecycle methods for the MapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}