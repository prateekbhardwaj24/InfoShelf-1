package com.card.infoshelf.Block;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.TagModel;
import com.card.infoshelf.bottomfragment.networkModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockedActivity extends AppCompatActivity {

    private RecyclerView blocked_recycler;
    private ArrayList<TagModel> list;
    private DatabaseReference blockedREf, UserREf;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private block_adapter adapter;
    private TextView blocked_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked);

        setTitle("Blocked");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        blocked_recycler = findViewById(R.id.blocked_recycler);
        blocked_tv = findViewById(R.id.blocked_tv);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        blockedREf = FirebaseDatabase.getInstance().getReference("BlockList");
        UserREf = FirebaseDatabase.getInstance().getReference("Users");

        blocked_recycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        blocked_recycler.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        list = new ArrayList<>();
        adapter = new block_adapter(this, list);
        blocked_recycler.setAdapter(adapter);

        getAllBlockedUser();
    }

    private void getAllBlockedUser() {
        blockedREf.child(CurrentUserId).orderByChild("state").equalTo("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    blocked_tv.setVisibility(View.GONE);
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String key = ds.getKey();

                        UserREf.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                TagModel user = dataSnapshot.getValue(TagModel.class);
                                list.add(user);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                else {

                    blocked_tv.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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