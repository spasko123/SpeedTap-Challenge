package com.example.tectdepot.speedtapchallenge;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    Button btnSaveScore, btnResetGame;
    TextView txtTimer, txtTapAndScore, txtScore;
    ImageView imgTap, imgGoBack, imgSoundEffects, imgTimer;
    boolean  isFirstTap = true, isGameFinished = true, isVolumeTurnedOn = true, isOkayToSaveToDB = true;
    int score = 0;
    String timerType = "30seconds", userUid;

    MediaPlayer mediaPlayerTap, mediaPlayerBtn, timer10sCountdown, timerOutSound, saveScoreSound;
    CountDownTimer timer;
    long totalTimeInMillis = 30 * 1000;
    Animation expandAnimation, expandAnimation2;
    Animation shrinkAnimation, shrinkAnimation2;
    List<String> usernamesArray = new ArrayList<>();
    List<String> scoresArray = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    String username;
    List<String> scoresFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnSaveScore = findViewById(R.id.btnSaveScore);
        btnResetGame = findViewById(R.id.btnResetGame);
        txtTimer = findViewById(R.id.txtTimer);
        txtTapAndScore = findViewById(R.id.txtTapAndScore);
        txtScore = findViewById(R.id.txtScore);
        imgTap = findViewById(R.id.imgTap);
        imgSoundEffects = findViewById(R.id.imgSoundEffects);
        imgGoBack = findViewById(R.id.imgGoBack);
        imgTimer = findViewById(R.id.imgTimer);
        expandAnimation = AnimationUtils.loadAnimation(this, R.anim.expand_anim);
        shrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.shrink_anim);
        expandAnimation2 = AnimationUtils.loadAnimation(this, R.anim.expand_anim);
        shrinkAnimation2 = AnimationUtils.loadAnimation(this, R.anim.shrink_anim);
        mediaPlayerTap = MediaPlayer.create(this, R.raw.tap_pop_sound);
        mediaPlayerBtn = MediaPlayer.create(this, R.raw.click_button);
        timer10sCountdown = MediaPlayer.create(this, R.raw.timer_10s_countdown);
        timerOutSound = MediaPlayer.create(this, R.raw.timer_out_sound);
        saveScoreSound = MediaPlayer.create(this, R.raw.save_score_sound);

        username = getIntent().getStringExtra("username");

         currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userUid = currentUser.getUid();
        }



            imgTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForTimerOptions();
            }
        });




        imgSoundEffects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isVolumeTurnedOn){
                    isVolumeTurnedOn = false;
                    imgSoundEffects.setImageResource(R.drawable.volume_off);
                }
                else{
                    isVolumeTurnedOn = true;
                    imgSoundEffects.setImageResource(R.drawable.volume_up);
                }
            }
        });


        imgGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, GameOptionsActivity.class));
                overridePendingTransition(R.anim.enter_anim2, R.anim.exit_anim2);
                mediaPlayerBtn.start();
                finish();
            }
        });



        imgTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFirstTap && isGameFinished){
                    imgTimer.setVisibility(View.GONE);
                    createTimer();
                    timer.start();
                    isFirstTap = false;
                    isGameFinished = false;
                    click();
                }
                else if(!isGameFinished){
                    click();
                }
            }
        });

        btnResetGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
            }
        });

        btnSaveScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSaveScore();
            }
        });


    }
    private void click(){
        if(isVolumeTurnedOn){
            if (mediaPlayerTap.isPlaying()) {
                mediaPlayerTap.seekTo(0);
            } else {
                mediaPlayerTap.start();
            }
        }
        score++;
        txtTapAndScore.setText("Score:");
        txtScore.setVisibility(View.VISIBLE);
        txtScore.setText("" + score);
        imgTap.startAnimation(expandAnimation);
        expandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgTap.startAnimation(shrinkAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }



    private void createTimer(){
        timer = new CountDownTimer(totalTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String timeLeft = String.format("%02d:%02d", minutes, seconds);
                txtTimer.setText(timeLeft);
                if (millisUntilFinished < 11000) {
                    if(isVolumeTurnedOn){
                        timer10sCountdown.start();
                    }
                    txtTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    txtTimer.startAnimation(expandAnimation2);
                    expandAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imgTap.startAnimation(shrinkAnimation2);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }

            @Override
            public void onFinish() {
                timerOutSound.start();
                txtTimer.setText("00:00");
                imgTimer.setVisibility(View.VISIBLE);
                isGameFinished = true;
                txtTimer.setVisibility(View.GONE);
                btnSaveScore.setVisibility(View.VISIBLE);
                btnResetGame.setVisibility(View.VISIBLE);
                getPointsFromDB();
                highestScore();
            }
        };
    }

    private void getPointsFromDB(){

        db.collection("accounts")
                .document(userUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String points = documentSnapshot.getString("points");
                            Toast.makeText(HomeActivity.this, "" + points, Toast.LENGTH_SHORT).show();
                            int pointsInt = 0;
                            if (points != null) {
                                pointsInt = Integer.parseInt(points);
                            }

                            if (points != null) {
                                savePointsToDb(pointsInt);
                            } else {
                                savePointsToDb(pointsInt);
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Error 102. Document doesn't exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void savePointsToDb(int pointsFromDb){
        int fin = score + pointsFromDb;
        String sFin = String.valueOf(fin);
        db.collection("accounts").document(userUid).update("points", sFin)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomeActivity.this, "Stored points!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UpdateError", "Error updating score field: " + e.getMessage());
                    }
                });
    }

    private void getScoresFromDB(String document) {
        usernamesArray.clear();
        scoresArray.clear();

        db.collection("scores")
                .document(document)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        scoresFromDB = (List<String>) documentSnapshot.get("arrayOfScores");
                        assert scoresFromDB != null;
                        for (String item : scoresFromDB) {
                            String temp = item;
                            boolean isUsernameFinished = false;
                            String tempScore = "", tempUsername = "";
                            for (int i = 0; i < temp.length(); i++) {
                                if (!isUsernameFinished) {
                                    if (temp.charAt(i) == '-') {
                                        isUsernameFinished = true;
                                    } else {
                                        tempUsername += temp.charAt(i);
                                    }
                                } else {
                                    tempScore += temp.charAt(i);
                                }
                            }
                            usernamesArray.add(tempUsername);
                            scoresArray.add(tempScore);
                        }
                    } else {

                    }

                    for (int i = 0; i < scoresArray.size(); i++) {
                        for (int j = i + 1; j < scoresArray.size(); j++) {
                            int score1 = Integer.parseInt(scoresArray.get(i));
                            int score2 = Integer.parseInt(scoresArray.get(j));
                            if (score1 < score2) {
                                Collections.swap(scoresArray, i, j);
                                Collections.swap(usernamesArray, i, j);
                            }
                        }
                    }

                    if (scoresArray.size() < 100) {

                        usernamesArray.add(username);
                        scoresArray.add(String.valueOf(score));
                        scoresFromDB.add(username + "-" + score);

                        Map<String, Object> data = new HashMap<>();
                        data.put("arrayOfScores", scoresFromDB);

                        db.collection("scores").document(document).set(data);
                    }

                    //Проверява последняи елемент дали е по-малък от сегашния направен резултат, и ако е така последния резултат се заменя с новия
                    if (scoresArray.size() >= 100 || score > Integer.parseInt(scoresArray.get(scoresArray.size() - 1))) {
                        usernamesArray.add(username);
                        scoresArray.add(String.valueOf(score));
                        scoresFromDB.add(username + "-" + score);
                        int lastIndex = scoresFromDB.size() - 1;
                        scoresFromDB.set(lastIndex, username + "-" + score);
                        Map<String, Object> data = new HashMap<>();
                        data.put("arrayOfScores", scoresFromDB);
                        db.collection("scores")
                                .document(document)
                                .set(data);
                    }
                });
    }

//    private List<String> createArrayOfScores() {
//        List<String> arrayOfScores = new ArrayList<>();
//        for (int i = 0; i < usernamesArray.size(); i++) {
//                arrayOfScores.add(usernamesArray.get(i) + "-" + scoresArray.get(i));
//        }
//
//
//        return arrayOfScores;
//    }

    private void resetGame(){
        isFirstTap = true;
        isGameFinished = true;
        score = 0;
        txtTimer.setTextColor(getResources().getColor(android.R.color.white));
        txtTimer.setVisibility(View.VISIBLE);
        btnSaveScore.setVisibility(View.GONE);
        btnResetGame.setVisibility(View.GONE);
        txtScore.setVisibility(View.INVISIBLE);
        txtTapAndScore.setText("Tap to start");
        isOkayToSaveToDB = true;
    }

    private void showDialogForTimerOptions() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_timer_options, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView txt30Seconds = dialog.findViewById(R.id.txt30Seconds);
        TextView txt1Minute = dialogView.findViewById(R.id.txt1Minute);
        TextView txt3Minutes = dialogView.findViewById(R.id.txt3Minutes);

        txt30Seconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalTimeInMillis = 30 * 1000;
                txtTimer.setText("00:30");
                dialog.dismiss();
                timerType = "30seconds";
            }
        });
        txt1Minute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalTimeInMillis = 60 * 1000;
                txtTimer.setText("01:00");
                dialog.dismiss();
                timerType = "1minute";
            }
        });
        txt3Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalTimeInMillis = 3 * 60 * 1000;
                txtTimer.setText("03:00");
                dialog.dismiss();
                timerType = "3minutes";
            }
        });
    }

    private void showDialogSaveScore() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_score, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText edtxUsername = dialog.findViewById(R.id.edtxUsername);
        Button btnSaveScoreDialog = dialog.findViewById(R.id.btnSaveScoreDialog);

        btnSaveScoreDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputUsername = edtxUsername.getText().toString();
                if (inputUsername.trim().isEmpty()) {
                    edtxUsername.setError("Please enter username!");
                    return;
                } else {
                    saveScoreToDB(inputUsername, score, timerType, dialog);
                }

            }
        });


    }

    private void highestScore(){
        db.collection("accounts")
                .document(userUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String record = documentSnapshot.getString("record");
                            if (record != null) {
                                int intRecord = Integer.parseInt(record);
                                if(score > intRecord){
                                    getScoresFromDB(timerType);
                                    saveHighScoreToDb(score);
                                    showDialogForNewRecord();
                                }
                            } else {
                                saveHighScoreToDb(score);
                            }
                        } else {

                        }
                    }
                });
    }


    private void saveHighScoreToDb(int score){
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("accounts").document(userId);
        Map<String, Object> userData = new HashMap<>();
        String scoreString = String.valueOf(score);
        userData.put("record", scoreString);

        userDocRef.update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void saveScoreToDB(String username, int score, String timerType, Dialog dialog){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("scores").document(timerType);

        documentRef.get()
                .addOnSuccessListener(documentSnapshot -> {

                    if(!isOkayToSaveToDB){
                        Toast.makeText(this, "Can't save the current score more than once!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }
                    List<String> currentArray = new ArrayList<>();

                    if (documentSnapshot.exists()) {
                        currentArray = (List<String>) documentSnapshot.get("arrayOfScores");
                    }


                    currentArray.add(username + "-" + score);

                    Map<String, Object> data = new HashMap<>();
                    data.put("arrayOfScores", currentArray);

                    documentRef.set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                if(isVolumeTurnedOn){
                                    saveScoreSound.start();
                                }
                                Toast.makeText(HomeActivity.this, "Successfully stored score to leaderboard!", Toast.LENGTH_SHORT).show();
                                isOkayToSaveToDB = false;
                                dialog.dismiss();

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(HomeActivity.this, "Error while saving score to leaderboard!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Database error. If error persists contact support!", Toast.LENGTH_SHORT).show();
                });
    }
    private void showDialogForNewRecord() {
        View dialogView = getLayoutInflater().inflate(R.layout.new_record_xml, null);

        ConstraintLayout conLayout = dialogView.findViewById(R.id.conLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        conLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

}
