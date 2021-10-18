package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SelectedItemSellActivity extends AppCompatActivity {

    ImageView iv_iv,iv_seller;
    TextView tv_title,tv_price,tv_con,tv_cat,tv_desc,tv_seller_name,tv_btn_viewp;
    String postKey;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;

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
                   String image = snapshot.child("postUri").getValue(String.class);

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
}