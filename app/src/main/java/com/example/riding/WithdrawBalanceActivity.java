package com.example.riding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class WithdrawBalanceActivity extends AppCompatActivity {

    private TextInputEditText accountNumber, ifsc, amount;
    private Button button_Withdraw;
    private TextView current_balance;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_balance);

        intent = getIntent();

        accountNumber = findViewById(R.id.account_number);
        ifsc = findViewById(R.id.ifsc);
        amount  = findViewById(R.id.amount);

        button_Withdraw = findViewById(R.id.withdrawButton);
        current_balance = findViewById(R.id.currentBalance);

        button_Withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WithdrawBalanceActivity.this, "Withdrawal Request Sent", Toast.LENGTH_LONG).show();
                //create the below functions to complete the activity
//                        withdrawBalance();
//                        updatePage();
            }
        });
    }
}