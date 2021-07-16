package com.card.infoshelf;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class comment_viewholder extends RecyclerView.ViewHolder {

    CircleImageView userProfile;
    TextView userName, user_c, c_like_count, reply, comment_time, allReplies;
    ImageView c_like_btn, cooment_send_neo, delete_c;
    LinearLayout reply_layout;
    EditText reply_et;
    ProgressDialog pd;
    private String userProfile1;
    private final String fcmServerKey ="AAAAXb76uxw:APA91bFE4oCMF8hCE9JLM_R_aLOAT838pI0-GHtpcZPB876r8bHF_aXQ2dOb9LCCytmN_G8UOxfTZx69HZrnB1aZjEAs8etZOYmFdokaGj3azt5OKI0sO01PrKZMXTrad2UbNLEnGFlo";

    public comment_viewholder(@NonNull View itemView) {
        super(itemView);

        userProfile = itemView.findViewById(R.id.userProfile);
        userName = itemView.findViewById(R.id.userName);
        c_like_btn = itemView.findViewById(R.id.c_like_btn);
        user_c = itemView.findViewById(R.id.user_c);
        c_like_count = itemView.findViewById(R.id.c_like_count);
        reply = itemView.findViewById(R.id.reply);
        comment_time = itemView.findViewById(R.id.comment_time);
        reply_layout = itemView.findViewById(R.id.reply_layout);
        reply_et = itemView.findViewById(R.id.reply_et);
        cooment_send_neo = itemView.findViewById(R.id.cooment_send_neo);
        allReplies = itemView.findViewById(R.id.allReplies);
        delete_c = itemView.findViewById(R.id.delete_c);
    }

    public String getFormattedDate(postDetailsActivity postDetailsActivity, String timestamp) {

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(Long.parseLong(timestamp));

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

    public void getUserInfo(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = snapshot.child("userName").getValue().toString();
                if (snapshot.child("profile_image").exists()){
                    String pUrl = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(pUrl).into(userProfile);
                }
                else {
                    userProfile.setImageResource(R.drawable.profile);
                }
                userName.setText(uName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setLikes(String uid, String postId, String cid, String currentUserId) {

        DatabaseReference CommentLikeREf = FirebaseDatabase.getInstance().getReference("Comment_Likes");

        CommentLikeREf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).child("Comment").child(cid).hasChild(currentUserId)){
                    int commentLikeCount = (int) snapshot.child(postId).child("Comment").child(cid).getChildrenCount();
                    c_like_count.setText(commentLikeCount+" ");
                    c_like_btn.setImageResource(R.drawable.favorite);

                }
                else {
                    int commentLikeCount = (int) snapshot.child(postId).child("Comment").child(cid).getChildrenCount();
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

    public void postReplyComment(String cid, String uid, String postId, postDetailsActivity postDetailsActivity, String currentUserId) {

        pd = new ProgressDialog(postDetailsActivity);
        pd.setMessage("Adding Comment...");

        String comment = reply_et.getText().toString().trim();

        if (TextUtils.isEmpty(comment)){

            Toast.makeText(postDetailsActivity, "Comment is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            String timestamp = String.valueOf(System.currentTimeMillis());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(postId).child("Comment").child(cid).child("Reply");

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("cId", cid);
            hashMap.put("timestamp", timestamp);
            hashMap.put("comment", comment);
            hashMap.put("uId", currentUserId);
            hashMap.put("rId", timestamp);
            hashMap.put("post_id", postId);

            ref.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    pd.dismiss();
                    Toast.makeText(postDetailsActivity, "Comment Added Successfully..", Toast.LENGTH_SHORT).show();
                    reply_et.setText("");
                    reply_layout.setVisibility(View.GONE);

                    if (!uid.equals(currentUserId)){

                        sendPushNotificationToUser(uid, currentUserId, postDetailsActivity, "Reply on your comment");

                        addToHisNotification(uid, postId, "Reply on your comment", currentUserId, "ReplyC", cid, timestamp);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(postDetailsActivity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void sendPushNotificationToUser(String uid, String currentUserId, postDetailsActivity activity, String reply_on_your_comment) {
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
                            String message = user_n+ " "+reply_on_your_comment;

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

                                sendNotification(to, activity);

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


    private void sendNotification(JSONObject to, postDetailsActivity activity) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", to, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Toast.makeText(, "Send", Toast.LENGTH_SHORT).show();
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

        RequestQueue queue = Volley.newRequestQueue(activity);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private void addToHisNotification(final String hisUid, String pId, String message, String currentUserId, String type, String cid, String timestamp){
        String timestamp1 = ""+System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();

        hashMap.put("pId",pId);
        hashMap.put("timestamp", timestamp1);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", currentUserId);
        hashMap.put("status", "0");
        hashMap.put("cid", cid);
        hashMap.put("rid", timestamp);
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
}
