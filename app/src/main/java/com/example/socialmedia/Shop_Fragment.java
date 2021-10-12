package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Shop_Fragment extends Fragment{

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference2;

    TextView shop;
    RecyclerView recyclerView2;

    String userid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        databaseReference2 = database.getReference("All sell");

        shop = getActivity().findViewById(R.id.tv_sell_shop);

        recyclerView2 = getActivity().findViewById(R.id.rv_shop);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));


        shop.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),ShopActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<SellMember> options2 =
                new FirebaseRecyclerOptions.Builder<SellMember>()
                        .setQuery(databaseReference2,SellMember.class)
                        .build();

        FirebaseRecyclerAdapter<SellMember,SellGroupHolder> firebaseRecyclerAdapter2 =
                new FirebaseRecyclerAdapter<SellMember, SellGroupHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellGroupHolder holder, int position, @NonNull SellMember model) {


                        holder.setSellItem(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),model.getDate(),model.getTime(),
                                model.getUid(),model.getTitle(),model.getPrice(),model.getCategory(),model.getCondition(),model.getDescription(),model.getPostKey());

                        String postKey = getItem(position).getPostKey();

                        holder.cons.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(),SelectedItemSellActivity.class);
                            intent.putExtra("p",postKey);
                            startActivity(intent);
                        });

                    }

                    @NonNull
                    @Override
                    public SellGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.sell_layout,parent,false);

                        return new SellGroupHolder(view);
                    }
                };


        firebaseRecyclerAdapter2.startListening();
//
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        recyclerView2.setLayoutManager(gridLayoutManager);
        recyclerView2.setAdapter(firebaseRecyclerAdapter2);
    }
}
