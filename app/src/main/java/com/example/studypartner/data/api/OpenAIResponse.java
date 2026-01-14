package com.example.studypartner.data.api;

/**
 * OpenAIResponse
 *
 * Callback interface for handling OpenAI API responses asynchronously.
 * This interface defines success and error callbacks for AI chat requests.
 *
 * Used by OpenAIClient to notify the calling code when an API request
 * completes successfully or encounters an error.
 *
 * Example Usage:
 * <pre>
 * openAIClient.sendChatRequest(userMessage, new OpenAIResponse() {
 *     {@literal @}Override
 *     public void onSuccess(String content) {
 *         // Handle successful response with AI-generated content
 *     }
 *
 *     {@literal @}Override
 *     public void onError(String errorMessage) {
 *         // Handle error with error message
 *     }
 * });
 * </pre>
 *
 */
public interface OpenAIResponse {

    /**
     * Called when the OpenAI API request completes successfully.
     *
     * This method is invoked on a background thread, so any UI updates
     * must be posted to the main thread.
     *
     * @param content The AI-generated response content
     */
    void onSuccess(String content);

    /**
     * Called when the OpenAI API request fails.
     *
     * This method is invoked on a background thread, so any UI updates
     * must be posted to the main thread.
     *
     * @param errorMessage A description of the error that occurred
     */
    void onError(String errorMessage);
}
