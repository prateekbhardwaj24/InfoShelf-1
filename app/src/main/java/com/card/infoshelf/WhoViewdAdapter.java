package com.card.infoshelf;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.bottomfragment.GridModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WhoViewdAdapter extends RecyclerView.Adapter<WhoViewdAdapter.MyViewHolder>{

    Context context;
    List<GridModel> ProfileId;

    String random;

    private FirebaseAuth mAuth;
    public WhoViewdAdapter(Context context, List<GridModel> profileId) {
        this.context = context;
        ProfileId = profileId;
    }

    @NonNull
    @Override
    public WhoViewdAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profilelayout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WhoViewdAdapter.MyViewHolder holder, int position) {

        GridModel gridModel = ProfileId.get(position);
        String userID = gridModel.getUserId();

        holder.userName.setText(gridModel.getUserName());
        mAuth = FirebaseAuth.getInstance();
        String senderUserID = mAuth.getCurrentUser().getUid();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, userProfileActivity.class);
                intent.putExtra("userid", userID);
                context.startActivity(intent);
            }
        });

        holder.getUserDetailsData(userID,senderUserID);
    }

    @Override
    public int getItemCount() {
        return ProfileId.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profPic,covPic;
        TextView userName,userBio,userStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profPic = itemView.findViewById(R.id.userProfile);
            covPic = itemView.findViewById(R.id.coverProfile);
            userName = itemView.findViewById(R.id.usernames);
            userBio = itemView.findViewById(R.id.userInfo);
            userStatus = itemView.findViewById(R.id.userstatus);
        }

        public void getUserDetailsData(String userID, String senderUserID) {
            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(userID);
            userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

//                    String email = snapshot.child("userEmail").getValue().toString();
                    String profession = snapshot.child("user_bio").getValue().toString();
                    int extra = profession.length()/5;
                    DatabaseReference getidemail = FirebaseDatabase.getInstance().getReference("Users").child(userID);
getidemail.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        String user_Name = snapshot.child("userName").getValue().toString();
       String Emailid =snapshot.child("userEmail").getValue().toString();
        random = Emailid.concat(user_Name).concat(String.valueOf(extra));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

                    if (snapshot.child("user_bio").exists()){
                        userBio.setVisibility(View.VISIBLE);
                        userBio.setText(snapshot.child("user_bio").getValue().toString());
                    }else{

                    }
//                    String profess = snapshot.child("profession").getValue().toString();
//                    if (profess.equals("Schooling")){
//                        userStatus.setText(profess + " at "+ snapshot.child("school_name").getValue().toString());
//                    }else if (profess.equals("Graduation")){
//                        userStatus.setText(snapshot.child("course").getValue().toString() + " at "+ snapshot.child("college_name").getValue().toString());
//                    }else{
//                        userStatus.setText("add job node at wva file");
//                    }
                    DatabaseReference counts = FirebaseDatabase.getInstance().getReference("ViewedProfile").child(senderUserID).child(userID).child("Count");
                    counts.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userStatus.setText(snapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    getUserProCov(userID);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        private void getUserProCov(String userID) {
            DatabaseReference userIdRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);
            userIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("cover_pic").exists()){
                        String user_Profile = snapshot.child("cover_pic").getValue().toString();
                        Picasso.get().load(user_Profile).into(covPic);
                    }else{

                        if (random.length() % 5 == 0){
                            covPic.setBackgroundResource(R.drawable.back1);
                        }else if (random.length()%5 ==1){
                            covPic.setBackgroundResource(R.drawable.back2);

                        }else if (random.length()%5==2){
                            covPic.setBackgroundResource(R.drawable.back3);

                        }else if (random.length()%5 == 3){
                            covPic.setBackgroundResource(R.drawable.back4);

                        }else {
                            covPic.setBackgroundResource(R.drawable.back5);

                        }

                    }
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(user_Profile).into(profPic);
                    }else {
                        profPic.setImageResource(R.drawable.profile);
                    }

                    userName.setText(snapshot.child("userName").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
