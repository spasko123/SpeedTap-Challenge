package com.example.tectdepot.speedtapchallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rvLeaderboard;
    ProgressBar progressBar;
    ImageView imgGoBack, imgSearch;
    TextView txt1, txt2, txt3;
    List<String> usernamesArray = new ArrayList<>();
    List<String> scoresArray = new ArrayList<>();
    MediaPlayer mediaPlayerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        progressBar = findViewById(R.id.progressBar);
        imgGoBack = findViewById(R.id.imgGoBack);
        imgSearch = findViewById(R.id.imgSearch);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        mediaPlayerBtn = MediaPlayer.create(this, R.raw.click_button);

        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScoresFromDB("30seconds");
                changeTimerChooserColor(txt1);
            }
        });
        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScoresFromDB("1minute");
                changeTimerChooserColor(txt2);
            }
        });
        txt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScoresFromDB("3minutes");
                changeTimerChooserColor(txt3);
            }
        });

        getScoresFromDB("30seconds");

        imgGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayerBtn.start();
                startActivity(new Intent(LeaderboardActivity.this, GameOptionsActivity.class));
                overridePendingTransition(R.anim.enter_anim2, R.anim.exit_anim2);
            }
        });


    }

    private void changeTimerChooserColor(TextView textView) {
        List<TextView> textViewsList = new ArrayList<>();
        textViewsList.add(txt1);
        textViewsList.add(txt2);
        textViewsList.add(txt3);

        for (int i = 0; i < textViewsList.size(); i++) {
            if (textViewsList.get(i) == textView) {
                textViewsList.get(i).setTextColor(Color.parseColor("#e3cdb3"));
            } else {
                textViewsList.get(i).setTextColor(Color.WHITE);
            }
        }
    }

    private void getScoresFromDB(String document){
        usernamesArray.clear();
        scoresArray.clear();
        db.collection("scores")

                .document(document)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> scoresFromDB = (List<String>) documentSnapshot.get("arrayOfScores");
                        assert scoresFromDB != null;
                        for (String item : scoresFromDB) {
                            String temp = item;
                            boolean isUsernameFinished = false;
                            String tempScore = "", tempUsername = "";
                            for(int i = 0; i < temp.length(); i++){
                                if(!isUsernameFinished){
                                    if(temp.charAt(i) == '-'){
                                        isUsernameFinished = true;
                                    }
                                    else{
                                        tempUsername += temp.charAt(i);
                                    }
                                }
                                else{
                                    tempScore += temp.charAt(i);
                                }
                            }
                            usernamesArray.add(tempUsername);
                            scoresArray.add(tempScore);
                        }
                        sortScoresAndPublish();
                    } else {

                    }
                })
                .addOnFailureListener(e -> {

                });
    }
    private void sortScoresAndPublish(){
        for(int i = 0; i < scoresArray.size(); i++){
            for (int j = i + 1; j < scoresArray.size(); j++){
                int score1 = Integer.parseInt(scoresArray.get(i));
                int score2 = Integer.parseInt(scoresArray.get(j));
                if(score1 < score2){
                    Collections.swap(scoresArray, i, j);
                    Collections.swap(usernamesArray, i, j);
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdapterForLeaderboard adapter = new AdapterForLeaderboard(this, usernamesArray, scoresArray);
        rvLeaderboard.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}