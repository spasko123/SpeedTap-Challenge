package com.example.tectdepot.speedtapchallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeToRegisterActivity extends AppCompatActivity {

    Button btnLogin, btnRegister, btnGuest;
    MediaPlayer mediaPlayerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_to_register);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnGuest = findViewById(R.id.btnGuest);

        mediaPlayerBtn = MediaPlayer.create(this, R.raw.click_button);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeToRegisterActivity.this, LoginActivity.class));
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeToRegisterActivity.this, RegisterActivity.class));
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                finish();
            }
        });
        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeToRegisterActivity.this, GameOptionsActivity.class));
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                finish();
            }
        });
    }
}