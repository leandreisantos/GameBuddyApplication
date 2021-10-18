package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MessageGroupActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    ImageView info,tempinvite;
    TextView sendBtn,groupName;

    //Database stuff
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference databaseReference;
    String sender_uid;

    //Drawer
    //Variables
    static final float END_SCALE = 0.7f;
    DrawerLayout drawerLayout;
    LinearLayout contentView;

    String postkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            postkey = bundle.getString("p");
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        //database stuff
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_uid = user.getUid();
        databaseReference = database.getReference("All group").child(postkey);

        info = findViewById(R.id.info_am);
        drawerLayout = findViewById(R.id.drawer_layout);
        contentView = findViewById(R.id.content);
        sendBtn = findViewById(R.id.imageButtonsend);
        groupName= findViewById(R.id.tv_groupname_amg);
        tempinvite = findViewById(R.id.temp_invite);

        NavigationView navigationView =findViewById(R.id.navigation_view);
        View headerview = navigationView.getHeaderView(0);
        TextView invite = headerview.findViewById(R.id.btn_add_mg);
        
        sendBtn.setOnClickListener(v -> doMessage());
        info.setOnClickListener(v -> navDrawer());

        tempinvite.setOnClickListener(v -> {
            Intent intent = new Intent(MessageGroupActivity.this,InvitePeopleGroupActivity.class);
            intent.putExtra("p",postkey);
            intent.putExtra("i",sender_uid);
            startActivity(intent);
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("name").getValue(String.class);
                String url = snapshot.child("url").getValue(String.class);
                groupName.setText(title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void doMessage() {
    }

    private void navDrawer() {
        if(drawerLayout.isDrawerVisible(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else{
            drawerLayout.openDrawer(GravityCompat.END);
        }

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        return true;
    }
}