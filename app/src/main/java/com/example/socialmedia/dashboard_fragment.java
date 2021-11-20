package com.example.socialmedia;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.Config.Config;
import com.example.socialmedia.EventController.AllEventActivity;
import com.example.socialmedia.EventController.EventActivity;
import com.example.socialmedia.EventController.EventMember;
import com.example.socialmedia.EventController.Eventholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class dashboard_fragment extends Fragment implements View.OnClickListener{

    RecyclerView featuredEvents,rvGameDeveloper,rvBestGame;
    ImageView addevent;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,db1,db2,db3,profileRef,referenceDeveloper,db4,event,devref;
    RelativeLayout sta,action,adventure,fps;
    //reference
    String name;
    String currentuid;
    TextView allevent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_fragment,container,false);
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(),PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        featuredEvents = getActivity().findViewById(R.id.df_event_rv);
        addevent = getActivity().findViewById(R.id.df_add_event);
        sta = getActivity().findViewById(R.id.rl_sta_dash);
        action = getActivity().findViewById(R.id.rl_ac_dash);
        adventure = getActivity().findViewById(R.id.rl_ad_dash);
        fps = getActivity().findViewById(R.id.rl_fps_dash);
        allevent = getActivity().findViewById(R.id.tv_allevent_dash);

        reference = database.getReference("All post").child("event");
        referenceDeveloper = database.getReference("All users");
        devref = database.getReference("All Developer");
        featuredEvents = getActivity().findViewById(R.id.df_event_rv);
        featuredEvents.setHasFixedSize(true);
        featuredEvents.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        rvGameDeveloper = getActivity().findViewById(R.id.rv_developer_dash);
        rvGameDeveloper.setHasFixedSize(true);
        rvGameDeveloper.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        rvBestGame = getActivity().findViewById(R.id.rv_mg_dash);
        rvBestGame.setHasFixedSize(true);
        rvBestGame.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        profileRef = database.getReference("All users");

        //REFERENCE TO REALTIME DATABASE IN FIREBASE
        db1 = database.getReference("All images").child(currentuid);
        event = database.getReference("Event Payment");
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All post events");
        db3.keepSynced(true);

        db4 = database.getReference("All strategy");

        addevent.setOnClickListener(this);

        sta.setOnClickListener(v -> GotoSga("Strategy Game","All strategy"));
        action.setOnClickListener(v -> GotoSga("Action Game","All action"));
        adventure.setOnClickListener(v -> GotoSga("Adventure Game","All adventure"));
        fps.setOnClickListener(v -> GotoSga("Fps Game","All fps"));

        allevent.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllEventActivity.class);
            startActivity(intent);
        });

    }

    private void GotoSga(String title,String keyword) {
        Intent intent = new Intent(getActivity(),SelectedGameActivity.class);
        intent.putExtra("s",title);
        intent.putExtra("k",keyword);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.df_add_event:
                event.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                          Intent intent = new Intent(getActivity(),EventActivity.class);
                          startActivity(intent);
                        } else {
                            showBottomSheet();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Data error", Toast.LENGTH_SHORT).show();
                    }
                });

        }
    }

    private void showBottomSheet() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_event_payment);

        TextView debit = dialog.findViewById(R.id.tv_debit_event);
        TextView paypal = dialog.findViewById(R.id.tv_paypal_event);

        debit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),DebitActivity.class);
            startActivity(intent);
        });
        paypal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),paypalActivity.class);
            startActivity(intent);
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }


    @Override
    public void onStart() {
        super.onStart();

        DocumentReference reference1;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference1 = firestore.collection("user").document(currentuid);
        reference1.get()
                .addOnCompleteListener(task -> {
                    if(!task.getResult().exists()){
                        Intent intent = new Intent(getActivity(),CreateProfile.class);
                        startActivity(intent);
                    }
                });


        FirebaseRecyclerOptions<EventMember> options =
                new FirebaseRecyclerOptions.Builder<EventMember>()
                        .setQuery(reference,EventMember.class)
                        .build();

        FirebaseRecyclerAdapter<EventMember, Eventholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<EventMember, Eventholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Eventholder holder, int position, @NonNull EventMember model) {

                        holder.setEvent(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),
                                model.getType(),model.getDesc(),model.getTitle(),model.getDate(),model.getPostkey(),model.getAddress(),model.getGame());

                        name = getItem(position).getName();
                        String title = getItem(position).getTitle();
                        String uri = getItem(position).getPostUri();
                        String time = getItem(position).getTime();
                        String name = getItem(position).getName();
                        String desc = getItem(position).getDesc();
                        String uid = getItem(position).getUid();
                        String event_postkey=getItem(position).getPostkey();

                        holder.EventView.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(),EventSelectedActivity.class);
                            intent.putExtra("p",event_postkey);
                            intent.putExtra("uid",uid);
                            startActivity(intent);
                        });



                    }

                    @NonNull
                    @Override
                    public Eventholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.featured_event_card_design,parent,false);

                        return new Eventholder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();

        featuredEvents.setAdapter(firebaseRecyclerAdapter);



        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(devref,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {

                        holder.setGameDeveloper(getActivity(),model.getUid());

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.developer_layout,parent,false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();
//
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.HORIZONTAL,false);
        rvGameDeveloper.setLayoutManager(gridLayoutManager);
        rvGameDeveloper.setAdapter(firebaseRecyclerAdapter1);


        FirebaseRecyclerOptions<GameMember> options2 =
                new FirebaseRecyclerOptions.Builder<GameMember>()
                        .setQuery(db4,GameMember.class)
                        .build();

        FirebaseRecyclerAdapter<GameMember,GameViewHolder> firebaseRecyclerAdapter2 =
                new FirebaseRecyclerAdapter<GameMember, GameViewHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull GameViewHolder holder, int position, @NonNull GameMember model) {



                        holder.SetBestGame(getActivity(),model.getTitle(),model.getDesc(),model.getPostkey(),model.getCat(),model.getPostUri1(),
                                model.getPostUri2(),model.getTime(),model.getDate());

                        String post_key = getItem(position).getPostkey();
                        String title = getItem(position).getTitle();


                    }

                    @NonNull
                    @Override
                    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.best_game_layout,parent,false);

                        return new GameViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter2.startListening();

        rvBestGame.setAdapter(firebaseRecyclerAdapter2);





    }

}
