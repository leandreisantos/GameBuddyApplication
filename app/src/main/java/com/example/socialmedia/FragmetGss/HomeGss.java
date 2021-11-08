package com.example.socialmedia.FragmetGss;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.socialmedia.MainActivity;
import com.example.socialmedia.MarketPlaceController.ViewItemPicture;
import com.example.socialmedia.R;
import com.example.socialmedia.SplashScreen;
import com.example.socialmedia.WaitingActivity;
import com.example.socialmedia.databaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class HomeGss extends Fragment {

    ImageView dp,bg;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,refjoin;
    TextView tvabout,new_hgss,edit,edit_about,owner,email,cat,add,edit_info,cancel_new_edit,save_new_edit;
    TextView cancel_about_edit,save_about_edit,join_com;
    EditText et_new,et_about;
    ConstraintLayout clnew,clabout;

    String keyword,post_key;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    String temp_new,temp_about;
    Boolean clicknew=true,clickabout=true;

    String new_et_data,about_et_data;

    JoinMember joinMember;

    Boolean isJoined = false;
    LottieAnimationView lot;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_gss,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null){
            keyword = extras.getString("k");
            post_key = extras.getString("id");
        }else {
            Toast.makeText(getActivity(), "No Dat error", Toast.LENGTH_SHORT).show();
        }

        joinMember = new JoinMember();


        dp = getActivity().findViewById(R.id.iv_dp_hgss);
        bg = getActivity().findViewById(R.id.iv_bg_hgss);

        tvabout = getActivity().findViewById(R.id.tv_about_gss);
        new_hgss = getActivity().findViewById(R.id.tv_new_hgss);
        edit = getActivity().findViewById(R.id.edit_home_gss);
        edit_info = getActivity().findViewById(R.id.edit_info_gss);
        owner = getActivity().findViewById(R.id.tv_owner_gss);
        email = getActivity().findViewById(R.id.tv_email_gss);
        cat = getActivity().findViewById(R.id.tv_cat_gss);
        add = getActivity().findViewById(R.id.tv_add_gss);
        join_com = getActivity().findViewById(R.id.tv_join_hgss);
        lot = getActivity().findViewById(R.id.loginlot);

        et_new = getActivity().findViewById(R.id.et_new_hgss);
        clnew = getActivity().findViewById(R.id.cl_new_edit);
        cancel_new_edit = getActivity().findViewById(R.id.tv_cancel_new);
        save_new_edit = getActivity().findViewById(R.id.tv_save_new);

        edit_about = getActivity().findViewById(R.id.edit_about_gss);
        clabout = getActivity().findViewById(R.id.cl_about_edit);
        cancel_about_edit = getActivity().findViewById(R.id.tv_cancel_about);
        save_about_edit = getActivity().findViewById(R.id.tv_save_about);
        et_about = getActivity().findViewById(R.id.et_about_hgss);

        reference = database.getReference(keyword).child(post_key);

        refjoin = database.getReference("Game Member").child(keyword).child(post_key);





        edit.setOnClickListener(v -> {
            if(clicknew){
                new_hgss.setVisibility(View.GONE);
                clnew.setVisibility(View.VISIBLE);
                et_new.setText(temp_new);
                edit.setBackgroundResource(R.drawable.ic_round_close_24);
                clicknew = false;
            }else{
              closeeditnew();
            }

        });

        join_com.setOnClickListener(v -> {
            if(isJoined){
                refjoin.child(currentuid).removeValue();
                isJoined = false;
                join_com.setText("Join Community");
            }else{
                joinProcess();

            }

        });

        cancel_new_edit.setOnClickListener(v -> closeeditnew());

        save_new_edit.setOnClickListener(v -> {
            new_et_data = et_new.getText().toString();
            reference.child("whatNew").setValue(new_et_data);
            closeeditnew();
            Toast.makeText(getActivity(), "What new changes updated", Toast.LENGTH_SHORT).show();
        });

        edit_about.setOnClickListener(v -> {
            if(clickabout){
                tvabout.setVisibility(View.GONE);
                clabout.setVisibility(View.VISIBLE);
                et_about.setText(temp_about);
                edit_about.setBackgroundResource(R.drawable.ic_round_close_24);
                clickabout = false;
            }else{
                closeeditabout();
            }
        });

        cancel_about_edit.setOnClickListener(v -> closeeditabout());

        save_about_edit.setOnClickListener(v -> {
            about_et_data = et_about.getText().toString();
            reference.child("about").setValue(about_et_data);
            closeeditabout();
            Toast.makeText(getActivity(), "About changes updated", Toast.LENGTH_SHORT).show();
        });

    }


    private void joinProcess() {

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        String id = refjoin.push().getKey();

        joinMember.setId(currentuid);
        joinMember.setDate(savedate);
        joinMember.setTime(savetime);
        joinMember.setGamePostkey(post_key);
        joinMember.setPostkey(id);
        joinMember.setPosition("Member");

        refjoin.child(currentuid).setValue(joinMember);
        Toast.makeText(getActivity(), "Joined", Toast.LENGTH_SHORT).show();
        join_com.setText("Joined");
        isJoined = true;


        lot.setVisibility(View.VISIBLE);

        lot.postDelayed(() -> {
            lot.animate().alpha(0.0f).setDuration(2000);
            lot.setVisibility(View.GONE);

        },5000);



    }

    private void closeeditnew(){
        new_hgss.setVisibility(View.VISIBLE);
        clnew.setVisibility(View.GONE);
        et_new.setText(temp_new);
        edit.setBackgroundResource(R.drawable.ic_baseline_edit_24);
        clicknew = true;
    }

    private void closeeditabout(){
        tvabout.setVisibility(View.VISIBLE);
        clabout.setVisibility(View.GONE);
        et_about.setText(temp_about);
        edit_about.setBackgroundResource(R.drawable.ic_baseline_edit_24);
        clickabout = true;
    }

    @Override
    public void onStart() {
        super.onStart();

        refjoin.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    isJoined=true;
                    join_com.setText("Joined");
                }else{
                    isJoined=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String dps = snapshot.child("postUri1").getValue(String.class);
                    String bgs = snapshot.child("postUri2").getValue(String.class);
                    String about = snapshot.child("about").getValue(String.class);
                    String new_g = snapshot.child("whatNew").getValue(String.class);
                    String id = snapshot.child("create").getValue(String.class);
                    String owner_s = snapshot.child("owner").getValue(String.class);
                    String email_s = snapshot.child("email").getValue(String.class);
                    String cat_s = snapshot.child("cat").getValue(String.class);
                    String add_s = snapshot.child("address").getValue(String.class);


                    temp_new = new_g;
                    temp_about = about;

                    Picasso.get().load(dps).into(dp);
                    Picasso.get().load(bgs).into(bg);
                    tvabout.setText(about);
                    new_hgss.setText(new_g);
                    owner.setText(owner_s);
                    email.setText(email_s);
                    cat.setText(cat_s);
                    add.setText(add_s);

                    dp.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), ViewItemPicture.class);
                        intent.putExtra("p",dps);
                        startActivity(intent);
                    });
                    bg.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), ViewItemPicture.class);
                        intent.putExtra("p",bgs);
                        startActivity(intent);
                    });

                    if(id.equals(currentuid)){
                        edit.setVisibility(View.VISIBLE);
                        edit_about.setVisibility(View.VISIBLE);
                        edit_info.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
