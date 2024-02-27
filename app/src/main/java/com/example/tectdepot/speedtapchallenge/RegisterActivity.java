package com.example.tectdepot.speedtapchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText edtxEmailAddress, edtxUsername, edtxPassword, edtxRepeatPassword;
    Button btnRegister;
    String emailAddress, username, password, repeatPassword, registrationCode;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        edtxEmailAddress = findViewById(R.id.edtxEmailAddress);
        edtxUsername = findViewById(R.id.edtxUsername);
        edtxPassword = findViewById(R.id.edtxPassword);
        edtxRepeatPassword = findViewById(R.id.edtxRepeatPassword);
        btnRegister = findViewById(R.id.btnRegister);

        registrationCode = "stap";

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAddress = edtxEmailAddress.getText().toString();
                username = edtxUsername.getText().toString();
                password = edtxPassword.getText().toString();
                repeatPassword = edtxRepeatPassword.getText().toString();
                boolean isInputOkay = checkInput();
                if(!isInputOkay){
                    return;
                }
                checkUsernameExists(username);
            }
        });

    }

    private void checkUsernameExists(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference accountsRef = db.collection("accounts");

        accountsRef.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                edtxUsername.setError("Username already exists!");
                            } else {
                                registerUser(emailAddress, password, username);
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error checking username availability.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void registerUser(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userDocRef = db.collection("accounts").document(userId);
                            Map<String, Object> userData = new HashMap<>();
                            List<String> skinsCollection = new ArrayList<>();
                            skinsCollection.add("38");
                            userData.put("username", username);
                            userData.put("email", email);
                            userData.put("skinsCollection", skinsCollection);

                            userDocRef.set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Email address is already registered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean checkInput () {
            if (emailAddress.isEmpty()) {
                return false;
            }
            if (username.isEmpty()) {
                return false;
            }
            if (password.isEmpty()) {
                return false;
            }
            if (repeatPassword.isEmpty()) {
                return false;
            }
            if (!emailAddress.contains("@")) {
                edtxEmailAddress.setError("Invalid email address!");
                return false;
            }
            if (username.length() < 4) {
                edtxUsername.setError("Username must have at least 4 chars!");
                return false;
            }
            if (password.length() < 6) {
                edtxPassword.setError("Password must have at least 6 chars!");
                return false;
            }
            if (!password.equals(repeatPassword)) {
                edtxRepeatPassword.setError("Passwords doesn't match!");
                return false;
            }
            return true;
        }


}