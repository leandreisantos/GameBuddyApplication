package com.example.socialmedia.EventController;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmedia.R;
import com.example.socialmedia.databaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class Eventholder extends RecyclerView.ViewHolder {

    ImageView iv;
    TextView desctxt,titletxt;
    public TextView EventView;
    public ImageButton int_icon,g_icon;
    public ConstraintLayout cons;
    DatabaseReference intref,goingref;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    TextView inttv,goingtv;

    public int intcount,goingcount;

    public Eventholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setEvent(FragmentActivity activity, String name, String url, String postUri, String time, String uid, String type, String desc,String title,String date,
                         String postkey,String address,String game){

        iv = itemView.findViewById(R.id.fecd_iv_item);
        desctxt = itemView.findViewById(R.id.fecd_desc_item);
        titletxt = itemView.findViewById(R.id.fecd_title_item);
        EventView = itemView.findViewById(R.id.fecd_vm_item);


        if(type != null){
            Picasso.get().load(postUri).into(iv);
            desctxt.setText(desc);
            titletxt.setText(title);
        }

    }
    public void setAllEvent(Application application, String name, String url, String postUri, String time, String uid, String type, String desc, String title,String post_key) {
        iv = itemView.findViewById(R.id.iv_bg_el);
        desctxt = itemView.findViewById(R.id.tv_title_el);
        titletxt = itemView.findViewById(R.id.tv_desc_el);
        int_icon = itemView.findViewById(R.id.tv_int_icon_el);
        g_icon = itemView.findViewById(R.id.tv_going_icon_el);
        cons = itemView.findViewById(R.id.cl_eil);

        Picasso.get().load(postUri).into(iv);
        desctxt.setText(desc);
        titletxt.setText(title);
    }
    public void intchecker(String postKey) {

        int_icon = itemView.findViewById(R.id.tv_int_icon_el);
        inttv = itemView.findViewById(R.id.tv_int_el);

        intref = database.getReference("event int");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();


        intref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postKey).hasChild(uid)) {
                    int_icon.setImageResource(R.drawable.interested_icon);
                    intcount = (int) snapshot.child(postKey).getChildrenCount();
                    inttv.setText(Integer.toString(intcount) + " interested");

                } else {
                    int_icon.setImageResource(R.drawable.not_interested_icon);
                    intcount = (int) snapshot.child(postKey).getChildrenCount();
                    inttv.setText(Integer.toString(intcount) + " interested");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void goingchecker(String postKey){
        g_icon = itemView.findViewById(R.id.tv_going_icon_el);
        goingtv = itemView.findViewById(R.id.tv_going_el);

        goingref = database.getReference("event going");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();


        goingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postKey).hasChild(uid)) {
                    g_icon.setImageResource(R.drawable.not_going_icon);
                    goingcount = (int) snapshot.child(postKey).getChildrenCount();
                    goingtv.setText(Integer.toString(goingcount) + " going");

                } else {
                    g_icon.setImageResource(R.drawable.going_icon);
                    goingcount = (int) snapshot.child(postKey).getChildrenCount();
                    goingtv.setText(Integer.toString(goingcount) + " going");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
