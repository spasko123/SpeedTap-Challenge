package com.example.tectdepot.speedtapchallenge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LandingActivity extends AppCompatActivity {

    Button btnStart;
    MediaPlayer mediaPlayerBtn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.landing_activity);
            btnStart = findViewById(R.id.btnStart);
            textView = findViewById(R.id.textView);
            mediaPlayerBtn = MediaPlayer.create(this, R.raw.click_button);


            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LandingActivity.this, WelcomeToRegisterActivity.class));
                    mediaPlayerBtn.start();
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                    finish();
                }
            });
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LandingActivity.this, OpenChestActivity.class));
                    mediaPlayerBtn.start();
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                }
            });
    }


}
