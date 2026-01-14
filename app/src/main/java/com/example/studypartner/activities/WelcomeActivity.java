package com.example.studypartner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studypartner.R;

/**
 * WelcomeActivity
 *
 * Splash screen activity that displays the studyPartner logo and branding
 * with fade-in animations. Automatically navigates to the login screen after
 * a brief delay to provide a welcoming entry point to the application.
 *
 * Features:
 * - Fade-in animations for logo, app name, and tagline
 * - Fullscreen display (hides action bar)
 * - Auto-navigation to login after 2.5 seconds
 * - Clean, professional branding presentation
 *
 */
public class WelcomeActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    // UI Components
    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView subLineTextView;

    /**
     * Initializes the welcome screen and starts animations.
     * Sets up automatic navigation to login screen after splash duration.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        hideActionBar();
        initializeViews();
        applyAnimations();
        scheduleNavigationToLogin();
    }

    /**
     * Hides the action bar to display splash screen in fullscreen.
     */
    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * Initializes all view references for splash screen elements.
     */
    private void initializeViews() {
        logoImageView = findViewById(R.id.logo);
        appNameTextView = findViewById(R.id.appName);
        subLineTextView = findViewById(R.id.subLine);
    }

    /**
     * Applies fade-in animations to all splash screen elements.
     * Loads animation from resources and applies to logo, app name, and tagline.
     */
    private void applyAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoImageView.startAnimation(fadeIn);
        appNameTextView.startAnimation(fadeIn);
        subLineTextView.startAnimation(fadeIn);
    }

    /**
     * Schedules automatic navigation to login screen after splash duration.
     * Uses Handler to delay navigation by SPLASH_DURATION milliseconds.
     */
    private void scheduleNavigationToLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToLogin();
            }
        }, SPLASH_DURATION);
    }

    /**
     * Navigates to the login activity and finishes the splash screen.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
