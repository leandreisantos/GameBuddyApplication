package com.example.socialmedia.FragmetGss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.AllUserMember;
import com.example.socialmedia.ChatActivity;
import com.example.socialmedia.ProfileViewholder;
import com.example.socialmedia.R;
import com.example.socialmedia.ShowUser;
import com.example.socialmedia.databaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowAllMemberGss_Activity extends AppCompatActivity {


    String bundle_postkey,bundle_title,bundle_cat;

    TextView title,back;
    RecyclerView rv;

    DatabaseReference profileRef;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_member_gss_);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            bundle_postkey = extras.getString("p");
            bundle_title = extras.getString("t");
            bundle_cat = extras.getString("c");

        }

        title = findViewById(R.id.tv_title_slm);
        back = findViewById(R.id.tv_back_slm);
        rv = findViewById(R.id.rv_list_slm);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(ShowAllMemberGss_Activity.this));
        profileRef = database.getReference("Game Member").child(bundle_cat).child(bundle_postkey);


        back.setOnClickListener(v -> onBackPressed());
        title.setText(bundle_title +" Member");
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<JoinMember> options1 =
                new FirebaseRecyclerOptions.Builder<JoinMember>()
                        .setQuery(profileRef,JoinMember.class)
                        .build();

        FirebaseRecyclerAdapter<JoinMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<JoinMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull JoinMember model) {


                        holder.setGameMember(getApplication(),model.getPostkey(),model.getId(),model.getGamePostkey(),model.getDate(),model.getTime(),
                                model.getPosition());

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_profile_slm,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();

        rv.setAdapter(firebaseRecyclerAdapter1);


    }
}