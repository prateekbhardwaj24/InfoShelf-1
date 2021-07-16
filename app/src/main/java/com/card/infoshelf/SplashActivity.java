package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.loginandsignIn.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    TextView i, infoshelf;
    ImageView hat;
    Animation left_to_right_animation, top_to_bottom_navigation;
    private static final int SPLASH_TIME_OUT = 2000;
    private FirebaseAuth mAuth;
    DatabaseReference checkDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        i = findViewById(R.id.i);
        infoshelf = findViewById(R.id.infoshelf);
        hat = findViewById(R.id.hat);

        mAuth = FirebaseAuth.getInstance();
        checkDetails = FirebaseDatabase.getInstance().getReference("UserDetails");

        left_to_right_animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.left_to_right_animation);
        top_to_bottom_navigation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_to_bottom_animation);
        i.startAnimation(left_to_right_animation);
        hat.startAnimation(top_to_bottom_navigation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentuser = mAuth.getCurrentUser();

                if (currentuser != null)
                {
                    if (currentuser.isEmailVerified()){

                        checkDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){
                                    if (snapshot.hasChild(currentuser.getUid())){
                                        Intent intent = new Intent(SplashActivity.this , MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Intent intent = new Intent(SplashActivity.this , FormActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                else {

                                    Intent intent = new Intent(SplashActivity.this , FormActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);
                                    finish();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    else {

                        Intent splashintent = new Intent(getApplicationContext(), LogInActivity.class);
                        splashintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(splashintent);
                        finish();
                    }

                }
                else
                {
                    Intent splashintent = new Intent(getApplicationContext(), LogInActivity.class);
                    splashintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(splashintent);
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);
    }
    @Override
    protected void onStart() {
        super.onStart();


    }
}