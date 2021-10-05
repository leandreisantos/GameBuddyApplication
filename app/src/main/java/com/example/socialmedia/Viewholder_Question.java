package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class Viewholder_Question extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView time_result,name_result,question_result,deletebtn,replybtn,replybtn2;
    ImageButton fvrt_btn;
    DatabaseReference favouriteref;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());

    public Viewholder_Question(@NonNull View itemView) {
        super(itemView);
    }

    public void setitem(FragmentActivity activity,String name,String url,String userid,String key,String question,String privacy,
                   String time,String category){

        imageView = itemView.findViewById(R.id.iv_que_item);
        time_result = itemView.findViewById(R.id.time_que_item_tv);
        name_result = itemView.findViewById(R.id.name_que_item_tv);
        question_result = itemView.findViewById(R.id.que_item_tv);
        replybtn = itemView.findViewById(R.id.reply_item_que);

        Picasso.get().load(url).into(imageView);
        time_result.setText(time);
        name_result.setText(name);
        question_result.setText(question);

    }


    public void favouriteChecker(String postKey) {

        fvrt_btn = itemView.findViewById(R.id.fvrt_f2_item);


        favouriteref = database.getReference("favourites");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();


        favouriteref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(postKey).hasChild(uid)){
                    fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_24);

                }else{
                    fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void setitemRelated(Application activity, String name, String url, String userid, String key, String question, String privacy,
                               String time){

        TextView timetv = itemView.findViewById(R.id.related_time_que_item_tv);
        ImageView imageView = itemView.findViewById(R.id.related_iv_que_item);
        TextView nametv = itemView.findViewById(R.id.related_name_que_item_tv);
        TextView quetv = itemView.findViewById(R.id.related_que_item_tv);
        replybtn2 = itemView.findViewById(R.id.related_reply_item_que);

        Picasso.get().load(url).into(imageView);
        nametv.setText(name);
        timetv.setText(time);
        quetv.setText(question);

    }
    public void setitemYour_ques(Application activity, String name, String url, String userid, String key, String question, String privacy,
                               String time){

        TextView timetv = itemView.findViewById(R.id.yourQue_time_que_item_tv);
        ImageView imageView = itemView.findViewById(R.id.yourQue_iv_que_item);
        TextView nametv = itemView.findViewById(R.id.yourQue_name_que_item_tv);
        TextView quetv = itemView.findViewById(R.id.yourQue_que_item_tv);
        deletebtn = itemView.findViewById(R.id.yourQue_reply_item_que);

        Picasso.get().load(url).into(imageView);
        nametv.setText(name);
        timetv.setText(time);
        quetv.setText(question);

    }
}
