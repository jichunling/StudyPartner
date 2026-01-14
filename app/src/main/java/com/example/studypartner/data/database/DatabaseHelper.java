package com.example.studypartner.data.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.studypartner.data.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DatabaseHelper Class
 *
 * Manages all database operations for the studyPartner application.
 * This class handles SQLite database creation, upgrades, and CRUD operations
 * for user profiles, preferences, and study partner matching.
 *
 * Database Schema:
 * - Table: users
 * - Stores user credentials, personal info, study preferences, and social media links
 *
 * @version 6.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database constants
    private static final String DATABASE_NAME = "User.db";
    private static final int DATABASE_VERSION = 6;

    // Table and column names
    private static final String TABLE_NAME = "users";
    private static final String COL_ID = "ID";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_PASSWORD = "PASSWORD";
    private static final String COL_FIRST_NAME = "FIRST_NAME";
    private static final String COL_LAST_NAME = "LAST_NAME";
    private static final String COL_AGE = "AGE";
    private static final String COL_GENDER = "GENDER";
    private static final String COL_PREFERRED_STUDY_TIME = "PREFERRED_STUDY_TIME";
    private static final String COL_TOPICS_INTERESTED = "TOPICS_INTERESTED";
    private static final String COL_STUDY_DIFFICULTY_LEVEL = "STUDY_DIFFICULTY_LEVEL";
    private static final String COL_ALREADY_SIGN_UP = "ALREADY_SIGN_UP";
    private static final String COL_OCCUPATION = "OCCUPATION";
    private static final String COL_LINKED_IN_URL = "LINKED_IN_URL";
    private static final String COL_GITHUB_URL = "GITHUB_URL";
    private static final String COL_PERSONAL_WEBSITE_URL = "PERSONAL_WEBSITE_URL";

    // Logging tag
    private static final String TAG = "DatabaseHelper";

    /**
     * Creates a new DatabaseHelper instance.
     *
     * @param context Application context for database operations
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * Creates the users table with all necessary columns.
     *
     * @param db The database instance
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT, " +
                COL_FIRST_NAME + " TEXT, " +
                COL_LAST_NAME + " TEXT, " +
                COL_AGE + " INTEGER, " +
                COL_GENDER + " TEXT, " +
                COL_PREFERRED_STUDY_TIME + " TEXT, " +
                COL_TOPICS_INTERESTED + " TEXT, " +
                COL_STUDY_DIFFICULTY_LEVEL + " TEXT, " +
                COL_ALREADY_SIGN_UP + " INTEGER DEFAULT 0, " +
                COL_OCCUPATION + " TEXT DEFAULT '', " +
                COL_LINKED_IN_URL + " TEXT DEFAULT '', " +
                COL_GITHUB_URL + " TEXT DEFAULT '', " +
                COL_PERSONAL_WEBSITE_URL + " TEXT DEFAULT '')";

        db.execSQL(createTableQuery);
    }

    /**
     * Called when the database needs to be upgraded.
     * Handles migration from older database versions by adding new columns.
     *
     * @param db The database instance
     * @param oldVersion The old database version
     * @param newVersion The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add ALREADY_SIGN_UP column for version 2+
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_ALREADY_SIGN_UP + " INTEGER DEFAULT 0");
        }

        // Add social media columns for version 6+
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_LINKED_IN_URL + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_GITHUB_URL + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PERSONAL_WEBSITE_URL + " TEXT DEFAULT ''");
        }
    }

    // ==================== User Authentication ====================

    /**
     * Inserts a new user into the database during signup.
     * Checks for existing users with the same email to prevent duplicates.
     *
     * @param email User's email address
     * @param password User's password (should be hashed in production)
     * @return true if user was successfully inserted, false if email already exists
     */
    public boolean insertUser(String email, String password) {
        if (checkIfUserAlreadyPresent(email)) {
            Log.w(TAG, "User already exists with email: " + email);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        return result != -1;
    }

    /**
     * Validates user credentials during login.
     *
     * @param email User's email address
     * @param password User's password
     * @return true if credentials are valid, false otherwise
     */
    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isValid;
    }

    /**
     * Checks if a user with the given email already exists in the database.
     *
     * @param email Email address to check
     * @return true if user exists, false otherwise
     */
    public boolean checkIfUserAlreadyPresent(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean isPresent = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isPresent;
    }

    // ==================== User Profile Management ====================

    /**
     * Updates user profile with personal information.
     *
     * @param email User's email (identifier)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age
     * @param gender User's gender
     * @param occupation User's occupation or major
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserProfile(String email, String firstName, String lastName, int age, String gender, String occupation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIRST_NAME, firstName);
        values.put(COL_LAST_NAME, lastName);
        values.put(COL_AGE, age);
        values.put(COL_GENDER, gender);
        values.put(COL_OCCUPATION, occupation);

        int rowsUpdated = db.update(TABLE_NAME, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsUpdated > 0;
    }

    /**
     * Updates user profile with study preferences.
     *
     * @param email User's email (identifier)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param age User's age
     * @param gender User's gender
     * @param topics Comma-separated topics of interest
     * @param studyTime Comma-separated preferred study times
     * @param difficultyLevel Preferred difficulty level
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserProfile(String email, String firstName, String lastName, int age, String gender,
                                     String topics, String studyTime, String difficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIRST_NAME, firstName);
        values.put(COL_LAST_NAME, lastName);
        values.put(COL_AGE, age);
        values.put(COL_GENDER, gender);
        values.put(COL_PREFERRED_STUDY_TIME, studyTime);
        values.put(COL_TOPICS_INTERESTED, topics);
        values.put(COL_STUDY_DIFFICULTY_LEVEL, difficultyLevel);

        int rowsUpdated = db.update(TABLE_NAME, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsUpdated > 0;
    }

    /**
     * Updates user's preferred study topics.
     *
     * @param email User's email
     * @param topics Comma-separated list of topics
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserTopic(String email, String topics) {
        return updateSingleField(email, COL_TOPICS_INTERESTED, topics);
    }

    /**
     * Updates user's preferred study time.
     *
     * @param email User's email
     * @param studyTime Comma-separated list of study times
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserStudyTime(String email, String studyTime) {
        return updateSingleField(email, COL_PREFERRED_STUDY_TIME, studyTime);
    }

    /**
     * Updates user's study difficulty level.
     *
     * @param email User's email
     * @param difficultyLevel New difficulty level (Beginner/Intermediate/Advanced)
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserStudyDifficultyLevel(String email, String difficultyLevel) {
        return updateSingleField(email, COL_STUDY_DIFFICULTY_LEVEL, difficultyLevel);
    }

    /**
     * Saves user's social media links.
     *
     * @param email User's email
     * @param linkedIn LinkedIn profile URL
     * @param github GitHub profile URL
     * @param personal Personal website URL
     * @return true if save was successful, false otherwise
     */
    public boolean saveSocials(String email, String linkedIn, String github, String personal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LINKED_IN_URL, linkedIn);
        values.put(COL_GITHUB_URL, github);
        values.put(COL_PERSONAL_WEBSITE_URL, personal);

        int rowsUpdated = db.update(TABLE_NAME, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsUpdated > 0;
    }

    /**
     * Marks user profile setup as complete.
     *
     * @param email User's email
     * @return true if update was successful, false otherwise
     */
    public boolean setUserSetupComplete(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ALREADY_SIGN_UP, 1);

        int rowsUpdated = db.update(TABLE_NAME, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsUpdated > 0;
    }

    /**
     * Checks if user has completed the initial profile setup.
     *
     * @param email User's email
     * @return true if setup is complete, false otherwise
     */
    public boolean isSetUp(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_ALREADY_SIGN_UP + " FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean isSetUp = false;
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            int setUp = cursor.getInt(cursor.getColumnIndex(COL_ALREADY_SIGN_UP));
            isSetUp = (setUp == 1);
            cursor.close();
        }
        db.close();

        return isSetUp;
    }

    // ==================== Password Management ====================

    /**
     * Retrieves user's password by email.
     * Note: This is for password recovery. Passwords should be hashed in production.
     *
     * @param email User's email
     * @return User's password, or empty string if not found
     */
    @SuppressLint("Range")
    public String getPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PASSWORD + " FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String password = "";
        if (cursor != null && cursor.moveToFirst()) {
            password = cursor.isNull(cursor.getColumnIndex(COL_PASSWORD)) ? "" : cursor.getString(cursor.getColumnIndex(COL_PASSWORD));
            cursor.close();
        }
        db.close();

        return password;
    }

    /**
     * Updates user's password.
     *
     * @param email User's email
     * @param newPassword New password (should be hashed in production)
     * @return true if update was successful, false otherwise
     */
    public boolean updatePassword(String email, String newPassword) {
        return updateSingleField(email, COL_PASSWORD, newPassword);
    }

    // ==================== User Retrieval Methods ====================

    /**
     * Retrieves complete user information for profile display.
     *
     * @param email User's email
     * @return User object with all information, or null if not found
     */
    @SuppressLint("Range")
    public User getUserDetailsForMyProfilePage(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor, true); // Include social links
            cursor.close();
        }
        db.close();

        return user;
    }

    /**
     * Retrieves user information by email.
     *
     * @param email User's email
     * @return User object, or null if not found
     */
    @SuppressLint("Range")
    public User getUserInfoByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor, false); // Exclude social links
            cursor.close();
        }
        db.close();

        return user;
    }

    /**
     * Gets user ID by email address.
     *
     * @param email User's email
     * @return User ID as string, or empty string if not found
     */
    @SuppressLint("Range")
    public String getUserIDByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_ID + " FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String userID = "";
        if (cursor != null && cursor.moveToFirst()) {
            userID = cursor.getString(cursor.getColumnIndex(COL_ID));
            cursor.close();
        }
        db.close();

        return userID;
    }

    /**
     * Gets user's study topics as a comma-separated string.
     *
     * @param email User's email
     * @return Comma-separated topics, or empty string if not found
     */
    @SuppressLint("Range")
    public String getUserTopicString(String email) {
        return getSingleField(email, COL_TOPICS_INTERESTED);
    }

    /**
     * Gets user's study time preferences as a comma-separated string.
     *
     * @param email User's email
     * @return Comma-separated study times, or empty string if not found
     */
    @SuppressLint("Range")
    public String getUserStudyTimeString(String email) {
        return getSingleField(email, COL_PREFERRED_STUDY_TIME);
    }

    /**
     * Gets user's topics as an ArrayList.
     *
     * @param email User's email
     * @return ArrayList of topics
     */
    public ArrayList<String> getUserTopicsByEmail(String email) {
        String topicsString = getUserTopicString(email);
        return parseCommaSeparatedString(topicsString);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return ArrayList of all User objects
     */
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = extractUserFromCursor(cursor, false);
                users.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return users;
    }

    // ==================== Study Partner Matching ====================

    /**
     * Finds potential study partners with matching topic interests.
     * Excludes the current user from results.
     *
     * @param userTopics List of topics the current user is interested in
     * @param currentUserEmail Current user's email to exclude from results
     * @return ArrayList of matching users
     */
    public ArrayList<User> getUsersWithSameTopics(List<String> userTopics, String currentUserEmail) {
        ArrayList<User> matchingUsers = new ArrayList<>();

        if (userTopics == null || userTopics.isEmpty()) {
            Log.w(TAG, "No topics provided for matching");
            return matchingUsers;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        // Build dynamic query for topic matching
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE ");
        for (int i = 0; i < userTopics.size(); i++) {
            queryBuilder.append(COL_TOPICS_INTERESTED).append(" LIKE ?");
            if (i < userTopics.size() - 1) {
                queryBuilder.append(" OR ");
            }
        }
        queryBuilder.append(" AND ").append(COL_EMAIL).append(" != ?");

        // Prepare query arguments
        ArrayList<String> args = new ArrayList<>();
        for (String topic : userTopics) {
            args.add("%" + topic.trim() + "%");
        }
        args.add(currentUserEmail);

        Cursor cursor = db.rawQuery(queryBuilder.toString(), args.toArray(new String[0]));

        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = extractUserFromCursor(cursor, false);
                matchingUsers.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        Log.d(TAG, "Found " + matchingUsers.size() + " matching users");
        return matchingUsers;
    }

    // ==================== Helper Methods ====================

    /**
     * Helper method to update a single field for a user.
     *
     * @param email User's email
     * @param columnName Column to update
     * @param value New value
     * @return true if update was successful, false otherwise
     */
    private boolean updateSingleField(String email, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnName, value);

        int rowsUpdated = db.update(TABLE_NAME, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsUpdated > 0;
    }

    /**
     * Helper method to get a single field value for a user.
     *
     * @param email User's email
     * @param columnName Column to retrieve
     * @return Field value, or empty string if not found
     */
    @SuppressLint("Range")
    private String getSingleField(String email, String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + columnName + " FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String value = "";
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.isNull(cursor.getColumnIndex(columnName)) ? "" : cursor.getString(cursor.getColumnIndex(columnName));
            cursor.close();
        }
        db.close();

        return value;
    }

    /**
     * Extracts a User object from a database cursor.
     * Reduces code duplication by centralizing User object creation.
     *
     * @param cursor Database cursor positioned at a user row
     * @param includeSocialLinks Whether to include social media links
     * @return User object with data from cursor
     */
    @SuppressLint("Range")
    private User extractUserFromCursor(Cursor cursor, boolean includeSocialLinks) {
        String email = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
        String password = cursor.getString(cursor.getColumnIndex(COL_PASSWORD));
        String firstName = cursor.getString(cursor.getColumnIndex(COL_FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndex(COL_LAST_NAME));
        int age = cursor.getInt(cursor.getColumnIndex(COL_AGE));
        String gender = cursor.getString(cursor.getColumnIndex(COL_GENDER));
        String studyTimeString = cursor.getString(cursor.getColumnIndex(COL_PREFERRED_STUDY_TIME));
        String topicsString = cursor.getString(cursor.getColumnIndex(COL_TOPICS_INTERESTED));
        String difficulty = cursor.isNull(cursor.getColumnIndex(COL_STUDY_DIFFICULTY_LEVEL)) ?
                "" : cursor.getString(cursor.getColumnIndex(COL_STUDY_DIFFICULTY_LEVEL));
        String occupation = cursor.isNull(cursor.getColumnIndex(COL_OCCUPATION)) ?
                "" : cursor.getString(cursor.getColumnIndex(COL_OCCUPATION));

        // Parse comma-separated strings into ArrayLists
        ArrayList<String> studyTimeList = parseCommaSeparatedString(studyTimeString);
        ArrayList<String> topicsList = parseCommaSeparatedString(topicsString);

        if (includeSocialLinks) {
            String linkedIn = cursor.isNull(cursor.getColumnIndex(COL_LINKED_IN_URL)) ?
                    "" : cursor.getString(cursor.getColumnIndex(COL_LINKED_IN_URL));
            String github = cursor.isNull(cursor.getColumnIndex(COL_GITHUB_URL)) ?
                    "" : cursor.getString(cursor.getColumnIndex(COL_GITHUB_URL));
            String personal = cursor.isNull(cursor.getColumnIndex(COL_PERSONAL_WEBSITE_URL)) ?
                    "" : cursor.getString(cursor.getColumnIndex(COL_PERSONAL_WEBSITE_URL));

            return new User(email, password, firstName, lastName, age, gender,
                    studyTimeList, topicsList, difficulty, occupation, linkedIn, github, personal);
        } else {
            return new User(email, password, firstName, lastName, age, gender,
                    studyTimeList, topicsList, difficulty, occupation);
        }
    }

    /**
     * Parses a comma-separated string into an ArrayList.
     *
     * @param input Comma-separated string
     * @return ArrayList of trimmed strings, or empty list if input is null/empty
     */
    private ArrayList<String> parseCommaSeparatedString(String input) {
        ArrayList<String> result = new ArrayList<>();

        if (input != null && !input.trim().isEmpty()) {
            String[] items = input.split(",");
            for (String item : items) {
                result.add(item.trim());
            }
        }

        return result;
    }
}
