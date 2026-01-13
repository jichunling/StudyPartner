package com.example.studybuddy.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studybuddy.R;

import java.util.List;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.ViewHolder> {
    private List<String> interestsList;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView interestTextView;

        public ViewHolder(View v){
            super(v);
            interestTextView = v.findViewById(R.id.interest_text);

            Log.println(Log.WARN, "interests in the adapter class ", String.valueOf(interestTextView));
        }
    }

    public InterestsAdapter(List<String> interests){
        this.interestsList = interests;
        Log.println(Log.WARN, "interests in the adapter class ", String.valueOf(interestsList));
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_interest, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull InterestsAdapter.ViewHolder holder, int position) {
        holder.interestTextView.setText(interestsList.get(position));

    }

    @Override
    public int getItemCount() {
        return interestsList.size();
    }
}
