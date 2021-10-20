package com.example.socialmedia.FragmetGss;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.MessageActivity;
import com.example.socialmedia.MessageMember;
import com.example.socialmedia.MessageViewHolder;
import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommunityGss extends Fragment {

    RecyclerView rv;
    EditText message;
    TextView send;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference rootref1,rootref2,typingref;
    MessageMember messageMember;

    String uid,keyword,post_key,title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.community_gss,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null){
            keyword = extras.getString("k");
            post_key = extras.getString("id");
            title = extras.getString("t");
        }else {
            Toast.makeText(getActivity(), "No Dat error", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        rv = getActivity().findViewById(R.id.rv_com_gss);
        message = getActivity().findViewById(R.id.et_message_gss);
        send = getActivity().findViewById(R.id.tv_send_gss);

        messageMember = new MessageMember();

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        rootref1 = database.getReference("MessageCommunity").child(keyword).child(post_key);

        send.setOnClickListener(v -> SendMessage());

    }

    private void SendMessage() {
        String message_et = message.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());


        String time = savedate + ":" + savetime;

        if(message_et.isEmpty()){
            Toast.makeText(getActivity(), "Cannot send empty message", Toast.LENGTH_SHORT).show();
        }else{
            long deletetime = System.currentTimeMillis();

            String id = rootref1.push().getKey();
            messageMember.setDate(savedate);
            messageMember.setTime(savetime);
            messageMember.setMessage(message_et);
            messageMember.setSenderuid(uid);
            messageMember.setType("t");
            messageMember.setDelete(deletetime);

            rootref1.child(id).setValue(messageMember);


        }

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<MessageMember> options1 =
                new FirebaseRecyclerOptions.Builder<MessageMember>()
                        .setQuery(rootref1,MessageMember.class)
                        .build();

        FirebaseRecyclerAdapter<MessageMember,MessageViewHolder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<MessageMember, MessageViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageMember model) {


                        //String message,time,date,type,senderuid,receiveruid;
                        holder.SetmessageCom(getActivity(),model.getMessage(),model.getTime(),model.getDate(),
                                model.getType(),model.getSenderuid(),model.getReceiveruid(),model.getSendername(),model.getAudio(),model.getImage());



                    }

                    @NonNull
                    @Override
                    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.message_layout,parent,false);

                        return new MessageViewHolder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();

        rv.setAdapter(firebaseRecyclerAdapter1);

    }
}
