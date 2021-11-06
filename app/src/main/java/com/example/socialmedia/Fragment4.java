package com.example.socialmedia;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.content.ClipboardManager;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.PostController.PostActivity;
import com.example.socialmedia.PostController.PostViewholder;
import com.example.socialmedia.PostController.Postmember;
import com.example.socialmedia.ReportController.ReportPostActivity;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.app.Activity.RESULT_OK;

public class Fragment4 extends Fragment implements View.OnClickListener{

    SwipeRefreshLayout sp;

    TextView button,logoref;
    RecyclerView recyclerView;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,likesref,db1,db2,db3,storyRef,likelist,referenceDel,ntref,reference_s;
    Boolean likechecker = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    NewMember newMember;

    //recylcerview for story
    RecyclerView recyclerViewstory;

    LinearLayoutManager linearLayoutManager;

    Uri imageUri;
    private static final int PICK_IMAGE = 1;

    String name_result,url_result,uid_result,usertoken;

    AllUserMember userMember;
    String currentuid;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment4,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         currentuid = user.getUid();

        button = getActivity().findViewById(R.id.createpost_f4);
        logoref = getActivity().findViewById(R.id.aname_f4);
        reference = database.getReference("All post").child("public");
        likesref = database.getReference("post likes");
        storyRef = database.getReference("All story");
        referenceDel = database.getReference("story");
        documentReference = db.collection("user").document(currentuid);
        sp = getActivity().findViewById(R.id.swipe_post);

        recyclerView = getActivity().findViewById(R.id.rv_post);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        newMember = new NewMember();
        ntref = database.getReference("notification").child(currentuid);


        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All post").child("public");
        db3.keepSynced(true);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerViewstory = getActivity().findViewById(R.id.rv_storyf4);
        recyclerViewstory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerViewstory.setLayoutManager(linearLayoutManager);
        recyclerViewstory.setItemAnimator(new DefaultItemAnimator());

        button.setOnClickListener(this);

        userMember = new AllUserMember();

        checkStory(currentuid);
        sp.setOnRefreshListener(() -> sp.setRefreshing(false));

        logoref.setOnClickListener(v -> sp.setRefreshing(false));

    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.createpost_f4) {
            Intent intent = new Intent(getActivity(), PostActivity.class);
            intent.putExtra("kp","p");
            startActivity(intent);
        }
    }

    private void checkStory(String currentuid){
        referenceDel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(currentuid)){

                }else{
                    Query query3 = storyRef.orderByChild("uid").equalTo(currentuid);
                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                                dataSnapshot1.getRef().removeValue();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBottomsheet(String url,String desc,String post_key_s) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_post_bottom_layout);

        ImageView dp = dialog.findViewById(R.id.iv_epb);
        EditText et = dialog.findViewById(R.id.et_desc_epb);
        TextView save = dialog.findViewById(R.id.tv_save_epb);
        Toast.makeText(getActivity(), post_key_s, Toast.LENGTH_SHORT).show();
        reference_s = database.getReference("All post").child("public").child(post_key_s);


        Picasso.get().load(url).into(dp);
        et.setText(desc);

        String et_text = et.getText().toString();

        String tempdesc = et_text;
        save.setOnClickListener(v -> {
            reference_s.child("descSharer").setValue(tempdesc);
            Toast.makeText(getActivity(), "Saved Changes", Toast.LENGTH_SHORT).show();
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

        documentReference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){
                        name_result = task.getResult().getString("name");
                        url_result = task.getResult().getString("url");
                        uid_result = task.getResult().getString("uid");
                    }
                });

        FirebaseRecyclerOptions<Postmember> options =
                new FirebaseRecyclerOptions.Builder<Postmember>()
                        .setQuery(reference,Postmember.class)
                        .build();

        FirebaseRecyclerAdapter<Postmember, PostViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Postmember, PostViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewholder holder, int position, @NonNull Postmember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.SetPost(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),
                                model.getType(),model.getDesc(),model.getPrivacy(),model.getDate(),model.getPostkey(),model.getDescSharer(),model.getPostkeySharer(),
                                model.getUidSharer(),model.getPrivacySharer(),model.getSharerType(),model.getTimeShare(),model.getDateShare(),model.getNameSharer(),model.getUrlSharer());

                        String name = getItem(position).getName();
                        String url = getItem(position).getPostUri();
                        String time = getItem(position).getTime();
                        String type = getItem(position).getType();
                        String userid = getItem(position).getUid();
                        String desc = getItem(position).getDesc();
                        String post_key = getItem(position).getPostkey();
                        String share_id = getItem(position).getUidSharer();
                        String type_share = getItem(position).getSharerType();
                        String url_share = getItem(position).getUrlSharer();
                        String desc_share = getItem(position).getDescSharer();
                        String postkey_share = getItem(position).getPostkeySharer();
                        String time_share = getItem(position).getTimeShare();
                        String name_share = getItem(position).getNameSharer();

                        holder.likechecker(postkey);
                        holder.commentchecker(postkey);

                        holder.sharebtn.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(),ShareActivity.class);
                            intent.putExtra("p","public");
                            intent.putExtra("k",post_key);
                            startActivity(intent);
                        });

                        holder.menuoptions.setOnClickListener(v -> showDialog(name,url,time,userid,type,post_key,share_id,type_share,
                                url_share,desc_share,postkey_share,time_share));

                        holder.imageViewprofile.setOnClickListener(v -> {
                            if(type_share!=null){
                                if (currentUserid.equals(share_id)) {
                                    Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(getActivity(),ViewUserActivity.class);
                                    intent.putExtra("n",name_share);
                                    intent.putExtra("u",url_share);
                                    intent.putExtra("uid",share_id);
                                    startActivity(intent);
                                }
                            }else{
                                if (currentUserid.equals(userid)) {
                                    Intent intent = new Intent(getActivity(),MyProfileActivity.class);
                                    startActivity(intent);

                                }else {
                                    Intent intent = new Intent(getActivity(),ViewUserActivity.class);
                                    intent.putExtra("n",name);
                                    intent.putExtra("u",url);
                                    intent.putExtra("uid",userid);
                                    startActivity(intent);
                                }
                            }
                        });
                        holder.likebtn.setOnClickListener(v -> {

                            likechecker = true;

                            likesref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(likechecker.equals(true)){
                                        if(snapshot.child(postkey).hasChild(currentUserid)){
                                            likesref.child(postkey).child(currentUserid).removeValue();
                                            likelist = database.getReference("like list").child(postkey).child(currentUserid);
                                            likelist.removeValue();
                                            //delete(time);

                                            ntref.child(currentUserid+"l").removeValue();
                                            likechecker = false;
                                        }else{
                                            likesref.child(postkey).child(currentUserid).setValue(true);
                                            likelist = database.getReference("like list").child(postkey);
                                            userMember.setName(name);
                                            userMember.setUid(currentUserid);
                                            userMember.setUrl(url);

                                            likelist.child(currentUserid).setValue(userMember);

                                            newMember.setName(name_result);
                                            newMember.setUid(currentUserid);
                                            newMember.setUrl(url_result);
                                            newMember.setSeen("no");
                                            newMember.setText("Like your post");
                                            newMember.setAction("L");

                                            ntref.child(currentUserid+"l").setValue(newMember);
                                            sendNotification(name_result,userid);


                                            likechecker = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        });
                        holder.tv_likes.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(),ShowLikedUser.class);
                            intent.putExtra("p",postkey);
                            startActivity(intent);
                        });
                        holder.commentbtn.setOnClickListener(view -> {
                            Intent intent = new Intent(getActivity(),CommentsActivity.class);
                            intent.putExtra("postkey",postkey);
                            intent.putExtra("name",name);
                            intent.putExtra("url",url);
                            intent.putExtra("uid",userid);
                            intent.putExtra("d",desc);
                            startActivity(intent);
                        });
                        holder.iv_post.setOnClickListener(v -> ShowPost(url,userid,postkey,name));

                    }




                    @NonNull
                    @Override
                    public PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout,parent,false);

                        return new PostViewholder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);

        //story firebase adapter
        FirebaseRecyclerOptions<StoryMember> options1 =
                new FirebaseRecyclerOptions.Builder<StoryMember>()
                        .setQuery(storyRef,StoryMember.class)
                        .build();

        FirebaseRecyclerAdapter<StoryMember,StoryViewHolder> firebaseRecyclerAdapterstory =
                new FirebaseRecyclerAdapter<StoryMember, StoryViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull StoryViewHolder holder, int position, @NonNull StoryMember model) {

                        holder.setStory(getActivity(),model.getPostUri(),model.getName(),model.getTimeEnd(),model.getTimeUpload(),
                                model.getType(),model.getCaption(),model.getUrl(),model.getUid());

                        String userid = getItem(position).getUid();
                        holder.imageView.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(),ShowStory.class);
                            intent.putExtra("u",userid);
                            startActivity(intent);
                        });



                    }
                    @NonNull
                    @Override
                    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.story_layout,parent,false);

                        return new StoryViewHolder(view);
                    }
                };


        firebaseRecyclerAdapterstory.startListening();

        recyclerViewstory.setAdapter(firebaseRecyclerAdapterstory);



    }

    private void ShowPost(String url,String uid,String postkey,String name) {
        Intent intent = new Intent(getActivity(),ViewImage.class);
        intent.putExtra("i",uid);
        intent.putExtra("iv",url);
        intent.putExtra("p",postkey);
        intent.putExtra("n",name);
        startActivity(intent);
    }

    public void shareother(String name,String url){
        String sharetext = name + "\n" + "\n" + url;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT,sharetext);
        intent.setType("text/plain");
    }


    void showDialog(String name,String url,String time,String userid,String type,String postKey,String s_id,String type_s,
                    String url_s,String desc_s,String postkey_s,String time_s){

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.post_options,null);
        TextView download = view.findViewById(R.id.download_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyurl = view.findViewById(R.id.copyurl_tv_post);
        TextView edit = view.findViewById(R.id.edit_tv_post);
        TextView report = view.findViewById(R.id.report_tv_post);


        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        alertDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        if(type_s!=null){
            if (s_id.equals(currentuid)) {
                delete.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                report.setVisibility(View.GONE);
            }else{
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
            }
        }else{
            if(userid.equals(currentuid)){
                delete.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                report.setVisibility(View.GONE);

            }else{
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
            }
        }

        if(type.equals("txt")) download.setVisibility(View.GONE);
        else download.setVisibility(View.VISIBLE);


        edit.setOnClickListener(v -> {
            if(type_s!=null){
                showBottomsheet(url_s,desc_s,postkey_s);
            }else{
                Intent intent = new Intent(getActivity(),EditPostActivity.class);
                intent.putExtra("p","public");
                intent.putExtra("k",postKey);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(v -> {
            if(type_s!=null){
                db3.child(postkey_s).removeValue();
                //Toast.makeText(getActivity(), "share deleted", Toast.LENGTH_SHORT).show();
            }else if(type_s == null){
                if(type.equals("txt")){
                    db3.child(postKey).removeValue();
                   // Toast.makeText(getActivity(), "txt deleted", Toast.LENGTH_SHORT).show();
                }else{
                    db3.child(postKey).removeValue();
                    db1.child(postKey).removeValue();
                    db2.child(postKey).removeValue();
                   // Toast.makeText(getActivity(), "iv or vv deleted", Toast.LENGTH_SHORT).show();
                }
            }else{
                //Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }

//            Query query = db1.orderByChild("time").equalTo(timefinal);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
//                        dataSnapshot1.getRef().removeValue();
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//            Query query2 = db2.orderByChild("time").equalTo(timefinal);
//            query2.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
//                        dataSnapshot1.getRef().removeValue();
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//            Query query3 = db3.orderByChild("time").equalTo(timefinal);
//            query3.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
//                        dataSnapshot1.getRef().removeValue();
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

            if(type.equals("txt")){
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            }else{
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                reference.delete().addOnSuccessListener(aVoid -> {
                });
            }

            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();

        });
        
        download.setOnClickListener(v -> {
            PermissionListener permissionListener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {

                    if(type.equals("iv")){

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Download");
                        request.setDescription("Downloading Image");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis()+".jpg");
                        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();
                    }else{

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Download");
                        request.setDescription("Downloading Video");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis()+".mp4");
                        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();

                    }

                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                    Toast.makeText(getActivity(), "No permission", Toast.LENGTH_SHORT).show();
                }
            };
            TedPermission.with(getActivity())
                    .setPermissionListener(permissionListener)
                    .setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE);


        });

        share.setOnClickListener(v -> {
            String sharetext = name + "\n" + "\n" + url;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT,sharetext);
            intent.setType("text/plain");
            alertDialog.dismiss();
        });

        copyurl.setOnClickListener(v -> {
            ClipboardManager cp = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("String",url);
            cp.setPrimaryClip(clip);
            clip.getDescription();
            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        report.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReportPostActivity.class);
            intent.putExtra("p","public");
            intent.putExtra("k",postKey);
            startActivity(intent);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(requestCode == PICK_IMAGE || resultCode == RESULT_OK||data != null||data.getData()!=null){
                imageUri = data.getData();

                String url = imageUri.toString();
                Intent intent = new Intent(getActivity(),StoryActivity.class);
                intent.putExtra("u",url);
                startActivity(intent);
            }

        }catch (Exception e){
            Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String name,String uid){

        FirebaseDatabase.getInstance(dbr.keyDb()).getReference("Token").child(uid).child("token")
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
                    new FcmNotificationSender(usertoken,"Social Media",name+" Like your post: "  ,
                            getContext(),getActivity());

            notificationsSender.SendNotifications();

        },3000);

    }

}
