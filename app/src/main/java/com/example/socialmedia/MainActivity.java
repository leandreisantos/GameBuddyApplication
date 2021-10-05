package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener{


    FirebaseAuth auth;
    String post;
    //Drawer
    //Variables
    static final float END_SCALE = 0.7f;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView username;
    ImageView imageView;
    LinearLayout contentView;

    ImageView menu_icon;

    FirebaseAuth mAuth;
    View hView;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference ntRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        ntRef = database.getReference("notification").child(uid);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        Toast.makeText(MainActivity.this, "Token is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    databaseReference dbr = new databaseReference();
                    FirebaseDatabase.getInstance(dbr.keyDb()).getReference().child(uid).child("token").setValue(token);

                    // Log and toast
                    //String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });

        //menu
        navigationView = findViewById(R.id.navigation_view);

        menu_icon = findViewById(R.id.menu_icon_df);
        drawerLayout = findViewById(R.id.drawer_layout);


        contentView = findViewById(R.id.content);
        mAuth = FirebaseAuth.getInstance();

        //navigation
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        hView = navigationView.getHeaderView(0);
        username =hView.findViewById(R.id.mh_username);
        imageView = hView.findViewById(R.id.mh_iv);

        auth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);


        Bundle extras = getIntent().getExtras();

        if (extras != null){
            post = extras.getString("post");
            if(post.equals("post")){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment4()).commit();
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new dashboard_fragment()).commit();
            }
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new dashboard_fragment()).commit();
        }


        menu_icon.setOnClickListener(v -> navDrawer());




    }

    public void navDrawer() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            drawerLayout.openDrawer(GravityCompat.START);
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

    private final BottomNavigationView.OnNavigationItemSelectedListener onNav = item -> {
        Fragment selected = null;

        if(item.getItemId() == R.id.dashboard_bottom){
            selected = new dashboard_fragment();
        }
        if(item.getItemId() == R.id.group_bottom){
            selected = new groupFragment();
        }
        if(item.getItemId() == R.id.ask_bottom){
            selected = new Fragment2();
        }
        if(item.getItemId() == R.id.queue_bottom){
            selected = new Fragment3();
        }
        if(item.getItemId() == R.id.home_bottom){
            selected = new Fragment4();
        }

//        switch(item.getItemId()){
//
//            case R.id.dashboard_bottom:
//                selected = new dashboard_fragment();
//                break;
//            case R.id.profile_bottom:
//                selected = new Fragment1();
//                break;
//            case R.id.ask_bottom:
//                selected = new Fragment2();
//                break;
//            case R.id.queue_bottom:
//                selected = new Fragment3();
//                break;
//            case R.id.home_bottom:
//                selected = new Fragment4();
//                break;
//
//        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).commit();



        return true;
    };


    @Override
    public void onClick(View v) {
        
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        if(item.getItemId()== R.id.nav_profile){
            Intent intent = new Intent(MainActivity.this,MyProfileActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()== R.id.nav_logout) {
            logout();
        }if(item.getItemId()== R.id.notifications){
            Intent intent3 = new Intent(MainActivity.this,NotificationActivity.class);
            startActivity(intent3);
            changeSeen();
        }
        //drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeSeen(){
        Map<String,Object> profile = new HashMap<>();
        profile.put("seen","yes");

        ntRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    dataSnapshot.getRef().updateChildren(profile)
                            .addOnSuccessListener(aVoid -> {

                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Logout")
                .setMessage("Are you sure to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                })
                .setNegativeButton("No", (dialog, which) -> {

                });
        builder.create();
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        DocumentReference referenceUser;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();


        referenceUser = firestore.collection("user").document(currentid);
        referenceUser.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){
                        String nameResult = task.getResult().getString("name");
                        String url = task.getResult().getString("url");

                        Picasso.get().load(url).into(imageView);
                        username.setText(nameResult);

                    }else{
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}