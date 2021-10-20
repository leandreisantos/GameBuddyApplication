package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.FragmetGss.CommunityGss;
import com.example.socialmedia.FragmetGss.HomeGss;
import com.example.socialmedia.FragmetGss.NewsFeedGss;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class GameSelectedSelectedActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    String keyword,post_key,title;
    TextView title_tv,tv_more,tv_close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selected_selected);

        Bundle extras =getIntent().getExtras();
        if (extras != null){
            keyword = extras.getString("k");
            post_key = extras.getString("id");
            title = extras.getString("t");
        }else {
            Toast.makeText(GameSelectedSelectedActivity.this, "No Dat error", Toast.LENGTH_SHORT).show();
        }

        title_tv = findViewById(R.id.tv_title_gss);
        tv_more = findViewById(R.id.tv_more_gss);
        tv_close = findViewById(R.id.tv_close_gss);

        title_tv.setText(title);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_gss,new HomeGss()).commit();

        tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        tv_close.setOnClickListener(v -> onBackPressed());

    }

    private void showBottomSheet() {
        final Dialog dialog = new Dialog(GameSelectedSelectedActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.f4_bottomsheet);

        TextView tvcp = dialog.findViewById(R.id.tv_cpf4);

        tvcp.setOnClickListener(v -> {
            Intent intent = new Intent(GameSelectedSelectedActivity.this,PostActivity.class);
            startActivity(intent);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onClick(View v) {

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