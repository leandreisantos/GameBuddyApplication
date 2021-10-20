package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegisterOptionActivity extends AppCompatActivity {

    TextView gamer,company,close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_option);

        gamer = findViewById(R.id.tv_gamer_ro);
        company = findViewById(R.id.tv_com_ro);
        close = findViewById(R.id.tv_close_ro);

        gamer.setOnClickListener(v -> {
            Intent itent = new Intent(RegisterOptionActivity.this,RegisterActivity.class);
            startActivity(itent);
        });

        close.setOnClickListener(v -> {
            Intent itent = new Intent(RegisterOptionActivity.this,WaitingActivity.class);
            startActivity(itent);
        });
    }
}