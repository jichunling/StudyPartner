package com.example.studypartner.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studypartner.R;
import com.example.studypartner.adapter.UserAdapter;
import com.example.studypartner.data.database.ConnectionsDB;
import com.example.studypartner.data.database.DatabaseHelper;
import com.example.studypartner.data.model.Connections;
import com.example.studypartner.data.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * LikeFragment
 *
 * Fragment displaying connection requests (likes) received by the current user.
 * Accessed via the bottom navigation bar in MainActivity.
 * Shows a list of users who have sent connection requests to the logged-in user.
 *
 * Features:
 * - Display list of users who sent connection requests
 * - Click on user to view their detailed profile
 * - Automatically loads data from ConnectionsDB
 * - Shows error messages if user not logged in
 * - Empty state if no connection requests received
 *
 * Flow:
 * 1. Retrieves current user email from SharedPreferences
 * 2. Queries ConnectionsDB for connection requests
 * 3. Converts connections to User objects
 * 4. Displays users in RecyclerView
 */
public class LikeFragment extends Fragment {

    private static final String TAG = "LikeFragment";

    // SharedPreferences keys
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_EMAIL = "userEmail";

    // UI Components
    private RecyclerView recyclerView;

    // Business Logic
    private ConnectionsDB connectionsDB;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    // Data
    private String currentUserEmail;

    /**
     * Creates and initializes the like fragment view.
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

        View view = inflater.inflate(R.layout.activity_likes, container, false);

        initializeComponents(view);

        if (!loadCurrentUserEmail()) {
            showErrorMessage("userEmail not found! Please log in again.");
            return view;
        }

        loadAndDisplayConnectionRequests();

        return view;
    }

    /**
     * Initializes all components including views and database helpers.
     *
     * @param view The fragment's root view
     */
    private void initializeComponents(View view) {
        initializeViews(view);
        initializeDatabases();
        initializeSharedPreferences();
    }

    /**
     * Initializes all view references.
     *
     * @param view The fragment's root view
     */
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    /**
     * Initializes database helpers.
     */
    private void initializeDatabases() {
        connectionsDB = new ConnectionsDB(requireContext());
        databaseHelper = new DatabaseHelper(requireContext());
    }

    /**
     * Initializes SharedPreferences.
     */
    private void initializeSharedPreferences() {
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Loads the current user's email from SharedPreferences.
     *
     * @return true if email found, false otherwise
     */
    private boolean loadCurrentUserEmail() {
        currentUserEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);

        if (currentUserEmail != null) {
            Log.d(TAG, "Current user email: " + currentUserEmail);
            return true;
        } else {
            Log.e(TAG, "User email is null. Cannot retrieve connections.");
            return false;
        }
    }

    /**
     * Loads connection requests and displays them in the RecyclerView.
     */
    private void loadAndDisplayConnectionRequests() {
        List<Connections> connectionRequests = getConnectionRequests();
        List<User> users = convertConnectionsToUsers(connectionRequests);
        displayUsers(users);

        Log.d(TAG, "Displayed " + users.size() + " connection requests");
    }

    /**
     * Retrieves connection requests for the current user.
     *
     * @return List of connection requests
     */
    private List<Connections> getConnectionRequests() {
        return connectionsDB.getConnectionRequests(currentUserEmail);
    }

    /**
     * Converts connection requests to User objects.
     *
     * @param connections List of connection requests
     * @return List of User objects
     */
    private List<User> convertConnectionsToUsers(List<Connections> connections) {
        List<User> users = new ArrayList<>();

        for (Connections connection : connections) {
            String senderEmail = connection.getSenderEmail();
            User user = databaseHelper.getUserInfoByEmail(senderEmail);

            if (user != null) {
                users.add(user);
            } else {
                Log.w(TAG, "Could not find user with email: " + senderEmail);
            }
        }

        return users;
    }

    /**
     * Displays the list of users in the RecyclerView.
     *
     * @param users List of users to display
     */
    private void displayUsers(List<User> users) {
        UserAdapter adapter = new UserAdapter(users);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display
     */
    private void showErrorMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
