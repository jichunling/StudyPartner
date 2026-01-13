package com.example.studybuddy.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studybuddy.R;
import com.example.studybuddy.adapter.SectionedUserAdapter;
import com.example.studybuddy.data.database.DatabaseHelper;
import com.example.studybuddy.data.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchUserActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_user);

        RecyclerView recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);

        Log.d("MatchUserActivity", "User email from SharedPreferences: " + userEmail);


        dbHelper = new DatabaseHelper(this);

        User currentUser = dbHelper.getUserInfoByEmail(userEmail);

        if (currentUser != null) {
            Log.d("MatchUserActivity", "Current user fetched: " + currentUser.getEmail());
            Log.d("MatchUserActivity", "Topics interested: " + currentUser.getTopicInterested());
        } else {
            Log.d("MatchUserActivity", "No user found with email: " + userEmail);
        }

        if (currentUser == null) {
            return;
        }


        List<String> currentUserTopics = currentUser.getTopicInterested();
        if (currentUserTopics.isEmpty()) {
            return;
        }


        ArrayList<User> users = getUsersWithMatchingTopics(currentUserTopics);

        Map<String, List<User>> sectionedData = organizeUsersByTopic(currentUserTopics, users);

        SectionedUserAdapter adapter = new SectionedUserAdapter(sectionedData);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<User> getUsersWithMatchingTopics(List<String> currentUserTopics) {
        if (currentUserTopics == null || currentUserTopics.isEmpty()) {
            return new ArrayList<>();
        }
        return dbHelper.getUsersWithSameTopics(currentUserTopics);
    }

    private Map<String, List<User>> organizeUsersByTopic(List<String> currentUserTopics, ArrayList<User> users) {
        Map<String, List<User>> sectionedData = new HashMap<>();
        if (users == null || users.isEmpty()) {
            return sectionedData;
        }

        for (String topic : currentUserTopics) {
            List<User> filteredUsers = new ArrayList<>();
            for (User user : users) {
                if (user.getTopicInterested() != null && user.getTopicInterested().contains(topic)) {
                    filteredUsers.add(user);
                }
            }
            if (!filteredUsers.isEmpty()) {
                sectionedData.put(topic, filteredUsers);
            }
        }
        return sectionedData;
    }
}
