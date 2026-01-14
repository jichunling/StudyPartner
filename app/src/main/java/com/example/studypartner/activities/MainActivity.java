package com.example.studypartner.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.studypartner.R;
import com.example.studypartner.fragments.AccountFragment;
import com.example.studypartner.fragments.GenAiFragment;
import com.example.studypartner.fragments.LikeFragment;
import com.example.studypartner.fragments.MatchUserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity
 *
 * Main hub activity for the studyPartner application after user login.
 * Manages bottom navigation and switches between four main sections:
 * - Matched Study Partners (shows compatible users)
 * - Likes (shows connection requests)
 * - AI Study Assistant (GenAI chat interface)
 * - Account Settings (user profile and preferences)
 *
 * Features:
 * - Bottom navigation bar with 4 tabs
 * - Fragment management for different sections
 * - Default landing on matched buddies screen
 * - Smooth fragment transitions
 *
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    // UI Components
    private BottomNavigationView bottomNavigationView;

    // Fragments - Reused instances to maintain state
    private MatchUserFragment matchUserFragment;
    private LikeFragment likeFragment;
    private GenAiFragment genAiFragment;
    private AccountFragment accountFragment;

    /**
     * Initializes the main activity and sets up bottom navigation.
     * Sets matched buddies as the default selected tab.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFragments();
        initializeNavigation();
    }

    /**
     * Initializes fragment instances that will be reused across navigation.
     * Maintains fragment state when switching between tabs.
     */
    private void initializeFragments() {
        matchUserFragment = new MatchUserFragment();
        likeFragment = new LikeFragment();
        genAiFragment = new GenAiFragment();
        accountFragment = new AccountFragment();
    }

    /**
     * Initializes the bottom navigation bar and sets default selection.
     */
    private void initializeNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set default selection to matched buddies
        bottomNavigationView.setSelectedItemId(R.id.nav_matched_buddies);
    }

    /**
     * Handles bottom navigation item selection.
     * Switches between different fragments based on selected tab.
     *
     * @param item The selected menu item
     * @return true if navigation was handled successfully
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        // Determine which fragment to display based on selection
        if (item.getItemId() == R.id.nav_matched_buddies) {
            selectedFragment = matchUserFragment;
        } else if (item.getItemId() == R.id.nav_likes) {
            selectedFragment = likeFragment;
        } else if (item.getItemId() == R.id.nav_genai) {
            selectedFragment = genAiFragment;
        } else if (item.getItemId() == R.id.nav_account) {
            selectedFragment = accountFragment;
        }

        // Replace current fragment with selected fragment
        if (selectedFragment != null) {
            replaceFragment(selectedFragment);
            return true;
        }

        return false;
    }

    /**
     * Replaces the current fragment with a new fragment.
     * Uses fragment transaction to switch between main sections.
     *
     * @param fragment The fragment to display
     */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
