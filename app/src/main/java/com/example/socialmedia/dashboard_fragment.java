package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.socialmedia.EventController.EventActivity;
import com.example.socialmedia.EventController.EventMember;
import com.example.socialmedia.EventController.Eventholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class dashboard_fragment extends Fragment implements View.OnClickListener{

    RecyclerView featuredEvents;
    ImageView addevent;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,db1,db2,db3;
    //reference
    String name;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_fragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        featuredEvents = getActivity().findViewById(R.id.df_event_rv);
        addevent = getActivity().findViewById(R.id.df_add_event);

        reference = database.getReference("All post events");
        featuredEvents = getActivity().findViewById(R.id.df_event_rv);
        featuredEvents.setHasFixedSize(true);
        featuredEvents.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

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
                Intent intent = new Intent(getActivity(), EventActivity.class);
                startActivity(intent);
                break;
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

    }

}
