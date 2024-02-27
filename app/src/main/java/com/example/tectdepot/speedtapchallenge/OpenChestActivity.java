package com.example.tectdepot.speedtapchallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class OpenChestActivity extends AppCompatActivity {
    ImageView imgRandomSkin;
    TextView txt;
    Button btnClose;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String userUid;
    int randomNumber, min, max;
    List<String> listOfAvailableSkins;
    GifImageView gif;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_chest);

        imgRandomSkin = findViewById(R.id.imgRandomSkin);
        btnClose = findViewById(R.id.btnClose);
        txt = findViewById(R.id.txt);
        gif = findViewById(R.id.gif);
        intent = getIntent();

        String scope1 = intent.getStringExtra("scope1");
        String scope2 = intent.getStringExtra("scope2");

        min = Integer.parseInt(scope1);
        max = Integer.parseInt(scope2);

        Toast.makeText(this, min + "-" + max, Toast.LENGTH_SHORT).show();

        listOfAvailableSkins = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userUid = currentUser.getUid();
        }


        int[] skinResources = new int[39];
        for (int i = 0; i < 39; i++) {
            int resourceId = getResources().getIdentifier("skin" + (i + 1), "drawable", getPackageName());
            skinResources[i] = resourceId;
        }

        DocumentReference docRef = db.collection("accounts").document(userUid);
        Random random = new Random();

        db.collection("accounts").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<String> skinsCollection = (List<String>) documentSnapshot.get("skinsCollection");
                    int check = 0;
                    while(check == 0){
                        int check2 = 0;
                        int randomNumber = random.nextInt(max - min) + min;
                        for(int i = 0; i < skinsCollection.size(); i++){
                            if((randomNumber == Integer.parseInt(skinsCollection.get(i)))){
                                check2 = 1;
                            }
                        }
                        if(check2 == 0){
                            check = 1;
                            gif.setVisibility(View.VISIBLE);
                            imgRandomSkin.setImageResource(skinResources[randomNumber]);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.spin_animation);
                            imgRandomSkin.startAnimation(animation);

                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    btnClose.setVisibility(View.VISIBLE);
                                    txt.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            db.collection("accounts").document(userUid).update("skinsCollection", FieldValue.arrayUnion(String.valueOf(randomNumber)))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                        }
                    }
                } else {
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OpenChestActivity.this, ShopActivity.class));
            }
        });
    }
}
