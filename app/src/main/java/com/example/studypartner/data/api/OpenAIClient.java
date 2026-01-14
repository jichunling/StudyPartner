package com.example.studypartner.data.api;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OpenAIClient
 *
 * Client class for interacting with the OpenAI ChatGPT API.
 * Provides methods to send chat messages to the AI and receive responses asynchronously.
 *
 * Features:
 * - Asynchronous API requests using OkHttp
 * - JSON request/response handling
 * - Error handling and logging
 * - Callback-based response delivery
 *
 * API Configuration:
 * - Model: GPT-3.5 Turbo
 * - Endpoint: https://api.openai.com/v1/chat/completions
 *
 * Security Note:
 * The API key is currently hardcoded for testing. In production, store API keys
 * securely using:
 * - Android Keystore for sensitive data
 * - BuildConfig with secrets in gradle.properties (not checked into version control)
 * - Remote configuration service
 *
 */
public class OpenAIClient {

    private static final String TAG = "OpenAIClient";

    // API Configuration
    private static final String API_KEY = "this is a fake api key for test"; // TODO: Move to secure storage
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    // JSON Media Type
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    // HTTP Client
    private final OkHttpClient client;

    /**
     * Constructs a new OpenAIClient with default OkHttpClient configuration.
     */
    public OpenAIClient() {
        this.client = new OkHttpClient();
    }

    /**
     * Sends a chat message to the OpenAI API and receives a response asynchronously.
     *
     * The request is executed on a background thread and the callback methods
     * (onSuccess or onError) are invoked when the request completes.
     *
     * @param userMessage The message from the user to send to the AI
     * @param callback Callback interface to handle success or error responses
     * @throws JSONException if there's an error creating the request JSON
     */
    public void sendChatRequest(String userMessage, OpenAIResponse callback) throws JSONException {
        // Validate input
        if (userMessage == null || userMessage.trim().isEmpty()) {
            callback.onError("User message cannot be empty");
            return;
        }

        if (callback == null) {
            Log.e(TAG, "Callback is null");
            return;
        }

        // Build and send request
        Request request = buildChatRequest(userMessage);
        executeChatRequest(request, callback);
    }

    /**
     * Builds an HTTP request for the OpenAI chat completion API.
     *
     * Request format:
     * {
     *   "model": "gpt-3.5-turbo",
     *   "messages": [
     *     {"role": "user", "content": "user message here"}
     *   ]
     * }
     *
     * @param userMessage The user's message content
     * @return Configured Request object ready to execute
     * @throws JSONException if there's an error creating the JSON request body
     */
    private Request buildChatRequest(String userMessage) throws JSONException {
        // Create message object
        JSONObject messageObject = new JSONObject();
        messageObject.put("role", "user");
        messageObject.put("content", userMessage);

        // Create messages array
        JSONArray messagesArray = new JSONArray();
        messagesArray.put(messageObject);

        // Create request body
        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("model", MODEL);
        requestBodyJson.put("messages", messagesArray);

        // Create HTTP request
        RequestBody requestBody = RequestBody.create(
                requestBodyJson.toString(),
                JSON_MEDIA_TYPE
        );

        return new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();
    }

    /**
     * Executes the chat request asynchronously and handles the response.
     *
     * @param request The HTTP request to execute
     * @param callback Callback to notify of success or failure
     */
    private void executeChatRequest(Request request, OpenAIResponse callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Request failed", e);
                callback.onError("Network request failed: " + e.getMessage());
            }
        });
    }

    /**
     * Handles the HTTP response from the OpenAI API.
     *
     * Response format:
     * {
     *   "choices": [
     *     {
     *       "message": {
     *         "content": "AI response here"
     *       }
     *     }
     *   ]
     * }
     *
     * @param response The HTTP response from the API
     * @param callback Callback to notify of success or failure
     */
    private void handleResponse(Response response, OpenAIResponse callback) {
        try {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                Log.d(TAG, "Response received: " + responseBody);

                String content = extractContentFromResponse(responseBody);
                callback.onSuccess(content);
            } else {
                String errorMsg = "Request failed with status code: " + response.code();
                Log.e(TAG, errorMsg);
                callback.onError(errorMsg);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading response", e);
            callback.onError("Failed to read response: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing response JSON", e);
            callback.onError("Failed to parse response: " + e.getMessage());
        }
    }

    /**
     * Extracts the AI-generated content from the API response JSON.
     *
     * @param responseBody The raw JSON response body
     * @return The AI-generated content string
     * @throws JSONException if the response format is invalid
     */
    private String extractContentFromResponse(String responseBody) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseBody);

        return jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}
