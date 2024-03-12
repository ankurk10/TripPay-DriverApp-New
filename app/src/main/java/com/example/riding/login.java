package com.example.riding;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private ImageView btnLogin;
    private TextView signup;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        signup=findViewById(R.id.btnsignup);

        // Initialize SharedPreferences
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        // Check if email and password are already saved
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        // Autofill the fields if saved
        editTextLoginEmail.setText(savedEmail);
        editTextLoginPassword.setText(savedPassword);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login logic
                login();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });

    }

    private void login() {
        // Get user input
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            // Display a toast message indicating that either email or password is empty
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
        } else {

            // Perform login request
            String loginUrl = "https://trippay.in/login.php";
            RequestQueue queue = Volley.newRequestQueue(this);

            // Define JSON object
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Handle successful login response
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.apply();
                                    Toast.makeText(login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                    // Retrieve user data from the response (adjust key names accordingly)
                                    int userId = response.getInt("user_id");
                                    String fullName = response.getString("full_name");
                                    String userEmail = response.getString("email");
                                    int driver = response.getInt("driver");
                                    if (driver == 0) {
                                        Intent intent = new Intent(login.this, driverdash.class);
                                        intent.putExtra("userid", userId);
                                        intent.putExtra("email", userEmail);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(login.this, driverdash.class);
                                        intent.putExtra("userid", userId);
                                        intent.putExtra("email", userEmail);
                                        startActivity(intent);
                                    }

                                } else {
                                    // Login failed
                                    String message = response.getString("message");
                                    Toast.makeText(login.this, "Login failed! " + message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(login.this, "Failed to parse JSON response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                            Toast.makeText(login.this, "Login failed! " + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

            // Add the request to the RequestQueue
            queue.add(jsonObjectRequest);
        }
    }

}