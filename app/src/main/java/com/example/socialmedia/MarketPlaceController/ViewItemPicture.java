package com.example.socialmedia.MarketPlaceController;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmedia.R;
import com.squareup.picasso.Picasso;

public class ViewItemPicture extends AppCompatActivity {

    ImageView pic;
    TextView back;
    String p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_picture);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            p = extras.getString("p");
        }


        pic = findViewById(R.id.iv_picture);
        back = findViewById(R.id.tv_back_av);


        back.setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected void onStart() {
        super.onStart();

        Picasso.get().load(p).into(pic);
    }
}