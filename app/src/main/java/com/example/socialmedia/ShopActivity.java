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

public class ShopActivity extends AppCompatActivity {

    ImageView close;
    TextView publish,iv,username;
    ImageView ivsell,iv_user;
    EditText title,price,cat,con,desc;

    String type,currentuid,name,url;

    private static final int PICK_FILE = 1;
    private Uri selectedUri;
    UploadTask uploadTask;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference,db1;

    DocumentReference reference;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    StorageReference storageReference;
    SellMember sellmember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        sellmember = new SellMember();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        databaseReference = database.getReference("All sell");

        storageReference = FirebaseStorage.getInstance().getReference("User sells");
        db1 = database.getReference("All images").child(currentuid);

        iv = findViewById(R.id.tv_iv_sell);
        ivsell=findViewById(R.id.iv_sell);
        iv_user=findViewById(R.id.iv_user_tv_sell);

        title = findViewById(R.id.sell_tv_title);
        price = findViewById(R.id.sell_tv_price);
        cat = findViewById(R.id.sell_tv_cat);
        con = findViewById(R.id.sell_tv_con);
        desc = findViewById(R.id.sell_tv_desc);

        close = findViewById(R.id.btn_sell_close);
        publish = findViewById(R.id.btn_publish_sell);
        username = findViewById(R.id.tv_user_name);


        iv.setOnClickListener(v -> chooseImage());
        publish.setOnClickListener(v -> DoPublish());
    }

    private void DoPublish() {
        String t, p, ca, co, d;

        t = title.getText().toString();
        p = price.getText().toString();
        ca = cat.getText().toString();
        co = con.getText().toString();
        d = desc.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        if(!(TextUtils.isEmpty(t) || TextUtils.isEmpty(p) || TextUtils.isEmpty(ca) || TextUtils.isEmpty(co) ||TextUtils.isEmpty(d)
                || selectedUri == null)){

            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if(task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    String id1 = databaseReference.push().getKey();
                    sellmember.setName(name);
                    sellmember.setUrl(url);
                    sellmember.setPostUri(downloadUri.toString());
                    sellmember.setDate(savedate);
                    sellmember.setTime(savetime);
                    sellmember.setUid(currentuid);
                    sellmember.setTitle(t);
                    sellmember.setPrice(p);
                    sellmember.setCategory(ca);
                    sellmember.setCondition(co);
                    sellmember.setDescription(d);
                    sellmember.setPostKey(id1);

                    //for image
                    String id = db1.push().getKey();
                    db1.child(id).setValue(sellmember);
                    //for both
                    databaseReference.child(id1).setValue(sellmember);

                    //progressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(ShopActivity.this, "Publish item", Toast.LENGTH_SHORT).show();

                } else{
                        Toast.makeText(ShopActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }

            });

        }else{
            Toast.makeText(this, "Input fields", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        reference = firestore.collection("user").document(currentuid);
        reference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){

                        name = task.getResult().getString("name");
                        url = task.getResult().getString("url");

                        Picasso.get().load(url).into(iv_user);
                        username.setText(name);

                    }else{
                        Intent intent = new Intent(ShopActivity.this,CreateProfile.class);
                        startActivity(intent);
                    }
                });



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

            selectedUri = data.getData();

            if(selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(ivsell);
                ivsell.setVisibility(View.VISIBLE);
                iv.setVisibility(View.INVISIBLE);
                type = "iv";
            }else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }

    }
}