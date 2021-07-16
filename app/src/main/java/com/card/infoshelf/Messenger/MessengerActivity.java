package com.card.infoshelf.Messenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.AllFriends.AllFriendsActivity;
import com.card.infoshelf.MainActivity;

import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.networkModel;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MessengerActivity extends AppCompatActivity {

    FloatingActionButton btn_add;
    private RecyclerView chat_rv;
    private ArrayList<networkModel> list;
    private MessengerAdaptor adaptor;
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
        setContentView(R.layout.activity_messenger);
        setTitle("Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        chat_rv = findViewById(R.id.chat_rv);
        no_chat = findViewById(R.id.no_chat);
        linearLayoutManager.setReverseLayout(true);
        chat_rv.setLayoutManager(linearLayoutManager);

        progressDialog = new ProgressDialog(MessengerActivity.this);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);


        progressDialog1 = new ProgressDialog(MessengerActivity.this);



        list = new ArrayList<>();
        adaptor = new MessengerAdaptor(this, list);
        chat_rv.setAdapter(adaptor);



        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");

        btn_add  = findViewById(R.id.btn_add);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MessengerActivity.this , AllFriendsActivity.class);
                startActivity(intent);
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

    @Override
    protected void onStart() {
        super.onStart();
        Ref.child("ChatList").child(CurrentUserId).orderByChild("time").limitToLast(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    no_chat.setVisibility(View.GONE);
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        networkModel user = dataSnapshot.getValue(networkModel.class);
                        list.add(user);
                    }
                    adaptor.notifyDataSetChanged();
                    progressDialog.dismiss();


                }
                else
                {
                    progressDialog.dismiss();
                    no_chat.setVisibility(View.VISIBLE);
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



        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("timeStamp", timeStamp); 
        onlineState.put("status", status);


        ref.updateChildren(onlineState);
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
        progressDialog1.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}