package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView imageView;
    ImageButton sendbtn,cambtn,micbtn;
    TextView username,typingtv,btnVc;
    EditText messageEt;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference rootref1,rootref2,typingref;
    MessageMember messageMember;
    Boolean typingchecker = false;
    String receiver_name,receiver_uid,sender_uid,url,usertoken;

    UploadTask uploadTask;

    MediaRecorder mediaRecorder;
    public static String filename = "recorded.3gp";
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+filename;
    //String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "recordingAudio.mp3";



    Uri uri;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_message);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            url = bundle.getString("u");
            receiver_name = bundle.getString("n");
            receiver_uid = bundle.getString("uid");
        }else{
            Toast.makeText(this, "user missing", Toast.LENGTH_SHORT).show();
        }

        messageMember = new MessageMember();
        recyclerView = findViewById(R.id.rv_message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        cambtn = findViewById(R.id.cam_sendmessage);

        imageView = findViewById(R.id.iv_message);
        messageEt = findViewById(R.id.messageet);
        sendbtn = findViewById(R.id.imageButtonsend);
        username = findViewById(R.id.username_messageTv);
        micbtn = findViewById(R.id.btn_mic);
        typingtv = findViewById(R.id.typingstatus);
        btnVc = findViewById(R.id.btn_vc);




        Picasso.get().load(url).into(imageView);
        username.setText(receiver_name);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_uid = user.getUid();

        rootref1 = database.getReference("Message").child(sender_uid).child(receiver_uid);
        rootref2 = database.getReference("Message").child(receiver_uid).child(sender_uid);
        typingref = database.getReference("typing");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(file);

        sendbtn.setOnClickListener(v -> SendMessage());
        cambtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE);
        });


        btnVc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this,VideoCallOutgoing.class);
                intent.putExtra("uid",receiver_uid);
                startActivity(intent);
            }
        });

        micbtn.setOnClickListener(v -> createDialog());

        typingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(sender_uid).hasChild(receiver_uid)){
                    typingtv.setVisibility(View.VISIBLE);
                }else{
                    typingtv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Typing();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void Typing() {

        typingchecker = true;
        typingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(typingchecker.equals(true)){
                    if(snapshot.child(receiver_uid).hasChild(sender_uid)){
                        typingchecker = false;
                    }else{
                        typingref.child(receiver_uid).child(sender_uid).setValue(true);
                        typingchecker = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createDialog() {

        LayoutInflater inflater = LayoutInflater.from(MessageActivity.this);
        View view = inflater.inflate(R.layout.recorder_layout,null);
        TextView textView = view.findViewById(R.id.tv_record_status);
        Button start = view.findViewById(R.id.btn_start_rc);
        Button stop = view.findViewById(R.id.btn_stop_rc);
        Button send = view.findViewById(R.id.btn_send_rc);

        AlertDialog alertDialog = new AlertDialog.Builder(MessageActivity.this)
                .setView(view)
                .create();

        alertDialog.show();

        start.setOnClickListener(v -> {
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            textView.setText("Audio recording......");

        });

        stop.setOnClickListener(v -> {
            mediaRecorder.stop();
            mediaRecorder.release();
            textView.setText("Recording stopped");
        });

        send.setOnClickListener(v -> {
            Uri audiofile = Uri.fromFile(new File(file));
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Audio files");

            final StorageReference reference = storageReference.child(System.currentTimeMillis() + filename);
            uploadTask = reference.putFile(audiofile);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Calendar cdate = Calendar.getInstance();
                    SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
                    final String savedate = currentdate.format(cdate.getTime());

                    Calendar ctime = Calendar.getInstance();
                    SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
                    final String savetime = currenttime.format(ctime.getTime());


                    String time = savedate + ":" + savetime;

                    long deletetime = System.currentTimeMillis();
                    messageMember.setDate(savedate);
                    messageMember.setTime(savetime);
                    messageMember.setAudio(downloadUri.toString());
                    messageMember.setReceiveruid(receiver_uid);
                    messageMember.setSenderuid(sender_uid);
                    messageMember.setType("a");
                    messageMember.setDelete(deletetime);

                    String id = rootref1.push().getKey();
                    rootref1.child(id).setValue(messageMember);

                    String id1 = rootref2.push().getKey();
                    rootref2.child(id1).setValue(messageMember);

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        alertDialog.dismiss();
                        Toast.makeText(MessageActivity.this, "file sent", Toast.LENGTH_SHORT).show();
                    },1000);

                } else {
                    Toast.makeText(MessageActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == PICK_IMAGE || resultCode == RESULT_OK||data != null||data.getData()!=null){
                uri = data.getData();

                String url = uri.toString();
                Intent intent = new Intent(MessageActivity.this,SendImage.class);
                intent.putExtra("u",url);
                intent.putExtra("n",receiver_name);
                intent.putExtra("ruid",receiver_uid);
                intent.putExtra("suid",sender_uid);
                startActivity(intent);
            }else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(this, "Error"+e, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStart() {
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
                        holder.Setmessage(getApplication(),model.getMessage(),model.getTime(),model.getDate(),
                                model.getType(),model.getSenderuid(),model.getReceiveruid(),model.getSendername(),model.getAudio(),model.getImage());

                        String audio = getItem(position).getMessage();

                        long delete = getItem(position).getDelete();
                        String type = getItem(position).getType();
                        String imageuri = getItem(position).getImage();
                        String date = getItem(position).getDate();
                        String time = getItem(position).getTime();
                        String sendername = getItem(position).getSendername();


                        holder.playsender.setOnClickListener(v -> {
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            holder.playsender.setImageResource(R.drawable.ic_baseline_pause_white);
                            try {
                                mediaPlayer.setDataSource(audio);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                holder.playsender.setClickable(false);
                                mediaPlayer.setOnCompletionListener(mp -> {

                                    holder.playsender.setImageResource(R.drawable.ic_baseline_play_white);
                                    mediaPlayer.stop();
                                    holder.playsender.setClickable(true);

                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        });

                        holder.sendertv.setOnLongClickListener(v -> {
                            createMessageDialog(delete,type,imageuri,date,time,sendername,audio,"tv_sv");

                            return false;
                        });
                        holder.iv_sender.setOnLongClickListener(v -> {
                            createMessageDialog(delete,type,imageuri,date,time,sendername,audio,"iv_sv");

                            return false;
                        });
                        holder.playsender.setOnLongClickListener(v -> {
                            createMessageDialog(delete,type,imageuri,date,time,sendername,audio,"play_sv");

                            return false;
                        });

                        holder.receivertv.setOnLongClickListener(v -> {
                            createMessageDialog(delete,type,imageuri,date,time,sendername,audio,"tv_rv");

                            return false;
                        });
                        holder.iv_receiver.setOnLongClickListener(v -> {
                            createMessageDialog(delete,type,imageuri,date,time,sendername,audio,"iv_rv");

                            return false;
                        });
                        holder.playreceiver.setOnLongClickListener(v -> {
                            createMessageDialog(delete,type,imageuri,date,time,sendername,audio,"play_rv");

                            return false;
                        });


                        holder.playreceiver.setOnClickListener(v -> {
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            holder.playreceiver.setImageResource(R.drawable.ic_baseline_pause_black);
                            try {
                                mediaPlayer.setDataSource(audio);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                holder.playreceiver.setClickable(false);
                                mediaPlayer.setOnCompletionListener(mp -> {
                                    holder.playreceiver.setImageResource(R.drawable.ic_baseline_play_black);
                                    mediaPlayer.stop();
                                    holder.playreceiver.setClickable(true);

                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        });



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

        recyclerView.setAdapter(firebaseRecyclerAdapter1);


    }

    private void createMessageDialog(long delete, String type, String imageuri, String date, String time, String sendername,String audio,String key_m){
        final Dialog dialog = new Dialog(MessageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_options);

        TextView unsend = dialog.findViewById(R.id.unsend_id);
        TextView details = dialog.findViewById(R.id.details_id);
        TextView download = dialog.findViewById(R.id.option1_id);
        TextView option2 = dialog.findViewById(R.id.option2_id);
        TextView datetv = dialog.findViewById(R.id.date_mo);
        TextView timetv = dialog.findViewById(R.id.time_mo);

        if(key_m.equals("play_rv")||key_m.equals("iv_rv")||key_m.equals("tv_rv")){
            unsend.setVisibility(View.GONE);
        }
        if(type.equals("t")){
            download.setVisibility(View.GONE);
        }else{
            download.setVisibility(View.VISIBLE);
        }
        unsend.setOnClickListener(v -> {
            if(type.equals("t")){
               unsenddeletedb(time,type,imageuri,audio,delete);
                dialog.dismiss();
            }else if(type.equals("i")){
                unsenddeletedb(time,type,imageuri,audio,delete);
                dialog.dismiss();
            }else if(type.equals("a")){
                unsenddeletedb(time,type,imageuri,audio,delete);
                dialog.dismiss();
            }
        });

        details.setOnClickListener(v -> {
            datetv.setVisibility(View.VISIBLE);
            timetv.setVisibility(View.VISIBLE);
            datetv.setText("Date :"+date);
            timetv.setText("Time : "+time);
        });

        download.setOnClickListener(v -> {
            PermissionListener permissionListener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {

                    if(type.equals("i")){

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageuri));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Download");
                        request.setDescription("Downloading Image");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,sendername+System.currentTimeMillis()+".jpg");
                        DownloadManager manager = (DownloadManager)MessageActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(MessageActivity.this, "downloaded", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                    }else if(type.equals("a")){

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(audio));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Download");
                        request.setDescription("Downloading Audio");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,sendername+System.currentTimeMillis()+".mp3");
                        DownloadManager manager = (DownloadManager)MessageActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(MessageActivity.this, "downloaded", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();


                    }

                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                    Toast.makeText(MessageActivity.this, "error", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            };
            TedPermission.with(MessageActivity.this)
                    .setPermissionListener(permissionListener)
                    .setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE);

        });





        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

    private void unsenddeletedb(String time,String type,String imageuri,String audio,long delete){
        Query rootref = rootref1.orderByChild("delete").equalTo(delete);
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(MessageActivity.this, "deleted", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query rootref_1 = rootref2.orderByChild("delete").equalTo(delete);
        rootref_1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(MessageActivity.this, "deleted", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(type.equals("a")){
            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(audio);
            reference.delete().addOnSuccessListener(aVoid -> Toast.makeText(MessageActivity.this, "deleted", Toast.LENGTH_SHORT).show());
        }else if(type.equals("i")){
            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(imageuri);
            reference.delete().addOnSuccessListener(aVoid -> Toast.makeText(MessageActivity.this, "deleted", Toast.LENGTH_SHORT).show());
        }

    }

    private void SendMessage() {

        String message = messageEt.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());


        String time = savedate + ":" + savetime;

        if(message.isEmpty()){
            Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
        }else{
            long deletetime = System.currentTimeMillis();
            messageMember.setDate(savedate);
            messageMember.setTime(savetime);
            messageMember.setMessage(message);
            messageMember.setReceiveruid(receiver_uid);
            messageMember.setSenderuid(sender_uid);
            messageMember.setType("t");
            messageMember.setDelete(deletetime);

            String id = rootref1.push().getKey();
            rootref1.child(id).setValue(messageMember);

            String id1 = rootref2.push().getKey();
            rootref2.child(id1).setValue(messageMember);


            sendNotification(receiver_uid,receiver_name,message);
            messageEt.setText("");


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        typingref.child(receiver_uid).child(sender_uid).removeValue();
    }

    private void sendNotification(String receiver_uid, String receiver_name, String message){

        FirebaseDatabase.getInstance().getReference().child(receiver_uid).child("token")
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
        handler.postDelayed(() -> {

            FcmNotificationSender notificationsSender =
                    new FcmNotificationSender(usertoken,"Social Media",receiver_name+" : " + message,
                            getApplicationContext(),MessageActivity.this);

            notificationsSender.SendNotifications();

        },3000);

    }
}