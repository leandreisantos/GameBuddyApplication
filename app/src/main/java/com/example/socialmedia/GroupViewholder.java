package com.example.socialmedia;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class GroupViewholder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView textViewName,btn;
    ImageView imageView;


    public GroupViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setGroupProfile(FragmentActivity fragmentActivity,String name, String uid,String url){

        cardView = itemView.findViewById(R.id.cv_gl);
        textViewName = itemView.findViewById(R.id.group_name_tv);
        imageView = itemView.findViewById(R.id.iv_gl);
        btn = itemView.findViewById(R.id.btn_gl);

        Picasso.get().load(url).into(imageView);
        textViewName.setText(name);
    }
}
