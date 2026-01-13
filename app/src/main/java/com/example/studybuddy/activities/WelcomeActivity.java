package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studybuddy.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        setContentView(R.layout.activity_welcome);

        // Hide the action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Getting all the components from UI and adding animation to it.
        ImageView logoImageView = findViewById(R.id.logo);
        TextView appName = findViewById(R.id.appName);
        TextView subLine = findViewById(R.id.subLine);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoImageView.startAnimation(fadeIn);
        appName.startAnimation(fadeIn);
        subLine.startAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);

    }
       // startActivity(intent);

//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        String savedEmail = sharedPreferences.getString("userEmail", null);

//        if (savedEmail != null) {
//            navigateToNextActivity(savedEmail);
//        } else {
//            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
//        finish();
//    }
//
//        private void navigateToNextActivity(String email) {
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        boolean isSetUp = dbHelper.isSetUp(email);
//
//        Intent intent;
//        if (isSetUp) {
//            intent = new Intent(WelcomeActivity.this, MatchUserActivity.class);
//        } else {
//            intent = new Intent(WelcomeActivity.this, UserProfileActivity.class);
//        }
//        startActivity(intent);

}
