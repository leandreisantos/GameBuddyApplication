package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.security.Permission;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference profileRef;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    RecyclerView recyclerView;
    EditText searchEt;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();


        
        searchEt = findViewById(R.id.search_userch);
        recyclerView = findViewById(R.id.rv_ch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        profileRef = database.getReference("followers").child(userid);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String query = searchEt.getText().toString();
                Query search = profileRef.orderByChild("name").startAt(query).endAt(query+"\uf0ff");

                FirebaseRecyclerOptions<AllUserMember> options1 =
                        new FirebaseRecyclerOptions.Builder<AllUserMember>()
                                .setQuery(search,AllUserMember.class)
                                .build();

                FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                        new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                                final String postkey = getRef(position).getKey();

                                holder.setProfileInchat(getApplication(),model.getName(),model.getUid(),model.getProf(),model.getUrl());


                                String  name = getItem(position).getName();
                                String  url = getItem(position).getUrl();
                                String uid = getItem(position).getUid();

                                // String uid = "VA0zC50hUhQoRIu8P6xuHLRCWVp1";

                                holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(ChatActivity.this,MessageActivity.class);
                                        intent.putExtra("n",name);
                                        intent.putExtra("u",url);
                                        intent.putExtra("uid",uid);
                                        startActivity(intent);



                                    }
                                });

                            }

                            @NonNull
                            @Override
                            public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.chat_profile_item,parent,false);

                                return new ProfileViewholder(view);
                            }
                        };


                firebaseRecyclerAdapter1.startListening();

                recyclerView.setAdapter(firebaseRecyclerAdapter1);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                startActivity(intent);
            }
        };

        TedPermission.with(ChatActivity.this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .check();

        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(profileRef,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                        final String postkey = getRef(position).getKey();

                        holder.setProfileInchat(getApplication(),model.getName(),model.getUid(),model.getProf(),model.getUrl());


                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();

                        // String uid = "VA0zC50hUhQoRIu8P6xuHLRCWVp1";

                        holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                    Intent intent = new Intent(ChatActivity.this,ShowUser.class);
                                    intent.putExtra("n",name);
                                    intent.putExtra("u",url);
                                    intent.putExtra("uid",uid);
                                    startActivity(intent);



                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_profile_item,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter1);


    }
}