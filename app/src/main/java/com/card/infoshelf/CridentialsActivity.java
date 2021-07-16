package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class CridentialsActivity extends AppCompatActivity {

    TextInputEditText Email, Pass;
    TextView err;
    Button resetPass;
    private String CurrentUserId, CurrentEmail;
    private FirebaseAuth mAuth;
    DatabaseReference Ref, newRef;
    private ProgressDialog progress;
    SwitchCompat notifySwitch,emailSwitch;
    Boolean validation = false;
    private static final String TOPIC_POST_NOTIFICATION = "POST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cridentials);

        setTitle("Credentials");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Email = findViewById(R.id.user_Email);
        Pass = findViewById(R.id.user_pass);
        resetPass = findViewById(R.id.resetPassbtn);
        err = findViewById(R.id.errortxt);
        notifySwitch = findViewById(R.id.switch_notify);
        emailSwitch = findViewById(R.id.switch_email);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        CurrentEmail = mAuth.getCurrentUser().getEmail();
        Ref = FirebaseDatabase.getInstance().getReference();
        newRef = FirebaseDatabase.getInstance().getReference();

        Email.setText(CurrentEmail);
        //check user entered password with database password

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationpass();
                if (validation == true){
                    progress = new ProgressDialog(CridentialsActivity.this);
                    progress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    progress.setCanceledOnTouchOutside(false);
                    progress.setMessage("Please wait...");
                    progress.setIndeterminate(true);
                    progress.setProgress(0);
                    progress.show();
                    checkPass(CurrentEmail, Pass.getText().toString());
                }else {
                }
            }
        });

        //notifySwitch on/off state
        notifyFetchState();
        notifyEmailfetchState();

        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    subscribedPostNotification();
                }
                else {
                    unsubscribedPostNotification();
                }
            }
        });

        notifySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStateInUsers();
            }
        });

        emailSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmailStateInUsers();
            }
        });

    }

    private void notifyEmailfetchState() {
        Ref.child("Users").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("EmailState").exists()){

                    String h = snapshot.child("EmailState").getValue().toString();

                    emailSwitch.setChecked(h.equals("1"));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeEmailStateInUsers() {
        HashMap hashMap = new HashMap();
        if (emailSwitch.isChecked()){
            hashMap.clear();
            hashMap.put("EmailState","1");
            newRef.child("Users").child(CurrentUserId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                }
            });
        }else{
            hashMap.clear();
            hashMap.put("EmailState","0");
            newRef.child("Users").child(CurrentUserId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                }
            });
        }
    }

    private void unsubscribedPostNotification() {

        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_POST_NOTIFICATION).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "You will not receive all notification related to post";
                if (!task.isSuccessful()){

                }

            }
        });
    }

    private void subscribedPostNotification() {

        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "You will receive all notification related to post";
                if (!task.isSuccessful()){

                }

            }
        });
    }

    private void validationpass() {
        if (Pass.getText().toString().isEmpty()){
            Pass.setError("Please enter current password");
        }else {
            validation = true;
        }
    }

    private void notifyFetchState() {
        Ref.child("Users").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("NotifyState").exists()){

                    String h = snapshot.child("NotifyState").getValue().toString();

                    notifySwitch.setChecked(h.equals("1"));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeStateInUsers() {
        HashMap stateSwitch = new HashMap();

        if (notifySwitch.isChecked()){
            stateSwitch.put("NotifyState","1");
            newRef.child("Users").child(CurrentUserId).updateChildren(stateSwitch).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                }
            });
        }else{
            stateSwitch.put("NotifyState","0");
            newRef.child("Users").child(CurrentUserId).updateChildren(stateSwitch).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                }
            });
        }


    }


    private void checkPass(String Email, String Pass) {

        mAuth.signInWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        // if isSuccessful then done messgae will be shown
                                        // and you can change the password
                                        progress.cancel();
                                        err.setText("We have sent verification email to " + Email + ", Please go through the email");
                                        err.setTextColor(Color.parseColor("#0C7111"));
                                        err.setVisibility(View.VISIBLE);

                                    } else {
                                        progress.cancel();

                                        err.setTextColor(Color.parseColor("#ff0000"));
                                        err.setText("An error occurred, Please Try Again ");
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progress.cancel();
                                    err.setTextColor(Color.parseColor("#ff0000"));
                                    err.setText("An error occurred, Please Try Again ");
                                }
                            });

                        } else {
                            progress.cancel();
                            err.setTextColor(Color.parseColor("#ff0000"));
                            err.setText("Password does not match, Please enter correct password");
                        }
                    }
                });


    }

    private void  status (String status){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap map = new HashMap();
        map.put("status" , status);
        map.put("timeStamp" , timeStamp);

        ref.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}