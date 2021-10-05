package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowLikedUser extends AppCompatActivity {

    RecyclerView recyclerView;
    String postkey,currentUid;
    DatabaseReference databaseReference;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_liked_user);

        recyclerView = findViewById(R.id.rv_likedby);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowLikedUser.this));
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            postkey = bundle.getString("p");
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        databaseReference= database.getReference("followers").child(postkey);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(databaseReference,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {

                        final String postkey = getRef(position).getKey();

                        holder.setLikeduser(getApplication(),model.getName(),model.getUid(),model.getProf(),model.getUrl());

                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String userid = getItem(position).getUid();


                        holder.vpfollower.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (currentUid.equals(userid)) {
                                    Intent intent = new Intent(ShowLikedUser.this,MyProfileActivity.class);
                                    startActivity(intent);

                                }else {
                                    Intent intent = new Intent(ShowLikedUser.this,ShowUser.class);
                                    intent.putExtra("n",name);
                                    intent.putExtra("u",url);
                                    intent.putExtra("uid",userid);
                                    startActivity(intent);
                                }
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.userlike_layout,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);


    }
}