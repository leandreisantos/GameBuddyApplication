package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nametv,texttv;


    public NewViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setNt(Application application,String url,String name,String text,String uid,String seen){

        texttv = itemView.findViewById(R.id.text_newtv);
        nametv = itemView.findViewById(R.id.name_new_tv);
        imageView = itemView.findViewById(R.id.iv_new);

        Picasso.get().load(url).into(imageView);
        nametv.setText(name);
        texttv.setText(text);

    }


}
