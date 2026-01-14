package com.example.studypartner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studypartner.data.model.Message;
import com.example.studypartner.R;

import java.util.List;

/**
 * MessageAdapter
 *
 * RecyclerView adapter for displaying chat messages in the AI study assistant.
 * Differentiates between user messages (right-aligned) and AI responses (left-aligned).
 *
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private final List<Message> messageList;

    /**
     * Creates a new MessageAdapter with the specified list of messages.
     *
     * @param messageList List of messages to display
     */
    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    /**
     * Creates a new ViewHolder for displaying a message.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new MyViewHolder
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new MyViewHolder(chatView);
    }

    /**
     * Binds message data to the ViewHolder.
     * Displays user messages on the right and AI responses on the left.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position in the message list
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (Message.SENT_BY_USER.equals(message.getSentBy())) {
            // User message - show on right
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText(message.getMessage());
        } else {
            // AI response - show on left
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftTextView.setText(message.getMessage());
        }
    }

    /**
     * Returns the total number of messages in the list.
     *
     * @return Number of messages to display
     */
    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    /**
     * ViewHolder class for chat message items.
     * Holds references to both left and right chat views.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView;
        LinearLayout rightChatView;
        TextView leftTextView;
        TextView rightTextView;

        /**
         * Creates a new ViewHolder and binds view references.
         *
         * @param itemView The view for this message item
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
        }
    }
}
