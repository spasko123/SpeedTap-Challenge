package com.example.tectdepot.speedtapchallenge;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Test extends AppCompatActivity {

    Button btn, btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPoints = getStoredScore() + 20;
                insertScore(newPoints);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Test.this, "" + getStoredScore(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void insertScore(int score) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SCORE, score);

        // Delete any existing scores
        db.delete(DatabaseHelper.TABLE_NAME, null, null);

        // Insert the new score
        long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Score inserted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to insert score", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private int getStoredScore() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        return databaseHelper.getStoredScore();
    }

}