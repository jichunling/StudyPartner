package com.example.studybuddy.activities;

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

import com.example.studybuddy.R;
import com.example.studybuddy.adapter.UserAdapter;
import com.example.studybuddy.data.database.ConnectionsDB;
import com.example.studybuddy.data.database.DatabaseHelper;
import com.example.studybuddy.data.model.Connections;
import com.example.studybuddy.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class LikeFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_likes, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        //Get all the current user's likes from connectionsDB
        ConnectionsDB connectionsDB = new ConnectionsDB(requireContext());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String myUserEmail = sharedPreferences.getString("userEmail", null);
        if (myUserEmail != null) {
            Log.d("LikeActivity", "userEmail: " + myUserEmail);
        } else {
            Log.e("LikesActivity", "userEmail is null. Cannot retrieve connections.");
            Toast.makeText(requireContext(), "userEmail not found!. Please log in again.", Toast.LENGTH_SHORT).show();
            return view;
        }
        List<Connections> likes = connectionsDB.getConnectionRequests(myUserEmail);


        //Convert "likes" into users
        DatabaseHelper db = new DatabaseHelper(requireContext());
        List<User> users = new ArrayList<>();
        for (Connections like : likes) {
            String senderEmail = like.getSenderEmail();

            // create user by his email
            User user = db.getUserInfoByEmail(senderEmail);
            users.add(user);
        }
        //Create Adapter
        UserAdapter adapter = new UserAdapter(users);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
