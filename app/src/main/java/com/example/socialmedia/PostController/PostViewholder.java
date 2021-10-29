package com.example.socialmedia.PostController;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.GetCurrentTime;
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

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewholder extends RecyclerView.ViewHolder {

    public ImageView imageViewprofile,iv_post,imageViewprofile_s,iv_post_s;
    public TextView tv_name,tv_desc,tv_likes,tv_comment,tv_time,tv_nameprofile;

    public TextView tv_nameprofile_s,tv_desc_s,tv_time_s;
    public ImageButton likebtn,menuoptions,commentbtn,sharebtn;
    DatabaseReference likesref,commentref;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    public int likescount,commentcount;
    CardView cv_main;
    ConstraintLayout share_cl;

    GetCurrentTime gc = new GetCurrentTime();
    String utime = gc.ctime();


    public PostViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void SetPost(FragmentActivity activity, String name,String url,String postUri,String time,String uid,String type,String desc,String postprivacy,String date,String post_key,
                        String descSharer,String postkeySharer,String uidSharer,String privacySharer,String sharerType,String timeShare,
                        String dateSharer,String nameSharer,String urlSharer){

        imageViewprofile = itemView.findViewById(R.id.iv_profile_item_post);
        iv_post = itemView.findViewById(R.id.iv_post_item);
       // tv_comment = itemView.findViewById(R.id.commentbutton_posts);
        tv_desc = itemView.findViewById(R.id.tv_desc_post);
        commentbtn = itemView.findViewById(R.id.commentbutton_posts);
        likebtn = itemView.findViewById(R.id.likebutton_posts);
        sharebtn = itemView.findViewById(R.id.sharebutton_posts);
        tv_likes = itemView.findViewById(R.id.tv_likes_post);
        menuoptions = itemView.findViewById(R.id.morebutton_posts);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        tv_nameprofile = itemView.findViewById(R.id.tv_name_post);
        cv_main = itemView.findViewById(R.id.cv_iv_main);
        share_cl = itemView.findViewById(R.id.cl_s);

        tv_nameprofile_s = itemView.findViewById(R.id.tv_name_post_s);
        tv_desc_s = itemView.findViewById(R.id.tv_desc_post_s);
        tv_time_s = itemView.findViewById(R.id.tv_time_post_s);
        imageViewprofile_s = itemView.findViewById(R.id.iv_profile_item_post_s);
        iv_post_s = itemView.findViewById(R.id.iv_post_item_s);


        SimpleExoPlayer exoPlayer;
        PlayerView playerView = itemView.findViewById(R.id.exoplayer_item_post);
        PlayerView playerView_s = itemView.findViewById(R.id.exoplayer_item_post_s);


//            Picasso.get().load(url).into(imageViewprofile);
//            Picasso.get().load(postUri).into(iv_post);
//            tv_desc.setText(desc);
//            tv_time.setText(time);
//            tv_nameprofile.setText(name);
//            playerView.setVisibility(View.INVISIBLE);

        if(sharerType != null){
            if(sharerType.equals("txt")){
                cv_main.setVisibility(View.GONE);
                share_cl.setVisibility(View.VISIBLE);

                tv_desc.setText(descSharer);
                tv_time.setText(timeShare);
                tv_nameprofile.setText(nameSharer);
                Picasso.get().load(urlSharer).into(imageViewprofile);

                if(type != null){
                    if(type.equals("txt")){
                        Picasso.get().load(url).into(imageViewprofile_s);
                        setItemShare(desc,time,name);
                        playerView.setVisibility(View.INVISIBLE);
                        iv_post_s.setVisibility(View.GONE);
                        playerView_s.setVisibility(View.GONE);
                    }
                    if(type.equals("iv")){
                        Picasso.get().load(url).into(imageViewprofile_s);
                        //iv_post.requestLayout();
                        //iv_post.getLayoutParams().height = 3000;
                        Picasso.get().load(postUri).into(iv_post_s);
                        setItemShare(desc,time,name);
                        playerView_s.setVisibility(View.INVISIBLE);
                    }
                    if(type.equals("vv")){
                        iv_post_s.setVisibility(View.INVISIBLE);
                        setItemShare(desc,time,name);
                        Picasso.get().load(url).into(imageViewprofile_s);
                        try{
                            SimpleExoPlayer simpleExoPlayer= new SimpleExoPlayer.Builder(activity).build();
                            playerView_s.setPlayer(simpleExoPlayer);
                            MediaItem mediaItem = MediaItem.fromUri(postUri);
                            simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                            simpleExoPlayer.prepare();
                            simpleExoPlayer.setPlayWhenReady(false);
                        }catch(Exception e){
                            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        } else{
            if(type != null){

                if(type.equals("txt")){
                    Picasso.get().load(url).into(imageViewprofile);
                    setItem(desc,time,name);
                    playerView.setVisibility(View.INVISIBLE);
                    iv_post.setVisibility(View.GONE);
                    playerView.setVisibility(View.GONE);
                }
                if(type.equals("iv")){
                    Picasso.get().load(url).into(imageViewprofile);
                    //iv_post.requestLayout();
                    //iv_post.getLayoutParams().height = 3000;
                    Picasso.get().load(postUri).into(iv_post);
                    setItem(desc,time,name);
                    playerView.setVisibility(View.INVISIBLE);
                }if(type.equals("vv")){
                    iv_post.setVisibility(View.INVISIBLE);
                    setItem(desc,time,name);
                    Picasso.get().load(url).into(imageViewprofile);
                    try{
//                    Uri uri = Uri.parse(postUri);
//                    SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(activity);
//                    DefaultHttpDataSourceFactory df = new DefaultHttpDataSourceFactory("video");
//                    ExtractorsFactory ef = new DefaultExtractorsFactory();
//                    MediaSource mediaSource = new ExtractorMediaSource(uri,df,ef,null,null);
//                    playerView.setPlayer(player);
//                    player.prepare(mediaSource);
//                    player.setPlayWhenReady(false);
                        SimpleExoPlayer simpleExoPlayer= new SimpleExoPlayer.Builder(activity).build();
                        playerView.setPlayer(simpleExoPlayer);
                        MediaItem mediaItem = MediaItem.fromUri(postUri);
                        simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                        simpleExoPlayer.prepare();
                        simpleExoPlayer.setPlayWhenReady(false);


                    }catch(Exception e){
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                Toast.makeText(activity, "Empty", Toast.LENGTH_SHORT).show();
            }
        }



    }

    private void setItem(String desc,String time,String name) {
        tv_desc.setText(desc);
        tv_time.setText(time);
        tv_nameprofile.setText(name);
    }
    private void setItemShare(String desc,String time,String name) {
        tv_desc_s.setText(desc);
        tv_time_s.setText(time);
        tv_nameprofile_s.setText(name);
    }

    public void likechecker(String postKey) {

        likebtn = itemView.findViewById(R.id.likebutton_posts);


        likesref = database.getReference("post likes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();


        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(postKey).hasChild(uid)){
                    likebtn.setImageResource(R.drawable.ic_baseline_favorite_24);
                    likescount = (int)snapshot.child(postKey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount));

                }else{
                    likebtn.setImageResource(R.drawable.ic_baseline_dislike_24);
                    likescount = (int)snapshot.child(postKey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void commentchecker(String postKey) {

        tv_comment = itemView.findViewById(R.id.tv_comment_post);
        commentref = database.getReference("All posts").child(postKey).child("Comments");

        commentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    commentcount = (int)snapshot.getChildrenCount();
                    tv_comment.setText(Integer.toString(commentcount));

                }catch(Exception e){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
