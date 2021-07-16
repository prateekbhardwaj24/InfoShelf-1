package com.card.infoshelf.ReportAndSuggestions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.card.infoshelf.MainActivity;
import com.card.infoshelf.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class reportActivity extends AppCompatActivity {

    TextInputEditText yourEamil,YourSubject,YourExplanation;
    Button btnSubmit;
    private DatabaseReference postRef;
    private String CurrentUserId,Email;
    private FirebaseAuth mAuth;
    Boolean valid = false;
    String timestamp = String.valueOf(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setTitle("Report And Suggestions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        yourEamil = findViewById(R.id.ReporterEmail);
        YourExplanation = findViewById(R.id.Explanation);
        YourSubject = findViewById(R.id.Subject);
        btnSubmit = findViewById(R.id.submitReport);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        postRef = FirebaseDatabase.getInstance().getReference();

        //get form data first
        Email = mAuth.getCurrentUser().getEmail();
        yourEamil.setText(Email);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
                if (valid == true){
                    ShowConfirmationBox();

                }

            }
        });
    }

    private void ShowConfirmationBox() {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(reportActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Do you want to submit the report for Spam/Suggestions");

        // Set Alert Title
        builder.setTitle("Report Spam/Suggestions");

        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.

        builder
                .setPositiveButton(
                        "Yes",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                submitDataToFirebase();

                            }
                        });

        // Set the Negative button with No name
        // OnClickListener method is use
        // of DialogInterface interface.
        builder
                .setNegativeButton(
                        "No",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                // If user click no
                                // then dialog box is canceled.
                                dialog.cancel();
                            }
                        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }

    private void checkValidation() {
        if (YourSubject.getText().toString().trim().length() == 0 || YourExplanation.getText().toString().trim().length() == 0){
            YourExplanation.setError("Please fill all fields");
        }else{
            valid = true;
        }

    }

    private void submitDataToFirebase() {
        HashMap hashMap = new HashMap();
        hashMap.put("userId",CurrentUserId);
        hashMap.put("EmailId",Email);
        hashMap.put("Subject",YourSubject.getText().toString());
        hashMap.put("Explanation",YourExplanation.getText().toString());
        hashMap.put("TimeStamp",timestamp);

        postRef.child("ReportSuggestions").child(timestamp).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showCompleteBox();
            }
        });
    }

    private void showCompleteBox() {
        // Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(reportActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Successfully submitted");

        // Set Alert Title
        builder.setTitle("Report Spam/Suggestions");

        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(false);

        Intent intent = new Intent(reportActivity.this, MainActivity.class);
        startActivity(intent);

        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.

//        builder
//                .setPositiveButton(
//                        "Yes",
//                        new DialogInterface
//                                .OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which)
//                            {
//
//                                // When the user click yes button
//                                // then app will close
//                                finish();
//                            }
//                        });

        // Set the Negative button with No name
        // OnClickListener method is use
        // of DialogInterface interface.
//        builder
//                .setNegativeButton(
//                        "No",
//                        new DialogInterface
//                                .OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which)
//                            {
//
//                                // If user click no
//                                // then dialog box is canceled.
//                                dialog.cancel();
//                            }
//                        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
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