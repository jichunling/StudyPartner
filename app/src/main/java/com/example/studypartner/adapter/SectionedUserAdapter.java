package com.example.studypartner.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studypartner.R;
import com.example.studypartner.activities.ShowOtherUserProfileActivity;
import com.example.studypartner.data.database.ConnectionsDB;
import com.example.studypartner.data.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SectionedUserAdapter
 *
 * RecyclerView adapter for displaying study partner users grouped by topic sections.
 * Features collapsible sections and a like/unlike functionality for connecting with users.
 * Each section represents a study topic, and users with matching interests are displayed under it.
 *
 */
public class SectionedUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SectionedUserAdapter";

    // View types
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_USER = 1;

    // Data structures
    private Map<String, List<User>> sectionedData;
    private List<Object> displayList;
    private Map<String, Boolean> sectionVisibilityMap;  // Tracks which sections are expanded
    private Map<String, Boolean> iconStateMap;  // Tracks which users are liked

    /**
     * Creates a new SectionedUserAdapter with the specified sectioned user data.
     *
     * @param sectionedData Map of topic sections to lists of matching users
     */
    public SectionedUserAdapter(Map<String, List<User>> sectionedData) {
        this.sectionedData = sectionedData;
        this.displayList = new ArrayList<>();
        this.sectionVisibilityMap = new HashMap<>();
        this.iconStateMap = new HashMap<>();
        buildDisplayList();
    }

    /**
     * Builds the display list from sectioned data based on section visibility.
     * Headers are always shown; user items are shown only if their section is expanded.
     */
    private void buildDisplayList() {
        displayList.clear();

        for (Map.Entry<String, List<User>> entry : sectionedData.entrySet()) {
            String section = entry.getKey();
            List<User> users = entry.getValue();

            // Add section header
            displayList.add(section);

            // Add users if section is expanded (default: expanded)
            if (sectionVisibilityMap.getOrDefault(section, true)) {
                displayList.addAll(users);
            }
        }

        Log.d(TAG, "Display list built with " + displayList.size() + " items");
    }

    /**
     * Updates the adapter with new sectioned data and refreshes the display.
     *
     * @param newSectionedData New map of topic sections to user lists
     */
    public void updateData(Map<String, List<User>> newSectionedData) {
        this.sectionedData = newSectionedData;
        buildDisplayList();
        notifyDataSetChanged();
    }

    /**
     * Returns the total number of items in the display list.
     *
     * @return Number of items (headers + visible users)
     */
    @Override
    public int getItemCount() {
        return displayList.size();
    }

    /**
     * Determines the view type for the item at the specified position.
     *
     * @param position Position in the display list
     * @return VIEW_TYPE_HEADER for section headers, VIEW_TYPE_USER for user items
     */
    @Override
    public int getItemViewType(int position) {
        return displayList.get(position) instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_USER;
    }

    /**
     * Creates a new ViewHolder based on the view type.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type (header or user)
     * @return HeaderViewHolder or UserViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     * Handles both section headers and user items with appropriate logic.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position in the display list
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            bindHeaderViewHolder((HeaderViewHolder) holder, position);
        } else {
            bindUserViewHolder((UserViewHolder) holder, position);
        }
    }

    /**
     * Binds section header data and sets up collapse/expand functionality.
     *
     * @param holder The HeaderViewHolder to bind
     * @param position Position in the display list
     */
    private void bindHeaderViewHolder(HeaderViewHolder holder, int position) {
        String sectionTitle = (String) displayList.get(position);
        holder.sectionTitle.setText(sectionTitle);

        // Set arrow icon based on section visibility
        boolean isExpanded = sectionVisibilityMap.getOrDefault(sectionTitle, true);
        updateHeaderUI(holder, isExpanded);

        // Toggle section visibility on click
        holder.sectionTitle.setOnClickListener(v -> {
            boolean currentVisibility = sectionVisibilityMap.getOrDefault(sectionTitle, true);
            boolean newVisibility = !currentVisibility;

            sectionVisibilityMap.put(sectionTitle, newVisibility);
            updateHeaderUI(holder, newVisibility);

            buildDisplayList();
            notifyDataSetChanged();
        });
    }

    /**
     * Updates header UI based on expanded/collapsed state.
     *
     * @param holder The HeaderViewHolder to update
     * @param isExpanded Whether the section is expanded
     */
    private void updateHeaderUI(HeaderViewHolder holder, boolean isExpanded) {
        if (isExpanded) {
            holder.sectionTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, R.drawable.arrow_drop_down, 0);
            holder.longUnderline.setVisibility(View.GONE);
        } else {
            holder.sectionTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, R.drawable.arrow_drop_up, 0);
            holder.longUnderline.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Binds user data and sets up profile viewing and connection functionality.
     *
     * @param holder The UserViewHolder to bind
     * @param position Position in the display list
     */
    private void bindUserViewHolder(UserViewHolder holder, int position) {
        User user = (User) displayList.get(position);

        // Display user information
        holder.firstNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        holder.timeTextView.setText(user.getFormattedStudyTime());

        // Set up profile viewing
        holder.firstNameTextView.setOnClickListener(v -> openUserProfile(v, user));

        // Set up like/unlike functionality
        updateLikeIcon(holder.toggleIcon, user.getEmail());
        holder.toggleIcon.setOnClickListener(v -> toggleUserLike(holder, user));
    }

    /**
     * Opens the detailed profile page for the specified user.
     *
     * @param view The view triggering the action
     * @param user The user whose profile to display
     */
    private void openUserProfile(View view, User user) {
        Intent intent = new Intent(view.getContext(), ShowOtherUserProfileActivity.class);
        intent.putExtra("email", user.getEmail());
        intent.putExtra("firstName", user.getFirstName());
        intent.putExtra("lastName", user.getLastName());
        intent.putExtra("topics", user.getTopicInterested());
        view.getContext().startActivity(intent);
    }

    /**
     * Toggles the like state for a user and creates/removes connection.
     *
     * @param holder The UserViewHolder containing the toggle icon
     * @param user The user to like/unlike
     */
    private void toggleUserLike(UserViewHolder holder, User user) {
        boolean currentState = iconStateMap.getOrDefault(user.getEmail(), false);
        boolean newState = !currentState;

        iconStateMap.put(user.getEmail(), newState);
        updateLikeIcon(holder.toggleIcon, user.getEmail());

        // Create connection if liked
        if (newState) {
            ConnectionsDB connectionsDB = new ConnectionsDB(holder.itemView.getContext());
            boolean isConnected = connectionsDB.insertConnectionRequest(user.getEmail(), user.getEmail());

            if (isConnected) {
                Log.d(TAG, "Connection request sent to " + user.getEmail());
            }
        }
    }

    /**
     * Updates the like icon based on the user's like state.
     *
     * @param icon The ImageView to update
     * @param userEmail The user's email to check state for
     */
    private void updateLikeIcon(ImageView icon, String userEmail) {
        boolean isLiked = iconStateMap.getOrDefault(userEmail, false);
        icon.setImageResource(isLiked ? R.drawable.thumb_up_fill : R.drawable.thumb_up_blank);
    }

    /**
     * ViewHolder for section header items.
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        View longUnderline;

        HeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            longUnderline = itemView.findViewById(R.id.longUnderline);
        }
    }

    /**
     * ViewHolder for user items.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTextView;
        TextView timeTextView;
        ImageView toggleIcon;

        UserViewHolder(View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            toggleIcon = itemView.findViewById(R.id.toggleIcon);
        }
    }
}
