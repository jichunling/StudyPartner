package com.example.studypartner.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studypartner.R;
import com.example.studypartner.adapter.SectionedUserAdapter;
import com.example.studypartner.data.database.DatabaseHelper;
import com.example.studypartner.data.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MatchUserActivity
 *
 * Standalone activity (not used in main navigation) displaying study partner matches.
 * Functionality duplicated by MatchUserFragment which is accessed via bottom navigation.
 * Shows potential study partners organized by shared topic interests.
 *
 * Features:
 * - Displays matched users organized by shared topics
 * - Uses sectioned RecyclerView with topic headers
 * - Shows only users with at least one matching topic
 * - Click on user to view their detailed profile
 *
 * Note: This activity is largely redundant with MatchUserFragment.
 * Consider deprecating in favor of the fragment-based approach.
 */
public class MatchUserActivity extends AppCompatActivity {

    private static final String TAG = "MatchUserActivity";

    // SharedPreferences keys
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // UI Components
    private RecyclerView recyclerView;

    // Business Logic
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    // Data
    private String currentUserEmail;
    private User currentUser;

    /**
     * Initializes the match user activity and sets up UI components.
     *
     * @param savedInstanceState Saved state from previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_user);

        initializeComponents();

        if (!loadCurrentUser()) {
            return;
        }

        if (!validateUserTopics()) {
            return;
        }

        loadAndDisplayMatches();
    }

    /**
     * Initializes all components including views and database helper.
     */
    private void initializeComponents() {
        initializeViews();
        initializeDatabase();
        initializeSharedPreferences();
    }

    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    /**
     * Initializes SharedPreferences and retrieves user email.
     */
    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);
        Log.d(TAG, "User email from SharedPreferences: " + currentUserEmail);
    }

    /**
     * Loads the current user's data from database.
     *
     * @return true if user loaded successfully, false otherwise
     */
    private boolean loadCurrentUser() {
        currentUser = databaseHelper.getUserInfoByEmail(currentUserEmail);

        if (currentUser != null) {
            Log.d(TAG, "Current user fetched: " + currentUser.getEmail());
            Log.d(TAG, "Topics interested: " + currentUser.getTopicInterested());
            return true;
        } else {
            Log.e(TAG, "No user found with email: " + currentUserEmail);
            return false;
        }
    }

    /**
     * Validates that the current user has topic interests.
     *
     * @return true if user has topics, false otherwise
     */
    private boolean validateUserTopics() {
        List<String> currentUserTopics = currentUser.getTopicInterested();

        if (currentUserTopics == null || currentUserTopics.isEmpty()) {
            Log.e(TAG, "User has no topics of interest.");
            return false;
        }

        return true;
    }

    /**
     * Loads matched users and displays them in the RecyclerView.
     */
    private void loadAndDisplayMatches() {
        List<String> currentUserTopics = currentUser.getTopicInterested();
        ArrayList<User> matchedUsers = getMatchedUsers(currentUserTopics);

        if (matchedUsers.isEmpty()) {
            Log.e(TAG, "No users found with matching topics.");
            return;
        }

        Map<String, List<User>> sectionedData = organizeUsersByTopic(currentUserTopics, matchedUsers);
        displayMatches(sectionedData);
    }

    /**
     * Retrieves users with matching topic interests.
     *
     * @param currentUserTopics List of current user's topics
     * @return List of matched users
     */
    private ArrayList<User> getMatchedUsers(List<String> currentUserTopics) {
        if (currentUserTopics == null || currentUserTopics.isEmpty()) {
            return new ArrayList<>();
        }

        return databaseHelper.getUsersWithSameTopics(currentUserTopics, currentUserEmail);
    }

    /**
     * Organizes users by their matching topics into sections.
     *
     * @param currentUserTopics Current user's topic interests
     * @param users List of matched users
     * @return Map of topics to lists of users interested in that topic
     */
    private Map<String, List<User>> organizeUsersByTopic(List<String> currentUserTopics, ArrayList<User> users) {
        Map<String, List<User>> sectionedData = new HashMap<>();

        if (users == null || users.isEmpty()) {
            return sectionedData;
        }

        for (String topic : currentUserTopics) {
            List<User> filteredUsers = getUsersForTopic(users, topic);

            if (!filteredUsers.isEmpty()) {
                sectionedData.put(topic, filteredUsers);
            }
        }

        return sectionedData;
    }

    /**
     * Filters users who are interested in a specific topic.
     *
     * @param users List of users to filter
     * @param topic Topic to filter by
     * @return List of users interested in the topic
     */
    private List<User> getUsersForTopic(ArrayList<User> users, String topic) {
        List<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getTopicInterested() != null && user.getTopicInterested().contains(topic)) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    /**
     * Displays the sectioned match data in the RecyclerView.
     *
     * @param sectionedData Map of topics to user lists
     */
    private void displayMatches(Map<String, List<User>> sectionedData) {
        SectionedUserAdapter adapter = new SectionedUserAdapter(sectionedData);
        recyclerView.setAdapter(adapter);
    }
}

