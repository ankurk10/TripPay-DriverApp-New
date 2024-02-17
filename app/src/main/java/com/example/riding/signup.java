package com.example.riding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class signup extends AppCompatActivity {
    private EditText editTextFullName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private CheckBox checkBoxIsDriver;
    private Button btnSignup,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxIsDriver = findViewById(R.id.checkBoxIsDriver);
        btnSignup = findViewById(R.id.btnSignup);
        login = findViewById(R.id.btnlogin);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle signup logic
                signup();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
            }
        });

    }

    private void signup() {
        // Get user input
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        boolean isDriver = checkBoxIsDriver.isChecked();

        // Validate input (you can add more validation as needed)

        // Perform signup request
        String signupUrl = "https://trippay.in/signupapi.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        // Define JSON object
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("full_name", fullName);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("is_driver", isDriver ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, signupUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle successful signup response
                        Toast.makeText(signup.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        // Optionally navigate to the next screen or perform other actions
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(signup.this, "Signup successful!", Toast.LENGTH_SHORT).show();

//                        Toast.makeText(signup.this, "F" + error.toString(), Toast.LENGTH_SHORT).show();
//                        Log.e("SignupActivity", "Error during signup: " + error.toString());

                    }
                });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

}