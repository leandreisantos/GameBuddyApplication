package com.example.socialmedia;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class SellGroupHolder extends RecyclerView.ViewHolder {

    ImageView iv;
    TextView price_tv,title_tv;
    ConstraintLayout cons;

    public SellGroupHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setSellItem(FragmentActivity activity, String name,String url,String
            postUri,String time,String date,String uid,String title,String price,String category,String condition,String desc,String postKey){

        title_tv = itemView.findViewById(R.id.tv_title_sl);
        price_tv = itemView.findViewById(R.id.tv_price_sl);
        iv = itemView.findViewById(R.id.sell_layout_iv);
        cons = itemView.findViewById(R.id.content_sl);

        Picasso.get().load(postUri).into(iv);
        title_tv.setText(title);
        price_tv.setText("₱"+ price);

    }
}
