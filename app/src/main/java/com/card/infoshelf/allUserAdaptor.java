package com.card.infoshelf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.card.infoshelf.bottomfragment.networkModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class allUserAdaptor extends RecyclerView.Adapter<allUserAdaptor.MyviewHolder>{

    private final Context context;
    private ArrayList<networkModel> list;
    private DatabaseReference UsersRef , ChatRef , FriendsRef , PostRef;
    private FirebaseAuth mAuth;
    private String currentState , senderUserID  , receiverUserID, pImage, body;
    private static final String fcmServerKey ="AAAAXb76uxw:APA91bFE4oCMF8hCE9JLM_R_aLOAT838pI0-GHtpcZPB876r8bHF_aXQ2dOb9LCCytmN_G8UOxfTZx69HZrnB1aZjEAs8etZOYmFdokaGj3azt5OKI0sO01PrKZMXTrad2UbNLEnGFlo";

    public allUserAdaptor(Context context, ArrayList<networkModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public allUserAdaptor.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.alluserlayout,parent,false);

        return new allUserAdaptor.MyviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull allUserAdaptor.MyviewHolder holder, int position) {

        networkModel user = list.get(position);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();
        receiverUserID = user.getUserId();
        currentState = "new";

        String userId = user.getUserId();
        String prof  = user.getProfession();

        holder.getUserInfo(prof, userId, user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, userProfileActivity.class);
                intent.putExtra("userid", userId);
                context.startActivity(intent);
            }
        });
        if (!senderUserID.equals(userId))
        {
            holder.request.setVisibility(View.VISIBLE);
            holder.request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String id = user.getUserId();
                    SendChatRequest(holder , id);
                }
            });
            holder.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = user.getUserId();
                    CancelChatRequest(holder , id , position);
                }
            });
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = user.getUserId();
                    AcceptChatRequest(holder , id);
                }
            });
            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = user.getUserId();
                    CancelChatRequest(holder , id, position);
                }
            });
            holder.accepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = user.getUserId();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Disconnect");
                    builder.setMessage("Are you sure to disconnect this network?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RemoveSpecificContact(holder , id);
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
        else
        {
            holder.request.setVisibility(View.GONE);
            holder.send.setVisibility(View.GONE);
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            holder.accepted.setVisibility(View.GONE);

        }

        ManageChatRequests(holder , userId);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(ArrayList<networkModel> filterList) {
        list = filterList;
        notifyDataSetChanged();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView userInfo;
        private final CircleImageView userProfile;
        private final ImageView request;
        private final ImageView accept;
        private final ImageView reject;
        private final ImageView send;
        private final ImageView accepted;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            userInfo = itemView.findViewById(R.id.userInfo);
            userProfile = itemView.findViewById(R.id.userProfile);
            request = itemView.findViewById(R.id.request);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
            send = itemView.findViewById(R.id.send);
            accepted = itemView.findViewById(R.id.accepted);
        }

        public void getUserInfo(String prof, String userId, networkModel user) {

            DatabaseReference infoREf = FirebaseDatabase.getInstance().getReference("UserDetails");
            infoREf.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String profession = snapshot.child("user_bio").getValue().toString();
                    userInfo.setText(profession);
                    getUserName(user.getUserId(), user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        public void getUserName(String userId, networkModel user) {

            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
            userNameRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String user_name = snapshot.child("userName").getValue().toString();
                    user.setUserName(user_name);
                    username.setText(user.getUserName());

                    //                for profile pic
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        user.setProfile_image(user_Profile);
                        Picasso.get().load(user.getProfile_image()).into(userProfile);
                    }
                    else {
                        userProfile.setImageResource(R.drawable.profile);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void ManageChatRequests(allUserAdaptor.MyviewHolder holder, String userId)
    {
        ChatRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId))
                {
                    String request_type = snapshot.child(userId).child("request_type").getValue().toString();

                    if (request_type.equals("sent"))
                    {
                        currentState = "request_sent";

                        holder.request.setVisibility(View.GONE);
                        holder.send.setVisibility(View.VISIBLE);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.GONE);
                        holder.accepted.setVisibility(View.GONE);


                    }
                    else if (request_type.equals("received"))
                    {
                        currentState = "request_received";

                        holder.request.setVisibility(View.GONE);
                        holder.send.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.reject.setVisibility(View.VISIBLE);
                        holder.accepted.setVisibility(View.GONE);

                    }
                    else if (request_type.equals("cancel"))
                    {
                        currentState = "new";

                        holder.request.setVisibility(View.VISIBLE);
                        holder.send.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.GONE);
                        holder.accepted.setVisibility(View.GONE);
                    }
                }
                else {

                    FriendsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(userId))
                            {
                                String f = snapshot.child(userId).child("Friends").getValue().toString();
                                if (f.equals("Saved"))
                                {
                                    currentState = "friends";

                                    holder.request.setVisibility(View.GONE);
                                    holder.send.setVisibility(View.GONE);
                                    holder.accept.setVisibility(View.GONE);
                                    holder.reject.setVisibility(View.GONE);
                                    holder.accepted.setVisibility(View.VISIBLE);

                                }
                                else if (f.equals("UnFriend"))
                                {
                                    currentState = "new";

                                    holder.request.setVisibility(View.VISIBLE);
                                    holder.send.setVisibility(View.GONE);
                                    holder.accept.setVisibility(View.GONE);
                                    holder.reject.setVisibility(View.GONE);
                                    holder.accepted.setVisibility(View.GONE);
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
    private void SendChatRequest(allUserAdaptor.MyviewHolder holder, String id)
    {
        currentState = "new";
        ChatRef.child(senderUserID).child(id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    ChatRef.child(id).child(senderUserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                currentState = "request_sent";
                                ManageChatRequests(holder , id);
                            }
                        }
                    });
                }

            }
        });


        addToHisNotification(id , "" , "Sent You a Request" , senderUserID , "RequestReceived");
        sendPushNotification(id , senderUserID , 0);


    }

    private void CancelChatRequest(allUserAdaptor.MyviewHolder holder, String id, int position)
    {
        ChatRef.child(senderUserID).child(id).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    ChatRef.child(id).child(senderUserID).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                currentState = "new";
                                ManageChatRequests(holder , id);
                                ChatRef.child(senderUserID).child(id).child("request_type").removeValue();
                                ChatRef.child(id).child(senderUserID).child("request_type").removeValue();
                            }
                        }
                    });
                }

            }
        });

        cancelNotification(id , "" , "Sent You a Request" , senderUserID , "RequestReceived");
        cancelNotification(senderUserID, "", "Sent You a Request", id, "RequestReceived");

    }


    private void AcceptChatRequest(allUserAdaptor.MyviewHolder holder  , String id)
    {
        FriendsRef.child(senderUserID).child(id).child("Friends").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    UsersRef.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists())
                            {
                                String name = snapshot.child("userName").getValue().toString();
                                FriendsRef.child(senderUserID).child(id).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ManageChatRequests(holder , id);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FriendsRef.child(id).child(senderUserID).child("Friends").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                UsersRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists())
                                        {
                                            String name = snapshot.child("userName").getValue().toString();
                                            FriendsRef.child(id).child(senderUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    ManageChatRequests(holder , id);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ChatRef.child(senderUserID).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            ChatRef.child(id).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "friends";
                                                    ManageChatRequests(holder , id);

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

        addToHisNotification(id , "" , "Your Request is Accepted" , senderUserID , "RequestAccepted");
        cancelNotification(senderUserID , "" , "Sent You a Request" , id , "RequestReceived");
        sendPushNotification(id , senderUserID , 1);

    }
    private void RemoveSpecificContact(allUserAdaptor.MyviewHolder holder  , String id)
    {
        FriendsRef.child(senderUserID).child(id).child("Friends").setValue("UnFriend").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    UsersRef.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists())
                            {
                                String name = snapshot.child("userName").getValue().toString();
                                FriendsRef.child(senderUserID).child(id).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        currentState = "new";
                                        ManageChatRequests(holder , id);


                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FriendsRef.child(id).child(senderUserID).child("Friends").setValue("UnFriend").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                UsersRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists())
                                        {
                                            String name = snapshot.child("userName").getValue().toString();
                                            FriendsRef.child(id).child(senderUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "new";
                                                    ManageChatRequests(holder , id);



                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ChatRef.child(senderUserID).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            ChatRef.child(id).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "new";
                                                    ManageChatRequests(holder , id);

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

        cancelNotification(senderUserID , "" , "Sent You a Request" , id , "RequestAccepted");
        cancelNotification(id , "" , "Sent You a Request" , senderUserID , "RequestAccepted");

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

        RequestQueue queue = Volley.newRequestQueue(context);
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
}
