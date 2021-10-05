package com.example.socialmedia;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment2 extends Fragment implements View.OnClickListener {

    FloatingActionButton fb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference,fvrtref,fvrt_listRef;
    RecyclerView recyclerView;
    Boolean fvrtChecker = false;

    ImageView imageView;
    ImageButton ibCat;

    QuestionMember member;

    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment2,container,false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        recyclerView = getActivity().findViewById(R.id.rv_f2);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference = database.getReference("All Questions");
        member = new QuestionMember();
        fvrtref = database.getReference("favourites");
        fvrt_listRef = database.getReference("favoriteList").child(currentUserid);

        imageView = getActivity().findViewById(R.id.iv_f2);
        fb = getActivity().findViewById(R.id.floatingActionButton);
        reference = db.collection("user").document(currentUserid);

        ibCat = getActivity().findViewById(R.id.btn_catf2);



        fb.setOnClickListener(this);
        imageView.setOnClickListener(this);

        ibCat.setOnClickListener(this);


        FirebaseRecyclerOptions<QuestionMember> options =
                new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(databaseReference,QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember,Viewholder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Question holder, int position, @NonNull QuestionMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();


                        holder.setitem(getActivity(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),model.getQuestion()
                        ,model.getPrivacy(),model.getTime(),model.getCategory());

                        String que = getItem(position).getQuestion();
                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        final String time = getItem(position).getTime();
                        String privacy = getItem(position).getPrivacy();
                        String userid = getItem(position).getUserid();

                        holder.replybtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getActivity(),ReplyActivity.class);
                                intent.putExtra("uid",userid);
                                intent.putExtra("q",que);
                                intent.putExtra("postkey",postkey);
                                //intent.putExtra("key",privacy);
                                startActivity(intent);

                            }
                        });




                        holder.favouriteChecker(postkey);
                        holder.fvrt_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                fvrtChecker = true;

                                fvrtref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(fvrtChecker.equals(true)){
                                            if(snapshot.child(postkey).hasChild(currentUserid)){
                                                fvrtref.child(postkey).child(currentUserid).removeValue();
                                                delete(time);
                                                fvrtChecker = false;
                                            }else{
                                                fvrtref.child(postkey).child(currentUserid).setValue(true);
                                                member.setName(name);
                                                member.setTime(time);
                                                member.setPrivacy(privacy);
                                                member.setUserid(userid);
                                                member.setUrl(url);
                                                member.setQuestion(que);


                                                    //String id = fvrt_listRef.push().getKey();
                                                    fvrt_listRef.child(postkey).setValue(member);
                                                    fvrtChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });



                    }

                    @NonNull
                    @Override
                    public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.question_item,parent,false);

                        return new Viewholder_Question(view);
                    }
                };


        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }

    void delete(String time){

        Query query = fvrt_listRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_f2:
                BottomSheetF2 bottomSheetF2 = new BottomSheetF2();
                bottomSheetF2.show(getFragmentManager(),"bottom");
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(getActivity(), AskActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_catf2:
                showCategorySheet();
                break;
        }
    }

    private void showCategorySheet() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.category_layout);

        TextView tvOld = dialog.findViewById(R.id.tvOld);
        TextView tvTech = dialog.findViewById(R.id.tvTech);
        TextView tvhealth = dialog.findViewById(R.id.tvHealth);
        TextView tvEduc = dialog.findViewById(R.id.tvEduc);
        TextView tvFood = dialog.findViewById(R.id.tvFodd);
        TextView tvsports = dialog.findViewById(R.id.tvSports);
        TextView tvNews = dialog.findViewById(R.id.tvNews);
        TextView tvFashion = dialog.findViewById(R.id.tvFashion);
        TextView tvBeauty = dialog.findViewById(R.id.tvBeauty);
        TextView tvLifestyle = dialog.findViewById(R.id.tvLs);

        tvTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "tech";
                sortQuestion(category);
                tvTech.setTextColor(Color.parseColor("#FFC107"));
            }
        });
        tvOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvhealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "health";
                sortQuestion(category);
            }
        });
        tvEduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "education";
                sortQuestion(category);
            }
        });
        tvFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "food";
                sortQuestion(category);
            }
        });
        tvsports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "sports";
                sortQuestion(category);
            }
        });
        tvNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "news";
                sortQuestion(category);
            }
        });
        tvFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "fashion";
                sortQuestion(category);
            }
        });
        tvBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "beauty";
                sortQuestion(category);
            }
        });
        tvLifestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "lifestyle";
                sortQuestion(category);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void sortQuestion(String category) {

        Query search = databaseReference.orderByChild("category").startAt(category).endAt(category+"\uf0ff");


        FirebaseRecyclerOptions<QuestionMember> options =
                new FirebaseRecyclerOptions.Builder<QuestionMember>()
                        .setQuery(search,QuestionMember.class)
                        .build();

        FirebaseRecyclerAdapter<QuestionMember,Viewholder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Question holder, int position, @NonNull QuestionMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();


                        holder.setitem(getActivity(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),model.getQuestion()
                                ,model.getPrivacy(),model.getTime(),model.getCategory());

                        String que = getItem(position).getQuestion();
                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        final String time = getItem(position).getTime();
                        String privacy = getItem(position).getPrivacy();
                        String userid = getItem(position).getUserid();

                        holder.replybtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getActivity(),ReplyActivity.class);
                                intent.putExtra("uid",userid);
                                intent.putExtra("q",que);
                                intent.putExtra("postkey",postkey);
                                //intent.putExtra("key",privacy);
                                startActivity(intent);

                            }
                        });




                        holder.favouriteChecker(postkey);
                        holder.fvrt_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                fvrtChecker = true;

                                fvrtref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(fvrtChecker.equals(true)){
                                            if(snapshot.child(postkey).hasChild(currentUserid)){
                                                fvrtref.child(postkey).child(currentUserid).removeValue();
                                                delete(time);
                                                fvrtChecker = false;
                                            }else{
                                                fvrtref.child(postkey).child(currentUserid).setValue(true);
                                                member.setName(name);
                                                member.setTime(time);
                                                member.setPrivacy(privacy);
                                                member.setUserid(userid);
                                                member.setUrl(url);
                                                member.setQuestion(que);


                                                //String id = fvrt_listRef.push().getKey();
                                                fvrt_listRef.child(postkey).setValue(member);
                                                fvrtChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });



                    }

                    @NonNull
                    @Override
                    public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.question_item,parent,false);

                        return new Viewholder_Question(view);
                    }
                };


        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String url = task.getResult().getString("url");

                            Picasso.get().load(url).into(imageView);
                        }else{
                            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}
