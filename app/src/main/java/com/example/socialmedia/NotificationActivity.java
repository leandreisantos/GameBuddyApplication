package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView search;
    EditText search_et;
    TextView lbl,back;


    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference ntRef,df,followingref;
    String userid;
    LinearLayoutManager linearLayoutManager;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        df = FirebaseDatabase.getInstance().getReference("notification").child(userid);

        followingref = FirebaseDatabase.getInstance().getReference("Followers").child(userid);


        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rv_new);
        search= findViewById(R.id.search_btn_an);
        search_et = findViewById(R.id.search_et_an);
        lbl = findViewById(R.id.tv_lbl);
        back = findViewById(R.id.tv_back_noti);

        ntRef = database.getReference("notification").child(userid);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ntRef.keepSynced(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        search.setOnClickListener(v -> {
            search.setVisibility(View.GONE);
            lbl.setVisibility(View.GONE);
            search_et.setVisibility(View.VISIBLE);
        });

        back.setOnClickListener(view -> onBackPressed());

        search_et.addTextChangedListener(new TextWatcher() {
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

    @Override
    protected void onStart() {
        super.onStart();
        showRec("n");
    }

    private void showBottomSheet() {
        final Dialog dialog = new Dialog(NotificationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.notification_bottom_sheet);

        ImageView iv = dialog.findViewById(R.id.iv_noti_m);
        TextView delete = dialog.findViewById(R.id.delete_noti_m);
        TextView report = dialog.findViewById(R.id.report_noti_m);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotification();
            }
        });

        Picasso.get().load(image).into(iv);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void deleteNotification() {

        Query query = df.orderByChild("uid").equalTo(userid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showRec(String id){
        FirebaseRecyclerOptions<NewMember> options1 = null;
        if(id.equals("n")){
                    options1 =
                    new FirebaseRecyclerOptions.Builder<NewMember>()
                            .setQuery(ntRef,NewMember.class)
                            .build();
        }else if(id.equals("s")){

            String query = search_et.getText().toString();
            Query search = ntRef.orderByChild("name").startAt(query).endAt(query+"\uf0ff");
                    options1 =
                    new FirebaseRecyclerOptions.Builder<NewMember>()
                            .setQuery(search,NewMember.class)
                            .build();
        }

        FirebaseRecyclerAdapter<NewMember,NewViewHolder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<NewMember, NewViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull NewViewHolder holder, int position, @NonNull NewMember model) {

                        holder.setNt(getApplication(),model.getUrl(),model.getName(),model.getText(),model.getUid(),model.getSeen(),model.getAction());
                        String name = getItem(position).getName();
                        String uid = getItem(position).getUid();
                        String url = getItem(position).getUid();
                        image = getItem(position).getUrl();

                        holder.nametv.setOnClickListener(v -> {
                            if (userid.equals(uid)) {
                                Intent intent = new Intent(NotificationActivity.this,MyProfileActivity.class);
                                startActivity(intent);

                            }else {
                                Intent intent = new Intent(NotificationActivity.this,ShowUser.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });
                        holder.more.setOnClickListener(v -> showBottomSheet());

                        holder.accept.setOnClickListener(v -> {
                            Toast.makeText(NotificationActivity.this, "accepted", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @NonNull
                    @Override
                    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.new_layout,parent,false);

                        return new NewViewHolder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
}