package com.card.infoshelf.AllFriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.card.infoshelf.R;
import com.card.infoshelf.allUserAdaptor;
import com.card.infoshelf.bottomfragment.networkModel;
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

public class AllFriendsActivity extends AppCompatActivity {

    private RecyclerView all_friend_rv;
    private ArrayList<networkModel> list;
    private AllFriendsAdaptor adaptor;
    private DatabaseReference Ref, userInfoRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private String userid;
    private ProgressDialog progressDialog;
    public static ProgressDialog progressDialog1;
    private TextView no_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends);
        setTitle("Add Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        all_friend_rv = findViewById(R.id.all_friends_rv);
        no_chat = findViewById(R.id.no_chat);
        all_friend_rv.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adaptor = new AllFriendsAdaptor(this, list);
        all_friend_rv.setAdapter(adaptor);




        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");


        progressDialog = new ProgressDialog(AllFriendsActivity.this);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);


        progressDialog1 = new ProgressDialog(AllFriendsActivity.this);

        Ref.child("Friends").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    no_chat.setVisibility(View.GONE);
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String id = dataSnapshot.getKey();
                        String state = dataSnapshot.child("Friends").getValue().toString();
                        if (state.equals("Saved"))
                        {
                            userInfoRef.child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    networkModel user = snapshot.getValue(networkModel.class);
                                    list.add(user);
                                    adaptor.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }



                    }
                    progressDialog.dismiss();



                }
                else
                {
                    no_chat.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void filer(String text) {
        ArrayList<networkModel> filterList = new ArrayList<>();

        for (networkModel model : list)
        {
            if (model.getUserName().toLowerCase().contains(text.toLowerCase()))
            {
                filterList.add(model);
            }
        }
        adaptor.filterList(filterList);

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu , menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filer(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);


    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        progressDialog1.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}