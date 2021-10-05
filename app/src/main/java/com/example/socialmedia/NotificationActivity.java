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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

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

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference ntRef,df;
    String userid;
    LinearLayoutManager linearLayoutManager;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        df = FirebaseDatabase.getInstance().getReference("notification");

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rv_new);

        ntRef = database.getReference("notification").child(userid);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ntRef.keepSynced(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<NewMember> options1 =
                new FirebaseRecyclerOptions.Builder<NewMember>()
                        .setQuery(ntRef,NewMember.class)
                        .build();

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
}