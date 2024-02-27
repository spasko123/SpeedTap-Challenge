package com.example.tectdepot.speedtapchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText edtxEmailAddress, edtxPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtxEmailAddress = findViewById(R.id.edtxEmailAddress);
        edtxPassword = findViewById(R.id.edtxPassword);
        btnLogin = findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edtxEmailAddress.getText().toString();
                password = edtxPassword.getText().toString();
                checkUserInput();
            }
        });


    }
    private void checkUserInput(){
        if(email.isEmpty()){
            edtxEmailAddress.setError("Enter email address!");
            return;
        }
        if(!email.contains("@")){
            edtxEmailAddress.setError("Invalid email address!");
            return;
        }
        if(password.length() < 6){
            edtxPassword.setError("All passwords contains at least 6 characters!");
            return;
        }
        checkLogin();
    }
    private void checkLogin(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, GameOptionsActivity.class));
                        } else {
                        }
                    }
                });
    }
}