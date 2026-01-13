package com.example.studybuddy;

public interface OpenAIResponse {
    void onSuccess(String content);
    void onError(String errorMessage);
}

