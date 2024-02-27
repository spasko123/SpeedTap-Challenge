package com.example.tectdepot.speedtapchallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameOptionsActivity extends AppCompatActivity {

    CardView cardOptionStartGame, cardOptionLeaderboard, cardOptionShop;
    TextView txtGreeting;
    MediaPlayer mediaPlayerBtn;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String userUid, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);

        cardOptionStartGame = findViewById(R.id.cardOptionStartGame);
        cardOptionLeaderboard = findViewById(R.id.cardOptionLeaderboard);
        cardOptionShop = findViewById(R.id.cardOptionShop);
        txtGreeting = findViewById(R.id.txtGreeting);
        mediaPlayerBtn = MediaPlayer.create(this, R.raw.click_button);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userUid = currentUser.getUid();
        }
        getUsernameFromDB();



        cardOptionShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GameOptionsActivity.this, ShopActivity.class);
                i.putExtra("username", username);
                startActivity(i);
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            }
        });

        cardOptionStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GameOptionsActivity.this, HomeActivity.class);
                i.putExtra("username", username);
                startActivity(i);
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            }
        });

        cardOptionLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GameOptionsActivity.this, LeaderboardActivity.class));
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            }
        });
    }
    private void getUsernameFromDB(){
        db.collection("accounts")
                .document(userUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String data = documentSnapshot.getString("username");
                            String greeting = checkTime();
                            txtGreeting.setText(greeting + ", " + data + "!");
                        } else {
                            Toast.makeText(GameOptionsActivity.this, "Error 102. Document doesn't exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private String checkTime(){
        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeStr = sdf.format(currentTime);
        int hour = Integer.parseInt(timeStr.split(":")[0]);

        String greeting;
        if (hour < 6) {
            greeting = "Good evening";
        } else if (hour < 12) {
            greeting = "Good morning";
        } else if (hour < 18) {
            greeting = "Good day";
        } else {
            greeting = "Good evening";
        }
        return greeting;
    }
}