package com.example.socialmedia.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.BottomSheetMenu;
import com.example.socialmedia.ChatActivity;
import com.example.socialmedia.CreateProfile;
import com.example.socialmedia.FollowerActivity;
import com.example.socialmedia.ImageActivity;
import com.example.socialmedia.IndividualPost;
import com.example.socialmedia.MainActivity;
import com.example.socialmedia.NotificationActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.StoryActivity;
import com.example.socialmedia.UpdateProfile;
import com.example.socialmedia.databaseReference;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class Fragment1 extends Fragment implements View.OnClickListener{

    ImageView imageView;
    TextView nameEt,profEt,bioEt,emailEt,webEt,postTv,storyadd,followertv;
    ImageButton ib_edit,imageButtonMenu;

    //notification
    TextView newtv;


    Button btnsendmessage;

    private static int PICK_IMAGE = 1;
    Uri imageUri;
    int postiv,post1,post2,newcount;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference db1,db2,db3,ntRef;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentid = user.getUid();

    MainActivity mainActivity = new MainActivity();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1,container,false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        db1 = database.getReference("followers").child(currentid);
        db2 = database.getReference("All images").child(currentid);
        db3 = database.getReference("All videos").child(currentid);

        ntRef = database.getReference("notification").child(currentid);

        //notifi
        newtv = getActivity().findViewById(R.id.tv_newf1);
        
        imageView = getActivity().findViewById(R.id.iv_f1);
        nameEt = getActivity().findViewById(R.id.tv_name_f1);
        profEt = getActivity().findViewById(R.id.tv_prof_f1);
        bioEt = getActivity().findViewById(R.id.tv_bio_f1);
        emailEt = getActivity().findViewById(R.id.tv_email_f1);
        webEt= getActivity().findViewById(R.id.tv_web_f1);
        postTv = getActivity().findViewById(R.id.tv_post_f1);
        storyadd = getActivity().findViewById(R.id.tv_addstories_f1);
        btnsendmessage = getActivity().findViewById(R.id.btn_sendmessage_f1);
        ib_edit=getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);
        followertv = getActivity().findViewById(R.id.tv_followers_f1);

        imageButtonMenu.setOnClickListener(this);
        ib_edit.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webEt.setOnClickListener(this);
        btnsendmessage.setOnClickListener(this);
        postTv.setOnClickListener(this);
        storyadd.setOnClickListener(this);
         followertv.setOnClickListener(this);

         newtv.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId()){

            case R.id.tv_newf1:
                Intent intent3 = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent3);
                changeSeen();
                break;
            case R.id.ib_edit_f1:
                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_f1:
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(),"bottomsheet");
                break;
            case R.id.tv_addstories_f1:

                Intent intentstory = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentstory.setType("image/*");
                startActivityForResult(intentstory,PICK_IMAGE);
                break;

            case R.id.tv_followers_f1:
                Intent follower = new Intent(getActivity(), FollowerActivity.class);
                follower.putExtra("u",currentid);
                startActivity(follower);
                break;
            case R.id.iv_f1:
                Intent intent1 = new Intent(getActivity(), ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_post_f1:
                Intent intent5 = new Intent(getActivity(), IndividualPost.class);
                startActivity(intent5);
                break;
            case R.id.btn_sendmessage_f1:
                Intent in = new Intent(getActivity(), ChatActivity.class);
                startActivity(in);
                break;
            case R.id.tv_web_f1:
                try{
                    String url = webEt.getText().toString();
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse(url));
                    startActivity(intent2);
                }catch(Exception e){
                    Toast.makeText(getActivity(), "Invalid url", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(requestCode == PICK_IMAGE || resultCode == RESULT_OK||data != null||data.getData()!=null){
                imageUri = data.getData();

                String url = imageUri.toString();
                Intent intent = new Intent(getActivity(), StoryActivity.class);
                intent.putExtra("u",url);
                startActivity(intent);
            }

        }catch (Exception e){
            Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Query query = ntRef.orderByChild("seen").equalTo("no");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    newcount = (int) snapshot.getChildrenCount();
                    newtv.setText(Integer.toString(newcount)+ " New");
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postiv = (int)snapshot.getChildrenCount();
                followertv.setText(Integer.toString(postiv));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post1 = (int)snapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post2 = (int)snapshot.getChildrenCount();
                String total = Integer.toString(post1+post2);
                postTv.setText(total);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        reference = firestore.collection("user").document(currentid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                   @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task){
                       if(task.getResult().exists()){

                           String nameResult = task.getResult().getString("name");
                           String bioResult = task.getResult().getString("bio");
                           String emailResult = task.getResult().getString("email");
                           String webResult = task.getResult().getString("web");
                           String url = task.getResult().getString("url");
                           String profResult = task.getResult().getString("prof");

                           Picasso.get().load(url).into(imageView);
                           nameEt.setText(nameResult);
                           bioEt.setText(bioResult);
                           emailEt.setText(emailResult);
                           webEt.setText(webResult);
                           profEt.setText(profResult);


                       }else{
                           Intent intent = new Intent(getActivity(), CreateProfile.class);
                           startActivity(intent);
                       }
                   }
                });

    }

    private void changeSeen(){
        Map<String,Object> profile = new HashMap<>();
        profile.put("seen","yes");

        ntRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    dataSnapshot.getRef().updateChildren(profile)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
