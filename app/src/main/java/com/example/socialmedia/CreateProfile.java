package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {

    EditText etname,etBio,etProfession,etEmail,etWeb;
    Button button;
    ImageView imageView,bgProfile;
    ProgressBar progressBar;
    Uri imageUridp,imageUribg,downloadUri2;
    UploadTask uploadTask,uploadTask2;
    StorageReference storageReference;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private static final int PICK_IMAGE=1;
    private static final int PICK_IMAGE1=1;
    AllUserMember member;
    String currentUserId;
    int picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member= new AllUserMember();

        imageView = findViewById(R.id.iv_cp);
        bgProfile = findViewById(R.id.iv_cp_bg);
        etBio = findViewById(R.id.et_bio_cp);
        etEmail= findViewById(R.id.et_email_cp);
        etname = findViewById(R.id.et_name_cp);
        etProfession = findViewById(R.id.et_profession_cp);
        etWeb = findViewById(R.id.et_web_cp);
        button = findViewById(R.id.btn_cp);
        progressBar = findViewById(R.id.progressbar_cp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");

        databaseReference = database.getReference("All users");

        button.setOnClickListener(v -> uploadData());

        imageView.setOnClickListener(v -> {
            chooseImage();
            picture = 1;
        });

        bgProfile.setOnClickListener(v -> {
            chooseImage();
            picture = 2;
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!=null){
                if(picture == 1){
                    imageUridp = data.getData();
                    Picasso.get().load(imageUridp).into(imageView);
                }else if(picture == 2){
                    imageUribg = data.getData();
                    Picasso.get().load(imageUribg).into(bgProfile);
                }else Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e){
            Toast.makeText(this, "Error"+e, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void uploadData() {

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        String name = etname.getText().toString();
        String bio = etBio.getText().toString();
        String web = etWeb.getText().toString();
        String prof = etProfession.getText().toString();
        String email = etEmail.getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(bio) && !TextUtils.isEmpty(web)
                && !TextUtils.isEmpty(prof) && !TextUtils.isEmpty(email) && imageUridp != null && imageUribg != null){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUridp));
            final StorageReference reference2 = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUribg));
            uploadTask = reference.putFile(imageUridp);
            uploadTask2 = reference2.putFile(imageUribg);

            Task<Uri> urlTask2 = uploadTask2.continueWithTask((Task<UploadTask.TaskSnapshot> task2) -> {
                if(!task2.isSuccessful()){
                    Toast.makeText(this, "error1", Toast.LENGTH_SHORT).show();
                    throw task2.getException();
                }
                return reference2.getDownloadUrl();
            }).addOnCompleteListener(task2 -> {
                if(task2.isSuccessful()){
                    downloadUri2 = task2.getResult();
                }
            });

            Task<Uri> urlTask = uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if(task.isSuccessful()&&downloadUri2!=null){
                    Uri downloadUri = task.getResult();

                    Map<String,String> profile = new HashMap<>();
                    profile.put("name",name);
                    profile.put("prof",prof);
                    profile.put("url",downloadUri.toString());
                    profile.put("url2",downloadUri2.toString());
                    profile.put("email",email);
                    profile.put("web",web);
                    profile.put("bio",bio);
                    profile.put("uid",currentUserId);
                    profile.put("privacy","Public");


                    member.setName(name.toUpperCase());
                    member.setProf(prof);
                    member.setUid(currentUserId);
                    member.setUrl(downloadUri.toString());
                    member.setAbout(bio);
                    member.setUid2(downloadUri2.toString());
                    member.setTime(savetime);
                    member.setDate(savedate);

                    databaseReference.child(currentUserId).setValue(member);

                    documentReference.set(profile)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();


                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(CreateProfile.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },2000);
                            });
                }
            });
            
        }else{
            Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }
    }
}