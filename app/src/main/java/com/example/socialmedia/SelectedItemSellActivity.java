package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.MarketPlaceController.ViewItemPicture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;


public class SelectedItemSellActivity extends AppCompatActivity {

    ImageView iv_iv,iv_seller;
    TextView tv_title,tv_price,tv_con,tv_cat,tv_desc,tv_seller_name,tv_btn_viewp,back,more;
    String postKey;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;

    String uid;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item_sell);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            postKey = extras.getString("p");
        }else {
            Toast.makeText(this, "No Dat error", Toast.LENGTH_SHORT).show();
        }

        reference = database.getReference("All sell").child(postKey);

        iv_iv =  findViewById(R.id.iv_iv_asi);
        iv_seller = findViewById(R.id.iv_seller_asi);

        tv_title = findViewById(R.id.tv_title_asi);
        tv_price = findViewById(R.id.tv_price_asi);
        tv_con = findViewById(R.id.tv_con_asi);
        tv_cat = findViewById(R.id.tv_cat_asi);
        tv_desc = findViewById(R.id.tv_desc_asi);
        tv_seller_name = findViewById(R.id.tv_sellername_asi);
        tv_btn_viewp = findViewById(R.id.tv_btn_p);
        back = findViewById(R.id.tv_back_asi);
        more = findViewById(R.id.tv_more_asi);

        back.setOnClickListener(v -> onBackPressed());

        more.setOnClickListener(v -> showDialog());

        iv_iv.setOnClickListener(v -> {
            Intent intent = new Intent(SelectedItemSellActivity.this, ViewItemPicture.class);
            intent.putExtra("p",image);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   String title = snapshot.child("title").getValue(String.class);
                   String price = snapshot.child("price").getValue(String.class);
                   String con = snapshot.child("condition").getValue(String.class);
                   String cat = snapshot.child("category").getValue(String.class);
                   String desc = snapshot.child("description").getValue(String.class);
                   String username = snapshot.child("name").getValue(String.class);
                   String url = snapshot.child("url").getValue(String.class);
                    image = snapshot.child("postUri").getValue(String.class);
                   uid = snapshot.child("uid").getValue(String.class);


                   tv_title.setText(title);
                   tv_price.setText("₱"+price);
                   tv_cat.setText(cat);
                   tv_con.setText(con);
                   tv_desc.setText(desc);
                   tv_seller_name.setText(username);
                Picasso.get().load(url).into(iv_seller);
                Picasso.get().load(image).into(iv_iv);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void showDialog(){

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.post_options,null);
        TextView download = view.findViewById(R.id.download_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyurl = view.findViewById(R.id.copyurl_tv_post);
        TextView edit = view.findViewById(R.id.edit_tv_post);
        TextView report = view.findViewById(R.id.report_tv_post);


        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        alertDialog.show();

        download.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        copyurl.setVisibility(View.GONE);
        edit.setText("Edit Sell Post");

        if(uid.equals(currentuid)){
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            report.setVisibility(View.GONE);
        }else{
            delete.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }




    }
}