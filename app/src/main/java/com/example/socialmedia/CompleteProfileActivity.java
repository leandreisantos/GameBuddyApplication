package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmedia.MarketPlaceController.ViewItemPicture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class CompleteProfileActivity extends AppCompatActivity {

    ImageView profilepic,profilebg;
    TextView nameEt,profEt,tv_about,tv_email,tv_web,back;

    //current user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        profilepic = findViewById(R.id.iv_mp);
        profilebg = findViewById(R.id.iv_bg_mp);
        nameEt = findViewById(R.id.name_mp);
        profEt = findViewById(R.id.prof_mp);
        tv_about = findViewById(R.id.tv_bio_cp);
        tv_email = findViewById(R.id.tv_email_cp);
        tv_web = findViewById(R.id.tv_web_cp);
        back = findViewById(R.id.tv_back_mp);

        back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();

        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentid);
        reference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){

                        String nameResult = task.getResult().getString("name");
                        String url = task.getResult().getString("url");
                        String url2 = task.getResult().getString("url2");
                        String profResult = task.getResult().getString("prof");
                        String about = task.getResult().getString("bio");
                        String email = task.getResult().getString("email");
                        String website = task.getResult().getString("web");

                        Picasso.get().load(url).into(profilepic);
                        Picasso.get().load(url2).into(profilebg);
                        nameEt.setText(nameResult);
                        profEt.setText(profResult);
                        tv_about.setText(about);
                        tv_email.setText(email);
                        tv_web.setText(website);

                        profilepic.setOnClickListener(v -> showImage(url));
                        profilebg.setOnClickListener(v -> showImage(url2));

                    }else{
                        Intent intent = new Intent(CompleteProfileActivity.this,CreateProfile.class);
                        startActivity(intent);
                    }
                });
    }
    private void showImage(String url) {
        Intent intent = new Intent(CompleteProfileActivity.this, ViewItemPicture.class);
        intent.putExtra("p",url);
        startActivity(intent);
    }
}