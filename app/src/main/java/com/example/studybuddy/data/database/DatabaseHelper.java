package com.example.studybuddy.data.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.studybuddy.data.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Database creation and CRUD operations
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "User.db";
    public static final String TABLE_NAME = "users";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "EMAIL";
    public static final String COL_3 = "PASSWORD";
    public static final String COL_4 = "FIRST_NAME";
    public static final String COL_5 = "LAST_NAME";
    public static final String COL_6 = "AGE";
    public static final String COL_7 = "GENDER";
    public static final String COL_8 = "PREFERRED_STUDY_TIME";
    public static final String COL_9 = "TOPICS_INTERESTED";
    public static final String COL_10 = "STUDY_DIFFICULTY_LEVEL";
    public static final String COL_11 = "ALREADY_SIGN_UP";
    public static final String COL_12 = "OCCUPATION";
    private static final String COL_13 = "LINKED_IN_URL";
    private static final String COL_14 = "GITHUB_URL";
    private static final String COL_15 = "PERSONAL_WEBSITE_URL";

    //version 2: add column 11 "AlreadySignUp"
    //version 3: add column 12 "OCCUPATION"
    //version 4: add column 13 "IS_PASSWORD_RESET_REQUIRED"
    //version 6 : added linked in and github link
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EMAIL TEXT, PASSWORD TEXT, FIRST_NAME TEXT, LAST_NAME TEXT, AGE INTEGER," +
                "GENDER TEXT, PREFERRED_STUDY_TIME TEXT, TOPICS_INTERESTED TEXT, " +
                "STUDY_DIFFICULTY_LEVEL TEXT, ALREADY_SIGN_UP INTEGER DEFAULT 0, OCCUPATION TEXT DEFAULT \" \", " +
                "IS_PASSWORD_RESET_REQUIRED TEXT DEFAULT \"no\"," +
                "LINKED_IN_URL TEXT DEFAULT \"\"," +
                "GITHUB_URL TEXT DEFAULT \"\"," +
                "PERSONAL_WEBSITE_URL TEXT DEFAULT \"\")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN IS_PASSWORD_RESET_REQUIRED TEXT DEFAULT \"no\"");
        }

//        if (oldVersion < 5) {
//            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN OCCUPATION TEXT DEFAULT \"\"");
//        }

        if(oldVersion < 6){
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN LINKED_IN_URL TEXT DEFAULT \"\"");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN GITHUB_URL TEXT DEFAULT \"\"");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN PERSONAL_WEBSITE_URL TEXT DEFAULT \"\"");
        }

        if(oldVersion < 2){
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN ALREADY_SIGN_UP INTEGER DEFAULT 0");
        }
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
    }

    public boolean updateUserProfile(String email, String firstName, String lastName, int age, String gender, String occupation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4, firstName);
        contentValues.put(COL_5, lastName);
        contentValues.put(COL_6, age);
        contentValues.put(COL_7, gender);
        contentValues.put(COL_12, occupation);

        int rowsUpdated = db.update(TABLE_NAME, contentValues, COL_2 + " = ?", new String[]{email});
        db.close();
        return rowsUpdated > 0;
    }

    /**
     * This function returns User Password
     * @param email - email address of the user
     * @return - password of the user
     */
    @SuppressLint("Range")
    public String getPassword(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_3 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?";
        String[] args = {email};

        Cursor cursor = db.rawQuery(query, args);
        String password = "";

        if(cursor != null && cursor.moveToFirst()){
            password = cursor.isNull(cursor.getColumnIndex(COL_3)) ? "" : cursor.getString(cursor.getColumnIndex(COL_3));
            cursor.close();
        }
        db.close();
        return password;
    }

    /**
     * This function saves the user information in the database
     * @param email - email of the user
     * @param linkedIn - linked in link
     * @param github - github link
     * @param personal - personal website link
     * @return - true if we are able to successfully save the data in the database else false
     */
    public boolean saveSocials(String email, String linkedIn, String github, String personal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_13, linkedIn);
        contentValues.put(COL_14, github);
        contentValues.put(COL_15, personal);

        int rowUpdate = db.update(TABLE_NAME, contentValues, COL_2 + " = ? ", new String[]{email});
        db.close();
        return rowUpdate > 0;
    }


    /**
     * This function is used to update user password
     * @param email - email address of the user
     * @param newPassword - new password set by the user
     * @return - true if password reset is successful otherwise false
     */
    public boolean updatePassword(String email, String newPassword){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3, newPassword);
        int rowUpdate = db.update(TABLE_NAME, contentValues, COL_2 + " = ? ", new String[]{email});
        db.close();
        return rowUpdate > 0;
    }

    public boolean updateUserProfile(String email, String firstName, String lastName, int age, String gender, String topics, String time, String level) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4, firstName);
        contentValues.put(COL_5, lastName);
        contentValues.put(COL_6, age);
        contentValues.put(COL_7, gender);
        contentValues.put(COL_8, time);
        contentValues.put(COL_9, topics);
        contentValues.put(COL_10, level);
        int rowsUpdated = db.update(TABLE_NAME, contentValues, COL_2 + " = ?", new String[]{email});
        db.close();
        return rowsUpdated > 0;
    }

    // Method to update user's topic preference
    public boolean updateUserTopic(String email, String topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_9, topic);

        int result = db.update(TABLE_NAME, contentValues, "EMAIL = ?", new String[]{email});
        db.close();
        return result > 0;
    }

    /**
     * This function check if the user is already present in the system.
     * @param email - user email
     * @return - true if the user is already a member, otherwise false
     */
    public boolean checkIfUserAlreadyPresent(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?";
        String[] args = {email};

        Cursor cursor = db.rawQuery(query, args);
        boolean isUserPresent = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isUserPresent;
    }


    // Method to update user's study time preference
    public boolean updateUserStudyTime(String email, String studyTimePreference) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_8, studyTimePreference);

        int result = db.update(TABLE_NAME, contentValues, "EMAIL = ?", new String[]{email});
        db.close();
        return result > 0;
    }


    public boolean updateUserStudyDifficultyLevel(String email, String difficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_10, difficultyLevel);

        int result = db.update(TABLE_NAME, contentValues, "EMAIL = ?", new String[]{email});
        db.close();
        return result > 0;
    }


    /**
     * This function is used to add user to the database when they signup
     * @param email - email address of the user
     * @param password - password of the user
     * @return - true if the user is added successfully otherwise returns false.
     */
    public boolean insertUser(String email, String password) {
        boolean userPresent = this.checkIfUserAlreadyPresent(email); //This is used to check if there is a user already present with the existing email
        if(userPresent){
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, email);
        contentValues.put(COL_3, password);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    //Validate user when login
    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL = ? AND PASSWORD = ?", new String[]{email, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    // Update user profile details after login
    public boolean updateUserProfile(int userId, String firstName, String lastName, int age, String gender,
                                     String preferredStudyTime, String topicsInterested, String studyDifficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4, firstName);
        contentValues.put(COL_5, lastName);
        contentValues.put(COL_6, age);
        contentValues.put(COL_7, gender);
        contentValues.put(COL_8, preferredStudyTime);
        contentValues.put(COL_9, topicsInterested);
        contentValues.put(COL_10, studyDifficultyLevel);


        int rowsUpdated = db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsUpdated > 0;
    }

    public boolean set_setUp(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11, 1);
        int rowsUpdated = db.update(TABLE_NAME, contentValues, "EMAIL = ?", new String[]{String.valueOf(email)});
        db.close();
        return rowsUpdated > 0;
    }


    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_2));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COL_3));
                @SuppressLint("Range") String firstName = cursor.getString(cursor.getColumnIndex(COL_4));
                @SuppressLint("Range") String lastName = cursor.getString(cursor.getColumnIndex(COL_5));
                @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(COL_6));
                @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(COL_7));
                @SuppressLint("Range") String studyTime = cursor.getString(cursor.getColumnIndex(COL_8));
                @SuppressLint("Range") String topics = cursor.getString(cursor.getColumnIndex(COL_9));
                @SuppressLint("Range") String difficulty = cursor.getString(cursor.getColumnIndex(COL_10));
               // @SuppressLint("Range") String occupation = cursor.getString(cursor.getColumnIndex(COL_12));

                ArrayList<String> studyTimeList = new ArrayList<>();
                if (studyTime != null && !studyTime.isEmpty()) {
                    String[] studyTimeArray = studyTime.split(",");
                    for (String time : studyTimeArray) {
                        studyTimeList.add(time.trim());
                    }
                }

                ArrayList<String> topicsList = new ArrayList<>();
                if (topics != null && !topics.isEmpty()) {
                    String[] topicsArray = topics.split(",");
                    for (String topic : topicsArray) {
                        topicsList.add(topic.trim());
                    }
                }

                User user = new User(email, password, firstName, lastName, age, gender, studyTimeList, topicsList, difficulty);
                //User user = new User(email, password, firstName, lastName, age, gender, studyTimeList, topicsList, difficulty, occupation);
                users.add(user);

            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return users;
    }


    @SuppressLint("Range")
    public ArrayList<User> getUsersWithSameTopics(List<String> currentUserTopics) {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Log.println(Log.WARN, "query to find matched buddies checking size", String.valueOf(currentUserTopics.size()));

        if (currentUserTopics == null || currentUserTopics.isEmpty()) {
            return users;
        }

        // Build the WHERE clause for topics
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(TABLE_NAME).append(" WHERE ");
        for (int i = 0; i < currentUserTopics.size(); i++) {
            queryBuilder.append(COL_9).append(" LIKE ?");
            if (i < currentUserTopics.size() - 1) {
                queryBuilder.append(" OR ");
            }
        }

        Log.println(Log.WARN, "query to find matched buddies", queryBuilder.toString());

        // Prepare query arguments
        String[] args = new String[currentUserTopics.size()];
        for (int i = 0; i < currentUserTopics.size(); i++) {
            args[i] = "%" + currentUserTopics.get(i).trim() + "%";
        }

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(queryBuilder.toString(), args);

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    Log.println(Log.WARN, "query to find matched buddies", cursor.getString(cursor.getColumnIndex(COL_2)));
                    // Extract user data from the cursor
                    @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_2));
                    @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COL_3));
                    @SuppressLint("Range") String firstName = cursor.getString(cursor.getColumnIndex(COL_4));
                    @SuppressLint("Range") String lastName = cursor.getString(cursor.getColumnIndex(COL_5));
                    @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(COL_6));
                    @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(COL_7));
                    @SuppressLint("Range") String studyTime = cursor.getString(cursor.getColumnIndex(COL_8));
                    @SuppressLint("Range") String topics = cursor.getString(cursor.getColumnIndex(COL_9));
                    @SuppressLint("Range") String difficulty = cursor.getString(cursor.getColumnIndex(COL_10));
                    //@SuppressLint("Range") String occupation = cursor.getString(cursor.getColumnIndex(COL_12));

                    // Parse study time preferences into a list
                    ArrayList<String> studyTimeList = new ArrayList<>();
                    if (studyTime != null && !studyTime.isEmpty()) {
                        for (String time : studyTime.split(",")) {
                            studyTimeList.add(time.trim());
                        }
                    }

                    // Parse topics of interest into a list
                    ArrayList<String> topicsList = new ArrayList<>();
                    if (topics != null && !topics.isEmpty()) {
                        for (String topic : topics.split(",")) {
                            topicsList.add(topic.trim());
                        }
                    }

                    // Create User object and add it to the list
                    User user = new User(email, password, firstName, lastName, age, gender, studyTimeList, topicsList, difficulty);
                    //User user = new User(email, password, firstName, lastName, age, gender, studyTimeList, topicsList, difficulty, occupation);
                    users.add(user);

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure cursor is closed to prevent resource leaks
            }
            db.close(); // Close database connection
        }

        return users;
    }


    public ArrayList<User> getUsersWithSameTopics(List<String> userTopics, String currentUserEmail) {
        ArrayList<User> matchingUsers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE ");
        for (int i = 0; i < userTopics.size(); i++) {
            queryBuilder.append(COL_9).append(" LIKE ?");
            if (i < userTopics.size() - 1) {
                queryBuilder.append(" OR ");
            }
        }
        queryBuilder.append(" AND " + COL_2 + " != ?");

        ArrayList<String> args = new ArrayList<>(userTopics.size());
        for (String topic : userTopics) {
            args.add("%" + topic + "%");
        }
        args.add(currentUserEmail);

        Cursor cursor = db.rawQuery(queryBuilder.toString(), args.toArray(new String[0]));

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_2));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COL_3));
                @SuppressLint("Range") String firstName = cursor.getString(cursor.getColumnIndex(COL_4));
                @SuppressLint("Range") String lastName = cursor.getString(cursor.getColumnIndex(COL_5));
                @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(COL_6));
                @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(COL_7));
                //@SuppressLint("Range") String occupation = cursor.getString(cursor.getColumnIndex(COL_12));

                @SuppressLint("Range")
                String preferredStudyTimeString = cursor.getString(cursor.getColumnIndex(COL_8));
                ArrayList<String> preferredStudyTime = new ArrayList<>();
                if (preferredStudyTimeString != null && !preferredStudyTimeString.isEmpty()) {
                    preferredStudyTime = new ArrayList<>(Arrays.asList(preferredStudyTimeString.split(",")));
                }


                @SuppressLint("Range")
                String topicsString = cursor.getString(cursor.getColumnIndex(COL_9));
                ArrayList<String> topicInterested = new ArrayList<>();
                if (topicsString != null && !topicsString.isEmpty()) {
                    topicInterested = new ArrayList<>(Arrays.asList(topicsString.split(",")));
                }

                @SuppressLint("Range") String studyDifficultyLevel = cursor.getString(cursor.getColumnIndex(COL_10));

                matchingUsers.add(new User(email, password, firstName, lastName, age, gender, preferredStudyTime, topicInterested, studyDifficultyLevel));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return matchingUsers;
    }


    public ArrayList<String> getUserTopicsByEmail(String email) {
        ArrayList<String> topics = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COL_9 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String topicsString = cursor.getString(cursor.getColumnIndex(COL_9));
            topics = new ArrayList<>(Arrays.asList(topicsString.split(",")));
            cursor.close();
        }
        db.close();

        return topics;
    }


    //Check if the user is already set up
    public boolean isSetUp(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT " + COL_11 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean alreadySetUp = false;
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int setUp = cursor.getInt(cursor.getColumnIndex(COL_11));
            if (setUp == 1) {
                alreadySetUp = true;
            }
            cursor.close();
            db.close();
        }
        return alreadySetUp;

    }

    @SuppressLint("Range")
    public String getUserTopicString(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_9 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String topics = "";

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String topicsString = cursor.getString(cursor.getColumnIndex(COL_9));
            topics = cursor.getString(cursor.getColumnIndex(COL_9));
            cursor.close();
        }
        db.close();

        return topics;

    }

    /**
     *
     * @param email - user email address
     * @return - preferred study time of the user as a string
     */
    @SuppressLint("Range")
    public String getUserStudyTimeString(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_8 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String studyTimeString = "";

        if (cursor != null && cursor.moveToFirst()) {
            studyTimeString = cursor.isNull(cursor.getColumnIndex(COL_8)) ? "" : cursor.getString(cursor.getColumnIndex(COL_8));
            cursor.close();
        }
        db.close();

        return studyTimeString;
    }


    //Get User ID (for sending connect request）
    @SuppressLint("Range")
    public String getUserIDByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String userID = "";
        String query = "SELECT " + COL_1 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            userID = cursor.getString(cursor.getColumnIndex(COL_1));
            cursor.close();
        }
        db.close();
        return userID;
    }

    public User getUserDetailsForMyProfilePage(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ TABLE_NAME +" WHERE " + COL_2 + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        User user = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String emailId = cursor.getString(cursor.getColumnIndex(COL_2));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COL_3));
                @SuppressLint("Range") String firstName = cursor.getString(cursor.getColumnIndex(COL_4));
                @SuppressLint("Range") String lastName = cursor.getString(cursor.getColumnIndex(COL_5));
                @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(COL_6));
                @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(COL_7));
                @SuppressLint("Range") String studyTime = cursor.getString(cursor.getColumnIndex(COL_8));
                @SuppressLint("Range") String topics = cursor.getString(cursor.getColumnIndex(COL_9));
                @SuppressLint("Range") String difficulty = cursor.isNull(cursor.getColumnIndex(COL_10)) ? "" : cursor.getString(cursor.getColumnIndex(COL_10));
                @SuppressLint("Range") String occupation = cursor.isNull(cursor.getColumnIndex(COL_12)) ? "" : cursor.getString(cursor.getColumnIndex(COL_12));
                @SuppressLint("Range") String linkedIn = cursor.isNull(cursor.getColumnIndex(COL_12)) ? "" : cursor.getString(cursor.getColumnIndex(COL_13));
                @SuppressLint("Range") String github = cursor.isNull(cursor.getColumnIndex(COL_12)) ? "" : cursor.getString(cursor.getColumnIndex(COL_14));
                @SuppressLint("Range") String personal = cursor.isNull(cursor.getColumnIndex(COL_12)) ? "" : cursor.getString(cursor.getColumnIndex(COL_15));

                Log.println(Log.WARN, "print occupation info", occupation);
                ArrayList<String> studyTimeList = new ArrayList<>();
                if (studyTime != null && !studyTime.isEmpty()) {
                    String[] studyTimeArray = studyTime.split(",");
                    for (String time : studyTimeArray) {
                        studyTimeList.add(time.trim());
                    }
                }

                ArrayList<String> topicsList = new ArrayList<>();
                if (topics != null && !topics.isEmpty()) {
                    String[] topicsArray = topics.split(",");
                    for (String topic : topicsArray) {
                        topicsList.add(topic.trim());
                    }
                }
                user = new User(email, password, firstName, lastName, age, gender, studyTimeList, topicsList, difficulty, occupation, linkedIn, github, personal);
                //user = new User(email, password, firstName, lastName, age, gender, studyTimeList, topicsList, difficulty);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return user;
    }




    @SuppressLint("Range")
    public User getUserInfoByEmail(String emailId){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ TABLE_NAME +" WHERE " + COL_2 + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{emailId});
        User user = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_2));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COL_3));
                @SuppressLint("Range") String firstName = cursor.getString(cursor.getColumnIndex(COL_4));
                @SuppressLint("Range") String lastName = cursor.getString(cursor.getColumnIndex(COL_5));
                @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(COL_6));
                @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(COL_7));
                @SuppressLint("Range") String studyTime = cursor.getString(cursor.getColumnIndex(COL_8));
                @SuppressLint("Range") String topics = cursor.getString(cursor.getColumnIndex(COL_9));
                @SuppressLint("Range") String difficulty = cursor.isNull(cursor.getColumnIndex(COL_10)) ? "" : cursor.getString(cursor.getColumnIndex(COL_10));
                //@SuppressLint("Range") String occupation = cursor.getString(cursor.getColumnIndex(COL_12));
                String occupation = cursor.isNull(cursor.getColumnIndex(COL_12)) ? "" : cursor.getString(cursor.getColumnIndex(COL_12));

                Log.println(Log.WARN, "print occupation info", occupation);
                ArrayList<String> studyTimeList = new ArrayList<>();
                if (studyTime != null && !studyTime.isEmpty()) {
                    String[] studyTimeArray = studyTime.split(",");
                    for (String time : studyTimeArray) {
                        studyTimeList.add(time.trim());
                    }
                }

                ArrayList<String> topicsList = new ArrayList<>();
                if (topics != null && !topics.isEmpty()) {
                    String[] topicsArray = topics.split(",");
                    for (String topic : topicsArray) {
                        topicsList.add(topic.trim());
                    }
                }
                user = new User(email, password, firstName, lastName, age, gender, studyTimeList, topicsList, difficulty, occupation);
                //user = new User(email, password, firstName, lastName, age, gender, studyTimeList, topicsList, difficulty);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return user;


    }

}