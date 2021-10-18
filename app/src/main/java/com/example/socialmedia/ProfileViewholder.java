package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileViewholder extends RecyclerView.ViewHolder {

    TextView textViewName,textViewProfession,viewUserProfile,sendmessagebtn;
    TextView namell,vp_ll,namefollower,vpfollower,professionFollower;

    ImageView ipl_profile;
    TextView ipl_name,ipl_prof,ipl_invite;

    ImageView imageView,iv_ll,iv_follower;
    CardView cardView;

    DatabaseReference databaseReference;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());

    String groupid;

    ImageView iv_developer;
    TextView tvNameDeveloper,tvAboutDeveloper;

    public ProfileViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setProfile(FragmentActivity fragmentActivity,String name,String uid,String prof,String url){

        cardView = itemView.findViewById(R.id.cardview_profile);
        textViewName = itemView.findViewById(R.id.tv_name_profile);
        textViewProfession = itemView.findViewById(R.id.tv_profession_profile);
        viewUserProfile = itemView.findViewById(R.id.viewUser_profile);
        imageView = itemView.findViewById(R.id.profile_imageview);

        Picasso.get().load(url).into(imageView);
        textViewProfession.setText(prof);
        textViewName.setText(name);
    }

    public void setGameDeveloper(FragmentActivity fragmentActivity,String name,String uid,String prof,String url,String about){

        iv_developer = itemView.findViewById(R.id.iv_profile_dl);
        tvNameDeveloper=itemView.findViewById(R.id.tv_name_dl);
        tvAboutDeveloper= itemView.findViewById(R.id.tv_about_dl);

        Picasso.get().load(url).into(iv_developer);
        tvNameDeveloper.setText(name);
        tvAboutDeveloper.setText(about);
    }

    public void setProfileInchat(Application fragmentActivity, String name, String uid, String prof,
                                 String url){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        ImageView imageView = itemView.findViewById(R.id.iv_ch_item);
        TextView nametv = itemView.findViewById(R.id.name_ch_item_tv);
        TextView proftv = itemView.findViewById(R.id.ch_itemprof_tv);
        sendmessagebtn = itemView.findViewById(R.id.send_messagech_item_btn);

        if (userid.equals(uid)){
            Picasso.get().load(url).into(imageView);
            nametv.setText(name);
            proftv.setText(prof);
            sendmessagebtn.setVisibility(View.INVISIBLE);
        }else {
            Picasso.get().load(url).into(imageView);
            nametv.setText(name);
            proftv.setText(prof);
        }


    }

    public void setLikeduser(Application fragmentActivity, String name, String uid, String prof,
                             String url){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();


        vp_ll = itemView.findViewById(R.id.vp_ll);
        namell = itemView.findViewById(R.id.name_ll);
        iv_ll = itemView.findViewById(R.id.iv_ll);

        Picasso.get().load(url).into(iv_ll);
        namell.setText(name);


    }

    public void setFollower(Application application, String name, String url,
                            String profession, String bio, String privacy, String email, String followers, String website){

        iv_follower = itemView.findViewById(R.id.iv_follower);
        professionFollower = itemView.findViewById(R.id.profession_follower);
        namefollower = itemView.findViewById(R.id.name_follower);
        vpfollower = itemView.findViewById(R.id.vp_follower);

        Picasso.get().load(url).into(iv_follower);
        namefollower.setText(name);
        professionFollower.setText(profession);

    }
    public void invitePeopleToGroup(Application application, String name, String url,
                            String profession, String bio, String privacy, String email, String followers, String website,String id){

        databaseReference = database.getReference("pending invite").child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ig =snapshot.child("groupid").getValue(String.class);
                if(ig.equals(groupid)){
                    ipl_profile = itemView.findViewById(R.id.iv_profile_ipl);
                    ipl_name = itemView.findViewById(R.id.tv_name_ipl);
                    ipl_prof = itemView.findViewById(R.id.tv_prof_ipl);

                    Picasso.get().load(url).into(ipl_profile);
                    ipl_name.setText(name);
                    ipl_prof.setText(profession);
                }else{
                    ipl_profile = itemView.findViewById(R.id.iv_profile_ipl);
                    ipl_name = itemView.findViewById(R.id.tv_name_ipl);
                    ipl_prof = itemView.findViewById(R.id.tv_prof_ipl);
                    ipl_invite = itemView.findViewById(R.id.tv_invite_ipl);

                    Picasso.get().load(url).into(ipl_profile);
                    ipl_name.setText(name);
                    ipl_prof.setText(profession);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
