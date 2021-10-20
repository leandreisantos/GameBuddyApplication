package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.socialmedia.FragmetGss.CommunityGss;
import com.example.socialmedia.FragmetGss.HomeGss;
import com.example.socialmedia.FragmetGss.NewsFeedGss;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class GameSelectedSelectedActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selected_selected);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_gss,new HomeGss()).commit();

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