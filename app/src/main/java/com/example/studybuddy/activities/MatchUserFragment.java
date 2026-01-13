package com.example.studybuddy.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class MatchUserFragment extends Fragment {

    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_match_user, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);

        dbHelper = new DatabaseHelper(requireContext());

        User currentUser = dbHelper.getUserInfoByEmail(userEmail);

        if (currentUser != null) {
            Log.d("MatchUserFragment", "Current user fetched: " + currentUser.getEmail());
            Log.d("MatchUserFragment", "Topics interested: " + currentUser.getTopicInterested());
        } else {
            Log.d("MatchUserFragment", "No user found with email: " + userEmail);
            return view;
        }

        List<String> currentUserTopics = currentUser.getTopicInterested();
        if (currentUserTopics == null || currentUserTopics.isEmpty()) {
            Log.e("MatchUserFragment", "User has no topics of interest.");
            return view;
        }

        ArrayList<User> users = getUsersWithMatchingTopics(currentUserTopics);

        users.removeIf(user -> user.getEmail().equals(currentUser.getEmail()));

        if (users == null || users.isEmpty()) {
            Log.e("MatchUserFragment", "No users found with matching topics.");
        } else {
            Log.d("MatchUserFragment", "Users retrieved: " + users.size());
        }

        Map<String, List<User>> sectionedData = organizeUsersByTopic(currentUserTopics, users);

        Log.d("MatchUserFragment", "Sectioned data size: " + sectionedData.size());

        SectionedUserAdapter adapter = new SectionedUserAdapter(sectionedData);
        recyclerView.setAdapter(adapter);

        return view;
    }


    private ArrayList<User> getUsersWithMatchingTopics(List<String> currentUserTopics) {
        if (currentUserTopics == null || currentUserTopics.isEmpty()) {
            Log.e("MatchUserFragment", "Current user has no topics to match.");
            return new ArrayList<>();
        }

        ArrayList<User> users = dbHelper.getUsersWithSameTopics(currentUserTopics);
        return users;
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
