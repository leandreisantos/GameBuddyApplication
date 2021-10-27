package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
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

import java.util.Collections;

public class EditPostActivity extends AppCompatActivity {

    TextView close,cansee,save;
    EditText et_desc;
    ImageView dp,pic;
    CardView cv;
    PlayerView playerView;
    ProgressBar pb;

    String status,post_key,temp_desc;
    String privacy_post;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("p");
            post_key = extras.getString("k");
        }
        else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();

        reference = database.getReference("All post").child(status).child(post_key);

        close = findViewById(R.id.tv_close_ep);
        dp = findViewById(R.id.iv_dp_ep);
        pic = findViewById(R.id.iv_pic_ep);
        et_desc = findViewById(R.id.et_desc_ep);
        cansee = findViewById(R.id.cansee_post);
        save = findViewById(R.id.tv_save_ep);
        cv = findViewById(R.id.cv_pic);
        playerView = findViewById(R.id.exoplayer_item_post);
        pb = findViewById(R.id.pb_ep);

        close.setOnClickListener(v -> onBackPressed());

        save.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            temp_desc = et_desc.getText().toString();
            reference.child("desc").setValue(temp_desc);
            reference.child("privacy").setValue(privacy_post);
            Toast.makeText(this, "save changes successfully", Toast.LENGTH_SHORT).show();
            onBackPressed();
        });
        cansee.setOnClickListener(v -> showBottomsheet());
    }

    private void showBottomsheet() {

        final Dialog dialog = new Dialog(EditPostActivity.this);
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


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String desc_s = snapshot.child("desc").getValue(String.class);
                String username = snapshot.child("name").getValue(String.class);
                String url = snapshot.child("url").getValue(String.class);
                String image = snapshot.child("postUri").getValue(String.class);
                String type = snapshot.child("type").getValue(String.class);
                String privacy = snapshot.child("privacy").getValue(String.class);

                privacy_post = privacy;

                if(privacy.equals("p")){
                    cansee.setText("Everyone can comment and see your post");
                    cansee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_public_24, 0, 0, 0);
                }else{
                    cansee.setText("Only your friends can comment and see your post");
                    cansee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.privacy_icon, 0, 0, 0);
                }


                if(type.equals("txt")){
                    cv.setVisibility(View.GONE);

                    et_desc.setText(desc_s);
                    Picasso.get().load(url).into(dp);

                }else if(type.equals("iv")){
                    cv.setVisibility(View.VISIBLE);
                    Picasso.get().load(image).into(pic);
                    playerView.setVisibility(View.GONE);
                    et_desc.setText(desc_s);
                    Picasso.get().load(url).into(dp);
                }else if(type.equals("vv")){
                    cv.setVisibility(View.VISIBLE);

                    pic.setVisibility(View.GONE);
                    playerView.setVisibility(View.VISIBLE);
                    et_desc.setText(desc_s);
                    Picasso.get().load(url).into(dp);
                    try{
                        SimpleExoPlayer simpleExoPlayer= new SimpleExoPlayer.Builder(EditPostActivity.this).build();
                        playerView.setPlayer(simpleExoPlayer);
                        MediaItem mediaItem = MediaItem.fromUri(image);
                        simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                        simpleExoPlayer.prepare();
                        simpleExoPlayer.setPlayWhenReady(false);


                    }catch(Exception e){
                        Toast.makeText(EditPostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EditPostActivity.this, "Unknown type", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}