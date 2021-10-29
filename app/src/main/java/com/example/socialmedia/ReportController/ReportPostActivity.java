package com.example.socialmedia.ReportController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
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

public class ReportPostActivity extends AppCompatActivity {

    TextView close,name,desc,send;
    String status,post_key;
    ImageView dp,pic;
    EditText et_reason;
    ProgressBar pb;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,refReport;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    CardView cv;
    PlayerView playerView;

    ReportMember member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("p");
            post_key = extras.getString("k");
        }
        else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();

        member = new ReportMember();

        reference = database.getReference("All post").child(status).child(post_key);
        refReport = database.getReference("All report").child(status).child(post_key);

        close = findViewById(R.id.tv_close_rp);
        name = findViewById(R.id.tv_name_rp);
        desc = findViewById(R.id.tv_desc_rp);
        cv = findViewById(R.id.cv_pic_rp);
        dp = findViewById(R.id.iv_dp_rp);
        pic = findViewById(R.id.iv_pic_rp);
        playerView = findViewById(R.id.exoplayer_item_post);
        et_reason = findViewById(R.id.et_reason_rp);
        send = findViewById(R.id.tv_btn_send_rp);
        pb = findViewById(R.id.pb_rp);

        close.setOnClickListener(v -> onBackPressed());

        send.setOnClickListener(v -> doReport());
    }

    private void doReport() {

        pb.setVisibility(View.VISIBLE);
        et_reason.setEnabled(false);
        send.setEnabled(false);

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        String reason = et_reason.getText().toString();

        if(!TextUtils.isEmpty(reason)){

            member.setPostkey(post_key);
            member.setReporterId(currentuid);
            member.setReason(reason);
            member.setTime(savetime);
            member.setDate(savedate);

            refReport.child(currentuid).setValue(member);
            Toast.makeText(this, "Report send to admin wait for response", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
            onBackPressed();

        }else{
            pb.setVisibility(View.GONE);
            et_reason.setEnabled(true);
            send.setEnabled(true);
            Toast.makeText(this, "Input some reason.", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String desc_s = snapshot.child("desc").getValue(String.class);
                String username = snapshot.child("name").getValue(String.class);
                String url = snapshot.child("url").getValue(String.class);
                String image = snapshot.child("postUri").getValue(String.class);
                String type = snapshot.child("type").getValue(String.class);

                if(type.equals("txt")){
                    cv.setVisibility(View.GONE);

                    name.setText(username);
                    desc.setText(desc_s);
                    Picasso.get().load(url).into(dp);

                }else if(type.equals("iv")){
                    cv.setVisibility(View.VISIBLE);
                    Picasso.get().load(image).into(pic);
                    playerView.setVisibility(View.GONE);
                    name.setText(username);
                    desc.setText(desc_s);
                    Picasso.get().load(url).into(dp);
                }else if(type.equals("vv")){
                    cv.setVisibility(View.VISIBLE);
                    pic.setVisibility(View.GONE);
                    name.setText(username);
                    desc.setText(desc_s);
                    Picasso.get().load(url).into(dp);
                    try{
                        SimpleExoPlayer simpleExoPlayer= new SimpleExoPlayer.Builder(ReportPostActivity.this).build();
                        playerView.setPlayer(simpleExoPlayer);
                        MediaItem mediaItem = MediaItem.fromUri(image);
                        simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                        simpleExoPlayer.prepare();
                        simpleExoPlayer.setPlayWhenReady(false);


                    }catch(Exception e){
                        Toast.makeText(ReportPostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ReportPostActivity.this, "Unknown type", Toast.LENGTH_SHORT).show();
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}