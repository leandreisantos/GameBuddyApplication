package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewUserActivity extends AppCompatActivity {

    TextView close;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference,databaseReference1,databaseReference2,postnoref,db1,db2,ntref;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference,documentReference1;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = user.getUid();

    String url,name,userid,temp_p;
    String uidreq,namereq,urlreq,professionreq;

    TextView post,name_tv,prof,send,follow;
    ImageView bg,dp,cp,vp;

    int followercount,postiv,postvv;

    RequestMember requestMember;
    NewMember newMember;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            url = extras.getString("u");
            name = extras.getString("n");
            userid = extras.getString("uid");
        }else {
            Toast.makeText(this, "Privact account", Toast.LENGTH_SHORT).show();
        }

        newMember = new NewMember();
        requestMember = new RequestMember();

        close = findViewById(R.id.tv_close_vu);
        post = findViewById(R.id.tv_post);
        bg = findViewById(R.id.iv_bg_vu);
        bg = findViewById(R.id.iv_bg_vu);
        dp = findViewById(R.id.iv_mp);
        name_tv = findViewById(R.id.name_mp);
        prof = findViewById(R.id.prof_mp);
        cp = findViewById(R.id.iv_cp);
        vp = findViewById(R.id.iv_pp);
        send = findViewById(R.id.tv_message_vu);
        follow = findViewById(R.id.tv_follow_vu);

        db1 = database.getReference("All post").child(userid);
        documentReference = db.collection("user").document(userid);
        databaseReference = database.getReference("Requests").child(userid);
        documentReference1 = db.collection("user").document(currentUserId);
        ntref = database.getReference("notification").child(userid);


        close.setOnClickListener(v -> onBackPressed());

        send.setOnClickListener(view -> {
            Intent intent = new Intent(ViewUserActivity.this,MessageActivity.class);
            intent.putExtra("n",name);
            intent.putExtra("u",url);
            intent.putExtra("uid",userid);
            startActivity(intent);
        });

        follow.setOnClickListener(v -> {
            String status = follow.getText().toString();
            if (status.equals("Follow")){
                follow();
            }else if (status.equals("Requested")){
                delRequest();
            }else if (status.equals("Following")){
                unFollow();
            }
        });


    }

    private void unFollow() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUserActivity.this);
        builder.setTitle("Unfollow")
                .setMessage("Are you sure to Unfollow?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference1.child(currentUserId).removeValue();
                        ntref.child(currentUserId+"f").removeValue();
                        follow.setText("Follow");
                        //followers_tv.setText("0");
                        Toast.makeText(ViewUserActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialogInterface, i) -> {

                });
        builder.create();
        builder.show();
    }

    private void delRequest() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        databaseReference.child(currentUserId).removeValue();
        follow.setText("Follow");
    }

    private void follow() {

        requestMember.setUserid(currentUserId);
        requestMember.setProfession(professionreq);
        requestMember.setUrl(urlreq);
        requestMember.setName(namereq);

        databaseReference.child(currentUserId).setValue(requestMember);

        newMember.setName(name);
        newMember.setUid(currentUserId);
        newMember.setUrl(url);
        newMember.setSeen("no");
        newMember.setText("Started Following you ");
        newMember.setAction("f");

        //sendNotification(userid, name_result, "sf");
        ntref.child(currentUserId + "f").setValue(newMember);


        follow.setText("Requested");
    }

    @Override
    protected void onStart() {
        super.onStart();

        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postiv = (int)snapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        post.setText(String.valueOf(postiv));


        documentReference.get()
                .addOnCompleteListener(task -> {

                    if (task.getResult().exists()){
                        String name_result = task.getResult().getString("name");
                        String prof_result = task.getResult().getString("prof");
                        String Url = task.getResult().getString("url");
                        String background = task.getResult().getString("url2");
                        String p = task.getResult().getString("privacy");

                        temp_p = p;

                        Picasso.get().load(Url).into(dp);
                        Picasso.get().load(background).into(bg);
                        name_tv.setText(name_result);
                        prof.setText(prof_result);

                        if (p.equals("Public")){

                        }else {
                            cp.setBackgroundResource(R.drawable.ic_baseline_lock_24);
                            vp.setBackgroundResource(R.drawable.ic_baseline_lock_24);
                        }


                    }else Toast.makeText(ViewUserActivity.this, "No Profile exist", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Toast.makeText(ViewUserActivity.this, "Error database", Toast.LENGTH_SHORT).show());

        documentReference1.get()
                .addOnCompleteListener(task -> {

                    if (task.getResult().exists()){
                        namereq = task.getResult().getString("name");
                        professionreq = task.getResult().getString("prof");
                        urlreq = task.getResult().getString("url");

                    }else {
                        Toast.makeText(ViewUserActivity.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(currentUserId)){
                    follow.setText("Requested");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
    
}