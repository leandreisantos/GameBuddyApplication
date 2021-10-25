package com.example.socialmedia.EventController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.socialmedia.GetCurrentTime;
import com.example.socialmedia.MainActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EventActivity extends AppCompatActivity {

    ImageView imageView,closebtn,useriv;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    EditText etdesc,etTitle;
    Button btnchoosefile;
    TextView btnuploadfile;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1,db2,db3,event;

    MediaController mediaController;
    String type;
    EventMember eventMember;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        eventMember = new EventMember();

        mediaController = new MediaController(this);

        progressBar = findViewById(R.id.event_pb_post);
        imageView = findViewById(R.id.event_iv_post);
        useriv = findViewById(R.id.event_iv);
        videoView = findViewById(R.id.event_vv_post);
        btnchoosefile = findViewById(R.id.event_btn_choosefile_post);
        btnuploadfile = findViewById(R.id.event_btn_uploadfile_post);
        etdesc = findViewById(R.id.event_et_decs_post);
        etTitle = findViewById(R.id.event_et_title_post);
        closebtn = findViewById(R.id.event_close);

        storageReference = FirebaseStorage.getInstance().getReference("User posts events");


        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All post events");
        event = database.getReference("Event Payment");

        btnuploadfile.setOnClickListener(v -> Dopost());

        btnchoosefile.setOnClickListener(v -> chooseImage());

        closebtn.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }



    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data!= null || data.getData() != null){

            selectedUri = data.getData();
            if(selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
            }else if(selectedUri.toString().contains("video")) {
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
            }else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentuid);

        documentReference.get()
                .addOnCompleteListener(task -> {

                    if(task.getResult().exists()){
                        name = task.getResult().getString("name");
                        url = task.getResult().getString("url");

                        Picasso.get().load(url).into(useriv);


                    }else{
                        Toast.makeText(EventActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }


                });
    }

    private void Dopost() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        String desc = etdesc.getText().toString();
        String title = etTitle.getText().toString();

              GetCurrentTime gc = new GetCurrentTime();
              String time = gc.ctime();

        if(TextUtils.isEmpty(desc) || selectedUri != null){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()){
                    throw task.getException();

                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    if(type.equals("iv")){
                        eventMember.setTitle(title);
                        eventMember.setDesc(desc);
                        eventMember.setName(name);
                        eventMember.setPostUri(downloadUri.toString());
                        eventMember.setTime(time);
                        eventMember.setUid(currentuid);
                        eventMember.setUrl(url);
                        eventMember.setType("iv");

                        //for image
                        String id = db1.push().getKey();
                        db1.child(id).setValue(eventMember);
                        //for both
                        String id1 = db3.push().getKey();
                        db3.child(id1).setValue(eventMember);

                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(EventActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();
                        deletedb();

                    }else if(type.equals("vv")){
                        eventMember.setTitle(title);
                        eventMember.setDesc(desc);
                        eventMember.setName(name);
                        eventMember.setPostUri(downloadUri.toString());
                        eventMember.setTime(time);
                        eventMember.setUid(currentuid);
                        eventMember.setUrl(url);
                        eventMember.setType("vv");

                        //for image
                        String id = db1.push().getKey();
                        db1.child(id).setValue(eventMember);
                        //for both
                        String id1 = db3.push().getKey();
                        db3.child(id1).setValue(eventMember);

                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(EventActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();
                        deletedb();
                    }else{
                        Toast.makeText(EventActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }


    }

    public void deletedb(){
        event.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EventActivity.this, "Data error", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(EventActivity.this,MainActivity.class);
        startActivity(intent);
    }
}