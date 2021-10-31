package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.PostController.PostActivity;
import com.example.socialmedia.PostController.Postmember;
import com.example.socialmedia.ReportController.ReportPostActivity;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

public class ShareActivity extends AppCompatActivity {

    String status,post_key;
    TextView close,name,desc,share,sharer,cansee;

    EditText et_desc;
    ImageView dp,pic,dp_sharer;

    CardView cv;
    PlayerView playerView;

    ProgressBar pb;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,reference2,db3,db4;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    String privacy_post= "p";
    Postmember postmember;

    String username_c,url_c;

    String desc_main=null,name_main=null,uri_main=null,time_main=null,uid_main=null,url_main
            =null,type_main=null,privacy_main=null,date_main=null,postkey_main=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("p");
            post_key = extras.getString("k");
        }
        else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();

        postmember = new Postmember();

        reference = database.getReference("All post").child(status).child(post_key);
        reference2 = database.getReference("All users").child(currentuid);
        db3 = database.getReference("All post").child("public");
        db4 = database.getReference("All post").child(currentuid);

        close = findViewById(R.id.tv_close_share);
        name = findViewById(R.id.tv_name_rp);
        desc = findViewById(R.id.tv_desc_rp);
        cv = findViewById(R.id.cv_pic_rp);
        dp = findViewById(R.id.iv_dp_rp);
        pic = findViewById(R.id.iv_pic_rp);
        playerView = findViewById(R.id.exoplayer_item_post);
        et_desc = findViewById(R.id.et_desc_as);
        share = findViewById(R.id.tv_btn_share);
        dp_sharer = findViewById(R.id.iv_dp);
        sharer = findViewById(R.id.tv_name_as);
        cansee = findViewById(R.id.cansee_post);
        pb = findViewById(R.id.pb_as);

        close.setOnClickListener(v -> onBackPressed());
        cansee.setOnClickListener(v -> showBottomsheet());
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dopost();
            }
        });
    }

    private void Dopost() {

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        String desc = et_desc.getText().toString();

        if(TextUtils.isEmpty(desc)) desc = "";

        String id1 = db3.push().getKey();
        postmember.setDesc(desc_main);
        postmember.setName(name_main);
        postmember.setPostUri(uri_main);
        postmember.setTime(time_main);
        postmember.setUid(uid_main);
        postmember.setUrl(url_main);
        postmember.setType(type_main);
        postmember.setPrivacy(privacy_main);
        postmember.setDate(date_main);
        postmember.setPostkey(postkey_main);

        postmember.setDescSharer(desc);
        postmember.setPostkeySharer(id1);
        postmember.setUidSharer(currentuid);
        postmember.setPrivacySharer(privacy_post);
        postmember.setSharerType("txt");
        postmember.setTimeShare(savetime);
        postmember.setNameSharer(username_c);
        postmember.setUrlSharer(url_c);

        //for both
        db3.child(id1).setValue(postmember);
        db4.child(id1).setValue(postmember);

        pb.setVisibility(View.INVISIBLE);

        Toast.makeText(ShareActivity.this, "Share posted", Toast.LENGTH_SHORT).show();
        onBackPressed();

    }

    private void showBottomsheet() {

        final Dialog dialog = new Dialog(ShareActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_menu_public_post);
        ConstraintLayout public_tv = dialog.findViewById(R.id.cl2);
        ConstraintLayout private_tv = dialog.findViewById(R.id.cl3);
        ImageView active_p = dialog.findViewById(R.id.iv_sta_p_bmp);
        ImageView active_pr = dialog.findViewById(R.id.iv_sta_pr_bmp);

        if(privacy_post.equals("p")){
            active_p.setVisibility(View.VISIBLE);
            active_pr.setVisibility(View.GONE);
        }else{
            active_p.setVisibility(View.GONE);
            active_pr.setVisibility(View.VISIBLE);
        }

        public_tv.setOnClickListener(v -> {
            active_p.setVisibility(View.VISIBLE);
            active_pr.setVisibility(View.GONE);
            privacy_post ="p";
            cansee.setText("Everyone can comment and see your post");
            cansee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_public_24, 0, 0, 0);

        });
        private_tv.setOnClickListener(v -> {
            active_p.setVisibility(View.GONE);
            active_pr.setVisibility(View.VISIBLE);
            privacy_post ="pr";
            cansee.setText("Only your friends can comment and see your post");
            cansee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.privacy_icon, 0, 0, 0);

        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username_c = snapshot.child("name").getValue(String.class);
                url_c = snapshot.child("url").getValue(String.class);

                Picasso.get().load(url_c).into(dp_sharer);
                sharer.setText(username_c);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    desc_main = snapshot.child("desc").getValue(String.class);
                    name_main = snapshot.child("name").getValue(String.class);
                    url_main = snapshot.child("url").getValue(String.class);
                    uri_main = snapshot.child("postUri").getValue(String.class);
                    time_main = snapshot.child("time").getValue(String.class);
                    uid_main = snapshot.child("uid").getValue(String.class);
                    type_main = snapshot.child("type").getValue(String.class);
                    privacy_main = snapshot.child("privacy").getValue(String.class);
                    date_main = snapshot.child("date").getValue(String.class);
                    postkey_main = snapshot.child("postkey").getValue(String.class);

                    if(type_main.equals("txt")){
                        cv.setVisibility(View.GONE);

                        name.setText(name_main);
                        desc.setText(desc_main);
                        Picasso.get().load(url_main).into(dp);

                    }else if(type_main.equals("iv")){
                        cv.setVisibility(View.VISIBLE);
                        Picasso.get().load(uri_main).into(pic);
                        playerView.setVisibility(View.GONE);
                        name.setText(name_main);
                        desc.setText(desc_main);
                        Picasso.get().load(url_main).into(dp);
                    }else if(type_main.equals("vv")){
                        cv.setVisibility(View.VISIBLE);
                        pic.setVisibility(View.GONE);
                        name.setText(name_main);
                        desc.setText(desc_main);
                        Picasso.get().load(url_main).into(dp);
                        try{
                            SimpleExoPlayer simpleExoPlayer= new SimpleExoPlayer.Builder(ShareActivity.this).build();
                            playerView.setPlayer(simpleExoPlayer);
                            MediaItem mediaItem = MediaItem.fromUri(uri_main);
                            simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                            simpleExoPlayer.prepare();
                            simpleExoPlayer.setPlayWhenReady(false);


                        }catch(Exception e){
                            Toast.makeText(ShareActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ShareActivity.this, "Unknown type", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}