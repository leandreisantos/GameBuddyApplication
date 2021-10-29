package com.example.socialmedia.PostController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.socialmedia.Fragment4;
import com.example.socialmedia.MainActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    //value for fragment
    String value="";

    TextView cansee;
    CardView closePanel;

    ImageView imageView,iv_profile_p,btn_p_close;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    EditText etdesc;
    TextView btnchoosefile,btnuploadfile,closeImage;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1,db2,db3,db4;
    String privacy_post= "p";
    String key_post,title_post;

    MediaController mediaController;
    String type;
    Postmember postmember;

    int width=0,height=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            key_post = extras.getString("kp");
            title_post = extras.getString("t");
        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }


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
        closePanel = findViewById(R.id.cl_parentclose_ap);
        closeImage = findViewById(R.id.tv_close_iv_ap);

        storageReference = FirebaseStorage.getInstance().getReference("User posts");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db4 = database.getReference("All post").child(currentuid);

        if(key_post.equals("p")){
            db3 = database.getReference("All post").child("public");
        }else{
            db3 = database.getReference("All post").child(title_post);
        }

        btnuploadfile.setOnClickListener(v -> Dopost());

       btnchoosefile.setOnClickListener(v -> chooseImage());

       btn_p_close.setOnClickListener(v -> {
           onBackPressed();
       });
       closeImage.setOnClickListener(v -> {
           imageView.setVisibility(View.GONE);
           videoView.setVisibility(View.GONE);
           selectedUri= null;
           closePanel.setVisibility(View.GONE);
       });

       cansee.setOnClickListener(v -> showBottomsheet());

    }

    private void showBottomsheet() {

        final Dialog dialog = new Dialog(PostActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_menu_public_post);
        ConstraintLayout public_tv = dialog.findViewById(R.id.cl2);
        ConstraintLayout private_tv = dialog.findViewById(R.id.cl3);
        ImageView active_p = dialog.findViewById(R.id.iv_sta_p_bmp);
        ImageView active_pr = dialog.findViewById(R.id.iv_sta_pr_bmp);

        if(privacy_post.equals("p")){
            active_p.setVisibility(View.VISIBLE);
            active_pr.setVisibility(View.GONE);
        }else{
            active_p.setVisibility(View.GONE);
            active_pr.setVisibility(View.VISIBLE);
        }

        public_tv.setOnClickListener(v -> {
            active_p.setVisibility(View.VISIBLE);
            active_pr.setVisibility(View.GONE);
            privacy_post ="p";
            cansee.setText("Everyone can comment and see your post");
            cansee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_public_24, 0, 0, 0);

        });
        private_tv.setOnClickListener(v -> {
            active_p.setVisibility(View.GONE);
            active_pr.setVisibility(View.VISIBLE);
            privacy_post ="pr";
            cansee.setText("Only your friends can comment and see your post");
            cansee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.privacy_icon, 0, 0, 0);

        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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

        if(requestCode == PICK_FILE && resultCode == RESULT_OK && data!= null && data.getData() != null){

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
                closePanel.setVisibility(View.VISIBLE);
//                   Toast.makeText(this, "height="+height, Toast.LENGTH_LONG).show();
//                  Toast.makeText(this, "width="+width, Toast.LENGTH_LONG).show();
            }else if(selectedUri.toString().contains("video")) {
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
                closePanel.setVisibility(View.VISIBLE);
//                     Toast.makeText(this, "height="+height, Toast.LENGTH_LONG).show();
//                     Toast.makeText(this, "width="+width, Toast.LENGTH_LONG).show();
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

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        //if post is picture only
        if(etdesc.getText().toString()==null && selectedUri !=null){
            desc ="";

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
                            String id1 = db3.push().getKey();
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(savetime);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("iv");
                            postmember.setPrivacy(privacy_post);
                            postmember.setDate(savedate);
                            postmember.setPostkey(id1);
                            postmember.setSharerType(null);

                            //for image
                            String id = db1.push().getKey();
                            db1.child(id).setValue(postmember);
                            //for both
                            db3.child(id1).setValue(postmember);
                            db4.child(id1).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();


                        }else if(type.equals("vv")){
                            String id4 = db3.push().getKey();
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(savetime);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("vv");
                            postmember.setDate(savedate);
                            postmember.setPrivacy(privacy_post);
                            postmember.setPostkey(id4);
                            postmember.setSharerType(null);

                            //for video
                            String id3 = db2.push().getKey();
                            db2.child(id3).setValue(postmember);

                            //for both
                            db3.child(id4).setValue(postmember);
                            db4.child(id4).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();

                    }
                });
            value = "post";
            Intent intent = new Intent(PostActivity.this, MainActivity.class);
            intent.putExtra("post",value);
            startActivity(intent);


        }
        else if(selectedUri == null&& !etdesc.getText().toString().trim().equals("")){
            desc = etdesc.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                //final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
                //uploadTask = reference.putFile(selectedUri);

            String id1 = db3.push().getKey();
            postmember.setDesc(desc);
            postmember.setName(name);
//          postmember.setPostUri(downloadUri.toString());
            postmember.setTime(savetime);
            postmember.setUid(currentuid);
            postmember.setUrl(url);
            postmember.setType("txt");
            postmember.setPrivacy(privacy_post);
            postmember.setDate(savedate);
            postmember.setPostkey(id1);
            postmember.setSharerType(null);

            //for image
            String id = db1.push().getKey();
            db1.child(id).setValue(postmember);
            //for both
            db3.child(id1).setValue(postmember);
            db4.child(id1).setValue(postmember);

            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();

            value = "post";
            Intent intent = new Intent(PostActivity.this,MainActivity.class);
            intent.putExtra("post",value);
            startActivity(intent);

        }
        else if(etdesc.getText().toString().trim().equals("")&&selectedUri == null){
            Toast.makeText(this, "Add something to post", Toast.LENGTH_SHORT).show();
        }
        else{
            desc = etdesc.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
                uploadTask = reference.putFile(selectedUri);

                Task<Uri> urlTask = uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
                    if(!task.isSuccessful()){
                        throw task.getException();

                    }
                    return reference.getDownloadUrl();
                }).addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        if(type.equals("iv")){
                            String id1 = db3.push().getKey();
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(savetime);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("iv");
                            postmember.setPrivacy(privacy_post);
                            postmember.setDate(savedate);
                            postmember.setPostkey(id1);
                            postmember.setSharerType(null);

                            //for image
                            String id = db1.push().getKey();
                            db1.child(id).setValue(postmember);
                            //for both
                            db3.child(id1).setValue(postmember);
                            db4.child(id1).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(PostActivity.this, "post uploaded", Toast.LENGTH_SHORT).show();


                        }else if(type.equals("vv")){
                            String id4 = db3.push().getKey();
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(savetime);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("vv");
                            postmember.setPrivacy(privacy_post);
                            postmember.setDate(savedate);
                            postmember.setPostkey(id4);
                            postmember.setSharerType(null);

                            //for video
                            String id3 = db2.push().getKey();
                            db2.child(id3).setValue(postmember);

                            //for both
                            db3.child(id4).setValue(postmember);
                            db4.child(id4).setValue(postmember);

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

        }


    }


}