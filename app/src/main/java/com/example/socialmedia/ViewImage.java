package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {

    String id,url,postkey,name;
    ImageButton like,comment,share;
    TextView clike,ccomment,back;
    ImageView iv_main;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference likesref,likelist,ntref,commentref;
    Boolean likechecker = false;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserid = user.getUid();

    AllUserMember userMember;

    int likescount,commentcount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            id = extras.getString("i");
            url = extras.getString("iv");
            postkey = extras.getString("p");
            name = extras.getString("n");
        }

        userMember = new AllUserMember();

        likesref = database.getReference("post likes");
        ntref = database.getReference("notification").child(currentUserid);

        iv_main = findViewById(R.id.iv_vm);
        like = findViewById(R.id.likebutton_posts);
        comment = findViewById(R.id.commentbutton_posts);

        clike = findViewById(R.id.tv_likes_view_im);
        ccomment = findViewById(R.id.tv_comment_view_im);
        back = findViewById(R.id.back_vm);

        like.setOnClickListener(v -> likefunction());
        comment.setOnClickListener(v -> {
            Intent intent = new Intent(ViewImage.this,CommentsActivity.class);
            intent.putExtra("postkey",postkey);
            intent.putExtra("name",name);
            intent.putExtra("url",url);
            intent.putExtra("uid",id);
            startActivity(intent);
        });

        back.setOnClickListener(v -> onBackPressed());

    }

    private void likefunction() {
        likechecker = true;

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(likechecker.equals(true)){
                    if(snapshot.child(postkey).hasChild(currentUserid)){
                        likesref.child(postkey).child(currentUserid).removeValue();
                        likelist = database.getReference("like list").child(postkey).child(currentUserid);
                        likelist.removeValue();
                        //delete(time);

                        ntref.child(currentUserid+"l").removeValue();
                        likechecker = false;
                    }else{
                        likesref.child(postkey).child(currentUserid).setValue(true);
                        likelist = database.getReference("like list").child(postkey);
                        userMember.setName(name);
                        userMember.setUid(currentUserid);
                        userMember.setUrl(url);

                        likelist.child(currentUserid).setValue(userMember);

                        likechecker = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Picasso.get().load(url).into(iv_main);

        commentref = database.getReference("All posts").child(postkey).child("Comments");

        commentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    commentcount = (int)snapshot.getChildrenCount();
                    ccomment.setText(Integer.toString(commentcount));

                }catch(Exception e){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(postkey).hasChild(currentUserid)){
                    like.setImageResource(R.drawable.ic_baseline_favorite_24);
                    likescount = (int)snapshot.child(postkey).getChildrenCount();
                    clike.setText(Integer.toString(likescount));

                }else{
                    like.setImageResource(R.drawable.ic_baseline_dislike_24);
                    likescount = (int)snapshot.child(postkey).getChildrenCount();
                    clike.setText(Integer.toString(likescount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }
}