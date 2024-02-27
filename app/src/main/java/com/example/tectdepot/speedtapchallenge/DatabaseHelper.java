package com.example.tectdepot.speedtapchallenge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "scores.db";
    public static final int DATABASE_VERSION = 3;

    public static final String TABLE_NAME = "scores";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SCORE = "score";

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SCORE + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void saveScore(int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);
        db.delete(TABLE_NAME, null, null);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public int getStoredScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        int storedScore = 0;

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_SCORE}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            storedScore = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
        }

        cursor.close();
        db.close();
        return storedScore;
    }

}

