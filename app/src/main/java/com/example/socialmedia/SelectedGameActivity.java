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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SelectedGameActivity extends AppCompatActivity {

    String title,keyword;
    TextView title_tv,add_game;
    RecyclerView rv;
    LinearLayoutManager linearLayoutManager;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_game);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("s");
            keyword = extras.getString("k");
        }
        else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();

        db1 = database.getReference(keyword);

        title_tv = findViewById(R.id.tv_title_sga);
        add_game = findViewById(R.id.tv_add_sga);
        rv = findViewById(R.id.rv_asg);

        linearLayoutManager = new LinearLayoutManager(SelectedGameActivity.this);
        rv.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        title_tv.setText(title);

        add_game.setOnClickListener(v -> {
            Intent intent = new Intent(SelectedGameActivity.this,AddGameActivity.class);
            intent.putExtra("k",keyword);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<GameMember> options =
                new FirebaseRecyclerOptions.Builder<GameMember>()
                        .setQuery(db1,GameMember.class)
                        .build();

        FirebaseRecyclerAdapter<GameMember,GameViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<GameMember, GameViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GameViewHolder holder, int position, @NonNull GameMember model) {



                        holder.SetGame(getApplication(),model.getTitle(),model.getDesc(),model.getPostkey(),model.getCat(),model.getPostUri1(),
                                model.getPostUri2(),model.getTime(),model.getDate());

                        String post_key = getItem(position).getPostkey();
                        String title = getItem(position).getTitle();

                        holder.cons.setOnClickListener(v -> {
                            Intent intent = new Intent(SelectedGameActivity.this,GameSelectedSelectedActivity.class);
                            intent.putExtra("k",keyword);
                            intent.putExtra("id",post_key);
                            intent.putExtra("t",title);
                            startActivity(intent);
                        });


                    }

                    @NonNull
                    @Override
                    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.gamelayout,parent,false);

                        return new GameViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();

        rv.setAdapter(firebaseRecyclerAdapter);


    }
}