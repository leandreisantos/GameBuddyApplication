package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment3 extends Fragment {


    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference,databaseReference1,profileRef,ntRef;
    RecyclerView recyclerView,recyclerView_profile;
    RequestMember requestMember;
    TextView requesttv,requestcount;
    EditText editText;
    String currentUserId,usertoken;
    String uid;
    NewMember newMember;
    TextView req;
    int count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment3,container,false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        databaseReference = database.getReference("Requests").child(currentUserId);
//        profileRef = database.getReference("All users");
        profileRef = database.getReference("All users");

        ntRef = database.getReference("notification").child(currentUserId);
        requestMember = new RequestMember();

        newMember = new NewMember();
        recyclerView_profile = getActivity().findViewById(R.id.recyclerview_profile);
        requestcount = getActivity().findViewById(R.id.req_count);


        editText = getActivity().findViewById(R.id.search_f3);

        recyclerView_profile.setHasFixedSize(true);


        recyclerView_profile.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView = getActivity().findViewById(R.id.recyclerview_requestf3);
        requesttv = getActivity().findViewById(R.id.requeststv);

        req = getActivity().findViewById(R.id.tv_request_f3);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        MediaController mediaController;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                search();
            }
        });

        req.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),RequestActivity.class);
            startActivity(intent);
        });

    }

    private void search() {

        String query = editText.getText().toString().toUpperCase();
        Query search = profileRef.orderByChild("name").startAt(query).endAt(query+"\uf0ff");

        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(search,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                        final String postkey = getRef(position).getKey();

                        holder.setProfile(getActivity(),model.getName(),model.getUid(),model.getProf(),model.getUrl());


                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        uid = getItem(position).getUid();


                        holder.viewUserProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (currentUserId.equals(uid)) {
                                    Intent intent = new Intent(getActivity(),MyProfileActivity.class);
                                    startActivity(intent);

                                }else {
                                    Intent intent = new Intent(getActivity(),ViewUserActivity.class);
                                    intent.putExtra("n",name);
                                    intent.putExtra("u",url);
                                    intent.putExtra("uid",uid);
                                    startActivity(intent);
                                }



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
//
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        recyclerView_profile.setLayoutManager(gridLayoutManager);
        recyclerView_profile.setAdapter(firebaseRecyclerAdapter1);
    }


    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                requestcount.setText(String.valueOf(count));
            }
            public void onCancelled(DatabaseError databaseError) { }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    requesttv.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                }else {
                    requesttv.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(profileRef,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                        final String postkey = getRef(position).getKey();

                        holder.setProfile(getActivity(),model.getName(),model.getUid(),model.getProf(),model.getUrl());


                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();

                       // String uid = "VA0zC50hUhQoRIu8P6xuHLRCWVp1";

                        holder.viewUserProfile.setOnClickListener(view -> {

                            if(currentUserId.equals(uid)) {
                                Intent intent = new Intent(getActivity(),MyProfileActivity.class);
                                startActivity(intent);

                            }else{
                                Intent intent = new Intent(getActivity(),ViewUserActivity.class);
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
//
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        recyclerView_profile.setLayoutManager(gridLayoutManager);
        recyclerView_profile.setAdapter(firebaseRecyclerAdapter1);


//        FirebaseRecyclerOptions<RequestMember> options =
//                new FirebaseRecyclerOptions.Builder<RequestMember>()
//                        .setQuery(databaseReference,RequestMember.class)
//                        .build();
//
//        FirebaseRecyclerAdapter<RequestMember,RequestViewholder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<RequestMember, RequestViewholder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull RequestViewholder holder, int position, @NonNull RequestMember model) {
//
//
//                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        String currentUserId = user.getUid();
//                        final String postkey = getRef(position).getKey();
//
//                        holder.setRequest(getActivity(),model.getName(),model.getUrl(),model.getProfession()
//                                ,model.getBio(),model.getPrivacy(),model.getEmail(),model.getFollowers(),model.getWebsite(),model.getUserid());
//
//                        String uid = getItem(position).getUserid();
//                        String name = getItem(position).getName();
//                        String bio = getItem(position).getBio();
//                        String email = getItem(position).getEmail();
//                        String privacy = getItem(position).getPrivacy();
//                        String url = getItem(position).getUrl();
//                        String website = getItem(position).getWebsite();
//                        String age = getItem(position).getProfession();
//
//
//                        holder.button2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                String name = getItem(position).getName();
//                                decline(name);
//                            }
//                        });
//                        holder.button1.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                String uid = getItem(position).getUserid();
//                                databaseReference1 = database.getReference("followers").child(currentUserId);
//                                requestMember.setName(name);
//
//                                requestMember.setUserid(uid);
//                                requestMember.setUrl(url);
//                                requestMember.setProfession(age);
//                                String id = databaseReference1.push().getKey();
//                                databaseReference1.child(uid).setValue(requestMember);
//                                databaseReference.child(currentUserId).child(uid).removeValue();
//
//                                Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
//                                sendNotification(currentUserId,name);
//                                decline(name);
//
//
//                                //handling request notification
//
//                                newMember.setName(name);
//                                newMember.setUid(uid);
//                                newMember.setUrl(url);
//                                newMember.setSeen("no");
//                                newMember.setText("Started Following you ");
//
//                                ntRef.child(uid+"f").setValue(newMember);
//
//
//                            }
//                        });
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public RequestViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext())
//                                .inflate(R.layout.request_item,parent,false);
//
//                        return new RequestViewholder(view);
//                    }
//                };
//
//        recyclerView.setAdapter(firebaseRecyclerAdapter);
//        firebaseRecyclerAdapter.startListening();
    }

    private void decline(String name) {

        Query query = databaseReference.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                }
                //   Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///
            }
        });
    }

    private void sendNotification(String currentUserId, String name){

        FirebaseDatabase.getInstance().getReference().child(currentUserId).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        usertoken = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FcmNotificationSender notificationsSender =
                        new FcmNotificationSender(usertoken,"Social Media",name+" Started Following you",
                                getContext(),getActivity());

                notificationsSender.SendNotifications();

            }
        },3000);

    }

}
