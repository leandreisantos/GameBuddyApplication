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

import com.example.socialmedia.AllUserMember;
import com.example.socialmedia.NewMember;
import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AllEventActivity extends AppCompatActivity {

    SwipeRefreshLayout sp;

    RecyclerView rv;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,intref,intlist,ntref,goingref,goinglist;
    TextView close;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    Boolean intChecker,goingChecker;

    AllUserMember userMember;
    NewMember newMember;
    String name_result,url_result,uid_result,usertoken;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserid = user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_event);

        userMember = new AllUserMember();
        newMember = new NewMember();

        reference = database.getReference("All post").child("event");
        intref = database.getReference("event int");
        goingref = database.getReference("event going");
        ntref = database.getReference("notification").child(currentUserid);
        documentReference = db.collection("user").document(currentUserid);
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

        documentReference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){
                        name_result = task.getResult().getString("name");
                        url_result = task.getResult().getString("url");
                        uid_result = task.getResult().getString("uid");
                    }
                });

        FirebaseRecyclerOptions<EventMember> options =
                new FirebaseRecyclerOptions.Builder<EventMember>()
                        .setQuery(reference,EventMember.class)
                        .build();

        FirebaseRecyclerAdapter<EventMember,Eventholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<EventMember, Eventholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Eventholder holder, int position, @NonNull EventMember model) {

                        holder.setAllEvent(getApplication(),model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),
                                model.getType(),model.getDesc(),model.getTitle(),model.getPostkey());

                        String post_key = getItem(position).getPostkey();
                        String name = getItem(position).getName();
                        String url = getItem(position).getPostUri();

                        holder.intchecker(post_key);
                        holder.goingchecker(post_key);

                        holder.g_icon.setOnClickListener(v -> {
                            goingChecker = true;

                            goingref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(goingChecker.equals(true)){
                                        if(snapshot.child(post_key).hasChild(currentUserid)){
                                            goingref.child(post_key).child(currentUserid).removeValue();
                                            goinglist = database.getReference("going list").child(post_key).child(currentUserid);
                                            goinglist.removeValue();
                                            //delete(time);

                                            ntref.child(currentUserid+"l").removeValue();
                                            goingChecker = false;
                                        }else{
                                            goingref.child(post_key).child(currentUserid).setValue(true);
                                            goinglist = database.getReference("going list").child(post_key);
                                            userMember.setName(name);
                                            userMember.setUid(currentUserid);
                                            userMember.setUrl(url);

                                            goinglist.child(currentUserid).setValue(userMember);

                                            newMember.setName(name_result);
                                            newMember.setUid(currentUserid);
                                            newMember.setUrl(url_result);
                                            newMember.setSeen("no");
                                            newMember.setText("Like your post");
                                            newMember.setAction("L");

                                            ntref.child(currentUserid+"l").setValue(newMember);
                                            //sendNotification(name_result,userid);


                                            goingChecker = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });

                        holder.int_icon.setOnClickListener(v -> {

                            intChecker = true;

                            intref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(intChecker.equals(true)){
                                        if(snapshot.child(post_key).hasChild(currentUserid)){
                                            intref.child(post_key).child(currentUserid).removeValue();
                                            intlist = database.getReference("int list").child(post_key).child(currentUserid);
                                            intlist.removeValue();
                                            //delete(time);

                                            ntref.child(currentUserid+"l").removeValue();
                                            intChecker = false;
                                        }else{
                                            intref.child(post_key).child(currentUserid).setValue(true);
                                            intlist = database.getReference("int list").child(post_key);
                                            userMember.setName(name);
                                            userMember.setUid(currentUserid);
                                            userMember.setUrl(url);

                                            intlist.child(currentUserid).setValue(userMember);

                                            newMember.setName(name_result);
                                            newMember.setUid(currentUserid);
                                            newMember.setUrl(url_result);
                                            newMember.setSeen("no");
                                            newMember.setText("Like your post");
                                            newMember.setAction("L");

                                            ntref.child(currentUserid+"l").setValue(newMember);
                                            //sendNotification(name_result,userid);


                                            intChecker = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        });

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