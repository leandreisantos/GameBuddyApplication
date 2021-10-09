package com.example.socialmedia.EventController;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmedia.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class Eventholder extends RecyclerView.ViewHolder {

    ImageView iv;
    TextView desctxt,titletxt;
    public TextView EventView;

    public Eventholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setEvent(FragmentActivity activity, String name, String url, String postUri, String time, String uid, String type, String desc,String title){

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
}
