package com.card.infoshelf.profileFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.TagAdapter;
import com.card.infoshelf.bottomfragment.TagModel;
import com.card.infoshelf.whoViewedProfile.GridSpacingItemDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Imagefragment extends Fragment {


    List<GridModel> imageArray = new ArrayList<>();
    DatabaseReference Ref,newRef;
    RecyclerView recyclerView;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    GridViewAdapter gridadapter;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_imagefragment, container, false);
        recyclerView = view.findViewById(R.id.Rv_grid);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
//        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3,1);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3,GridLayoutManager.VERTICAL,false);
        int spanCount = 3; // 3 columns
        int spacing = 3; // 50px
        boolean includeEdge = true;

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        Collections.reverse(imageArray);
        gridadapter = new GridViewAdapter(getContext(),imageArray);

        recyclerView.setAdapter(gridadapter);
        Ref = FirebaseDatabase.getInstance().getReference();
        loadPostAndImages();
        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    private void loadPostAndImages() {
        Ref.child("UserPostData").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    imageArray.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String timestampUrl = ds.getValue().toString();
                        newRef = FirebaseDatabase.getInstance().getReference("POSTFiles").child(timestampUrl);
                        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                GridModel model = snapshot.getValue(GridModel.class);
                                String type = model.getFileType();
                                Log.d("type", type);
                                if (type.equals("image") || type.equals("none")){
                                    imageArray.add(model);
                                    gridadapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}