package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RequestActivity extends AppCompatActivity {

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference,databaseReference1,profileRef,ntRef;

    RecyclerView recyclerView;

    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = user.getUid();

    RequestMember requestMember;

    NewMember newMember;

    TextView back;
    String usertoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        requestMember = new RequestMember();

        databaseReference = database.getReference("Requests").child(currentUserId);
        ntRef = database.getReference("notification").child(currentUserId);
        newMember = new NewMember();
        recyclerView = findViewById(R.id.recyclerview_requestf3);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(RequestActivity.this));

        back = findViewById(R.id.tv_back_req);

        back.setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<RequestMember> options =
                new FirebaseRecyclerOptions.Builder<RequestMember>()
                        .setQuery(databaseReference,RequestMember.class)
                        .build();

        FirebaseRecyclerAdapter<RequestMember,RequestViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<RequestMember, RequestViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestViewholder holder, int position, @NonNull RequestMember model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = user.getUid();
                        final String postkey = getRef(position).getKey();

                        holder.setRequest(getApplication(),model.getName(),model.getUrl(),model.getProfession()
                                ,model.getBio(),model.getPrivacy(),model.getEmail(),model.getFollowers(),model.getWebsite(),model.getUserid());

                        String uid = getItem(position).getUserid();
                        String name = getItem(position).getName();
                        String bio = getItem(position).getBio();
                        String email = getItem(position).getEmail();
                        String privacy = getItem(position).getPrivacy();
                        String url = getItem(position).getUrl();
                        String website = getItem(position).getWebsite();
                        String age = getItem(position).getProfession();


                        holder.button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = getItem(position).getName();
                                decline(name);
                            }
                        });
                        holder.button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String uid = getItem(position).getUserid();
                                databaseReference1 = database.getReference("followers").child(currentUserId);
                                requestMember.setName(name);

                                requestMember.setUserid(uid);
                                requestMember.setUrl(url);
                                requestMember.setProfession(age);
                                String id = databaseReference1.push().getKey();
                                databaseReference1.child(uid).setValue(requestMember);
                                databaseReference.child(currentUserId).child(uid).removeValue();

                                Toast.makeText(RequestActivity.this, "Accepted", Toast.LENGTH_SHORT).show();
                                sendNotification(currentUserId,name);
                                decline(name);


                                //handling request notification

                                newMember.setName(name);
                                newMember.setUid(uid);
                                newMember.setUrl(url);
                                newMember.setSeen("no");
                                newMember.setText("Started Following you ");

                                ntRef.child(uid+"f").setValue(newMember);


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.request_item,parent,false);

                        return new RequestViewholder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void decline(String name) {

        Query query = databaseReference.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                }
                //   Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///
            }
        });
    }

    private void sendNotification(String currentUserId, String name){

        FirebaseDatabase.getInstance().getReference().child(currentUserId).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        usertoken = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FcmNotificationSender notificationsSender =
                        new FcmNotificationSender(usertoken,"Social Media",name+" Started Following you",
                                getApplicationContext(),RequestActivity.this);

                notificationsSender.SendNotifications();

            }
        },3000);

    }
}