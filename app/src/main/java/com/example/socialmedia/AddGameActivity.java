package com.example.socialmedia;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddGameActivity extends AppCompatActivity {

    ImageView dp,bg;
    TextView add,close;
    EditText title,desc,about,email,address,owner;

    int picture;

    private static final int PICK_FILE = 1;
    private Uri selectedUridp,selectedUribg,downloadUri2;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db3;

    StorageReference storageReference;
    UploadTask uploadTask,uploadTask2;

    GameMember gameMember;

    String keyword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        gameMember = new GameMember();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            keyword = extras.getString("k");
        }
        else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();


        dp = findViewById(R.id.iv_ag_dp);
        bg = findViewById(R.id.iv_ag_bg);
        add = findViewById(R.id.tv_add_ag);

        title = findViewById(R.id.et_title_ag);
        desc = findViewById(R.id.et_desc_ag);
        desc = findViewById(R.id.et_desc_ag);
        about = findViewById(R.id.et_about_ag);
        email = findViewById(R.id.et_email_ag);
        address = findViewById(R.id.et_address_ag);
        owner = findViewById(R.id.et_owner_ag);
        close = findViewById(R.id.tv_close_ag);

        storageReference = FirebaseStorage.getInstance().getReference("All game");
        db3 = database.getReference(keyword);

        close.setOnClickListener(v -> onBackPressed());

        dp.setOnClickListener(v -> {
            chooseImage();
            picture = 1;
        });

        bg.setOnClickListener(v -> {
            chooseImage();
            picture = 2;
        });

        add.setOnClickListener(v -> AddGame());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE);
    }
    private String getFileExt(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data!= null || data.getData() != null){

            if(picture == 1){
                selectedUridp = data.getData();
                if(selectedUridp.toString().contains("image")) Picasso.get().load(selectedUridp).into(dp);
                else Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }else if(picture == 2){
                selectedUribg = data.getData();
                if(selectedUribg.toString().contains("image"))Picasso.get().load(selectedUribg).into(bg);
                else Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this, "Error found no image", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void AddGame() {


        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        String desc_et = desc.getText().toString();
        String title_et = title.getText().toString();
        String about_et = about.getText().toString();
        String email_et = email.getText().toString();
        String address_et = address.getText().toString();
        String owner_et = owner.getText().toString();

        if(!(TextUtils.isEmpty(desc_et)&&TextUtils.isEmpty(title_et)&&TextUtils.isEmpty(about_et)&&TextUtils.isEmpty(email_et)&&TextUtils.isEmpty(address_et)
                &&TextUtils.isEmpty(owner_et))&&selectedUridp != null&&selectedUribg !=null) {

            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(selectedUridp));
            final StorageReference reference2 = storageReference.child(System.currentTimeMillis() + "." + getFileExt(selectedUribg));

            uploadTask = reference.putFile(selectedUridp);
            uploadTask2 = reference2.putFile(selectedUribg);


            Task<Uri> urlTask2 = uploadTask2.continueWithTask((Task<UploadTask.TaskSnapshot> task2) -> {
                if(!task2.isSuccessful()){
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

                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    String id = db3.push().getKey();

                         gameMember.setTitle(title_et);
                         gameMember.setDesc(desc_et);
                         gameMember.setPostkey(id);
                         gameMember.setCat(keyword);
                         gameMember.setPostUri1(downloadUri.toString());
                         gameMember.setPostUri2(downloadUri2.toString());
                         gameMember.setTime(savetime);
                         gameMember.setDate(savedate);
                         gameMember.setAbout(about_et);
                         gameMember.setEmail(email_et);
                         gameMember.setAddress(address_et);
                         gameMember.setOwner(owner_et);


                        db3.child(id).setValue(gameMember);


                        Toast.makeText(AddGameActivity.this, "Game Addedd", Toast.LENGTH_SHORT).show();

                }
            });

        }else Toast.makeText(this, "Please fill up all fields", Toast.LENGTH_SHORT).show();

    }
}