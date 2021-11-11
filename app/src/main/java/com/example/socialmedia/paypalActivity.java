package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class paypalActivity extends AppCompatActivity {

    TextView back;
    Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        back = findViewById(R.id.tv_back_ap);
        proceed = findViewById(R.id.btn_debit);

        proceed.setOnClickListener(v -> Toast.makeText(paypalActivity.this, "Invalid", Toast.LENGTH_SHORT).show());
        back.setOnClickListener(v -> onBackPressed());
    }
}