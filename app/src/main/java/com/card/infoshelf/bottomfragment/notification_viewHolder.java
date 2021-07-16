package com.card.infoshelf.bottomfragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.card.infoshelf.R;
import com.card.infoshelf.postDetailsActivity;
import com.card.infoshelf.userProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class notification_viewHolder extends RecyclerView.Adapter<notification_viewHolder.Myviewholder>
{

    private final Context context;
    private final ArrayList<noti_model> list;
    private FirebaseAuth mAuth;
    private String CurrentUserId;

    public notification_viewHolder(Context context, ArrayList<noti_model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public notification_viewHolder.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);
        return new Myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull notification_viewHolder.Myviewholder holder, int position) {

        noti_model model = list.get(position);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        String uId = model.getsUid();
        String noti_time = model.getTimestamp();
        String message = model.getNotification();
        String pid = model.getpId();
        String status = model.getStatus();
        String type = model.getType();

        String showPostTime = holder.getFormateDate(context , noti_time);

        holder.noti_time.setText(showPostTime);
        holder.reactText.setText(model.getNotification());

        holder.getUserInfo(uId);

        if (model.getType().equals("likeP") || model.getType().equals("CommentP") || model.getType().equals("PlaceInternPost") || model.getType().equals("CasualPost")){
            holder.getPostInfo(pid, context, model);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (model.getType().equals("RequestReceived") || model.getType().equals("RequestAccepted")){
                    Intent intent = new Intent(context, userProfileActivity.class);
                    intent.putExtra("userid", model.getsUid());
                    context.startActivity(intent);
                }
                if (model.getType().equals("likeP") || model.getType().equals("CommentP") || model.getType().equals("PlaceInternPost") || model.getType().equals("CasualPost")){

                    checkPostisExistOrNot(model, context);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this notification?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(CurrentUserId).child("Notifications").child(noti_time).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Notification Deleted..", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                return false;
            }
        });

    }

    private void checkPostisExistOrNot(noti_model model, Context context) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles");
        ref.child(model.getpId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    Intent intent = new Intent(context, postDetailsActivity.class);
                    intent.putExtra("pId", model.getpId());
                    context.startActivity(intent);
                }
                else {
                    Toast.makeText(context, "This post has been deleted by user", Toast.LENGTH_SHORT).show();
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

    public class Myviewholder extends RecyclerView.ViewHolder {

        TextView time, userName, reactText, noti_time, fileName;
        CircleImageView userProfile;
        ImageView post_img;

        public Myviewholder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            userName = itemView.findViewById(R.id.userName);
            reactText = itemView.findViewById(R.id.reactText);
            noti_time = itemView.findViewById(R.id.noti_time);
            userProfile = itemView.findViewById(R.id.userProfile);
            post_img = itemView.findViewById(R.id.post_img);
            fileName = itemView.findViewById(R.id.fileName);

        }

        public String getFormateDate(Context context, String noti_time) {

            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(Long.parseLong(noti_time));

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

        public void getUserInfo(String uId) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

            userRef.orderByChild("userId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()){

                        if (ds.child("profile_image").exists()){
                            String userIamge = ""+ds.child("profile_image").getValue().toString();
                            Picasso.get().load(userIamge).into(userProfile);
                        }
                        else {
                            userProfile.setImageResource(R.drawable.profile);
                        }

                        String user_name = ""+ds.child("userName").getValue().toString();
                        userName.setText(user_name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void getPostInfo(String pid, Context context, noti_model model) {

            DatabaseReference postREf = FirebaseDatabase.getInstance().getReference("POSTFiles");
            postREf.child(pid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String FileType = snapshot.child("FileType").getValue().toString();
                        String postImage = snapshot.child("PostURL").getValue().toString();
                        String file_name = snapshot.child("FileName").getValue().toString();

                            if (FileType.equals("image")){
                                post_img.setVisibility(View.VISIBLE);
                                Picasso.get().load(postImage).into(post_img);

                            }
                            else if (FileType.equals("none")){
                                post_img.setVisibility(View.GONE);
                            }
                            else if (FileType.equals("video")){

                                post_img.setVisibility(View.VISIBLE);
                                long interval = getPosition()*1000;
                                RequestOptions options = new RequestOptions().frame(interval);
//            Glide.with(context).asBitmap().load(videoUrl).apply(options).into(holder.Post_image);
                                Glide.with(context)
                                        .load(postImage)
                                        .apply(options)
                                        .error(R.drawable.ic_baseline_video_library_24)
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(post_img);

                            }
                            else if (FileType.equals("pdf")){
                                fileName.setVisibility(View.VISIBLE);
                                fileName.setText(file_name);
                                post_img.setVisibility(View.VISIBLE);
                                post_img.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
                            }
                            else if (FileType.equals("ppt") || FileType.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||FileType.equals("vnd.ms-powerpoint")){
                                post_img.setVisibility(View.VISIBLE);
                                fileName.setVisibility(View.VISIBLE);
                                fileName.setText(file_name);
                                post_img.setImageResource(R.drawable.ppt);
                            }
                            else if (FileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| FileType.equals("xlsx") || FileType.equals("application/vnd.ms-excel")){
                                post_img.setVisibility(View.VISIBLE);
                                fileName.setVisibility(View.VISIBLE);
                                fileName.setText(file_name);
                                post_img.setImageResource(R.drawable.excel);
                            }
                            else if (FileType.equals("application/msword") || FileType.equals("doc") || FileType.equals("docx") || FileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                                post_img.setVisibility(View.VISIBLE);
                                fileName.setVisibility(View.VISIBLE);
                                fileName.setText(file_name);
                                post_img.setImageResource(R.drawable.word);
                            }
                            else {
                                fileName.setVisibility(View.VISIBLE);
                                fileName.setText(file_name);
                                post_img.setVisibility(View.VISIBLE);
                                post_img.setImageResource(R.drawable.zip);
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
