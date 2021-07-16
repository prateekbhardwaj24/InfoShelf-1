package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyCommentActivity extends AppCompatActivity {

    private RecyclerView reply_c_recycler;
    private CircleImageView userProfile;
    private TextView userName, user_c, comment_time, c_like_count, reply, allReplies;
    private ImageView c_like_btn, cooment_send_neo;
    private String uid, pid, cid, postU_UserId;
    private LinearLayout reply_layout;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private EditText reply_et;
    private ProgressDialog pd;
    private boolean ismPrecessLikes = false;
    private DatabaseReference CommentLikeREf,commentREf;
    private FirebaseRecyclerAdapter<modelComment, reply_comments_viewholder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_comment);

        uid = getIntent().getStringExtra("uid");
        cid = getIntent().getStringExtra("cid");
        pid = getIntent().getStringExtra("pid");
        postU_UserId = getIntent().getStringExtra("postU_UserId");

        userProfile = findViewById(R.id.userProfile);
        userName = findViewById(R.id.userName);
        user_c = findViewById(R.id.user_c);
        comment_time = findViewById(R.id.comment_time);
        c_like_count = findViewById(R.id.c_like_count);
        reply = findViewById(R.id.reply);
        c_like_btn = findViewById(R.id.c_like_btn);
        reply_c_recycler = findViewById(R.id.reply_c_recycler);
        reply_layout = findViewById(R.id.reply_layout);
        allReplies = findViewById(R.id.allReplies);
        cooment_send_neo = findViewById(R.id.cooment_send_neo);
        reply_et = findViewById(R.id.reply_et);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        CommentLikeREf = FirebaseDatabase.getInstance().getReference("Comment_Likes");
        commentREf = FirebaseDatabase.getInstance().getReference("POSTFiles");

        reply_c_recycler.setLayoutManager(new LinearLayoutManager(this));


        getUserInfo();
        getCommentsInfo();
        checkAllReplies();
        LoadReplyComments();

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReplyCommentActivity.this, userProfileActivity.class);
                i.putExtra("userid", uid);
                startActivity(i);
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReplyCommentActivity.this, userProfileActivity.class);
                i.putExtra("userid", uid);
                startActivity(i);
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply_layout.setVisibility(View.VISIBLE);
            }
        });

        cooment_send_neo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReplyComment();
            }
        });
        setLikes();
        c_like_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ismPrecessLikes = true;

                CommentLikeREf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (ismPrecessLikes == true){
                            if (snapshot.child(pid).child("Comment").child(cid).hasChild(CurrentUserId)){
                                CommentLikeREf.child(pid).child("Comment").child(cid).child(CurrentUserId).removeValue();
                                deleteCommentLikeNotification(uid, pid);
                                ismPrecessLikes = false;
                            }
                            else {
                                CommentLikeREf.child(pid).child("Comment").child(cid).child(CurrentUserId).setValue("Liked");
                                ismPrecessLikes = false;

                                if (!uid.equals(CurrentUserId)){

                                    sendPushNotificationToUser(uid, CurrentUserId, ReplyCommentActivity.this, "Liked your comment");
                                    addToHisNotification(uid, pid, "Liked your comment", CurrentUserId, "likeC", cid);
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

    private void sendPushNotificationToUser(String uid, String currentUserId, ReplyCommentActivity replyCommentActivity, String type) {
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
                            String message = user_n+ " "+type;

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

    private void deleteCommentLikeNotification(String uid, String pid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).child("Notifications").orderByChild("sUid").equalTo(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds1 : snapshot.getChildren()){
                    String message = ""+ds1.child("type").getValue().toString();
                    String pId = ""+ds1.child("pId").getValue().toString();

                    if (pId.equals(pid) && message.equals("likeC")){
                        ds1.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToHisNotification(String hisUid, String pid, String message, String currentUserId, String type, String cid) {
        String timestamp = ""+System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();

        hashMap.put("pId",pid);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", currentUserId);
        hashMap.put("status", "0");
        hashMap.put("cid", cid);
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

    private void LoadReplyComments() {
        FirebaseRecyclerOptions<modelComment> options = new FirebaseRecyclerOptions.Builder<modelComment>().setQuery(commentREf.child(pid).child("Comment").child(cid).child("Reply"), modelComment.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<modelComment, reply_comments_viewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull reply_comments_viewholder holder, int position, @NonNull modelComment model) {

                final String userId = model.getuId();
                final String postId = model.getPost_id();
                final String cRId = model.getTimestamp();
                final String comments = model.getComment();
                String timestamp = model.getTimestamp();
                String show_post_time = holder.getFormattedDate(ReplyCommentActivity.this , timestamp);

                holder.getUserInfo(userId);
                holder.user_c.setText(comments);
                holder.comment_time.setText(show_post_time);

                holder.userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ReplyCommentActivity.this, userProfileActivity.class);
                        intent.putExtra("userid", model.getuId());
                        startActivity(intent);
                    }
                });

                holder.userProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ReplyCommentActivity.this, userProfileActivity.class);
                        intent.putExtra("userid", model.getuId());
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (userId.equals(CurrentUserId) || uid.equals(CurrentUserId) || postU_UserId.equals(CurrentUserId)){
                            DeleteReplyComment(postId, cid, uid, cRId);
                        }
                        else {
                            showDefaultDialog();
                        }

                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public reply_comments_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_comment_layout, parent, false);
                return new reply_comments_viewholder(v);
            }
        };
        adapter.notifyDataSetChanged();
        adapter.startListening();
        reply_c_recycler.setAdapter(adapter);
    }

    private void showDefaultDialog() {
        Toast.makeText(this, "Yor Are not able to delete this comment", Toast.LENGTH_SHORT).show();
    }

    private void DeleteReplyComment(String postId, String cid, String uid, String cRId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ReplyCommentActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this comment?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(postId);
                ref.child("Comment").child(cid).child("Reply").child(cRId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("postData", "pid"+postId +" cid"+ cid + " rid"+ cRId);
                        deleteReplyCommentNotification(postId, uid, cRId);
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteReplyCommentNotification(String postId, String uid, String cRId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Notifications").orderByChild("rid").equalTo(cRId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void setLikes() {

        DatabaseReference CommentLikeREf = FirebaseDatabase.getInstance().getReference("Comment_Likes");
        CommentLikeREf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(pid).child("Comment").child(cid).hasChild(CurrentUserId)){
                    int commentLikeCount = (int) snapshot.child(pid).child("Comment").child(cid).getChildrenCount();
                    c_like_count.setText(commentLikeCount+" ");
                    c_like_btn.setImageResource(R.drawable.favorite);

                }
                else {
                    int commentLikeCount = (int) snapshot.child(pid).child("Comment").child(cid).getChildrenCount();
                    c_like_count.setText(commentLikeCount+" ");
//                    holder.comment_like.setVisibility(View.VISIBLE);
                    c_like_btn.setImageResource(R.drawable.fav);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postReplyComment() {
        pd = new ProgressDialog(ReplyCommentActivity.this);
        pd.setMessage("Adding Comment...");

        String comment = reply_et.getText().toString().trim();

        if (TextUtils.isEmpty(comment)){

            Toast.makeText(ReplyCommentActivity.this, "Comment is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            String timestamp = String.valueOf(System.currentTimeMillis());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(pid).child("Comment").child(cid).child("Reply");

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("cId", cid);
            hashMap.put("timestamp", timestamp);
            hashMap.put("comment", comment);
            hashMap.put("uId", CurrentUserId);
            hashMap.put("rId", timestamp);
            hashMap.put("post_id", pid);

            ref.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    pd.dismiss();
                    Toast.makeText(ReplyCommentActivity.this, "Comment Added Successfully..", Toast.LENGTH_SHORT).show();
                    reply_et.setText("");
                    reply_layout.setVisibility(View.GONE);

                    if (!uid.equals(CurrentUserId)){

                        sendPushNotificationToUser(uid, CurrentUserId, ReplyCommentActivity.this, "Reply on your comment");

                        addToHisNotification(uid, pid, "Reply on your Comment", CurrentUserId, "ReplyC", cid);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(ReplyCommentActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void checkAllReplies() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(pid).child("Comment").child(cid).child("Reply");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    allReplies.setVisibility(View.VISIBLE);
                    int countReplies = (int) snapshot.getChildrenCount();
                    allReplies.setText(""+countReplies+" replies");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCommentsInfo() {
        DatabaseReference commentsREf = FirebaseDatabase.getInstance().getReference("POSTFiles");
        commentsREf.child(pid).child("Comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String comments = snapshot.child(cid).child("comment").getValue().toString();
                    String time = snapshot.child(cid).child("timestamp").getValue().toString();

                    String show_post_time = getFormattedDate(ReplyCommentActivity.this , time);
                    user_c.setText(comments);
                    comment_time.setText(show_post_time);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getFormattedDate(ReplyCommentActivity replyCommentActivity, String time) {

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(Long.parseLong(time));

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }

    private void getUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = snapshot.child("userName").getValue().toString();
                if (snapshot.child("profile_image").exists()){
                    String pUrl = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(pUrl).into(userProfile);

                }
                userName.setText(uName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}