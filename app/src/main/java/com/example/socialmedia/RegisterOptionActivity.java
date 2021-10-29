package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegisterOptionActivity extends AppCompatActivity {

    TextView gamer,company,close,gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_option);

        gamer = findViewById(R.id.tv_gamer_ro);
        company = findViewById(R.id.tv_com_ro);
        close = findViewById(R.id.tv_close_ro);
        gd = findViewById(R.id.tv_developer_ro);

        gamer.setOnClickListener(v -> {
            Intent itent = new Intent(RegisterOptionActivity.this,RegisterActivity.class);
            itent.putExtra("k","g");
            startActivity(itent);
        });

        gd.setOnClickListener(v -> {
            Intent itent = new Intent(RegisterOptionActivity.this,RegisterActivity.class);
            itent.putExtra("k","gd");
            startActivity(itent);
        });

        company.setOnClickListener(v -> {
            Intent itent = new Intent(RegisterOptionActivity.this,RegisterActivity.class);
            itent.putExtra("k","c");
            startActivity(itent);
        });

        close.setOnClickListener(v -> {
            onBackPressed();
        });


    }
}