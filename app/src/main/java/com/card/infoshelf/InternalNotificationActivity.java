package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.card.infoshelf.bottomfragment.noti_model;
import com.card.infoshelf.bottomfragment.notification_viewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InternalNotificationActivity extends AppCompatActivity {

    private RecyclerView noti_recycler;
    //    private FirebaseRecyclerAdapter<noti_model, notification_viewHolder> adapter;
    private DatabaseReference NotiRef, blockListRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private ArrayList<noti_model> list;
    private notification_viewHolder notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_notification);

        noti_recycler = findViewById(R.id.noti_recycler);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        NotiRef = FirebaseDatabase.getInstance().getReference("Users");
        blockListRef = FirebaseDatabase.getInstance().getReference("BlockList");

        noti_recycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        noti_recycler.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        list = new ArrayList<>();
        notificationAdapter = new notification_viewHolder(this, list);
        noti_recycler.setAdapter(notificationAdapter);

//        loadNotification();
        LoadNotificationData();
        UpdateNotificationStatus();
    }


    private void LoadNotificationData() {
        ArrayList<String> listBlock = new ArrayList<>();

        blockListRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                for (DataSnapshot ds : dataSnapshot1.getChildren()){
                    String key = ds.getKey();
                    listBlock.add(key);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        NotiRef.child(CurrentUserId).child("Notifications").orderByChild("pUid").equalTo(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds1 : snapshot.getChildren()){

                    String id = ds1.child("sUid").getValue().toString();

                    if (listBlock.contains(id)) {
                    }
                    else {
                        noti_model user = ds1.getValue(noti_model.class);
                        list.add(user);
                    }

                }
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateNotificationStatus() {
        NotiRef.child(CurrentUserId).child("Notifications").orderByChild("pUid").equalTo(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String status = ""+ds.child("status").getValue().toString();
                    String timestamp = ""+ds.child("timestamp").getValue().toString();

                    if (status.equals("0")){
                        NotiRef.child(CurrentUserId).child("Notifications").child(timestamp).child("status").setValue("1");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}