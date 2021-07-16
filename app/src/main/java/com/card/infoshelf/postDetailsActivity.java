package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.card.infoshelf.bottomfragment.TagModel;
import com.card.infoshelf.bottomfragment.TimelineFragment;
import com.card.infoshelf.editPost.EditPostActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class postDetailsActivity extends AppCompatActivity {

    private TextView userName, postTime, fileName, like_count, postDesc, edit, delete, share, bookmark, copyUrl, done, post_views, hashtag_realted, hashtag_company, hashTag_lpa, profession;
    private ImageView download_btn, like_btn,imageSheet, share_btn, more_btn, cooment_send_neo, post_image, doc_img, fullScreen;
    EmojiconEditText add_commnet_et;
    private EditText userinput;
    EmojIconActions emojIconActions;
    ImageView emoji;
    View root_view;
    private String pId, UserId, PostURL, TextBoxData, FileType;
    private FirebaseAuth mAuth;
    private String CurrentUserId, userProfile, userProfile1, user_image;
    ProgressDialog pd;
    private PlayerView video_view;
    private CardView documentView;
    SimpleExoPlayer exoPlayer;
    private boolean mPrecessLikes = false;
    private boolean ismPrecessLikes = false;
    private RecyclerView post_comment_recycler;
    private BottomSheetDialog moreBottomSheet, sharebottomsheet,imageBottomsheet;
    private FirebaseRecyclerAdapter<modelComment, comment_viewholder> adaptor;
    private DatabaseReference commentREf, CommentLikeREf, RootRef, shareRef;
    private LinearLayout delete_l, edit_l, share_l, bookmark_l, copyUrl_l, report_post_l;
    private RecyclerView recyclerView;
    private shareBottomSheetAdapter shareAdapter;
    List<TagModel> contacts = new ArrayList<>();
    private boolean bookmarkProcess = false;
    private List<String> sendCkeckLitData;
    private ProgressBar progress_bar;
    private boolean isVideo = false;
    private ProgressDialog progressDialog;
    private final String fcmServerKey ="AAAAXb76uxw:APA91bFE4oCMF8hCE9JLM_R_aLOAT838pI0-GHtpcZPB876r8bHF_aXQ2dOb9LCCytmN_G8UOxfTZx69HZrnB1aZjEAs8etZOYmFdokaGj3azt5OKI0sO01PrKZMXTrad2UbNLEnGFlo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        pId = getIntent().getStringExtra("pId");

        setTitle("Post Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = findViewById(R.id.userName);
        postTime = findViewById(R.id.postTime);
        fileName = findViewById(R.id.fileName);
        like_count = findViewById(R.id.like_count);
        download_btn = findViewById(R.id.download_btn);
        like_btn = findViewById(R.id.like_btn);
        share_btn = findViewById(R.id.share_btn);
        more_btn = findViewById(R.id.more_btn);
        root_view= findViewById(R.id.root_view);
        emoji = findViewById(R.id.emojicon_icon);
        add_commnet_et = findViewById(R.id.add_commnet_et);
        cooment_send_neo = findViewById(R.id.cooment_send_neo);
        postDesc = findViewById(R.id.postDesc);
        post_image = findViewById(R.id.post_image);
        video_view = findViewById(R.id.video_view);
        documentView = findViewById(R.id.documentView);
        post_comment_recycler = findViewById(R.id.post_comment_recycler);
        doc_img = findViewById(R.id.doc_img);
        post_views = findViewById(R.id.post_views);
        hashtag_company = findViewById(R.id.hashtag_company);
        hashtag_realted = findViewById(R.id.hashtag_company);
        hashTag_lpa = findViewById(R.id.hashTag_lpa);
        profession = findViewById(R.id.profession);
        fullScreen = video_view.findViewById(R.id.fullScreen);
        progress_bar = findViewById(R.id.progress_bar);

        post_comment_recycler.setLayoutManager(new LinearLayoutManager(this));

        moreBottomSheet = new BottomSheetDialog(postDetailsActivity.this, R.style.BottomSheetStyle);
        sharebottomsheet = new BottomSheetDialog(postDetailsActivity.this, R.style.BottomSheetStyle);
        imageBottomsheet = new BottomSheetDialog(postDetailsActivity.this, R.style.BottomSheetStyle);
        View v = LayoutInflater.from(postDetailsActivity.this).inflate(R.layout.more_bottom_sheet, findViewById(R.id.sheet2));
        View viewShare = LayoutInflater.from(postDetailsActivity.this).inflate(R.layout.share_bottom_sheet, findViewById(R.id.sharebottomlayout));
        View viewimage = LayoutInflater.from(postDetailsActivity.this).inflate(R.layout.image_bottom_sheet, findViewById(R.id.ImagebottomSheet));
        moreBottomSheet.setContentView(v);
        sharebottomsheet.setContentView(viewShare);
        imageBottomsheet.setContentView(viewimage);

        //share sheet irtem
        recyclerView = sharebottomsheet.findViewById(R.id.shareRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        shareAdapter = new shareBottomSheetAdapter(contacts);
        recyclerView.setAdapter(shareAdapter);

        userinput = sharebottomsheet.findViewById(R.id.TypeChipsTag);
        done = sharebottomsheet.findViewById(R.id.DoneTag);

        // more bottom item
        edit_l = moreBottomSheet.findViewById(R.id.edit_l);
        delete_l = moreBottomSheet.findViewById(R.id.delete_l);
        share = moreBottomSheet.findViewById(R.id.share);
        bookmark = moreBottomSheet.findViewById(R.id.bookmark);
        delete = moreBottomSheet.findViewById(R.id.delete);
        edit = moreBottomSheet.findViewById(R.id.edit);
        share_l = moreBottomSheet.findViewById(R.id.share_l);
        bookmark_l = moreBottomSheet.findViewById(R.id.bookmark_l);
        report_post_l = moreBottomSheet.findViewById(R.id.report_post_l);

        imageSheet = imageBottomsheet.findViewById(R.id.imageInSheet);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");
        commentREf = FirebaseDatabase.getInstance().getReference("POSTFiles");
        CommentLikeREf = FirebaseDatabase.getInstance().getReference("Comment_Likes");
        RootRef = FirebaseDatabase.getInstance().getReference();
        shareRef = FirebaseDatabase.getInstance().getReference();

        emojIconActions = new EmojIconActions(this, root_view, add_commnet_et, emoji);
        emojIconActions.ShowEmojIcon();

        //set progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

        loadPostInfo();
        loadComment();
        setLikes(pId, CurrentUserId);
        sharebottomsheetDataWork();
        getPostViews();
        CountViews(pId);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCkeckLitData = shareAdapter.getArraySend();
                if (!sendCkeckLitData.isEmpty()){
                    for (int i=0; i<sendCkeckLitData.size(); i++){
                        sharePost(sendCkeckLitData.get(i));

                    }
                    sendCkeckLitData.clear();

                }
                sharebottomsheet.dismiss();

            }

        });

        postDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDesc.setMaxLines(Integer.MAX_VALUE);
            }
        });

        edit_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(postDetailsActivity.this, EditPostActivity.class);
                intent.putExtra("PostId",pId);
                intent.putExtra("UserId",UserId);
                intent.putExtra("PostUrl",PostURL);
                startActivity(intent);
            }
        });

        cooment_send_neo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(postDetailsActivity.this, userProfileActivity.class);
                intent.putExtra("userid", UserId);
                startActivity(intent);
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrecessLikes = true;
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (mPrecessLikes == true){

                            if (snapshot.child(pId).hasChild(CurrentUserId)){
                                likeRef.child(pId).child(CurrentUserId).removeValue();
                                deleteNotification(UserId, pId, CurrentUserId);
                                mPrecessLikes = false;
                            }
                            else {
                                likeRef.child(pId).child(CurrentUserId).child("uId").setValue(CurrentUserId);
                                mPrecessLikes = false;

                                if (!UserId.equals(CurrentUserId)){

                                    sendPushNotificationToUser(UserId, CurrentUserId, postDetailsActivity.this, "Liked your post", "LikeP");
                                    addToHisNotification(UserId, pId, "Liked your post", CurrentUserId, "likeP");
                                }

                                int post_like_count = (int) snapshot.child(pId).getChildrenCount();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharebottomsheet.show();
            }
        });
        share_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharebottomsheet.show();
            }
        });

        bookmark_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bookmarkProcess = true;

                DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
                bookmarkREf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (bookmarkProcess == true) {
                            if (snapshot.child(CurrentUserId).hasChild(pId)) {
                                bookmarkREf.child(CurrentUserId).child(pId).removeValue();
                                bookmarkProcess = false;
                                moreBottomSheet.dismiss();
                            } else {
                                bookmarkREf.child(CurrentUserId).child(pId).child("postId").setValue(pId);
                                bookmarkProcess = false;
                                Toast.makeText(postDetailsActivity.this, "Bookmarked", Toast.LENGTH_SHORT).show();
                                moreBottomSheet.dismiss();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void CountViews(String pId) {
        DatabaseReference postviewsRef = FirebaseDatabase.getInstance().getReference("PostViews");
        postviewsRef.child(pId).child(CurrentUserId).child("userId").setValue(CurrentUserId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
    private void sendPushNotificationToUser(String userId, String currentUserId, postDetailsActivity postDetailsActivity, String type, String notificationType) {
        DatabaseReference userREf = FirebaseDatabase.getInstance().getReference("Users");
        userREf.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String state = snapshot.child("NotifyState").getValue().toString();
                String token = snapshot.child("token").getValue().toString();
                String name = snapshot.child("userName").getValue().toString();

                if (state.equals("1")){
                    userREf.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String user_n = dataSnapshot.child("userName").getValue().toString();

                            if (dataSnapshot.child("profile_image").exists()){
                                userProfile = dataSnapshot.child("profile_image").getValue().toString();
                            }
                            String message = user_n+ " "+type;

                            JSONObject to = new JSONObject();
                            JSONObject data = new JSONObject();
                            try {
                                data.put("title", message);
                                data.put("body", TextBoxData);
                                data.put("pid", pId);
                                data.put("sender", currentUserId);
                                data.put("type", notificationType);
                                data.put("imageUrl", PostURL);
                                data.put("userProfile", userProfile);

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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    private void deleteNotification(String userId, String pId, String currentUserId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userId).child("Notifications").orderByChild("sUid").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String message = ""+ds.child("type").getValue().toString();
                    String postid = ""+ds.child("pId").getValue().toString();

                    if (postid.equals(pId) && message.equals("likeP")){
                        ds.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostViews() {
        DatabaseReference postviewsRef = FirebaseDatabase.getInstance().getReference("PostViews");
        postviewsRef.child(pId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int countViews = (int) snapshot.getChildrenCount();
                    post_views.setText(""+countViews);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sharebottomsheetDataWork() {

//        Ref = FirebaseDatabase.getInstance().getReference("Friends").child(CurrentUserId);
        contacts.clear();
        shareRef.child("Friends").child(CurrentUserId).orderByChild("Friends").equalTo("Saved").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        String id = npsnapshot.getKey();
//                        Ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
                        shareRef.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                TagModel l = snapshot.getValue(TagModel.class);
                                contacts.add(l);
                                shareAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        userinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userInput = s.toString();
                List<TagModel> newContactList = new ArrayList<>();

                for (TagModel contacts : contacts) {
                    if (contacts.getUserName().toLowerCase().contains(userInput.toLowerCase())) {
                        newContactList.add(contacts);
                    }
                }
                shareAdapter.filterList(newContactList);

            }
        });
    }

    private void postComment() {

        pd = new ProgressDialog(this);
        pd.setMessage("Adding Comment...");

        final String comment = add_commnet_et.getText().toString().trim();

        if (TextUtils.isEmpty(comment)){

            Toast.makeText(this, "Comment is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        else {

            String timestamp = String.valueOf(System.currentTimeMillis());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(pId).child("Comment");

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("cId", timestamp);
            hashMap.put("timestamp", timestamp);
            hashMap.put("comment", comment);
            hashMap.put("uId", CurrentUserId);
//        hashMap.put("comment_like", "0");
            hashMap.put("post_id", pId);

            ref.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    pd.dismiss();
                    add_commnet_et.setText("");
//                    UpdateCommentCount();

                    if (!UserId.equals(CurrentUserId)){

                        sendPushNotificationToUser(UserId, CurrentUserId, postDetailsActivity.this, "Commented on your post", "CommentP");
                        addToHisNotification1(UserId, pId, "Commented on your post", CurrentUserId, "CommentP", timestamp);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(postDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void addToHisNotification1(String hisUid, String pId, String message, String currentUserId, String type, String timestamp) {
        String timestamp1 = ""+System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();

        hashMap.put("pId",pId);
        hashMap.put("timestamp", timestamp1);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", currentUserId);
        hashMap.put("status", "0");
        hashMap.put("cid", timestamp);
        hashMap.put("type", type);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp1).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    boolean mProcessComment = false;
    private void UpdateCommentCount() {

        mProcessComment = true;

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(pId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessComment){
                    String comments = ""+ snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToHisNotification(final String hisUid, String pId, String message, String currentUserId, String type){
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

    private void loadComment() {

        FirebaseRecyclerOptions<modelComment> options = new FirebaseRecyclerOptions.Builder<modelComment>().setQuery(commentREf.child(pId).child("Comment"), modelComment.class)
                .build();
        adaptor = new FirebaseRecyclerAdapter<modelComment, comment_viewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull comment_viewholder holder, int position, @NonNull modelComment model) {

                final String uid = model.getuId();
                final String postId = model.getPost_id();
                final String cid = model.getcId();
                final String comments = model.getComment();
                String timestamp = model.getTimestamp();
                String show_post_time = holder.getFormattedDate(postDetailsActivity.this , timestamp);

                holder.getUserInfo(uid);
                holder.user_c.setText(comments);
                holder.comment_time.setText(show_post_time);

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (uid.equals(CurrentUserId) || UserId.equals(CurrentUserId)){
                            DeleteComment(postId, cid, uid);
                        }
                        else {
                            showDefaultDialog();
                        }

                        return false;
                    }
                });

                holder.userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(postDetailsActivity.this, userProfileActivity.class);
                        i.putExtra("userid", uid);
                        startActivity(i);
                    }
                });

                holder.userProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(postDetailsActivity.this, userProfileActivity.class);
                        i.putExtra("userid", uid);
                        startActivity(i);
                    }
                });

                holder.allReplies.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(postDetailsActivity.this, ReplyCommentActivity.class);
                        intent.putExtra("pid", postId);
                        intent.putExtra("uid", uid);
                        intent.putExtra("cid", cid);
                        intent.putExtra("postU_UserId", UserId);
                        startActivity(intent);
                    }
                });
                checkAllReplies(cid, postId, holder);

                holder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.reply_layout.setVisibility(View.VISIBLE);
                    }
                });

                holder.cooment_send_neo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.postReplyComment(cid, uid, postId, postDetailsActivity.this, CurrentUserId);
                    }
                });

                holder.setLikes(uid, postId, cid, CurrentUserId);

                holder.c_like_btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ismPrecessLikes = true;

                        CommentLikeREf.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (ismPrecessLikes == true){
                                    if (snapshot.child(postId).child("Comment").child(cid).hasChild(CurrentUserId)){
                                        CommentLikeREf.child(postId).child("Comment").child(cid).child(CurrentUserId).removeValue();
                                        deleteCommentLikeNotification(uid, postId);
                                        ismPrecessLikes = false;
                                    }
                                    else {
                                        CommentLikeREf.child(postId).child("Comment").child(cid).child(CurrentUserId).setValue("Liked");
                                        ismPrecessLikes = false;

                                        if (!uid.equals(CurrentUserId)){

                                            sendPushNotificationToUser1(uid, CurrentUserId, postDetailsActivity.this, "Liked your comment", "LikeC");
                                            addToHisNotification1(uid, pId, "Liked your comment", CurrentUserId, "likeC", model.getcId());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                
            }

            @NonNull
            @Override
            public comment_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
                return new comment_viewholder(v);
            }
        };
        adaptor.notifyDataSetChanged();
        adaptor.startListening();
        post_comment_recycler.setAdapter(adaptor);

    }

    private void sendPushNotificationToUser1(String uid, String currentUserId, postDetailsActivity activity, String body, String likeC) {
        DatabaseReference userREf = FirebaseDatabase.getInstance().getReference("Users");
        userREf.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String state = snapshot.child("NotifyState").getValue().toString();
                String token = snapshot.child("token").getValue().toString();
                String name = snapshot.child("userName").getValue().toString();

                if (state.equals("1")){
                    userREf.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String user_n = dataSnapshot.child("userName").getValue().toString();
                            String message = user_n+ " "+body;

                            if (dataSnapshot.child("profile_image").exists()){
                                userProfile1 = dataSnapshot.child("profile_image").getValue().toString();
                            }

                            JSONObject to = new JSONObject();
                            JSONObject data = new JSONObject();
                            try {
                                data.put("title", user_n);
                                data.put("body", message);
                                data.put("sender", currentUserId);
                                data.put("type", "ReplyC");
                                data.put("userProfile", userProfile1);

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

    private void deleteCommentLikeNotification(String uid, String postId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).child("Notifications").orderByChild("sUid").equalTo(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds1 : snapshot.getChildren()){
                    String message = ""+ds1.child("type").getValue().toString();
                    String pId = ""+ds1.child("pId").getValue().toString();

                    if (pId.equals(postId) && message.equals("likeC")){
                        ds1.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDefaultDialog() {
        Toast.makeText(this, "You are not able to delete this comment", Toast.LENGTH_SHORT).show();
    }

    private void DeleteComment(String postId, String cid, String uid) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(postDetailsActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this comment?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(postId);
                ref.child("Comment").child(cid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference cLikeRef = FirebaseDatabase.getInstance().getReference("Comment_Likes");
                        cLikeRef.child(postId).child("Comment").child(cid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleteCommentFromNotification(postId, uid, cid);
                                deleteReplyCommentAndLikeNotificationFromUser(uid, postId, cid);
                                Toast.makeText(postDetailsActivity.this, "Comment Deleted Successfully..", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(postDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteReplyCommentAndLikeNotificationFromUser(String uid, String postId, String cid) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
        reference1.child(uid).child("Notifications").orderByChild("cid").equalTo(cid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                     for (DataSnapshot ds1 : snapshot.getChildren()){
                    ds1.getRef().removeValue();
                }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteCommentFromNotification(String postId, String uid, String cid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(UserId).child("Notifications").orderByChild("cid").equalTo(cid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkAllReplies(String cid, String postId, comment_viewholder holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(postId).child("Comment").child(cid).child("Reply");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.allReplies.setVisibility(View.VISIBLE);
                    int countReplies = (int) snapshot.getChildrenCount();
                    holder.allReplies.setText(""+countReplies+" replies");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(pId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String FileName = snapshot.child("FileName").getValue().toString();
                FileType = snapshot.child("FileType").getValue().toString();
                PostURL = snapshot.child("PostURL").getValue().toString();
                TextBoxData = snapshot.child("TextBoxData").getValue().toString();
                UserId = snapshot.child("UserId").getValue().toString();
                String timeStamp = snapshot.child("timeStamp").getValue().toString();
                String C_To = snapshot.child("CompanyTo").getValue().toString();
                String R_To = snapshot.child("RelatedTo").getValue().toString();
                String ctcType = snapshot.child("ctcType").getValue().toString();
                String ctcValue = snapshot.child("ctcValue").getValue().toString();

                fullScreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(postDetailsActivity.this, openVideoActivity.class);
                        intent.putExtra("videoUrl", PostURL);
                        startActivity(intent);
                    }
                });

                hashtag_realted.setText("#"+ R_To);
                hashtag_company.setText("#"+ C_To);

                if (ctcType.equals("none")){
                    hashTag_lpa.setVisibility(View.GONE);
                }
                else {
                    hashTag_lpa.setVisibility(View.VISIBLE);
                    hashTag_lpa.setText(ctcValue+" "+ctcType);
                }

                String showPostTime = getFormateDate(getApplicationContext() , timeStamp);

                more_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        report_post_l.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(postDetailsActivity.this);
                                builder.setTitle("Delete");
                                builder.setMessage("Are you sure to report this post?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        reportPost(pId);
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

                        share_l.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                        loadShareBottomSheetdata(pId);
                                sharebottomsheet.show();
                                sharebottomsheetDataWork();
                            }
                        });

                        if (UserId.equals(CurrentUserId)){
                            delete_l.setVisibility(View.VISIBLE);
                            edit_l.setVisibility(View.VISIBLE);

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(postDetailsActivity.this);
                                    builder.setTitle("Delete");
                                    builder.setMessage("Are you sure to delete this post?");
                                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            deletePost(pId, PostURL, UserId, C_To, R_To);
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.create().show();

                                }
                            });

                            moreBottomSheet.show();
                        }
                        else {
                            delete_l.setVisibility(View.GONE);
                            edit_l.setVisibility(View.GONE);
                            moreBottomSheet.show();
                        }

                    }
                });


                if (FileType.equals("image")){
                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    Picasso.get().load(PostURL).into(post_image);
                    video_view.setVisibility(View.GONE);
                    documentView.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.GONE);
                    isVideo = false;

                }
                else if (FileType.equals("video")){
                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    post_image.setVisibility(View.GONE);
                    documentView.setVisibility(View.GONE);
                    isVideo = true;

                    // set video

                    setVideo(PostURL, getApplicationContext());

                }

                else if (FileType.equals("pdf")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
                    progress_bar.setVisibility(View.GONE);
                    isVideo = false;

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if (FileType.equals("ppt") || FileType.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||FileType.equals("vnd.ms-powerpoint")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.ppt);
                    progress_bar.setVisibility(View.GONE);
                    isVideo = false;

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if (FileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| FileType.equals("xlsx") || FileType.equals("application/vnd.ms-excel")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.excel);
                    progress_bar.setVisibility(View.GONE);
                    isVideo = false;

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if (FileType.equals("application/msword") || FileType.equals("doc") || FileType.equals("docx") || FileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.word);
                    progress_bar.setVisibility(View.GONE);
                    isVideo = false;

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if (FileType.equals("none")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    documentView.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.GONE);
                    isVideo = false;
                }
                else {
                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.zip);
                    progress_bar.setVisibility(View.GONE);
                    isVideo = false;

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }

                post_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(PostURL).into(imageSheet);
                        imageBottomsheet.show();
                    }
                });

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(UserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child("profile_image").exists()){
                            String pUrl = snapshot.child("profile_image").getValue().toString();
                        }
                        String uName = snapshot.child("userName").getValue().toString();

                        userName.setText(uName);

                        getUserDetailsData(UserId);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void reportPost(String pId) {
        String t = String.valueOf(System.currentTimeMillis());
        HashMap map = new HashMap();
        map.put("reportBy", CurrentUserId);
        map.put("postId", pId);

        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("ReportPost");
        reportRef.child(t).setValue(map);
    }

    private void getUserDetailsData(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserDetails");
        reference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("user_bio").exists()){
                    profession.setVisibility(View.VISIBLE);
                    String userBio = snapshot.child("user_bio").getValue().toString();
                    profession.setText(userBio);
                }
                else {
                    profession.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deletePost(String pId, String postURL, String userId, String c_To, String r_To) {
        if (postURL.equals("none")){
            deleteWithoutImage(pId, userId, c_To, r_To);
        }
        else {
            deleteWithImage(pId, postURL, userId, c_To, r_To);
        }
    }

    private void deleteWithImage(String pId, String postUrl, String userId, String c_To, String r_To) {

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        TextView tv = progressDialog.findViewById(R.id.tvtv);
        tv.setText("Deleting..");

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(postUrl);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference query = FirebaseDatabase.getInstance().getReference("POSTFiles");
                query.child(pId).removeValue();

                deleteDataFromPostViews(pId);
                deleteDataFromallCompanies(pId, r_To, c_To);
                deleteDataFromUserData(pId, userId);
                deleteDataFromLikes(pId);
                deleteDataFromCommentLikes(pId);
                deleteDataFromNotification(userId, pId, pd);
                deleteDataFromReplyNotification(userId, pId);
                progressDialog.dismiss();
                Intent intent = new Intent(postDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void deleteDataFromReplyNotification(String userId, String pId) {
        DatabaseReference ref6 = FirebaseDatabase.getInstance().getReference("Users");
        ref6.child(userId).child("Notifications").orderByChild("pId").equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds6 : snapshot.getChildren()) {
                        String typeP = ""+ds6.child("type").getValue().toString();
                        if (typeP.equals("CommentP")){
                            String id = ""+ds6.child("sUid").getValue().toString();

                            ref6.child(id).child("Notifications").orderByChild("pId").equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot dss : dataSnapshot.getChildren()){
                                            dss.getRef().removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteDataFromNotification(String userId, String pId, ProgressDialog pd) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference("Users");
        ref5.child(userId).child("Notifications").orderByChild("pId").equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds5 : snapshot.getChildren()){
                        ds5.getRef().removeValue();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteDataFromCommentLikes(String pId) {
        DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference("Comment_Likes");
        ref4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(pId)){
                    ref4.child(pId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteDataFromLikes(String pId) {
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Likes");
        ref3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(pId)){
                    ref3.child(pId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteDataFromUserData(String pId, String userId) {
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("UserPostData");
        ref2.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(pId)){
                    ref2.child(userId).child(pId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteDataFromallCompanies(String pId, String r_to, String c_to) {
        String c2 = c_to.toLowerCase();
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("allCompanies");
        ref1.child(r_to).child(c2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(pId)){

                    ref1.child(r_to).child(c2).child(pId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteDataFromPostViews(String pId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostViews");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(pId)){
                    ref.child(pId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteWithoutImage(String pId, String userId, String c_To, String r_To) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        TextView tv = progressDialog.findViewById(R.id.tvtv);
        tv.setText("Deleting..");

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);

        DatabaseReference query = FirebaseDatabase.getInstance().getReference("POSTFiles");
        query.child(pId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteDataFromPostViews(pId);
                deleteDataFromallCompanies(pId, r_To, c_To);
                deleteDataFromUserData(pId, userId);
                deleteDataFromLikes(pId);
                deleteDataFromCommentLikes(pId);
                deleteDataFromNotification(userId, pId, progressDialog);
                deleteDataFromReplyNotification(userId, pId);
                progressDialog.dismiss();
                Intent intent = new Intent(postDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setLikes(String pId, String userId) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(pId).hasChild(userId)){

                    int  likeCount = (int) snapshot.child(pId).getChildrenCount();
                    like_count.setText(likeCount+" ");
                    like_btn.setImageResource(R.drawable.favorite);

                }
                else {
                    int  likeCount = (int) snapshot.child(pId).getChildrenCount();
                    like_count.setText(likeCount+" ");
                    like_btn.setImageResource(R.drawable.fav);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setVideo(String postURL, Context applicationContext) {
        try {

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, trackSelector);
            Uri videouri = Uri.parse(postURL);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);

            video_view.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);

            exoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    if (playbackState == Player.STATE_BUFFERING){
                        progress_bar.setVisibility(View.VISIBLE);
                    }
                    else if (playbackState == Player.STATE_READY){
                        progress_bar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });

        } catch (Exception e) {
            // below line is used for handling our errors.
            Log.e("TAG", "Error : " + e.toString());
        }
    }

    private String getFormateDate(Context applicationContext, String postTime) {

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(Long.parseLong(postTime));

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return ""+DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }

    public class shareBottomSheetAdapter extends RecyclerView.Adapter<shareBottomSheetAdapter.MyviewHolder>{

        private List<TagModel> tagPersonList;
        private final List<String> sendList = new ArrayList<>();

        public shareBottomSheetAdapter(List<TagModel> tagPersonList) {
            this.tagPersonList = tagPersonList;
        }

        public List<String> getArraySend(){
            return sendList;
        }

        @NonNull
        @Override
        public shareBottomSheetAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_view,parent,false);
            return new MyviewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull shareBottomSheetAdapter.MyviewHolder holder, int position) {

            TagModel tagModel = tagPersonList.get(position);
            holder.PersonName.setText(tagModel.getUserName());
            String imageUri = tagModel.getProfile_image();
            Picasso.get().load(imageUri).into(holder.personimg);

            holder.checkBox.setChecked(false);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        sendList.add(tagModel.getUserId());
//                            tagModel.setChecker(true);
                    }else {
                        sendList.remove(tagModel.getUserId());
//                            tagModel.setChecker(false);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return tagPersonList.size();
        }

        public void filterList(List<TagModel> newContacts) {
            tagPersonList = newContacts;
            notifyDataSetChanged();
        }

        public class MyviewHolder extends RecyclerView.ViewHolder {

            TextView PersonName;
            LinearLayout rootView;
            CheckBox checkBox;
            ImageView personimg;

            public MyviewHolder(@NonNull View itemView) {
                super(itemView);

                PersonName = itemView.findViewById(R.id.personnameTag);
                rootView = itemView.findViewById(R.id.rootView);
                checkBox = itemView.findViewById(R.id.sharepersonBtn);
                personimg = itemView.findViewById(R.id.PersonImage);

            }
        }
    }

    private void sharePost(String to) {

        String CurrentTime, CurrentDate;
        String messageSenderRef = "Messages/" + CurrentUserId + "/" + to;
        String messageReceiverRef = "Messages/" + to + "/" + CurrentUserId;

        Calendar date = Calendar.getInstance();
        SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("dd MMM , yyyy");
        CurrentDate = CurrentDateFormat.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
        CurrentTime = CurrentTimeFormat.format(time.getTime());

        String timestamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(CurrentUserId).child(to).push();


        String messagePushID = userMessageKeyRef.getKey();


        Map messageTextBody = new HashMap();
        messageTextBody.put("message", pId);
        messageTextBody.put("type", "post");
        messageTextBody.put("from", CurrentUserId);
        messageTextBody.put("postShareType", FileType);
        messageTextBody.put("to", to);
        messageTextBody.put("messageID", messagePushID);
        messageTextBody.put("timestamp", timestamp);
        messageTextBody.put("time", CurrentTime);
        messageTextBody.put("date", CurrentDate);
        messageTextBody.put("isSeen", "0");

        Map messageTextBody1 = new HashMap();
        messageTextBody1.put("message", pId);
        messageTextBody1.put("type", "post");
        messageTextBody1.put("from", CurrentUserId);
        messageTextBody.put("postShareType", FileType);
        messageTextBody1.put("to", to);
        messageTextBody1.put("messageID", messagePushID);
        messageTextBody1.put("timestamp", timestamp);
        messageTextBody1.put("time", CurrentTime);
        messageTextBody1.put("date", CurrentDate);
        messageTextBody1.put("isSeen", "1");

        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody1);


        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    UpdateChatList(timestamp, CurrentUserId, to);

                    sendPushNotificationOfSahrePost(to);
//                    shareCount = 1;
                } else {

//                    Toast.makeText(getActivity(), "Error....", Toast.LENGTH_SHORT).show();
                }

            }
        });

        UpdateChatListAndLastMessage("Shared a Post" , to);

    }

    private void UpdateChatListAndLastMessage(String shared_a_post, String data){
        String CurrentTime  , MessageText;
        String timestamp = String.valueOf(System.currentTimeMillis());
        MessageText = data;
        Calendar time = Calendar.getInstance();
        SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
        CurrentTime = CurrentTimeFormat.format(time.getTime());

        HashMap map = new HashMap();
        map.put("timestamp", timestamp);
        map.put("lastMessag", data);
        map.put("sender", CurrentUserId);
        map.put("receiver", data);
        map.put("isSeen", "0");
        map.put("count", "0");

        RootRef.child("LastMessage").child(data).child(CurrentUserId).setValue(map);
        updateMessageCountReceiver(data);


        HashMap map1 = new HashMap();
        map1.put("timestamp", timestamp);
        map1.put("lastMessag", data);
        map1.put("sender", CurrentUserId);
        map1.put("receiver", data);
        map1.put("isSeen", "1");
        map1.put("count", "0");

        RootRef.child("LastMessage").child(CurrentUserId).child(data).setValue(map1);
        updateMessageCountSender(data);

    }

    private void updateMessageCountSender(String data) {
        RootRef.child("Messages").child(data).child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RootRef.child("Messages").child(data).child(CurrentUserId).orderByChild("to").equalTo(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                RootRef.child("Messages").child(data).child(CurrentUserId).orderByChild("isSeen").equalTo("0").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        long count = s.getChildrenCount();
                                        String c = String.valueOf(count);
                                        RootRef.child("LastMessage").child(CurrentUserId).child(data).child("count").setValue(c);


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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateMessageCountReceiver(String data) {
        RootRef.child("Messages").child(CurrentUserId).child(data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RootRef.child("Messages").child(CurrentUserId).child(data).orderByChild("to").equalTo(data).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                RootRef.child("Messages").child(CurrentUserId).child(data).orderByChild("isSeen").equalTo("0").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        long count = s.getChildrenCount();
                                        String c = String.valueOf(count);
                                        RootRef.child("LastMessage").child(data).child(CurrentUserId).child("count").setValue(c);


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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateChatList(String timestamp, String sender, String receiver) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatList");
        HashMap map = new HashMap();
        map.put("userId", receiver);
        map.put("time", timestamp);
        ref.child(sender).child(receiver).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap map1 = new HashMap();
                map1.put("userId", sender);
                map1.put("time", timestamp);

                ref.child(receiver).child(sender).setValue(map1);
            }
        });
    }

    private void sendPushNotificationOfSahrePost(String to) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(to).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String state = snapshot.child("NotifyState").getValue().toString();

                if (state.equals("1")){

                    reference.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String user_name = dataSnapshot.child("userName").getValue().toString();

                            if (dataSnapshot.child("profile_image").exists()) {
                                user_image = dataSnapshot.child("profile_image").getValue().toString();
                            }

                            String message = user_name + " shared a post with you";

//                        Toast.makeText(getActivity(), ""+token, Toast.LENGTH_SHORT).show();

                            JSONObject to = new JSONObject();
                            JSONObject data = new JSONObject();

                            try {
                                data.put("title", user_name);
                                data.put("body", message);
                                data.put("sender", CurrentUserId);
                                data.put("type", "sharedPost");
                                data.put("imageUrl", PostURL);
                                data.put("userProfile", user_image);


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

        if (isVideo == true){
            exoPlayer.setPlayWhenReady(false);

            exoPlayer.getPlaybackState();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (isVideo == true){
            exoPlayer.setPlayWhenReady(true);

            exoPlayer.getPlaybackState();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}