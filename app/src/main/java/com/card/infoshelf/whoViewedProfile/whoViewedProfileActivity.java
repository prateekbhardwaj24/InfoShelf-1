package com.card.infoshelf.whoViewedProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.card.infoshelf.R;
import com.card.infoshelf.WhoViewdAdapter;
import com.card.infoshelf.bottomfragment.GridModel;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class whoViewedProfileActivity extends AppCompatActivity {

    List<GridModel> ProfileId = new ArrayList<>();
    DatabaseReference Ref,newRef;
    RecyclerView recyclerView;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    TextView noViewedProfile;
    WhoViewdAdapter whoViewdAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_viewed_profile);

        setTitle("Who Viewed Your Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        recyclerView = findViewById(R.id.wvyp_recycler);
        noViewedProfile = findViewById(R.id.noViewedProfile);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(whoViewedProfileActivity.this,2,GridLayoutManager.VERTICAL,false);
        int spanCount = 2; // 3 columns
        int spacing = 15; // 50px
        boolean includeEdge = true;


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        whoViewdAdapter = new WhoViewdAdapter(this, ProfileId);

        recyclerView.setAdapter(whoViewdAdapter);

        Ref = FirebaseDatabase.getInstance().getReference();
        Ref.child("ViewedProfile").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    noViewedProfile.setVisibility(View.GONE);
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String userIds = ds.getKey();
//                        String userIds = ds.child("ViewedById").getValue().toString();
                        String count = ds.child("Count").toString();
                        newRef = FirebaseDatabase.getInstance().getReference().child("UserDetails").child(userIds);
                        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                GridModel model = snapshot.getValue(GridModel.class);
                                ProfileId.add(model);
                                whoViewdAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }else {
                    noViewedProfile.setVisibility(View.VISIBLE);
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