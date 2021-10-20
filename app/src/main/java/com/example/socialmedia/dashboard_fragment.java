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
import com.example.socialmedia.EventController.EventActivity;
import com.example.socialmedia.EventController.EventMember;
import com.example.socialmedia.EventController.Eventholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

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

    RecyclerView featuredEvents,rvGameDeveloper;
    ImageView addevent;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,db1,db2,db3,profileRef,referenceDeveloper;
    RelativeLayout sta,action,adventure,fps;
    //reference
    String name;


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
        String currentuid = user.getUid();

        featuredEvents = getActivity().findViewById(R.id.df_event_rv);
        addevent = getActivity().findViewById(R.id.df_add_event);
        sta = getActivity().findViewById(R.id.rl_sta_dash);
        action = getActivity().findViewById(R.id.rl_ac_dash);
        adventure = getActivity().findViewById(R.id.rl_ad_dash);
        fps = getActivity().findViewById(R.id.rl_fps_dash);

        reference = database.getReference("All post events");
        referenceDeveloper = database.getReference("All users");
        featuredEvents = getActivity().findViewById(R.id.df_event_rv);
        featuredEvents.setHasFixedSize(true);
        featuredEvents.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        rvGameDeveloper = getActivity().findViewById(R.id.rv_developer_dash);
        rvGameDeveloper.setHasFixedSize(true);
        rvGameDeveloper.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        profileRef = database.getReference("All users");

        //REFERENCE TO REALTIME DATABASE IN FIREBASE
        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All post events");
        db3.keepSynced(true);

        addevent.setOnClickListener(this);

        sta.setOnClickListener(v -> GotoSga("Strategy Game","All strategy"));
        action.setOnClickListener(v -> GotoSga("Action Game","ac"));
        adventure.setOnClickListener(v -> GotoSga("Adventure Game","ad"));
        fps.setOnClickListener(v -> GotoSga("Fps Game","f"));

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
                //processPayment();
            showBottomSheet();
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

        FirebaseRecyclerOptions<EventMember> options =
                new FirebaseRecyclerOptions.Builder<EventMember>()
                        .setQuery(reference,EventMember.class)
                        .build();

        FirebaseRecyclerAdapter<EventMember, Eventholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<EventMember, Eventholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Eventholder holder, int position, @NonNull EventMember model) {

                        holder.setEvent(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),
                                model.getType(),model.getDesc(),model.getTitle());

                        name = getItem(position).getName();
                        String title = getItem(position).getTitle();
                        String uri = getItem(position).getPostUri();
                        String time = getItem(position).getTime();
                        String name = getItem(position).getName();
                        String desc = getItem(position).getDesc();
                        String uid = getItem(position).getUid();

                        holder.EventView.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(),EventSelectedActivity.class);
                            intent.putExtra("t",title);
                            intent.putExtra("uri",uri);
                            intent.putExtra("ti",time);
                            intent.putExtra("n",name);
                            intent.putExtra("d",desc);
                            intent.putExtra("i",uid);
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
                        .setQuery(referenceDeveloper,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {

                        holder.setGameDeveloper(getActivity(),model.getName(),model.getUid(),model.getProf(),model.getUrl(),model.getAbout());

                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();


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





    }

}
