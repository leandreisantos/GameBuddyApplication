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

public class InvitePeopleGroupActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String userid,postKey;
    DatabaseReference databaseReference,pendingInvite,ntref;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    NewMember newMember;
    InviteMember inviteMember;
    String currentuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_people_group);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            userid = bundle.getString("i");
            postKey = bundle.getString("p");
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        databaseReference= database.getReference("followers").child(userid);
        pendingInvite = database.getReference("pending invite");

        newMember = new NewMember();
        inviteMember = new InviteMember();
        //ntref = database.getReference("notification").child(currentuid);

        recyclerView = findViewById(R.id.rv_aip);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(InvitePeopleGroupActivity.this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<RequestMember> options1 =
                new FirebaseRecyclerOptions.Builder<RequestMember>()
                        .setQuery(databaseReference,RequestMember.class)
                        .build();

        FirebaseRecyclerAdapter<RequestMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<RequestMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull RequestMember model) {


                        holder.invitePeopleToGroup(getApplication(),model.getName(),model.getUrl(),model.getProfession(),model.getBio(),model.getPrivacy(),
                                model.getEmail(),model.getFollowers(),model.getWebsite(),model.getUserid());

                        String id = getItem(position).getUserid();
                        holder.groupid="postKey";

                        holder.ipl_invite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                inviteMember.setInviteid(id);
                                inviteMember.setWhoInvite(currentuid);
                                inviteMember.setGroupid(postKey);
                                inviteMember.setStatus("Pending");

                                pendingInvite.child(id).setValue(inviteMember);

                                Toast.makeText(InvitePeopleGroupActivity.this, "Invited", Toast.LENGTH_SHORT).show();


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.invite_people_layout,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
}