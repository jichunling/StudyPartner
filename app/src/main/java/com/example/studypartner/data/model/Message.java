package com.example.studypartner.data.model;

/**
 * Message
 *
 * Simple data model representing a chat message in the AI study assistant.
 * Each message has content and a sender identifier (user or bot).
 *
 * This class is used by the MessageAdapter to display chat conversations
 * between the user and the AI assistant in the GenAiFragment.
 *
 */
public class Message {

    /**
     * Constant identifier for messages sent by the user.
     */
    public static final String SENT_BY_USER = "user";

    /**
     * Constant identifier for messages sent by the AI bot.
     */
    public static final String SENT_BY_BOT = "bot";

    // Message content
    private String message;

    // Sender identifier (user or bot)
    private String sentBy;

    /**
     * Constructs a new Message.
     *
     * @param message The message content
     * @param sentBy The sender identifier (SENT_BY_USER or SENT_BY_BOT)
     */
    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    /**
     * Gets the message content.
     *
     * @return The message text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message content.
     *
     * @param message The message text to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the sender identifier.
     *
     * @return The sender identifier (SENT_BY_USER or SENT_BY_BOT)
     */
    public String getSentBy() {
        return sentBy;
    }

    /**
     * Sets the sender identifier.
     *
     * @param sentBy The sender identifier (SENT_BY_USER or SENT_BY_BOT)
     */
    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    /**
     * Checks if this message was sent by the user.
     *
     * @return true if sent by user, false if sent by bot
     */
    public boolean isSentByUser() {
        return SENT_BY_USER.equals(sentBy);
    }

    /**
     * Checks if this message was sent by the bot.
     *
     * @return true if sent by bot, false if sent by user
     */
    public boolean isSentByBot() {
        return SENT_BY_BOT.equals(sentBy);
    }
}
