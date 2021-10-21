package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MyProfileActivity extends AppCompatActivity {

    ImageView profilepic,profilebg,createPost,notifi,sendMessage;
    TextView nameEt,profEt,tv_followers,tvPost,tvEdit;
    int postiv,post2,post1;

    //database reference
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1,db2,db3,ntRef;

    //current user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentid = user.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //pointer
        profilepic = findViewById(R.id.iv_mp);
        profilebg = findViewById(R.id.iv_bg_mp);
        nameEt = findViewById(R.id.name_mp);
        profEt = findViewById(R.id.prof_mp);
        tv_followers = findViewById(R.id.tv_followers);
        tvPost = findViewById(R.id.tv_post);
        tvEdit = findViewById(R.id.tv_edit);
        createPost = findViewById(R.id.iv_pp);
        notifi = findViewById(R.id.iv_np);
        sendMessage = findViewById(R.id.iv_smp);


        db1 = database.getReference("followers").child(currentid);
        db2 = database.getReference("All images").child(currentid);
        db3 = database.getReference("All videos").child(currentid);

        tvEdit.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this,UpdateProfile.class);
            startActivity(intent);
        });
        createPost.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this,PostActivity.class);
            startActivity(intent);
        });
        notifi.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this,NotificationActivity.class);
            startActivity(intent);
        });
        sendMessage.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this,ChatActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postiv = (int)snapshot.getChildrenCount();
                tv_followers.setText(Integer.toString(postiv));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post1 = (int)snapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post2 = (int)snapshot.getChildrenCount();
                String total = Integer.toString(post1+post2);
                tvPost.setText(total);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference = firestore.collection("user").document(currentid);
        reference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){

                        String nameResult = task.getResult().getString("name");
                        String url = task.getResult().getString("url");
                        String url2 = task.getResult().getString("url2");
                        String profResult = task.getResult().getString("prof");

                        Picasso.get().load(url).into(profilepic);
                        Picasso.get().load(url2).into(profilebg);
                        nameEt.setText(nameResult);
                        profEt.setText(profResult);

                    }else{
                        Intent intent = new Intent(MyProfileActivity.this,CreateProfile.class);
                        startActivity(intent);
                    }
                });
    }
}