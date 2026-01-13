package com.example.studybuddy.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studybuddy.R;
import com.example.studybuddy.activities.ShowOtherUserProfileActivity;
import com.example.studybuddy.data.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;

    // Constructor
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    // ViewHolder class to hold the views for each item
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        TextView ageTextView;
        TextView genderTextView;
        CardView userInfoContainer;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            //ageTextView = itemView.findViewById(R.id.ageTextView);
            //genderTextView = itemView.findViewById(R.id.genderTextView);
            userInfoContainer = itemView.findViewById(R.id.userInfo);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_user_adaptor, parent, false);
        return new UserViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Get the current user
        User user = userList.get(position);

        // Bind data to the views
        holder.nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        holder.emailTextView.setText(user.getEmail());
       // holder.ageTextView.setText("Age: " + user.getAge());
       // holder.genderTextView.setText("Gender: " + user.getGender());

        holder.nameTextView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ShowOtherUserProfileActivity.class);
            intent.putExtra("email", user.getEmail());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size(); // Number of users to display
    }
}
