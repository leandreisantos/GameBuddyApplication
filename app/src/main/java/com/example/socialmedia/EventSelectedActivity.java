package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.EventController.Eventholder;
import com.example.socialmedia.MarketPlaceController.ViewItemPicture;
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

public class EventSelectedActivity extends AppCompatActivity {

    String postkey_bundle,uid_bundle;
    TextView title_tv,back,tv_name,tv_time,tv_desc,tv_more,tv_add,tv_date,tv_game;
    ImageView iv_event,iv;
    TextView viewProfile;
    TextView interest,going;
    int goingcount,intcount;

    //databse stuff
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference referenceEvent,goingref,goinglist,intref,intlist;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentid = user.getUid();
    DocumentReference reference;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    Boolean intChecker,goingChecker;

    AllUserMember userMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_selected);

        userMember = new AllUserMember();

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            postkey_bundle = extras.getString("p");
            uid_bundle = extras.getString("uid");
        }else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }

        reference = firestore.collection("user").document(uid_bundle);

        referenceEvent = database.getReference("All post").child("event").child(postkey_bundle);
        goingref = database.getReference("event going");
        intref = database.getReference("event int");

        title_tv =  findViewById(R.id.tv_title_aes);
        iv_event = findViewById(R.id.iv_event_aes);
        back = findViewById(R.id.back_aes);
        tv_more = findViewById(R.id.more_aes);
        tv_add = findViewById(R.id.tv_loc_es);
        tv_time = findViewById(R.id.tv_time_es);
        tv_date = findViewById(R.id.tv_date_es);
        tv_desc = findViewById(R.id.tv_desc_es);
        iv = findViewById(R.id.iv_creator_es);
        tv_game = findViewById(R.id.tv_game_es);
        tv_name = findViewById(R.id.tv_name_creator);
        viewProfile = findViewById(R.id.tv_profile_es);
        interest = findViewById(R.id.tv_int_es);
        going = findViewById(R.id.tv_going_es);


        back.setOnClickListener(v -> {
            onBackPressed();
        });
        tv_more.setOnClickListener(v -> showDialog());

        going.setOnClickListener(v -> goingFunction());
        interest.setOnClickListener(v -> intFunction());

    }

    private void intFunction() {
        intChecker = true;

        intref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(intChecker.equals(true)){
                    if(snapshot.child(postkey_bundle).hasChild(currentid)){
                        intref.child(postkey_bundle).child(currentid).removeValue();
                        intlist = database.getReference("int list").child(postkey_bundle).child(currentid);
                        intlist.removeValue();
                        //delete(time);

                        intChecker = false;
                    }else{
                        intref.child(postkey_bundle).child(currentid).setValue(true);
                        intlist = database.getReference("int list").child(postkey_bundle);
                        userMember.setUid(currentid);

                        intlist.child(currentid).setValue(userMember);


                        intChecker = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void goingFunction() {
        goingChecker = true;
        goingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(goingChecker.equals(true)){
                    if(snapshot.child(postkey_bundle).hasChild(currentid)){
                        goingref.child(postkey_bundle).child(currentid).removeValue();
                        goinglist = database.getReference("going list").child(postkey_bundle).child(currentid);
                        goinglist.removeValue();
                        //delete(time);

                        //ntref.child(currentUserid+"l").removeValue();
                        goingChecker = false;
                    }else{
                        goingref.child(postkey_bundle).child(currentid).setValue(true);
                        goinglist = database.getReference("going list").child(postkey_bundle);
                        userMember.setUid(currentid);

                        goinglist.child(currentid).setValue(userMember);

                        goingChecker = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(EventSelectedActivity.this);
        View view = inflater.inflate(R.layout.post_options,null);
        TextView download = view.findViewById(R.id.download_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyurl = view.findViewById(R.id.copyurl_tv_post);
        TextView edit = view.findViewById(R.id.edit_tv_post);
        TextView report_dia = view.findViewById(R.id.report_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(EventSelectedActivity.this)
                .setView(view)
                .create();

        alertDialog.show();

        download.setVisibility(View.GONE);

        if(!currentid.equals(uid_bundle)){
            delete.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }else{
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            report_dia.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        intref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postkey_bundle).hasChild(currentid)) {
                    //int_icon.setImageResource(R.drawable.interested_icon);
                    interest.setCompoundDrawablesWithIntrinsicBounds(R.drawable.interested_icon, 0, 0, 0);
                    intcount = (int) snapshot.child(postkey_bundle).getChildrenCount();
                    interest.setText(Integer.toString(intcount) + " interested");

                } else {
                    //int_icon.setImageResource(R.drawable.not_interested_icon);
                    interest.setCompoundDrawablesWithIntrinsicBounds(R.drawable.not_interested_icon, 0, 0, 0);
                    intcount = (int) snapshot.child(postkey_bundle).getChildrenCount();
                    interest.setText(Integer.toString(intcount) + " interested");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        goingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postkey_bundle).hasChild(currentid)) {
                    //going.setImageResource(R.drawable.not_going_icon);
                    going.setCompoundDrawablesWithIntrinsicBounds(R.drawable.not_going_icon, 0, 0, 0);
                    goingcount = (int) snapshot.child(postkey_bundle).getChildrenCount();
                    going.setText(Integer.toString(goingcount) + " going");

                } else {
                    //g_icon.setImageResource(R.drawable.going_icon);
                    going.setCompoundDrawablesWithIntrinsicBounds(R.drawable.going_icon, 0, 0, 0);
                    goingcount = (int) snapshot.child(postkey_bundle).getChildrenCount();
                    going.setText(Integer.toString(goingcount) + " going");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        reference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){
                        String url = task.getResult().getString("url");
                        Picasso.get().load(url).into(iv);
                    }else Toast.makeText(EventSelectedActivity.this, "No data", Toast.LENGTH_SHORT).show();
                });

        referenceEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                String uri = snapshot.child("postUri").getValue(String.class);
                String location = snapshot.child("address").getValue(String.class);
                String time = snapshot.child("time").getValue(String.class);
                String date = snapshot.child("date").getValue(String.class);
                String game = snapshot.child("game").getValue(String.class);
                String desc = snapshot.child("desc").getValue(String.class);


                Picasso.get().load(uri).into(iv_event);
                title_tv.setText(title);
                tv_add.setText(location);
                tv_time.setText(time);
                tv_date.setText(date);
                tv_game.setText(game);
                tv_desc.setText(desc);

                iv_event.setOnClickListener(v -> {
                    Intent intent = new Intent(EventSelectedActivity.this, ViewItemPicture.class);
                    intent.putExtra("p",uri);
                    startActivity(intent);
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}