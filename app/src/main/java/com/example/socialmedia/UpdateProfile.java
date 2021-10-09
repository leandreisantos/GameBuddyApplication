package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class UpdateProfile extends AppCompatActivity {

    EditText etname,etBio,etProfession,etEmail,etWeb;
    Button button,button_del;
    ImageView iv;

    //database stuff
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();



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
        button_del = findViewById(R.id.btn_del);

        button.setOnClickListener(v -> updateProfile());
        button_del.setOnClickListener(v -> deleteProf());
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this,ImageActivity.class);
                startActivity(intent);
            }
        });

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
                        String profResult = task.getResult().getString("prof");

                        etname.setText(nameResult);
                        etBio.setText(bioResult);
                        etEmail.setText(emailResult);
                        etWeb.setText(webResult);
                        etProfession.setText(profResult);
                        Picasso.get().load(url).into(iv);

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


        final DocumentReference sDoc = db.collection("user").document(currentuid);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
           // DocumentSnapshot snapshot = transaction.get(sfDocRef);

            transaction.update(sDoc, "name",name);
            transaction.update(sDoc, "prof",prof);
            transaction.update(sDoc, "email",email);
            transaction.update(sDoc, "web",web);
            transaction.update(sDoc, "bio",bio);


            // Success
            return null;
        }).addOnSuccessListener(aVoid -> Toast.makeText(UpdateProfile.this, "Updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UpdateProfile.this, "failed", Toast.LENGTH_SHORT).show());
    }
}