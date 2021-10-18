package com.example.socialmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public static final int PAYPAL_REQUEST_CODE = 7171;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // use sandbox for test
            .clientId(Config.PAYPAL_CLIENT_ID);


    RecyclerView featuredEvents,rvGameDeveloper;
    ImageView addevent;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,db1,db2,db3,profileRef,referenceDeveloper;
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

        //Start paypal services
        Intent intent = new Intent(getActivity(),PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        getActivity().startService(intent);

        featuredEvents = getActivity().findViewById(R.id.df_event_rv);
        addevent = getActivity().findViewById(R.id.df_add_event);

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

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.df_add_event:
                //processPayment();
                Intent intent = new Intent(getActivity(), EventActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void processPayment() {
        String amout = "100";
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amout)), "USD",
        "Donate for GameBuddyDevelopers",PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null){
                    try{
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        //startActivity(new Intent(getActivity(),PaymentDetails.class))
                        Toast.makeText(getActivity(), "Payment Completed", Toast.LENGTH_SHORT).show();

                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
            }

        }else if(requestCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Toast.makeText(getActivity(), "Invalid", Toast.LENGTH_SHORT).show();
        }
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
