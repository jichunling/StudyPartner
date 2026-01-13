package com.example.studybuddy.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studybuddy.Message;
import com.example.studybuddy.MessageAdapter;
import com.example.studybuddy.OpenAIClient;
import com.example.studybuddy.OpenAIResponse;
import com.example.studybuddy.R;

import java.util.ArrayList;
import java.util.List;

public class GenAiFragment extends Fragment {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;

    MessageAdapter messageAdapter;
    private OpenAIClient openAIClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_genai, container, false);

        messageList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
        welcomeTextView = view.findViewById(R.id.welcome_text);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);


        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

//        userInput = findViewById(R.i.userInput);
//        Button sendButton = findViewById(R.id.sendButton);
//        responseTextView = findViewById(R.id.responseTextView);

        openAIClient = new OpenAIClient();

        sendButton.setOnClickListener(v -> {
            String inputText = messageEditText.getText().toString().trim();
            addMessage(inputText, Message.SENT_BY_USER);
            messageEditText.setText("");
            welcomeTextView.setVisibility(View.GONE);

            if (!inputText.isEmpty()) {
                try {
                    openAIClient.sendChatRequest(inputText, new OpenAIResponse() {
                        @Override
                        public void onSuccess(String content) {
//                            runOnUiThread(() -> responseTextView.setText(content));
                            addMessage(content, Message.SENT_BY_BOT);
                        }

                        @Override
                        public void onError(String errorMessage) {
//                            runOnUiThread(() -> responseTextView.setText(errorMessage));
                            addMessage(errorMessage, Message.SENT_BY_BOT);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return view;
    }

    void addMessage(String message, String sentBy) {
        requireActivity().runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

        });
    }
}
