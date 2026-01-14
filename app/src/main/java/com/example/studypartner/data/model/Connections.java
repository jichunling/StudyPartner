package com.example.studypartner.data.model;

/**
 * Connections Model Class
 *
 * Represents a connection relationship between two study partners in the studyPartner application.
 * This class manages the connection status between users who want to study together.
 *
 * Connection Status Values:
 * - "pending" : Connection request sent but not yet accepted
 * - "accepted" : Both users are connected as study partners
 * - "rejected" : Connection request was declined
 *
 */
public class Connections {
    private String connectionID;
    private String senderEmail;
    private String receiverEmail;
    private String status;

    /**
     * Creates a new connection between two users.
     *
     * @param connectionID Unique identifier for this connection
     * @param senderEmail Email of the user who initiated the connection
     * @param receiverEmail Email of the user receiving the connection request
     * @param status Current status of the connection (pending/accepted/rejected)
     */
    public Connections(String connectionID, String senderEmail, String receiverEmail, String status) {
        this.connectionID = connectionID;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.status = status != null ? status : "pending";
    }

    // ==================== Getters ====================

    /**
     * Gets the unique connection identifier.
     *
     * @return Connection ID
     */
    public String getConnectionID() {
        return connectionID;
    }

    /**
     * Gets the email of the user who sent the connection request.
     *
     * @return Sender's email address
     */
    public String getSenderEmail() {
        return senderEmail;
    }

    /**
     * Gets the email of the user receiving the connection request.
     *
     * @return Receiver's email address
     */
    public String getReceiverEmail() {
        return receiverEmail;
    }

    /**
     * Gets the current status of the connection.
     *
     * @return Connection status (pending/accepted/rejected)
     */
    public String getStatus() {
        return status;
    }

    // ==================== Setters ====================

    /**
     * Sets the sender's email address.
     *
     * @param senderEmail New sender email
     */
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    /**
     * Sets the receiver's email address.
     *
     * @param receiverEmail New receiver email
     */
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    /**
     * Sets the connection status.
     *
     * @param status New connection status (pending/accepted/rejected)
     */
    public void setStatus(String status) {
        this.status = status != null ? status : "pending";
    }

    // ==================== Utility Methods ====================

    /**
     * Checks if the connection is currently pending acceptance.
     *
     * @return true if status is "pending", false otherwise
     */
    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }

    /**
     * Checks if the connection has been accepted by both parties.
     *
     * @return true if status is "accepted", false otherwise
     */
    public boolean isAccepted() {
        return "accepted".equalsIgnoreCase(status);
    }

    /**
     * Checks if the connection request was rejected.
     *
     * @return true if status is "rejected", false otherwise
     */
    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(status);
    }
}
