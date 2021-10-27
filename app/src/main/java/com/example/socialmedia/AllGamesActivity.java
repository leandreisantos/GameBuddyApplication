package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AllGamesActivity extends AppCompatActivity {

    String title,keyword;
    TextView title_tv,add_game,back;
    RecyclerView rv;
    LinearLayoutManager linearLayoutManager;
    EditText search_game;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_games);

        keyword = "All game";

        db1 = database.getReference("All game");

        back = findViewById(R.id.tv_back_sga);
        search_game = findViewById(R.id.et_search_sga);

        rv = findViewById(R.id.rv_asg);
        linearLayoutManager = new LinearLayoutManager(AllGamesActivity.this);
        rv.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(AllGamesActivity.this,MainActivity.class);
            startActivity(intent);
        });

        search_game.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showRec("s");
            }
        });
    }

    private void showRec(String id) {

        FirebaseRecyclerOptions<GameMember> options = null;

        if (id.equals("n")) {
            options =
                    new FirebaseRecyclerOptions.Builder<GameMember>()
                            .setQuery(db1, GameMember.class)
                            .build();
        } else if (id.equals("s")) {

            String query = search_game.getText().toString();
            Query search = db1.orderByChild("title").startAt(query).endAt(query + "\uf0ff");
            options =
                    new FirebaseRecyclerOptions.Builder<GameMember>()
                            .setQuery(search, GameMember.class)
                            .build();
        }

        FirebaseRecyclerAdapter<GameMember,GameViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<GameMember, GameViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GameViewHolder holder, int position, @NonNull GameMember model) {



                        holder.SetGame(getApplication(),model.getTitle(),model.getDesc(),model.getPostkey(),model.getCat(),model.getPostUri1(),
                                model.getPostUri2(),model.getTime(),model.getDate());

                        String post_key = getItem(position).getPostkey();
                        String title = getItem(position).getTitle();

                        holder.cons.setOnClickListener(v -> {
                            Intent intent = new Intent(AllGamesActivity.this,GameSelectedSelectedActivity.class);
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

    @Override
    protected void onStart() {
        super.onStart();
        showRec("n");
    }
}