package com.card.infoshelf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.card.infoshelf.bottomfragment.nearByModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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

public class allNearByUserAdapter extends RecyclerView.Adapter<allNearByUserAdapter.Myviewholder> {

    private final Context context;
    private ArrayList<nearByModel> list;
    private DatabaseReference UsersRef , ChatRef , FriendsRef , PostRef;
    private FirebaseAuth mAuth;
    private String currentState , senderUserID  , receiverUserID, pImage, body;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final String fcmServerKey ="AAAAXb76uxw:APA91bFE4oCMF8hCE9JLM_R_aLOAT838pI0-GHtpcZPB876r8bHF_aXQ2dOb9LCCytmN_G8UOxfTZx69HZrnB1aZjEAs8etZOYmFdokaGj3azt5OKI0sO01PrKZMXTrad2UbNLEnGFlo";

    public allNearByUserAdapter(Context context, ArrayList<nearByModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.alluserlayout,parent,false);
        return new Myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder holder, int position) {

        nearByModel user = list.get(position);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();
        receiverUserID = user.getUserId();
        currentState = "new";

        String userId = user.getUserId();

        holder.username.setText(user.getUserName());
        holder.getUserInfo(userId, user);
        getUserLocation(user.getLatitude(), user.getLongitude(), holder);

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

    private void getUserLocation(double latitude, double longitude, Myviewholder holder) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(context, latitude, longitude, holder);

        } else {
//            ActivityCompat.requestPermissions(SendCurrentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(Context context, double latitude, double longitude, Myviewholder holder) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1);

                    LocationCallback locationCallback = new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {

                            Location location1 = locationResult.getLastLocation();

                            double mlatitude = location1.getLatitude();
                            double mlongitude = location1.getLongitude();

                            float[] results = new float[1];
                            Location.distanceBetween(mlatitude, mlongitude, latitude, longitude, results);
                            float distance = results[0];
                            String text = String.format("%.2f", distance);
                            float meter = Float.parseFloat(text);
                            float km = meter/1000;
                            String distanceKm = String.format("%.2f", km);
                            holder.userInfo.setText(""+distanceKm+" km"+" away");

                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                }
            });
        }else {
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }

    private void ManageChatRequests(Myviewholder holder, String userId) {
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

    private void RemoveSpecificContact(Myviewholder holder, String id) {
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

    private void AcceptChatRequest(Myviewholder holder, String id) {
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

    private void CancelChatRequest(Myviewholder holder, String id, int position) {
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

    private void SendChatRequest(Myviewholder holder, String id) {
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(ArrayList<nearByModel> filterList) {
        list = filterList;
        notifyDataSetChanged();
    }

    public class Myviewholder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView userInfo;
        private final CircleImageView userProfile;
        private final ImageView request;
        private final ImageView accept;
        private final ImageView reject;
        private final ImageView send;
        private final ImageView accepted;

        public Myviewholder(@NonNull View itemView) {
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

        public void getUserInfo(String userId, nearByModel user) {
            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
            userNameRef.child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //                for profile pic
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
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
}
