package com.example.socialmedia.EventController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllEventActivity extends AppCompatActivity {

    SwipeRefreshLayout sp;

    RecyclerView rv;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;
    TextView close;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_event);

        reference = database.getReference("All post").child("event");
        sp = findViewById(R.id.sp_ae);
        close = findViewById(R.id.tv_close_ae);

        rv = findViewById(R.id.rv_el);
        rv.setHasFixedSize(false);
        rv.setLayoutManager(new LinearLayoutManager(this));

        sp.setOnRefreshListener(() -> sp.setRefreshing(false));

        close.setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<EventMember> options =
                new FirebaseRecyclerOptions.Builder<EventMember>()
                        .setQuery(reference,EventMember.class)
                        .build();

        FirebaseRecyclerAdapter<EventMember,Eventholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<EventMember, Eventholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Eventholder holder, int position, @NonNull EventMember model) {

                        holder.setAllEvent(getApplication(),model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),
                                model.getType(),model.getDesc(),model.getTitle());

                    }


                    @NonNull
                    @Override
                    public Eventholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.event_item_layout,parent,false);

                        return new Eventholder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();

        rv.setAdapter(firebaseRecyclerAdapter);
    }
}