package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class WaitingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        Button loginBtn=findViewById(R.id.loginbtn);
        Button registerBtn=findViewById(R.id.registerbtn);

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(WaitingActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(WaitingActivity.this, RegisterOptionActivity.class);
            startActivity(intent);
        });

    }
}