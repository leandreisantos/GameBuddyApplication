package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchActivity extends AppCompatActivity {

    RecyclerView rv;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference devref;

    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        devref = database.getReference("All Developer");

        rv = findViewById(R.id.rv_as);
        back = findViewById(R.id.tv_back_as);
        rv.setHasFixedSize(true);

        rv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(devref,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {

                        holder.setallGameDeveloper(getApplication(),model.getUid());

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.developer_layout,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();
        rv.setAdapter(firebaseRecyclerAdapter1);

    }
}