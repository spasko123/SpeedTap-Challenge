package com.example.tectdepot.speedtapchallenge;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ShopActivity extends AppCompatActivity {

    TextView txtPoints;
    ImageView imgGoBack;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String userUid;
    int points;
    MediaPlayer mediaPlayerBtn;
    Button btnBuyTier1, btnBuyTier2, btnBuyTier3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        txtPoints = findViewById(R.id.txtPoints);
        imgGoBack = findViewById(R.id.imgGoBack);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mediaPlayerBtn = MediaPlayer.create(this, R.raw.click_button);
        btnBuyTier1 = findViewById(R.id.btnBuyTier1);
        btnBuyTier2 = findViewById(R.id.btnBuyTier2);
        btnBuyTier3 = findViewById(R.id.btnBuyTier3);

        btnBuyTier1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShopActivity.this, OpenChestActivity.class);
                i.putExtra("scope1", "0");
                i.putExtra("scope2", "10");
                startActivity(i);
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            }
        });
        btnBuyTier2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShopActivity.this, OpenChestActivity.class);
                i.putExtra("scope1", "11");
                i.putExtra("scope2", "23");
                startActivity(i);
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            }
        });
        btnBuyTier3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShopActivity.this, OpenChestActivity.class);
                i.putExtra("scope1", "23");
                i.putExtra("scope2", "38");
                startActivity(i);
                mediaPlayerBtn.start();
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            }
        });


        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userUid = currentUser.getUid();
        }
        getPointsFromDb();



        imgGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayerBtn.start();
                startActivity(new Intent(ShopActivity.this, GameOptionsActivity.class));
                overridePendingTransition(R.anim.enter_anim2, R.anim.exit_anim2);
            }
        });

        db.collection("accounts").document(userUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            int count = 0;
                            List<Long> skinsList = (List<Long>) document.get("skinsCollection");
                                for(int i = 0; i < skinsList.size(); i++){
                                    if(skinsList.get(i) <= 10){
                                        count++;
                                    }
                                }
                                if(count == 11){
                                    btnBuyTier1.setText("Locked");
                                }

                            }
                        } else {
                        }
                });

    }

    private void getPointsFromDb(){
        db.collection("accounts")
                .document(userUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String data = documentSnapshot.getString("points");
                            txtPoints.setText(data + " points");
                            if(data != null){
                                points = Integer.parseInt(data);
                            }
                        } else {
                            Toast.makeText(ShopActivity.this, "Error 102. Document doesn't exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}