package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class UpdateProfile extends AppCompatActivity {

    EditText etname,etBio,etProfession,etEmail,etWeb;
    TextView button,button_del,back;
    ImageView iv,bg;
    UploadTask uploadTask;

    //database stuff
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();
    private static final int PICK_IMAGE=1;
    private static final int PICK_IMAGE1=1;
    int picture;
    Uri imageUridp,imageUribg,downloadUri2;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        documentReference=db.collection("user").document(currentuid);

        etBio = findViewById(R.id.et_bio_up);
        etEmail = findViewById(R.id.et_email_up);
        etname = findViewById(R.id.et_name_up);
        etProfession = findViewById(R.id.et_profession_up);
        etWeb = findViewById(R.id.et_web_up);
        button = findViewById(R.id.btn_up);
        iv = findViewById(R.id.iv_up);
        back = findViewById(R.id.tv_back_up);
        bg = findViewById(R.id.iv_cp_bg);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
        //button_del = findViewById(R.id.btn_del);

        button.setOnClickListener(v -> updateProfile());
        //button_del.setOnClickListener(v -> deleteProf());
        iv.setOnClickListener(v -> {
            chooseImage();
            picture = 1;
        });

        back.setOnClickListener(view -> onBackPressed());

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == PICK_IMAGE1 && resultCode == RESULT_OK && data != null && data.getData()!=null){
                if(picture == 1){
                    imageUridp = data.getData();
                    Picasso.get().load(imageUridp).into(iv);
                }else if(picture == 2){
                    imageUribg = data.getData();
                    Picasso.get().load(imageUribg).into(bg);
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



    private void deleteProf() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
        builder.setTitle("Delete Profile")
                .setMessage("Are you sure to delete?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //   StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl()

                        deleteImage();
                        documentReference.delete()

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(UpdateProfile.this, "Profile deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(UpdateProfile.this, "Profile delete failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
        
    }


    private void deleteImage() {
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {

                            String Url = task.getResult().getString("url");
                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(Url);
                            reference.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                        }
                                    });

                        } else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(UpdateProfile.this, "failed", Toast.LENGTH_SHORT).show();
                    }

                });
    }



    @Override
    protected void onStart() {
        super.onStart();


        documentReference.get()
                .addOnCompleteListener(task -> {

                    if(task.getResult().exists()){

                        String nameResult = task.getResult().getString("name");
                        String bioResult = task.getResult().getString("bio");
                        String emailResult = task.getResult().getString("email");
                        String webResult = task.getResult().getString("web");
                        String url = task.getResult().getString("url");
                        String url2 = task.getResult().getString("url2");
                        String profResult = task.getResult().getString("prof");

                        etname.setText(nameResult);
                        etBio.setText(bioResult);
                        etEmail.setText(emailResult);
                        etWeb.setText(webResult);
                        etProfession.setText(profResult);
                        Picasso.get().load(url).into(iv);
                        Picasso.get().load(url2).into(bg);

                    }else{
                        Toast.makeText(UpdateProfile.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                    }


                });
    }

    private void updateProfile() {
        String name = etname.getText().toString();
        String bio = etBio.getText().toString();
        String prof = etProfession.getText().toString();
        String web = etWeb.getText().toString();
        String email = etEmail.getText().toString();

//        final DocumentReference sDoc = db.collection("user").document(currentuid);
//        db.runTransaction((Transaction.Function<Void>) transaction -> {
//           // DocumentSnapshot snapshot = transaction.get(sfDocRef);
//
//            transaction.update(sDoc, "name",name);
//            transaction.update(sDoc, "prof",prof);
//            transaction.update(sDoc, "email",email);
//            transaction.update(sDoc, "web",web);
//            transaction.update(sDoc, "bio",bio);
//            transaction.update(sDoc,"url",)
//
//
//
//            // Success
//            return null;
//        }).addOnSuccessListener(aVoid -> Toast.makeText(UpdateProfile.this, "Updated", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(UpdateProfile.this, "failed", Toast.LENGTH_SHORT).show());



        final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUridp));
        uploadTask = reference.putFile(imageUridp);
        Task<Uri> urlTask = uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
            if(!task.isSuccessful()){
                throw task.getException();

            }
            return reference.getDownloadUrl();
        }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {

            if(task.isSuccessful()){
                Uri downloadUri = task.getResult();

                final DocumentReference sDoc1 = db.collection("user").document(currentuid);
                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    // DocumentSnapshot snapshot = transaction.get(sfDocRef);

                    transaction.update(sDoc1, "name",name);
                    transaction.update(sDoc1, "prof",prof);
                    transaction.update(sDoc1, "email",email);
                    transaction.update(sDoc1, "web",web);
                    transaction.update(sDoc1, "bio",bio);
                    transaction.update(sDoc1, "url",downloadUri);


                    // Success
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateProfile.this, "Updated", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> Toast.makeText(UpdateProfile.this, "failed", Toast.LENGTH_SHORT).show());


            }
        });


    }
}