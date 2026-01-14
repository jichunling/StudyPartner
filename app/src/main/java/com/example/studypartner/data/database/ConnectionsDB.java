package com.example.studypartner.data.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.studypartner.data.model.Connections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * ConnectionsDB
 *
 * SQLite database helper class for managing study partner connection requests.
 * Handles the storage and retrieval of connection requests between users, tracking
 * the sender, receiver, and status of each connection.
 *
 * Database Structure:
 * - ID: Auto-incrementing primary key
 * - SENDER_EMAIL: Email of the user who sent the connection request
 * - RECEIVER_EMAIL: Email of the user who received the connection request
 * - STATUS: Current status of the connection (Sent, Accepted, Rejected)
 *
 * Features:
 * - Create and insert connection requests
 * - Retrieve pending connection requests for a user
 * - Prevents duplicate connections between same users
 * - Version 3 database schema
 *
 */
public class ConnectionsDB extends SQLiteOpenHelper {

    private static final String TAG = "ConnectionsDB";

    // Database configuration
    private static final String DATABASE_NAME = "Connections.db";
    private static final int DATABASE_VERSION = 3;

    // Table and column names
    public static final String TABLE_NAME = "Connections";
    public static final String COL_ID = "ID";
    public static final String COL_SENDER_EMAIL = "SENDER_EMAIL";
    public static final String COL_RECEIVER_EMAIL = "RECEIVER_EMAIL";
    public static final String COL_STATUS = "STATUS";

    // Connection status values
    public static final String STATUS_SENT = "Sent";
    public static final String STATUS_ACCEPTED = "Accepted";
    public static final String STATUS_REJECTED = "Rejected";

    /**
     * Constructs a new ConnectionsDB helper.
     *
     * @param context Application context
     */
    public ConnectionsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the connections table when the database is created for the first time.
     *
     * @param db The database to create tables in
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SENDER_EMAIL + " TEXT, " +
                COL_RECEIVER_EMAIL + " TEXT, " +
                COL_STATUS + " TEXT)";
        db.execSQL(createTableQuery);
        Log.d(TAG, "Connections table created successfully");
    }

    /**
     * Handles database upgrades by dropping the old table and creating a new one.
     * Note: This will delete all existing connection data.
     *
     * @param db The database to upgrade
     * @param oldVersion The old database version
     * @param newVersion The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Inserts a new connection request into the database.
     *
     * Creates a connection request from the sender to the receiver with status "Sent".
     * The connection is immediately marked as sent and awaits receiver action.
     *
     * @param senderEmail Email of the user sending the connection request
     * @param receiverEmail Email of the user receiving the connection request
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertConnectionRequest(String senderEmail, String receiverEmail) {
        // Validate input
        if (!isValidEmail(senderEmail) || !isValidEmail(receiverEmail)) {
            Log.e(TAG, "Invalid email addresses provided");
            return false;
        }

        // Prevent self-connections
        if (senderEmail.equals(receiverEmail)) {
            Log.e(TAG, "Cannot create connection request to self");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        // Create connection request with "Sent" status
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SENDER_EMAIL, senderEmail);
        contentValues.put(COL_RECEIVER_EMAIL, receiverEmail);
        contentValues.put(COL_STATUS, STATUS_SENT);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        if (result != -1) {
            Log.d(TAG, "Connection request inserted: " + senderEmail + " -> " + receiverEmail);
            return true;
        } else {
            Log.e(TAG, "Failed to insert connection request");
            return false;
        }
    }

    /**
     * Retrieves all connection requests received by a specific user.
     *
     * Returns a list of connection requests where the specified user is the receiver.
     * Automatically filters out:
     * - Self-connections (sender equals receiver)
     * - Duplicate requests from the same sender
     *
     * @param receiverEmail Email of the user whose connection requests to retrieve
     * @return ArrayList of Connections objects representing pending requests, or empty list if none found
     */
    public ArrayList<Connections> getConnectionRequests(String receiverEmail) {
        // Validate input
        if (receiverEmail == null || receiverEmail.trim().isEmpty()) {
            Log.e(TAG, "Receiver email is null or empty. Returning empty list.");
            return new ArrayList<>();
        }

        ArrayList<Connections> connections = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query for connection requests where user is receiver
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_RECEIVER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{receiverEmail});

        // Use set to track unique senders and prevent duplicates
        Set<String> senderSet = new HashSet<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Connections connection = extractConnectionFromCursor(cursor, receiverEmail);

                // Only add if not self-connection and sender is unique
                if (connection != null &&
                    !connection.getSenderEmail().equals(receiverEmail) &&
                    !senderSet.contains(connection.getSenderEmail())) {
                    connections.add(connection);
                    senderSet.add(connection.getSenderEmail());
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        Log.d(TAG, "Retrieved " + connections.size() + " connection requests for " + receiverEmail);
        return connections;
    }

    /**
     * Extracts a Connections object from the current cursor position.
     *
     * @param cursor Cursor positioned at a connection record
     * @param receiverEmail Email of the receiver for this connection
     * @return Connections object, or null if extraction fails
     */
    @SuppressLint("Range")
    private Connections extractConnectionFromCursor(Cursor cursor, String receiverEmail) {
        try {
            String connectionId = cursor.getString(cursor.getColumnIndex(COL_ID));
            String senderEmail = cursor.getString(cursor.getColumnIndex(COL_SENDER_EMAIL));
            String status = cursor.getString(cursor.getColumnIndex(COL_STATUS));

            return new Connections(connectionId, senderEmail, receiverEmail, status);
        } catch (Exception e) {
            Log.e(TAG, "Error extracting connection from cursor", e);
            return null;
        }
    }

    /**
     * Validates that an email address is not null or empty.
     *
     * @param email Email address to validate
     * @return true if email is valid (not null or empty), false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty();
    }
}
