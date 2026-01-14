package com.example.studypartner.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
 * MatchUserFragment
 *
 * Fragment displaying study partner matches based on shared topic interests.
 * Accessed via the bottom navigation bar in MainActivity.
 * Shows potential study partners organized by topic, allowing users to find
 * partners with similar learning interests.
 *
 * Features:
 * - Displays matched users organized by shared topics
 * - Uses sectioned RecyclerView with topic headers
 * - Filters out the current user from results
 * - Shows only users with at least one matching topic
 * - Click on user to view their detailed profile
 *
 * Matching Algorithm:
 * 1. Retrieves current user's topic interests
 * 2. Finds all users sharing at least one topic
 * 3. Organizes results by topic (users can appear under multiple topics)
 * 4. Excludes current user from results
 */
public class MatchUserFragment extends Fragment {

    private static final String TAG = "MatchUserFragment";

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
     * Creates and initializes the match user fragment view.
     *
     * @param inflater LayoutInflater to inflate views
     * @param container Parent view container
     * @param savedInstanceState Saved state from previous instance
     * @return The fragment view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_match_user, container, false);

        initializeComponents(view);

        if (!loadCurrentUser()) {
            return view;
        }

        if (!validateUserTopics()) {
            return view;
        }

        loadAndDisplayMatches();

        return view;
    }

    /**
     * Initializes all components including views and database helper.
     *
     * @param view The fragment's root view
     */
    private void initializeComponents(View view) {
        initializeViews(view);
        initializeDatabase();
        initializeSharedPreferences();
    }

    /**
     * Initializes all view references.
     *
     * @param view The fragment's root view
     */
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    /**
     * Initializes the database helper.
     */
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(requireContext());
    }

    /**
     * Initializes SharedPreferences and retrieves user email.
     */
    private void initializeSharedPreferences() {
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);
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
        removeCurrentUserFromMatches(matchedUsers);

        if (matchedUsers.isEmpty()) {
            Log.e(TAG, "No users found with matching topics.");
            return;
        }

        Log.d(TAG, "Users retrieved: " + matchedUsers.size());

        Map<String, List<User>> sectionedData = organizeUsersByTopic(currentUserTopics, matchedUsers);
        Log.d(TAG, "Sectioned data size: " + sectionedData.size());

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
            Log.e(TAG, "Current user has no topics to match.");
            return new ArrayList<>();
        }

        return databaseHelper.getUsersWithSameTopics(currentUserTopics, currentUserEmail);
    }

    /**
     * Removes the current user from the matched users list.
     *
     * @param users List of matched users
     */
    private void removeCurrentUserFromMatches(ArrayList<User> users) {
        users.removeIf(user -> user.getEmail().equals(currentUser.getEmail()));
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
