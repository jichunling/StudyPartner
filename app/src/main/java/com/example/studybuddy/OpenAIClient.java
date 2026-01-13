package com.example.studybuddy;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OpenAIClient {
    private static final String API_KEY="this is a fake api key for test";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    //    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client;

    public OpenAIClient() {
        client = new OkHttpClient();
    }

    public void sendChatRequest(String userMessage, OpenAIResponse status) throws Exception {
        //to create request body
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", userMessage);


        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new org.json.JSONArray().put(message));

        //to create request
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        //
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    System.out.println(responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String content = jsonResponse
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        status.onSuccess(content);
                    } catch (Exception e) {
                        Log.e("OpenAIClient", "Request Failed", e);
                        status.onError("Failed to parse response " + e.getMessage());
                    }
                } else {
                    status.onError("Request failed：" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OpenAIClient", "Request Failed", e);
                status.onError("Request failed：" + e.getMessage());

            }
        });
    }
}
