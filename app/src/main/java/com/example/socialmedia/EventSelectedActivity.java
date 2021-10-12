package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EventSelectedActivity extends AppCompatActivity {

    String uri,title,time,name,desc,uid,url;
    TextView title_tv,back,tv_name,tv_time,tv_desc,tv_more,gProfile;
    ImageView iv_event,iv;

    //databse stuff
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentid = user.getUid();
    DocumentReference reference;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_selected);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            uri = extras.getString("uri");
            title = extras.getString("t");
            time = extras.getString("ti");
            name = extras.getString("n");
            desc = extras.getString("d");
            uid = extras.getString("i");
        }else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }

        reference = firestore.collection("user").document(uid);

        title_tv =  findViewById(R.id.tv_title_aes);
        iv_event = findViewById(R.id.iv_event_aes);
        back = findViewById(R.id.back_aes);
        tv_name = findViewById(R.id.tvc_name);
        tv_time = findViewById(R.id.time_lbl);
        tv_desc = findViewById(R.id.caption_lbl);
        iv = findViewById(R.id.iv_aes);
        tv_more = findViewById(R.id.more_aes);
        gProfile = findViewById(R.id.tv_aes_goToProfile);


        back.setOnClickListener(v -> {
            Intent intent = new Intent(EventSelectedActivity.this,MainActivity.class);
            startActivity(intent);
        });
        tv_more.setOnClickListener(v -> showDialog());
        gProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentid.equals(uid)) {
                    Intent intent = new Intent(EventSelectedActivity.this,MyProfileActivity.class);
                    startActivity(intent);

                }else {
                    Intent intent = new Intent(EventSelectedActivity.this,ShowUser.class);
                    intent.putExtra("n",name);
                    intent.putExtra("u",url);
                    intent.putExtra("uid",uid);
                    startActivity(intent);
                }
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

        AlertDialog alertDialog = new AlertDialog.Builder(EventSelectedActivity.this)
                .setView(view)
                .create();

        alertDialog.show();

        download.setVisibility(View.GONE);

        if(!currentid.equals(uid)){
            delete.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }else{
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){
                        String url = task.getResult().getString("url");
                        Picasso.get().load(url).into(iv);
                    }else Toast.makeText(EventSelectedActivity.this, "No data", Toast.LENGTH_SHORT).show();
                });

        title_tv.setText(title);
        tv_name.setText(name);
        tv_time.setText(time);
        tv_desc.setText(desc);
        Picasso.get().load(uri).into(iv_event);
    }
}