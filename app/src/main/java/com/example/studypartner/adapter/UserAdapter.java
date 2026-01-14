package com.example.studypartner.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studypartner.R;
import com.example.studypartner.activities.ShowOtherUserProfileActivity;
import com.example.studypartner.data.model.User;

import java.util.List;

/**
 * UserAdapter
 *
 * RecyclerView adapter for displaying a list of study partner users.
 * Each user is displayed in a card view showing their name and email.
 * Clicking on a user opens their detailed profile.
 *
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;

    /**
     * Creates a new UserAdapter with the specified list of users.
     *
     * @param userList List of users to display
     */
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    /**
     * Creates a new ViewHolder for displaying user information.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new UserViewHolder
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_user_adaptor, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Binds user data to the ViewHolder at the specified position.
     * Sets up click listener to view the user's full profile.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position in the user list
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Display user's full name and email
        holder.nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        holder.emailTextView.setText(user.getEmail());

        // Set up click listener to view user profile
        holder.nameTextView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ShowOtherUserProfileActivity.class);
            intent.putExtra("email", user.getEmail());
            v.getContext().startActivity(intent);
        });
    }

    /**
     * Returns the total number of users in the list.
     *
     * @return Number of users to display
     */
    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    /**
     * ViewHolder class for user list items.
     * Holds references to the views for each user card.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        CardView userInfoContainer;

        /**
         * Creates a new ViewHolder and binds view references.
         *
         * @param itemView The view for this user item
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            userInfoContainer = itemView.findViewById(R.id.userInfo);
        }
    }
}
