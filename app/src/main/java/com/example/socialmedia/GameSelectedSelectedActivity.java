package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.FragmetGss.CommunityGss;
import com.example.socialmedia.FragmetGss.HomeGss;
import com.example.socialmedia.FragmetGss.NewsFeedGss;
import com.example.socialmedia.FragmetGss.ShowAllMemberGss_Activity;
import com.example.socialmedia.PostController.PostActivity;
import com.example.socialmedia.ReportGameCommunityController.ReportGameActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GameSelectedSelectedActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    String bundler_keyword,bundle_post_key,bundle_title;
    TextView title_tv,tv_more,tv_close;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,ref1,ref2,ref3,ref4;

    TextView tvcp,tv_s,tv_r,tv_d,tv_m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selected_selected);

        Bundle extras =getIntent().getExtras();
        if (extras != null){
            bundler_keyword = extras.getString("k");
            bundle_post_key = extras.getString("id");
            bundle_title = extras.getString("t");
        }else {
            Toast.makeText(GameSelectedSelectedActivity.this, "No Dat error", Toast.LENGTH_SHORT).show();
        }

        reference = database.getReference(bundler_keyword).child(bundle_post_key);

        title_tv = findViewById(R.id.tv_title_gss);
        tv_more = findViewById(R.id.tv_more_gss);
        tv_close = findViewById(R.id.tv_close_gss);

        title_tv.setText(bundle_title);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_gss,new HomeGss()).commit();

        tv_more.setOnClickListener(v -> showBottomSheet());
        tv_close.setOnClickListener(v -> onBackPressed());

    }

    private void showBottomSheet() {

        checkReference();
        final Dialog dialog = new Dialog(GameSelectedSelectedActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.f4_bottomsheet);

         tvcp = dialog.findViewById(R.id.tv_cpf4);
         tv_s = dialog.findViewById(R.id.tv_csf4);
         tv_r = dialog.findViewById(R.id.tv_report);
         tv_d = dialog.findViewById(R.id.tv_delete);
         tv_m = dialog.findViewById(R.id.tv_member);

        tv_s.setVisibility(View.GONE);

        tvcp.setOnClickListener(v -> {
            Intent intent = new Intent(GameSelectedSelectedActivity.this, PostActivity.class);
            intent.putExtra("kp","gs");
            intent.putExtra("t",bundle_title);
            startActivity(intent);
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void checkReference() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    String dps = snapshot.child("postUri1").getValue(String.class);
                    String bgs = snapshot.child("postUri2").getValue(String.class);
                    String about = snapshot.child("about").getValue(String.class);
                    String new_g = snapshot.child("whatNew").getValue(String.class);
                    String id = snapshot.child("create").getValue(String.class);
                    String owner_s = snapshot.child("owner").getValue(String.class);
                    String email_s = snapshot.child("email").getValue(String.class);
                    String cat_s = snapshot.child("cat").getValue(String.class);
                    String add_s = snapshot.child("address").getValue(String.class);
                    String postkey = snapshot.child("postkey").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);



                    if(id.equals(currentuid)){
                        tv_r.setVisibility(View.GONE);
                    }else{
                        tv_r.setVisibility(View.VISIBLE);
                        tv_d.setVisibility(View.GONE);
                    }

                    if(postkey!=null){
                        tv_r.setOnClickListener(v -> {
                            Intent intent = new Intent(GameSelectedSelectedActivity.this, ReportGameActivity.class);
                            intent.putExtra("p",postkey);
                            startActivity(intent);
                        });

                        tv_m.setOnClickListener(v -> {
                            Intent intent = new Intent(GameSelectedSelectedActivity.this, ShowAllMemberGss_Activity.class);
                            intent.putExtra("p",postkey);
                            intent.putExtra("t",title);
                            intent.putExtra("c",cat_s);
                            startActivity(intent);
                        });

                        tv_d.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteCom();
                            }
                        });


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteCom() {
        LayoutInflater inflater = LayoutInflater.from(GameSelectedSelectedActivity.this);
        View view = inflater.inflate(R.layout.confirm_delete_game_layout,null);

        TextView delete = view.findViewById(R.id.tv_delete_cdg);
        TextView cancel = view.findViewById(R.id.tv_cancel_cdg);


        AlertDialog alertDialog = new AlertDialog.Builder(GameSelectedSelectedActivity.this)
                .setView(view)
                .create();
        alertDialog.show();

        delete.setOnClickListener(v -> {
            ref1 = database.getReference(bundler_keyword).child(bundle_post_key);
            ref2 = database.getReference("All game").child(bundle_post_key);
//            ref3 = database.getReference("Game Member").child(bundler_keyword).child(bundle_post_key);
//            ref4 = database.getReference("All post").child(bundle_title);
            ref1.removeValue();
            ref2.removeValue();
//            ref3.removeValue();
//            ref4.removeValue();

            Toast.makeText(GameSelectedSelectedActivity.this, "Game deleted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GameSelectedSelectedActivity.this,SelectedGameActivity.class);
            intent.putExtra("s",bundle_title);
            intent.putExtra("k",bundler_keyword);
            startActivity(intent);
        });



    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNav = item -> {
        Fragment selected = null;

        if(item.getItemId() == R.id.home_bottom_gss) selected = new HomeGss();
        if(item.getItemId() == R.id.newsfeed_gss) selected = new NewsFeedGss();
        if(item.getItemId() == R.id.community_gss) selected = new CommunityGss();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_gss,selected).commit();

        return true;
    };

}