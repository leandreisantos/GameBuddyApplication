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

    ImageView bg,dp,bestbg,bestdp;
    TextView title_tv,desc_tv,besttitle,bestcat;
    ConstraintLayout cons;


    public GameViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void SetBestGame(FragmentActivity fragmentActivity, String title, String desc, String postkey, String cat, String postUri1,
                        String postUri2, String time, String date){

        bestbg = itemView.findViewById(R.id.iv_bg_bgl);
        bestdp = itemView.findViewById(R.id.iv_dp_bgl);
        besttitle = itemView.findViewById(R.id.tv_title_bgl);
        bestcat = itemView.findViewById(R.id.tv_cat_bgl);

        besttitle.setText(title);
        bestcat.setText(cat);
        Picasso.get().load(postUri1).into(bestdp);
        Picasso.get().load(postUri2).into(bestbg);
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
