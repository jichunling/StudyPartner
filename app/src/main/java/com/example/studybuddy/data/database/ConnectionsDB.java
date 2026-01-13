package com.example.studybuddy.data.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.studybuddy.data.model.Connections;

import java.util.ArrayList;

import com.example.studybuddy.data.model.Connections;
import com.example.studybuddy.data.model.User;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConnectionsDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Connections.db";
    public static final String TABLE_NAME = "Connections";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "SENDER_EMAIL";
    public static final String COL_3 = "RECEIVER_EMAIL";
    public static final String COL_4 = "STATUS";

    public ConnectionsDB(Context context) {
        //changed to version 3
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "SENDER_EMAIL TEXT, RECEIVER_EMAIL TEXT, STATUS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertConnectionRequest(String senderEmail, String receiverEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        //不需要
//        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?";
//        Cursor cursor = db.rawQuery(query, new String[]{senderEmail});
//
//        if (cursor != null && cursor.moveToFirst()) {
//            cursor.close();
//            db.close();
//            return false;
//        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, senderEmail);
        contentValues.put(COL_3, receiverEmail);
        contentValues.put(COL_4, "Sent");

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        return result != -1;
    }



    public ArrayList<Connections> getConnectionRequests(String receiverEmail) {
        if (receiverEmail == null) {
            Log.e("ConnectionsDB", "receiverEmail is null. Returning empty list.");
            return new ArrayList<>();
        }
        ArrayList<Connections> connections = new ArrayList<>();
        SQLiteDatabase connectDB = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_3 + " = ?";
        Cursor cursor = connectDB.rawQuery(query, new String[]{receiverEmail});
        Set<String> senderSet = new HashSet<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String connectionID = cursor.getString(cursor.getColumnIndex(COL_1));
                @SuppressLint("Range") String senderEmail = cursor.getString(cursor.getColumnIndex(COL_2));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex(COL_4));
                Connections c = new Connections(connectionID, senderEmail, receiverEmail, status);

                if(!senderEmail.equals(receiverEmail) && !senderSet.contains(senderEmail)) {
                    connections.add(c);
                    senderSet.add(senderEmail);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        connectDB.close();
        return connections;
    }
}
