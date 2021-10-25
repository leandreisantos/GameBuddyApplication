package com.example.socialmedia.FragmetGss;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeGss extends Fragment {

    ImageView dp,bg;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;
    TextView tvabout;

    String keyword,post_key;


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



        dp = getActivity().findViewById(R.id.iv_dp_hgss);
        bg = getActivity().findViewById(R.id.iv_bg_hgss);
        tvabout = getActivity().findViewById(R.id.tv_about_gss);

        reference = database.getReference(keyword).child(post_key);
    }

    @Override
    public void onStart() {
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dps = snapshot.child("postUri1").getValue(String.class);
                String bgs = snapshot.child("postUri2").getValue(String.class);
                String about = snapshot.child("about").getValue(String.class);

                Picasso.get().load(dps).into(dp);
                Picasso.get().load(bgs).into(bg);
                tvabout.setText(about);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
