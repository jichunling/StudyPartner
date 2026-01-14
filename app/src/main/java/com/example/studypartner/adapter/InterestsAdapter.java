package com.example.studypartner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studypartner.R;

import java.util.List;

/**
 * InterestsAdapter
 *
 * RecyclerView adapter for displaying a list of study topic interests.
 * Used to show topics/subjects that users are interested in studying.
 *
 */
public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.ViewHolder> {

    private final List<String> interestsList;

    /**
     * Creates a new InterestsAdapter with the specified list of interests.
     *
     * @param interests List of topic interests to display
     */
    public InterestsAdapter(List<String> interests) {
        this.interestsList = interests;
    }

    /**
     * Creates a new ViewHolder for displaying an interest item.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_interest, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds interest data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position in the interests list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String interest = interestsList.get(position);
        holder.interestTextView.setText(interest);
    }

    /**
     * Returns the total number of interests in the list.
     *
     * @return Number of interests to display
     */
    @Override
    public int getItemCount() {
        return interestsList != null ? interestsList.size() : 0;
    }

    /**
     * ViewHolder class for interest list items.
     * Holds reference to the TextView for each interest.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView interestTextView;

        /**
         * Creates a new ViewHolder and binds view references.
         *
         * @param itemView The view for this interest item
         */
        public ViewHolder(View itemView) {
            super(itemView);
            interestTextView = itemView.findViewById(R.id.interest_text);
        }
    }
}
