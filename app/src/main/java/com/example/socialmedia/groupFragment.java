package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class groupFragment extends Fragment {

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference,databaseReference2;
    String userid;

    RecyclerView recyclerView,recyclerView2;
    TextView cr_com;
    EditText search;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.group_fragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        groupFragment moreFragment = new groupFragment();
//        FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().replace(R.id.content, moreFragment).addToBackStack("more").commit();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        databaseReference= database.getReference("followers").child(userid);
        databaseReference2 = database.getReference("All group");

        cr_com = getActivity().findViewById(R.id.createcom);
        search = getActivity().findViewById(R.id.search_com);


        recyclerView = getActivity().findViewById(R.id.recyclerview_profile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView2 = getActivity().findViewById(R.id.recyclerview_group);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search_info();
            }
        });

        cr_com.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),CreateGroup.class);
            startActivity(intent);
        });
    }

    private void search_info() {
        String query = search.getText().toString();
        Query search = databaseReference.orderByChild("name").startAt(query).endAt(query+"\uf0ff");


    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(databaseReference,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {



                        holder.setProfile(getActivity(),model.getName(),model.getUid(),model.getProf(),model.getUrl());


                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();

                        // String uid = "VA0zC50hUhQoRIu8P6xuHLRCWVp1";

                        holder.viewUserProfile.setOnClickListener(view -> {

                            if(userid.equals(uid)) {
                                Intent intent = new Intent(getActivity(),MainActivity.class);
                                startActivity(intent);

                            }else{
                                Intent intent = new Intent(getActivity(),ShowUser.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }


                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.profile,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);



        FirebaseRecyclerOptions<AllGroupMember> options2 =
                new FirebaseRecyclerOptions.Builder<AllGroupMember>()
                        .setQuery(databaseReference2,AllGroupMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllGroupMember,GroupViewholder> firebaseRecyclerAdapter2 =
                new FirebaseRecyclerAdapter<AllGroupMember, GroupViewholder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull GroupViewholder holder, int position, @NonNull AllGroupMember model) {


                        holder.setGroupProfile(getActivity(),model.getName(),model.getUid(),model.getUrl());

                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();

                        holder.btn.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(),MessageGroupActivity.class);
                            startActivity(intent);
                        });

                    }

                    @NonNull
                    @Override
                    public GroupViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.group_layout,parent,false);

                        return new GroupViewholder(view);
                    }
                };


        firebaseRecyclerAdapter2.startListening();
//
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        recyclerView2.setLayoutManager(gridLayoutManager);
        recyclerView2.setAdapter(firebaseRecyclerAdapter2);


    }
}
