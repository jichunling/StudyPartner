package com.example.studypartner.fragments;

import android.os.Bundle;
import android.util.Log;
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

import com.example.studypartner.data.model.Message;
import com.example.studypartner.adapter.MessageAdapter;
import com.example.studypartner.data.api.OpenAIClient;
import com.example.studypartner.data.api.OpenAIResponse;
import com.example.studypartner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * GenAiFragment
 *
 * Fragment providing AI-powered study assistant functionality using OpenAI's ChatGPT API.
 * Accessed via the bottom navigation bar in MainActivity.
 * Displays a chat interface where users can ask study-related questions and receive AI responses.
 *
 * Features:
 * - Chat interface with RecyclerView displaying message history
 * - Send text messages to OpenAI ChatGPT API
 * - Receive and display AI responses
 * - Automatic scrolling to latest messages
 * - Welcome text that hides after first message
 * - Real-time UI updates for incoming messages
 *
 * Note: API key is currently hardcoded in OpenAIClient (should be moved to secure storage)
 */
public class GenAiFragment extends Fragment {

    private static final String TAG = "GenAiFragment";

    // UI Components
    private RecyclerView recyclerView;
    private TextView welcomeTextView;
    private EditText messageEditText;
    private ImageButton sendButton;

    // Business Logic
    private MessageAdapter messageAdapter;
    private OpenAIClient openAIClient;

    // Data
    private List<Message> messageList;

    /**
     * Creates and initializes the GenAI fragment view.
     *
     * @param inflater LayoutInflater to inflate views
     * @param container Parent view container
     * @param savedInstanceState Saved state from previous instance
     * @return The fragment view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_genai, container, false);

        initializeComponents(view);
        setupRecyclerView();
        setupClickListeners();

        return view;
    }

    /**
     * Initializes all components including views and data structures.
     *
     * @param view The fragment's root view
     */
    private void initializeComponents(View view) {
        initializeViews(view);
        initializeMessageList();
        initializeOpenAIClient();
    }

    /**
     * Initializes all view references.
     *
     * @param view The fragment's root view
     */
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        welcomeTextView = view.findViewById(R.id.welcome_text);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);
    }

    /**
     * Initializes the message list.
     */
    private void initializeMessageList() {
        messageList = new ArrayList<>();
    }

    /**
     * Initializes the OpenAI client for API communication.
     */
    private void initializeOpenAIClient() {
        openAIClient = new OpenAIClient();
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * Sets up click listeners for interactive elements.
     */
    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> handleSendMessage());
    }

    /**
     * Handles the send message button click.
     * Sends user message to OpenAI API and displays response.
     */
    private void handleSendMessage() {
        String inputText = getInputText();

        if (inputText.isEmpty()) {
            return;
        }

        addUserMessage(inputText);
        clearInputField();
        hideWelcomeText();
        sendToOpenAI(inputText);
    }

    /**
     * Gets the input text from the message edit field.
     *
     * @return Trimmed input text
     */
    private String getInputText() {
        return messageEditText.getText().toString().trim();
    }

    /**
     * Adds a user message to the chat.
     *
     * @param message The user's message text
     */
    private void addUserMessage(String message) {
        addMessage(message, Message.SENT_BY_USER);
    }

    /**
     * Clears the input text field.
     */
    private void clearInputField() {
        messageEditText.setText("");
    }

    /**
     * Hides the welcome text after first message.
     */
    private void hideWelcomeText() {
        welcomeTextView.setVisibility(View.GONE);
    }

    /**
     * Sends a message to the OpenAI API and handles the response.
     *
     * @param message The message to send to the API
     */
    private void sendToOpenAI(String message) {
        try {
            openAIClient.sendChatRequest(message, new OpenAIResponse() {
                @Override
                public void onSuccess(String content) {
                    Log.d(TAG, "Received successful response from OpenAI");
                    addBotMessage(content);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Error from OpenAI: " + errorMessage);
                    addBotMessage(errorMessage);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception sending request to OpenAI", e);
            addBotMessage("Error: Failed to send message");
        }
    }

    /**
     * Adds a bot response message to the chat.
     *
     * @param message The bot's response text
     */
    private void addBotMessage(String message) {
        addMessage(message, Message.SENT_BY_BOT);
    }

    /**
     * Adds a message to the chat and updates the UI.
     * Runs on UI thread to ensure thread safety.
     *
     * @param message The message text
     * @param sentBy Who sent the message (user or bot)
     */
    private void addMessage(String message, String sentBy) {
        requireActivity().runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            scrollToLatestMessage();
        });
    }

    /**
     * Scrolls the RecyclerView to show the latest message.
     */
    private void scrollToLatestMessage() {
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
    }
}
