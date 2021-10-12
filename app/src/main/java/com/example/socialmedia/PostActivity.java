package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;

public class PostActivity extends AppCompatActivity {

    //value for fragment
    String value="";

    TextView cansee;

    ImageView imageView,iv_profile_p,btn_p_close;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    EditText etdesc;
    TextView btnchoosefile,btnuploadfile;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1,db2,db3;

    MediaController mediaController;
    String type;
    Postmember postmember;

    int width=0,height=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postmember = new Postmember();

        mediaController = new MediaController(this);

        progressBar = findViewById(R.id.pb_post);
        imageView = findViewById(R.id.iv_post);
        videoView = findViewById(R.id.vv_post);
        btnchoosefile = findViewById(R.id.btn_choosefile_post);
        btnuploadfile = findViewById(R.id.btn_uploadfile_post);
        etdesc = findViewById(R.id.et_decs_post);
        iv_profile_p = findViewById(R.id.iv_profile_post);
        btn_p_close = findViewById(R.id.btn_post_close);
        cansee = findViewById(R.id.cansee_post);

        storageReference = FirebaseStorage.getInstance().getReference("User posts");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All post");

        btnuploadfile.setOnClickListener(v -> Dopost());

       btnchoosefile.setOnClickListener(v -> chooseImage());

       btn_p_close.setOnClickListener(v -> {
//               value = "post";
//               Intent intent = new Intent(PostActivity.this,MainActivity.class);
//               intent.putExtra("post",value);
//               startActivity(intent);
           FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
           fragmentTransaction.replace(R.id.container,new Fragment4()).commit();
       });

       cansee.setOnClickListener(v -> {

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

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(new File(selectedUri.getPath()).getAbsolutePath(), options);
                BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(selectedUri),
                        null,
                        options);
                height = options.outWidth;
                width = options.outHeight;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
//                   Toast.makeText(this, "height="+height, Toast.LENGTH_LONG).show();
//                  Toast.makeText(this, "width="+width, Toast.LENGTH_LONG).show();
            }else if(selectedUri.toString().contains("video")) {
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
                     Toast.makeText(this, "height="+height, Toast.LENGTH_LONG).show();
                     Toast.makeText(this, "width="+width, Toast.LENGTH_LONG).show();
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

                        Picasso.get()
                                .load(url)
                                .fit()
                                .into(iv_profile_p);


                    }else{
                        Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }


                });
    }

    private void Dopost() {

        String desc;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        //if post is picture only
        if(etdesc.getText().toString()==null && selectedUri !=null){
            desc ="";
//          Toast.makeText(this, "No text just image only", Toast.LENGTH_SHORT).show();
            //desc = etdesc.getText().toString();

            GetCurrentTime gc = new GetCurrentTime();
            String time = gc.ctime();


//            if(TextUtils.isEmpty(desc) || selectedUri != null){

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
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("iv");

                            //for image
                            String id = db1.push().getKey();
                            db1.child(id).setValue(postmember);
                            //for both
                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();


                        }else if(type.equals("vv")){
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("vv");

                            //for video
                            String id3 = db2.push().getKey();
                            db2.child(id3).setValue(postmember);

                            //for both
                            String id4 = db3.push().getKey();
                            db3.child(id4).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            value = "post";
            Intent intent = new Intent(PostActivity.this,MainActivity.class);
            intent.putExtra("post",value);
            startActivity(intent);

//            /}else{
//                Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
//            }

        }else if(selectedUri == null&& !etdesc.getText().toString().trim().equals("")){
            desc = etdesc.getText().toString();

            GetCurrentTime gc = new GetCurrentTime();
            String time = gc.ctime();

                progressBar.setVisibility(View.VISIBLE);
                //final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
                //uploadTask = reference.putFile(selectedUri);

            postmember.setDesc(desc);
            postmember.setName(name);
//          postmember.setPostUri(downloadUri.toString());
            postmember.setTime(time);
            postmember.setUid(currentuid);
            postmember.setUrl(url);
            postmember.setType("txt");

            //for image
            String id = db1.push().getKey();
            db1.child(id).setValue(postmember);
            //for both
            String id1 = db3.push().getKey();
            db3.child(id1).setValue(postmember);

            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();

            value = "post";
            Intent intent = new Intent(PostActivity.this,MainActivity.class);
            intent.putExtra("post",value);
            startActivity(intent);

        }else if(etdesc.getText().toString().trim().equals("")&&selectedUri == null){
            Toast.makeText(this, "Add something to post", Toast.LENGTH_SHORT).show();
        }
        else{
            desc = etdesc.getText().toString();

            GetCurrentTime gc = new GetCurrentTime();
            String time = gc.ctime();


//            if(TextUtils.isEmpty(desc) || selectedUri != null){

                progressBar.setVisibility(View.VISIBLE);
                final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
                uploadTask = reference.putFile(selectedUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();

                        }
                        return reference.getDownloadUrl();
                    }
                }).addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        if(type.equals("iv")){
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("iv");

                            //for image
                            String id = db1.push().getKey();
                            db1.child(id).setValue(postmember);
                            //for both
                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();


                        }else if(type.equals("vv")){
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("vv");

                            //for video
                            String id3 = db2.push().getKey();
                            db2.child(id3).setValue(postmember);

                            //for both
                            String id4 = db3.push().getKey();
                            db3.child(id4).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            value = "post";
            Intent intent = new Intent(PostActivity.this,MainActivity.class);
            intent.putExtra("post",value);
            startActivity(intent);

//            }else{
//                Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
//            }
        }

        

        }


}