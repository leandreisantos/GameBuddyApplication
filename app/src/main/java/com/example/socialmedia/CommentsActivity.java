package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class CommentsActivity extends AppCompatActivity {

    ImageView usernameImageview,post_pic_user;
    TextView usernameTextview,descTextview,close;
    TextView commentsBtn;
    EditText commentsEdittext;
    String url,name,post_key,userid,bundleuid;
    DatabaseReference Commentref,userCommentref,likesref,ntref;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    String name_result,age_result,Url,uid,bio_result,web_result,email_result,usertoken,desc;
    RecyclerView recyclerView;
    Boolean likeChecker = false;

    NewMember newMember;
    CommentsMember commentsMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        commentsMember = new CommentsMember();

        newMember = new NewMember();
        recyclerView = findViewById(R.id.recycler_view_comments);

        recyclerView.setHasFixedSize(true);
        //   MediaController mediaController;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentsBtn = findViewById(R.id.btn_comments);
        usernameImageview = findViewById(R.id.imageviewUser_comment);
        usernameTextview = findViewById(R.id.name_comments_tv);
        descTextview = findViewById(R.id.tv_desc_ac);
        commentsEdittext = findViewById(R.id.et_comments);
        post_pic_user = findViewById(R.id.iv_post_pic);
        close = findViewById(R.id.tv_close_ac);
        Bundle extras = getIntent().getExtras();


        if (extras != null){
            url = extras.getString("url");
            name = extras.getString("name");
            post_key = extras.getString("postkey");
            bundleuid = extras.getString("uid");
            desc = extras.getString("d");
        }else {
            Toast.makeText(this, "No Data pass", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        Commentref = database.getReference("All posts").child(post_key).child("Comments");

        likesref = database.getReference("comment likes");
        userCommentref = database.getReference("User Posts").child(userid);

        ntref = database.getReference("notification").child(bundleuid);

        commentsBtn.setOnClickListener(view -> comment());

        close.setOnClickListener(view -> onBackPressed());
    }
    @Override
    protected void onStart() {
        super.onStart();

        Picasso.get().load(url).into(usernameImageview);
        descTextview.setText(desc);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(userid);

        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().exists()) {
                        name_result = task.getResult().getString("name");
                        age_result = task.getResult().getString("age");
                        bio_result = task.getResult().getString("bio");
                        email_result = task.getResult().getString("email");
                        web_result = task.getResult().getString("website");
                        Url = task.getResult().getString("url");
                        uid = task.getResult().getString("uid");
                    }
                });


        DocumentReference documentReference2 = db.collection("user").document(bundleuid);

        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().exists()) {
                        String name_user_post = task.getResult().getString("name");
                        String url_user_post = task.getResult().getString("url");

                        usernameTextview.setText(name_user_post);
                        Picasso.get().load(url_user_post).into(post_pic_user);

                    }
                });

        FirebaseRecyclerOptions<CommentsMember> options =
                new FirebaseRecyclerOptions.Builder<CommentsMember>()
                        .setQuery(Commentref,CommentsMember.class)
                        .build();

        FirebaseRecyclerAdapter<CommentsMember,CommentsViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CommentsMember, CommentsViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewholder holder, int position, @NonNull CommentsMember model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = user.getUid();
                        final String postkey = getRef(position).getKey();
                        String time = getItem(position).getTime();

                        holder.setComment(getApplication(),model.getComment(),model.getTime(),model.getUrl(),model.getUsername(),model.getUid());

                        holder.LikeChecker(postkey);

                        holder.delete.setOnClickListener(view -> {
                            Query query = Commentref.orderByChild("time").equalTo(time);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                        dataSnapshot1.getRef().removeValue();

                                        Toast.makeText(CommentsActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        });
                        holder.likebutton.setOnClickListener(view -> {

                            likeChecker = true;

                            likesref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (likeChecker.equals(true)){
                                        if (snapshot.child(postkey).hasChild(currentUserId)){
                                            likesref.child(postkey).child(currentUserId).removeValue();
                                            likeChecker = false;

                                        }else {
                                            likesref.child(postkey).child(currentUserId).setValue(true);
                                            likeChecker = false;
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
                    public CommentsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.comments_item,parent,false);

                        return new CommentsViewholder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void comment() {
        GetCurrentTime gc = new GetCurrentTime();
        String time = gc.ctime();
        String comment = commentsEdittext.getText().toString();
        if (comment != null){

            commentsMember.setComment(comment);
            commentsMember.setUsername(name_result);
            commentsMember.setUid(uid);
            commentsMember.setTime(time);
            commentsMember.setUrl(Url);

            String pushkey = Commentref.push().getKey();
            Commentref.child(pushkey).setValue(commentsMember);

            commentsEdittext.setText("");


            newMember.setName(name);
            newMember.setUid(userid);
            newMember.setUrl(Url);
            newMember.setSeen("no");
            newMember.setText("Commented on your post: " + comment);
            newMember.setAction("C");

            String key = ntref.push().getKey();
            ntref.child(key).setValue(newMember);
            sendNotification(bundleuid,name_result,comment);

            Toast.makeText(this, "Commented", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Please write comment", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendNotification(String bundleuid, String name_result, String comment){

        FirebaseDatabase.getInstance().getReference().child(bundleuid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        usertoken = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            FcmNotificationSender notificationsSender =
                    new FcmNotificationSender(usertoken,"Social Media",name_result+" Commented on your post: " + comment,
                            getApplicationContext(),CommentsActivity.this);

            notificationsSender.SendNotifications();

        },3000);

    }

    }
