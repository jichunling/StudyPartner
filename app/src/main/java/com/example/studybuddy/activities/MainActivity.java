package com.example.studybuddy.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView
                = findViewById(R.id.bottom_navigation);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.nav_matched_buddies);

    }
    MatchUserFragment matchUserFragment = new MatchUserFragment();
    LikeFragment likeFragment = new LikeFragment();
    GenAiFragment genAiFragment = new GenAiFragment();
    AccountFragment accountFragment = new AccountFragment();


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.nav_matched_buddies) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, matchUserFragment)
                        .commit();
                return true;
            } else
                if (item.getItemId() == R.id.nav_likes) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, likeFragment)
                        .commit();
                return true;
            } else
                if (item.getItemId() == R.id.nav_genai) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, genAiFragment)
                        .commit();
                return true;
            }
                else if (item.getItemId() == R.id.nav_account) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, accountFragment)
                        .commit();
                return true;
            }
            return false;
    }
}


