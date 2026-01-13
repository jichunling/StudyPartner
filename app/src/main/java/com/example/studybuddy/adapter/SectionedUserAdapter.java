package com.example.studybuddy.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studybuddy.R;
import com.example.studybuddy.activities.ShowOtherUserProfileActivity;
import com.example.studybuddy.data.database.ConnectionsDB;
import com.example.studybuddy.data.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionedUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Map<String, List<User>> sectionedData;
    private List<Object> displayList;
    private Map<String, Boolean> sectionVisibilityMap;
    private Map<String, Boolean> iconStateMap;

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_USER = 1;

    public SectionedUserAdapter(Map<String, List<User>> sectionedData) {
        this.sectionedData = sectionedData;
        this.displayList = new ArrayList<>();
        this.sectionVisibilityMap = new HashMap<>();
        this.iconStateMap = new HashMap<>();
        buildDisplayList();
    }

    private void buildDisplayList() {
        displayList.clear();
        for (Map.Entry<String, List<User>> entry : sectionedData.entrySet()) {
            String section = entry.getKey();
            List<User> users = entry.getValue();

            displayList.add(section);

            if (sectionVisibilityMap.getOrDefault(section, true)) {
                displayList.addAll(users);
            }
        }
        Log.d("SectionedUserAdapter", "Display List: " + displayList.toString());
        for (int i = 0; i < displayList.size(); i++) {
            Log.d("SectionedUserAdapter", "displayList[" + i + "] = " + displayList.get(i));
        }
    }

    public void updateData(Map<String, List<User>> newSectionedData) {
        this.sectionedData = newSectionedData;
        buildDisplayList();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        Log.d("SectionedUserAdapter", "Item count: " + displayList.size());
        return displayList.size();
//        return sectionedData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (displayList.get(position) instanceof String) {
            Log.d("SectionedUserAdapter", "View type for position " + position + ": HEADER");

            return VIEW_TYPE_HEADER;
        } else {
            Log.d("SectionedUserAdapter", "View type for position " + position + ": USER");

            return VIEW_TYPE_USER;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("SectionedUserAdapter", "onBindViewHolder called for position: " + position);
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            String sectionTitle = (String) displayList.get(position);
            Log.d("SectionedUserAdapter", "Binding header: " + sectionTitle);

            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.sectionTitle.setText(sectionTitle);

            View longUnderline = headerHolder.longUnderline;

            boolean isVisible = sectionVisibilityMap.getOrDefault(sectionTitle, true);
            if (isVisible) {
                headerHolder.sectionTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_down, 0);
                longUnderline.setVisibility(View.GONE);
            } else {
                headerHolder.sectionTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_up, 0);
                longUnderline.setVisibility(View.VISIBLE);
            }

            headerHolder.sectionTitle.setOnClickListener(v -> {
                boolean currentVisibility = sectionVisibilityMap.getOrDefault(sectionTitle, true);
                sectionVisibilityMap.put(sectionTitle, !currentVisibility);

                if (currentVisibility) {
                    headerHolder.sectionTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_up, 0);
                    longUnderline.setVisibility(View.VISIBLE);
                } else {
                    headerHolder.sectionTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_down, 0);
                    longUnderline.setVisibility(View.GONE);
                }

                buildDisplayList();
                notifyDataSetChanged();
            });
        } else if (getItemViewType(position) == VIEW_TYPE_USER){
            User user = (User) displayList.get(position);
            Log.d("SectionedUserAdapter", "Binding user: " + user);

            Log.println(Log.WARN, "user data recycler", user.getEmail());
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.firstNameTextView.setText(user.getFirstName()+" "+user.getLastName());
            //userHolder.lastNameTextView.setText(user.getLastName());
            userHolder.timeTextView.setText(user.getFormattedStudyTime());

            ImageView userToggleIcon = userHolder.toggleIcon;

            //Enabling the user to click the first name and visit that user's profile
            userHolder.firstNameTextView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ShowOtherUserProfileActivity.class);
                intent.putExtra("email", user.getEmail());
                intent.putExtra("firstName", user.getFirstName());
                intent.putExtra("lastName", user.getLastName());
                intent.putExtra("topics", user.getTopicInterested());
                v.getContext().startActivity(intent);
            });

            if (iconStateMap.getOrDefault(user.getEmail(), false)) {
                userToggleIcon.setImageResource(R.drawable.thumb_up_fill);

                String currentUserEmail = user.getEmail();

                ConnectionsDB connectionsDB = new ConnectionsDB(userHolder.itemView.getContext());

                boolean isConnected = connectionsDB.insertConnectionRequest(currentUserEmail, user.getEmail());

                if (isConnected) {
                    Log.d("SectionedUserAdapter", "Connection request sent between " + currentUserEmail + " and " + user.getEmail());
                }
            } else {
                userToggleIcon.setImageResource(R.drawable.thumb_up_blank);
            }

            userToggleIcon.setOnClickListener(v -> {
                boolean currentState = iconStateMap.getOrDefault(user.getEmail(), false);
                iconStateMap.put(user.getEmail(), !currentState);

                if (iconStateMap.get(user.getEmail())) {
                    userToggleIcon.setImageResource(R.drawable.thumb_up_fill);

                    String currentUserEmail = user.getEmail();
                    // Use itemView.getContext() to get the context
                    ConnectionsDB connectionsDB = new ConnectionsDB(v.getContext());

                    boolean isConnected = connectionsDB.insertConnectionRequest(currentUserEmail, user.getEmail());

                    if (isConnected) {
                        Log.d("SectionedUserAdapter", "Connection request sent between " + currentUserEmail + " and " + user.getEmail());
                    }
                } else {
                    userToggleIcon.setImageResource(R.drawable.thumb_up_blank);
                }
            });
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_user_user_adaptor, parent, false);
                    .inflate(R.layout.item_user, parent, false);

            return new UserViewHolder(view);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        View longUnderline;

        HeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            longUnderline = itemView.findViewById(R.id.longUnderline);
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTextView, timeTextView;
        ImageView toggleIcon;

        UserViewHolder(View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            toggleIcon = itemView.findViewById(R.id.toggleIcon);
        }
    }
}
