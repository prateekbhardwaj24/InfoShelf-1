package com.card.infoshelf.Messenger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.card.infoshelf.Messaging.MessageModel;
import com.card.infoshelf.Messaging.MessagingActivity;
import com.card.infoshelf.R;
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
import com.squareup.picasso.Picasso;

import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessengerAdaptor extends RecyclerView.Adapter<MessengerAdaptor.ChatListHolder> {

    private final Context context;
    private ArrayList<networkModel> list;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private String l_msg, l_time;


    public MessengerAdaptor(Context context, ArrayList<networkModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);
        return new ChatListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListHolder holder, int position) {

        networkModel user = list.get(position);
        String userId = user.getUserId();
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        holder.getUserName(userId, user);
        holder.getLastMessage(userId , position, user);
        holder.CountUnReadMessages(position, user, userId, holder, CurrentUserId);

//        getLastMessage(holder, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference  ref = FirebaseDatabase.getInstance().getReference();
                ref.child("Friends").child(CurrentUserId).child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String state = snapshot.child("Friends").getValue().toString();
                        Intent intent = new Intent(context, MessagingActivity.class);
                        intent.putExtra("userid", userId);
                        intent.putExtra("state", state);
                        context.startActivity(intent);

                        MessengerActivity.progressDialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        MessengerActivity.progressDialog1.setCanceledOnTouchOutside(false);


                        MessengerActivity.progressDialog1.show();
                        MessengerActivity.progressDialog1.setContentView(R.layout.progress_dialog);

                        ProgressBar progressBar1 = MessengerActivity.progressDialog1.findViewById(R.id.spin_kit);
                        Sprite doubleBounce1 = new Wave();
                        progressBar1.setIndeterminateDrawable(doubleBounce1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        CountUnReadMessages(position, user, userId, holder, CurrentUserId);

    }

    private void CountUnReadMessages(int position, networkModel user, String userId, ChatListHolder holder, String currentUserId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Messages").child(userId).child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ref.child("Messages").child(userId).child(CurrentUserId).orderByChild("to").equalTo(CurrentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ref.child("Messages").child(userId).child(currentUserId).orderByChild("isSeen").equalTo("0").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        long count = snapshot.getChildrenCount();
                                        String c = String.valueOf(count);
                                        if (c.equals("0")) {
                                            holder.tv_count.setVisibility(View.GONE);
                                        } else {
                                            holder.tv_count.setVisibility(View.VISIBLE);
                                            holder.tv_count.setText(count + "");
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

    public class ChatListHolder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView lastMessage;
        private final TextView time;
        private final TextView tv_count;
        private final CircleImageView userProfile;
        private final CircleImageView status;

        public ChatListHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            time = itemView.findViewById(R.id.tv_time);
            userProfile = itemView.findViewById(R.id.userProfile);
            status = itemView.findViewById(R.id.status);
            tv_count = itemView.findViewById(R.id.tv_count);
        }

        public void getUserName(String userId, networkModel user) {

            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
            userNameRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String user_name = snapshot.child("userName").getValue().toString();
                        String s = snapshot.child("status").getValue().toString();
                        user.setUserName(user_name);
                        username.setText(user.getUserName());

                        if (snapshot.child("profile_image").exists()) {
                            String user_Profile = snapshot.child("profile_image").getValue().toString();
                            user.setProfile_image(user_Profile);
                            Picasso.get().load(user.getProfile_image()).placeholder(R.drawable.def_user).into(userProfile);
                        } else {
                            Picasso.get().load(R.drawable.def_user).placeholder(R.drawable.def_user).into(userProfile);
                        }
                        if (s.equals("online")) {
                            status.setVisibility(View.VISIBLE);
                        } else {
                            status.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        public void getLastMessage(String userId, int position , networkModel m) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(CurrentUserId).child(m.getUserId());
            ref.limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren())
                    {

                        String key = ds.getKey();
                       ref.child(key).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if (snapshot.exists())
                               {
                                   String type =snapshot.child("type").getValue().toString();
                                   String msg = snapshot.child("message").getValue().toString();
                                   String msgTime = snapshot.child("timestamp").getValue().toString();
                                   m.setMessage(msg);
                                   String showPostTime = getFormateDate(context, msgTime);
                                   time.setText(showPostTime);
                                   if (type.equals("text"))
                                   {
                                       lastMessage.setText(m.getMessage());
                                   }
                                   else if (type.equals("image"))
                                   {
                                       lastMessage.setText("Send a Image");
                                   }
                                   else if (type.equals("doc"))
                                   {
                                       lastMessage.setText("Send a Document");
                                   }
                                   else if (type.equals("post"))
                                   {
                                       lastMessage.setText("Shared a Post");
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
        private String getFormateDate(Context context, String msgTime) {
            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(Long.parseLong(msgTime));

            Calendar now = Calendar.getInstance();

            final String timeFormatString = "h:mm aa";
            final String dateTimeFormatString = "dd/MM/yyyy";
            final long HOURS = 60 * 60 * 60;
            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
                return "" + DateFormat.format(timeFormatString, smsTime);
            } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
                return "Yesterday ";
            } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
                return DateFormat.format(dateTimeFormatString, smsTime).toString();
            } else {
                return DateFormat.format("dd/MM/yyyy", smsTime).toString();
            }
        }

        public void CountUnReadMessages(int position, networkModel m, String userId, ChatListHolder holder, String currentUserId) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("LastMessage").child(CurrentUserId).child(m.getUserId());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {

                        if (snapshot.child("count").exists()){
                            String msg = snapshot.child("count").getValue().toString();
                            m.setCount(Integer.parseInt(msg));
                            if (m.getCount() == 0) {
                                tv_count.setVisibility(View.GONE);
                            } else {
                                tv_count.setVisibility(View.VISIBLE);
                                tv_count.setText(""+m.getCount());
                            }
                        }




                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void filterList(ArrayList<networkModel> filterList) {
        list = filterList;
        notifyDataSetChanged();
    }

}
