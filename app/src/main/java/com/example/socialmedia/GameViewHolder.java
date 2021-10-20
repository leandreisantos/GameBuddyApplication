package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class GameViewHolder  extends RecyclerView.ViewHolder {

    ImageView bg,dp;
    TextView title_tv,desc_tv;
    ConstraintLayout cons;


    public GameViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void SetGame(Application application, String title, String desc, String postkey, String cat, String postUri1,
                        String postUri2, String time, String date){

        bg = itemView.findViewById(R.id.iv_gl_bg);
        dp = itemView.findViewById(R.id.iv_gl_dp);
        title_tv = itemView.findViewById(R.id.tv_gl_title);
        desc_tv = itemView.findViewById(R.id.tv_gl_desc);
        cons = itemView.findViewById(R.id.cons_gl);

        title_tv.setText(title);
        desc_tv.setText(desc);
        Picasso.get().load(postUri1).into(dp);
        Picasso.get().load(postUri2).into(bg);
    }
}
