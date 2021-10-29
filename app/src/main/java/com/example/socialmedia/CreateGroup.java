package com.example.socialmedia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreateGroup extends AppCompatActivity {

    ImageView close,profilecg;
    EditText cgName;
    TextView btncg;
    ProgressBar pb;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference,databaseReference2;
    UploadTask uploadTask;
    StorageReference storageReference;
    String currentUserId;
    AllGroupMember member;

    Uri imageUri;
    private static final int PICK_IMAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        member = new AllGroupMember();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference("Group images");
        databaseReference = database.getReference("All group");
        databaseReference2 = database.getReference("Group");

        close = findViewById(R.id.btn_cg_close);
        profilecg = findViewById(R.id.iv_cg);
        cgName = findViewById(R.id.et_cg_name);
        btncg = findViewById(R.id.btn_cg);
        pb = findViewById(R.id.pb_cg);


        btncg.setOnClickListener(v -> createcg());
        profilecg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE);
        });
        close.setOnClickListener(v -> onBackPressed());

    }

    private void createcg() {

        pb.setVisibility(View.VISIBLE);
        cgName.setEnabled(false);
        btncg.setEnabled(false);
        profilecg.setEnabled(false);

        String namecg = cgName.getText().toString();

        if(!TextUtils.isEmpty(namecg)&&imageUri != null){
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
            uploadTask = reference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    String id = databaseReference.push().getKey();

                    member.setName(namecg.toUpperCase());
                    member.setUid(currentUserId);
                    member.setUrl(downloadUri.toString());
                    member.setPostkey(id);
                    member.setMember1(currentUserId);

                    databaseReference.child(id).setValue(member);
                    databaseReference2.child(currentUserId).child(id).setValue(member);

                    Toast.makeText(CreateGroup.this, "Group created", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });

        }else{
            pb.setVisibility(View.GONE);
            cgName.setEnabled(true);
            btncg.setEnabled(true);
            profilecg.setEnabled(true);
            Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null&& data.getData()!=null){
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(profilecg);
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
}