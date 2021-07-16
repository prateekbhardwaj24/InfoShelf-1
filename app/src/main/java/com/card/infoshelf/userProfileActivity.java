package com.card.infoshelf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.card.infoshelf.Friends.FriendsActivity;
import com.card.infoshelf.Requests.userProfileTabAccessAdaptor;
import com.card.infoshelf.bottomfragment.GridModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfileActivity extends AppCompatActivity {

    private TextView userName, userinfo, viewProfile, titleProfile, f_count, timeline_count;
    private LinearLayout network, block_l, viewProfile_l, accept, reject;
    private String userid;
    private ImageView coverPic, dialogImage, menuOption, request,send, accepted, imagebottom;
    private CircleImageView userProfile;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private BottomSheetDialog bottomSheetDialog, imageBottomSheet;
    private Dialog mDialog;
    private DatabaseReference UsersRef, ChatRef, FriendsRef, PostRef, blockREf, chatList;
    private String currentState, senderUserID, receiverUserID, pImage, body;
    private static final String fcmServerKey ="AAAAXb76uxw:APA91bFE4oCMF8hCE9JLM_R_aLOAT838pI0-GHtpcZPB876r8bHF_aXQ2dOb9LCCytmN_G8UOxfTZx69HZrnB1aZjEAs8etZOYmFdokaGj3azt5OKI0sO01PrKZMXTrad2UbNLEnGFlo";
    String profession;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private userProfileTabAccessAdaptor tabaccessAdaptor;
    private Dialog blockDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().hide();

        userid = getIntent().getStringExtra("userid");

        userinfo = findViewById(R.id.userinfo);
        userName = findViewById(R.id.userName);
        coverPic = findViewById(R.id.coverPic);
        userProfile = findViewById(R.id.userProfile);
        request = findViewById(R.id.request);
        accept = findViewById(R.id.accept);
        reject = findViewById(R.id.reject);
        send = findViewById(R.id.send);
        accepted = findViewById(R.id.accepted);
        network = findViewById(R.id.network);
        f_count = findViewById(R.id.f_count);
        menuOption = findViewById(R.id.menuOption);
        timeline_count = findViewById(R.id.timeline_count);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        blockREf = FirebaseDatabase.getInstance().getReference("BlockList");
        chatList = FirebaseDatabase.getInstance().getReference("ChatList");


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        senderUserID = mAuth.getCurrentUser().getUid();
        receiverUserID = userid;
        currentState = "new";

        blockDialog = new Dialog(this);
        blockDialog.setContentView(R.layout.block_dialog);
        blockDialog.setCancelable(false);

        Button yes = blockDialog.findViewById(R.id.yes);
        Button no = blockDialog.findViewById(R.id.no);

        imageBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        View imageview = LayoutInflater.from(this).inflate(R.layout.image_bottom_sheet, findViewById(R.id.ImagebottomSheet));
        imageBottomSheet.setContentView(imageview);

        //imagebottomsheet items
        imagebottom = imageBottomSheet.findViewById(R.id.imageInSheet);

        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.show_profile_cover_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogImage = mDialog.findViewById(R.id.dialog_image);

        myViewPager = findViewById(R.id.my_profile_pager);
        tabaccessAdaptor = new userProfileTabAccessAdaptor(this.getSupportFragmentManager());
        myViewPager.setAdapter(tabaccessAdaptor);

        myTabLayout = findViewById(R.id.my_profile_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        myTabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_picture_in_picture_24);
        myTabLayout.getTabAt(1).setIcon(R.drawable.video);
        myTabLayout.getTabAt(2).setIcon(R.drawable.document);
        myTabLayout.getTabAt(3).setIcon(R.drawable.about);

        int color = Color.parseColor("#007a4a");
        int unselectedColor = Color.parseColor("#59090909");
        myTabLayout.getTabAt(0).getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        myTabLayout.getTabAt(1).getIcon().setColorFilter(unselectedColor, PorterDuff.Mode.SRC_IN);
        myTabLayout.getTabAt(2).getIcon().setColorFilter(unselectedColor, PorterDuff.Mode.SRC_IN);
        myTabLayout.getTabAt(3).getIcon().setColorFilter(unselectedColor, PorterDuff.Mode.SRC_IN);
//        myTabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_IN);

        myTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myTabLayout.getTabAt(tab.getPosition()).getIcon().setColorFilter(color,PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                myTabLayout.getTabAt(tab.getPosition()).getIcon().setColorFilter(unselectedColor, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        bottomSheetDialog = new BottomSheetDialog(userProfileActivity.this, R.style.BottomSheetStyle);

        View view = LayoutInflater.from(userProfileActivity.this).inflate(R.layout.bottom_sheet_all_profile, findViewById(R.id.sheet1));

        bottomSheetDialog.setContentView(view);

        viewProfile = bottomSheetDialog.findViewById(R.id.viewProfile);
        titleProfile = bottomSheetDialog.findViewById(R.id.titleProfile);
        block_l = bottomSheetDialog.findViewById(R.id.block_l);
        viewProfile_l = bottomSheetDialog.findViewById(R.id.viewProfile_l);

        FriendsRef.child(userid).orderByChild("Friends").equalTo("Saved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    int count = (int) snapshot.getChildrenCount();
                    f_count.setText(""+count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        menuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleProfile.setVisibility(View.GONE);
                viewProfile_l.setVisibility(View.GONE);
                block_l.setVisibility(View.VISIBLE);

                block_l.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        blockDialog.show();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Object, String> haspmap = new HashMap<>();
                haspmap.put("id", userid);
                haspmap.put("state", "1");

                blockREf.child(CurrentUserId).child(userid).setValue(haspmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        removeFromChatRequestNode(userid);

                        chatList.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (snapshot.hasChild(userid)){
                                        FriendsRef.child(CurrentUserId).child(userid).child("Friends").setValue("Blocked");
                                        FriendsRef.child(userid).child(CurrentUserId).child("Friends").setValue("BlockedBy");
                                    }
                                }
                                else {
                                    FriendsRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                if (snapshot.hasChild(userid)){
                                                    FriendsRef.child(CurrentUserId).child(userid).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        Toast.makeText(userProfileActivity.this, "Blocked", Toast.LENGTH_SHORT).show();
                    }
                });
                HashMap<Object, String> haspmap1 = new HashMap<>();
                haspmap1.put("id", CurrentUserId);
                haspmap1.put("state", "0");

                blockREf.child(userid).child(CurrentUserId).setValue(haspmap1);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockDialog.dismiss();
            }
        });


        coverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleProfile.setText("Cover Photo");
                viewProfile.setText("View Cover Photo");
                titleProfile.setVisibility(View.VISIBLE);
                block_l.setVisibility(View.GONE);
                viewProfile_l.setVisibility(View.VISIBLE);

                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage("cover");
                        Toast.makeText(userProfileActivity.this, "Cover Photo", Toast.LENGTH_SHORT).show();
                    }
                });

                bottomSheetDialog.show();

            }
        });


        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleProfile.setText("Profile Photo");
                viewProfile.setText("View Profile Photo");
                titleProfile.setVisibility(View.VISIBLE);
                block_l.setVisibility(View.GONE);
                viewProfile_l.setVisibility(View.VISIBLE);

                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage("profile");
                        Toast.makeText(userProfileActivity.this, "profile Photo", Toast.LENGTH_SHORT).show();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userProfileActivity.this , FriendsActivity.class);
                intent.putExtra("userid" , userid);
                startActivity(intent);
            }
        });

        if (!senderUserID.equals(userid)) {
            request.setVisibility(View.VISIBLE);
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SendChatRequest();
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CancelChatRequest();
                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AcceptChatRequest();
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CancelChatRequest();
                }
            });
            accepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(userProfileActivity.this);
                    builder.setTitle("Disconnect");
                    builder.setMessage("Are you sure to disconnect this network?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RemoveSpecificContact();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();

                }
            });
        }
        else {
            request.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            accepted.setVisibility(View.GONE);


        }

        ManageChatRequests();

        getUserInfo();

        whoViewedYourProfile();

        CountTimeline();

    }

    private void CountTimeline() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserPostData");
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countT = (int) snapshot.getChildrenCount();
                timeline_count.setText(""+countT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeFromChatRequestNode(String userid) {
        ChatRef.child(CurrentUserId).child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ChatRef.child(CurrentUserId).child(userid).removeValue();
                    ChatRef.child(userid).child(CurrentUserId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void whoViewedYourProfile() {
        DatabaseReference ViewedRef = FirebaseDatabase.getInstance().getReference("ViewedProfile");
        if (!userid.equals(CurrentUserId)){
            int count = 1;
            ViewedRef.child(userid).child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String VT = snapshot.child("Count").getValue().toString();
                        int ViewedTimes = Integer.parseInt(VT);
                        int c = ViewedTimes + count;
                        HashMap<Object, String> haspmap = new HashMap<>();
                        haspmap.put("Count", String.valueOf(c));
                        haspmap.put("ViewedById", CurrentUserId);
                        ViewedRef.child(userid).child(CurrentUserId).setValue(haspmap);
                    }
                    else {
                        HashMap<Object, String> haspmap = new HashMap<>();
                        haspmap.put("Count", String.valueOf(count));
                        haspmap.put("ViewedById", CurrentUserId);
                        ViewedRef.child(userid).child(CurrentUserId).setValue(haspmap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void openImage(String cover) {
        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
        userNameRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (cover.equals("cover")){
                    //  for cover pic
                    if (snapshot.child("cover_pic").exists()){
                        String user_cover = snapshot.child("cover_pic").getValue().toString();
                        Picasso.get().load(user_cover).into(imagebottom);
                        imageBottomSheet.show();

                    }
                }else
                {
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(user_Profile).into(imagebottom);
                        imageBottomSheet.show();


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo() {

        DatabaseReference infoREf = FirebaseDatabase.getInstance().getReference("UserDetails");
        infoREf.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GridModel gridModel =  snapshot.getValue(GridModel.class);
                profession = snapshot.child("profession").getValue().toString();

                if (profession.equals("Schooling")) {
                    userinfo.setText(gridModel.getUser_bio());
                    getUserName(userid);

                }
                if (profession.equals("Graduation")) {
                    userinfo.setText(gridModel.getUser_bio());
                    getUserName(userid);
                }
                if (profession.equals("Job")) {

                    userinfo.setText(gridModel.getUser_bio());
                    getUserName(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserName(String userId) {

        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
        userNameRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user_name = snapshot.child("userName").getValue().toString();
                userName.setText(user_name);

                String email = snapshot.child("userEmail").getValue().toString();

                int extra = profession.length()/2;
                String random = email.concat(user_name).concat(String.valueOf(extra));
//                for profile pic
                if (snapshot.child("profile_image").exists()) {
                    String user_Profile = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(user_Profile).into(userProfile);
                }
//                else {
//                    Picasso.get().load(R.drawable.profile).into(userProfile);
//                }

//                for cover pic
                if (snapshot.child("cover_pic").exists()) {
                    String user_cover = snapshot.child("cover_pic").getValue().toString();

                    Picasso.get().load(user_cover).into(coverPic);
                }
                else {
                    if (random.length() % 5 == 0){
                        coverPic.setBackgroundResource(R.drawable.back1);
                    }else if (random.length()% 5 ==1){
                        coverPic.setBackgroundResource(R.drawable.back2);

                    }else if (random.length()% 5==2){
                        coverPic.setBackgroundResource(R.drawable.back3);

                    }else if (random.length()% 5 == 3){
                        coverPic.setBackgroundResource(R.drawable.back4);

                    }else {
                        coverPic.setBackgroundResource(R.drawable.back5);

                    }                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ManageChatRequests() {
        ChatRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(receiverUserID)) {
                    String request_type = snapshot.child(receiverUserID).child("request_type").getValue().toString();

                    if (request_type.equals("sent")) {
                        currentState = "request_sent";

                        request.setVisibility(View.GONE);
                        send.setVisibility(View.VISIBLE);
                        accept.setVisibility(View.GONE);
                        reject.setVisibility(View.GONE);
                        accepted.setVisibility(View.GONE);


                    } else if (request_type.equals("received")) {
                        currentState = "request_received";

                        request.setVisibility(View.GONE);
                        send.setVisibility(View.GONE);
                        accept.setVisibility(View.VISIBLE);
                        reject.setVisibility(View.VISIBLE);
                        accepted.setVisibility(View.GONE);

                    } else if (request_type.equals("cancel")) {
                        currentState = "new";

                        request.setVisibility(View.VISIBLE);
                        send.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                        reject.setVisibility(View.GONE);
                        accepted.setVisibility(View.GONE);
                    }
                } else {

                    FriendsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(receiverUserID)) {
                                String f = snapshot.child(receiverUserID).child("Friends").getValue().toString();
                                if (f.equals("Saved")) {
                                    currentState = "friends";

                                    request.setVisibility(View.GONE);
                                    send.setVisibility(View.GONE);
                                    accept.setVisibility(View.GONE);
                                    reject.setVisibility(View.GONE);
                                    accepted.setVisibility(View.VISIBLE);

                                } else if (f.equals("UnFriend")) {
                                    currentState = "new";

                                    request.setVisibility(View.VISIBLE);
                                    send.setVisibility(View.GONE);
                                    accept.setVisibility(View.GONE);
                                    reject.setVisibility(View.GONE);
                                    accepted.setVisibility(View.GONE);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SendChatRequest() {
        currentState = "new";
        ChatRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    ChatRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                currentState = "request_sent";
                                ManageChatRequests();
                            }
                        }
                    });
                }

            }
        });

        addToHisNotification(receiverUserID , "" , "Sent You a Request" , senderUserID , "RequestReceived");
        sendPushNotification(receiverUserID , senderUserID , 0);
    }

    private void CancelChatRequest() {

        ChatRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    ChatRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                currentState = "new";
                                ManageChatRequests();
                                ChatRef.child(senderUserID).child(receiverUserID).child("request_type").removeValue();
                                ChatRef.child(receiverUserID).child(senderUserID).child("request_type").removeValue();
                            }
                        }
                    });
                }

            }
        });


        cancelNotification(receiverUserID , "" , "Sent You a Request" , senderUserID , "RequestReceived");
        cancelNotification(senderUserID, "", "Sent You a Request", receiverUserID, "RequestReceived");
    }

    private void AcceptChatRequest() {
        FriendsRef.child(senderUserID).child(receiverUserID).child("Friends").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                String name = snapshot.child("userName").getValue().toString();
                                FriendsRef.child(senderUserID).child(receiverUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ManageChatRequests();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FriendsRef.child(receiverUserID).child(senderUserID).child("Friends").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UsersRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            String name = snapshot.child("userName").getValue().toString();
                                            FriendsRef.child(receiverUserID).child(senderUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    ManageChatRequests();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ChatRef.child(senderUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ChatRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "friends";
                                                    ManageChatRequests();

                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });

        addToHisNotification(receiverUserID , "" , "Your Request is Accepted" , senderUserID , "RequestAccepted");
        cancelNotification(senderUserID , "" , "Sent You a Request" , receiverUserID , "RequestReceived");
        sendPushNotification(receiverUserID , senderUserID , 1);
    }

    private void RemoveSpecificContact() {
        FriendsRef.child(senderUserID).child(receiverUserID).child("Friends").setValue("UnFriend").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                String name = snapshot.child("userName").getValue().toString();
                                FriendsRef.child(senderUserID).child(receiverUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        currentState = "new";
                                        ManageChatRequests();


                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FriendsRef.child(receiverUserID).child(senderUserID).child("Friends").setValue("UnFriend").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UsersRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            String name = snapshot.child("userName").getValue().toString();
                                            FriendsRef.child(receiverUserID).child(senderUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "new";
                                                    ManageChatRequests();


                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ChatRef.child(senderUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ChatRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "new";
                                                    ManageChatRequests();

                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });

        cancelNotification(senderUserID , "" , "Sent You a Request" , receiverUserID , "RequestAccepted");
        cancelNotification(receiverUserID , "" , "Sent You a Request" , senderUserID , "RequestAccepted");

    }

    private void sendPushNotification(String receiverUserID , String currentUserId , int i) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(receiverUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String state = snapshot.child("NotifyState").getValue().toString();

                if (state.equals("1")){

                    ref.child("Users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot sp) {

                            String name = sp.child("userName").getValue().toString();

                            if (sp.child("profile_image").exists()){
                                pImage = sp.child("profile_image").getValue().toString();
                            }

                            if (i == 0)
                            {
                                body = "You received a chat request from "+name;

                            }else if (i == 1)
                            {
                                body = "Your request has been accepted by "+name;

                            }

                            JSONObject to = new JSONObject();
                            JSONObject data = new JSONObject();
                            try {
                                data.put("title", "Chat Request");
                                data.put("body", body);
                                data.put("sender", currentUserId);
                                data.put("type", "Request");
                                data.put("userProfile", pImage);

                                to.put("to", token);
                                to.put("data", data);

                                sendNotification(to);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", to, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Toast.makeText(getActivity(), "Send", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("content-type", "application/json");
                header.put("authorization", "key=" + fcmServerKey);
                return header;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    private void addToHisNotification(final String hisUid, String pId, String message, String currentUserId , String type){
        String timestamp = ""+System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();

        hashMap.put("pId",pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", currentUserId);
        hashMap.put("status", "0");
        hashMap.put("type", type);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void cancelNotification(final String hisUid, String pId, String message, String currentUserId , String type) {

        String timestamp = ""+System.currentTimeMillis();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").orderByChild("sUid").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (ds.child("type").getValue().toString().equals(type))
                    {
                        ds.getRef().removeValue();
                    }
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
}