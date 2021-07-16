package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.card.infoshelf.bottomfragment.TagModel;
import com.card.infoshelf.bottomfragment.timeLine_model;
import com.card.infoshelf.editPost.EditPostActivity;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class bookmarkedActivity extends AppCompatActivity {

    private RecyclerView bookmarked_recycler, recyclerView;
    private filterPostAdapter postFilterAdapter;
    private shareBottomSheetAdapter shareAdapter;
    private ArrayList<timeLine_model> filterPostList;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private DatabaseReference postRef, RootRef, shareRef, blockListRef;
    private BottomSheetDialog moreBottomSheet, sharebottomsheet,imageBottomSheet;
    private LinearLayout edit_l, delete_l, share_l, report_post_l, bookmark_l;
    private TextView copyUrl, share, bookmark, delete, edit, done,No_bookmarked;
    private EditText userinput;
    List<TagModel> contacts = new ArrayList<>();
    private String postId, userProfile, postImageUrl, postShareType, user_image;
    private List<String> sendCkeckLitData;
    private  ImageView imagebottom;

    private final String fcmServerKey ="AAAAXb76uxw:APA91bFE4oCMF8hCE9JLM_R_aLOAT838pI0-GHtpcZPB876r8bHF_aXQ2dOb9LCCytmN_G8UOxfTZx69HZrnB1aZjEAs8etZOYmFdokaGj3azt5OKI0sO01PrKZMXTrad2UbNLEnGFlo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked);

        setTitle("Bookmarked");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        bookmarked_recycler = findViewById(R.id.bookmarked_recycler);
        No_bookmarked = findViewById(R.id.no_bookmarked);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
        RootRef = FirebaseDatabase.getInstance().getReference();
        shareRef = FirebaseDatabase.getInstance().getReference();
        blockListRef = FirebaseDatabase.getInstance().getReference("BlockList");

        bookmarked_recycler.setLayoutManager(new LinearLayoutManager(this));
        filterPostList = new ArrayList<>();
        postFilterAdapter = new filterPostAdapter(this, filterPostList);
        bookmarked_recycler.setAdapter(postFilterAdapter);

        // bottom sheet
        moreBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        sharebottomsheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);

        View v = LayoutInflater.from(this).inflate(R.layout.more_bottom_sheet, findViewById(R.id.sheet2));
        View viewShare = LayoutInflater.from(this).inflate(R.layout.share_bottom_sheet, findViewById(R.id.sharebottomlayout));

        moreBottomSheet.setContentView(v);
        sharebottomsheet.setContentView(viewShare);

        imageBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        View imageview = LayoutInflater.from(this).inflate(R.layout.image_bottom_sheet, findViewById(R.id.ImagebottomSheet));
        imageBottomSheet.setContentView(imageview);

        //imagebottomsheet items
        imagebottom = imageBottomSheet.findViewById(R.id.imageInSheet);

        // more bottom item
        edit_l = moreBottomSheet.findViewById(R.id.edit_l);
        delete_l = moreBottomSheet.findViewById(R.id.delete_l);
        share = moreBottomSheet.findViewById(R.id.share);
        bookmark = moreBottomSheet.findViewById(R.id.bookmark);
        delete = moreBottomSheet.findViewById(R.id.delete);
        edit = moreBottomSheet.findViewById(R.id.edit);
        share_l = moreBottomSheet.findViewById(R.id.share_l);
        report_post_l = moreBottomSheet.findViewById(R.id.report_post_l);
        bookmark_l = moreBottomSheet.findViewById(R.id.bookmark_l);

        delete_l.setVisibility(View.GONE);
        edit_l.setVisibility(View.GONE);
        bookmark_l.setVisibility(View.GONE);

        //share sheet irtem
        recyclerView = sharebottomsheet.findViewById(R.id.shareRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        shareAdapter = new shareBottomSheetAdapter(contacts);
        recyclerView.setAdapter(shareAdapter);

        userinput = sharebottomsheet.findViewById(R.id.TypeChipsTag);
        done = sharebottomsheet.findViewById(R.id.DoneTag);

        getBookmarkedPosts();
        sharebottomsheetDataWork();

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

    private void getBookmarkedPosts() {

        ArrayList<String> listBlock = new ArrayList<>();

        blockListRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                for (DataSnapshot ds : dataSnapshot1.getChildren()){
                    String key = ds.getKey();
                    listBlock.add(key);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    No_bookmarked.setVisibility(View.GONE);
                    filterPostList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String key = ""+ds.getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles");
                        ref.orderByChild("timeStamp").equalTo(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds1 : dataSnapshot.getChildren()){

                                    String id = ds1.child("UserId").getValue().toString();

                                    if (listBlock.contains(id)) {
                                    }
                                    else {
                                        timeLine_model user = ds1.getValue(timeLine_model.class);
                                        filterPostList.add(user);
                                    }
                                }

                                postFilterAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else{
                    No_bookmarked.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class filterPostAdapter extends RecyclerView.Adapter<filterPostAdapter.Myviewholder>{

        private final Context context;
        private final ArrayList<timeLine_model> list;
        private boolean bookmarkProcess = false;

        public filterPostAdapter(Context context, ArrayList<timeLine_model> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public filterPostAdapter.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
            return new Myviewholder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull filterPostAdapter.Myviewholder holder, int position) {

            timeLine_model model = list.get(position);

            String postTime = model.getTimeStamp();
            String pId = model.getTimeStamp();

            String showPostTime = holder.getFormateDate(context, postTime);
            String postUrl = model.getPostURL();
            String postDesc = model.getTextBoxData();
            String type = model.getFileType();
            String userId = model.getUserId();
            String C_To = model.getCompanyTo();
            String R_To = model.getRelatedTo();
            String ctcvalue = model.getCtcValue();
            String ctcType = model.getCtcType();

            if (ctcType.equals("none")){
                holder.hashTag_lpa.setVisibility(View.GONE);
            }
            else {
                holder.hashTag_lpa.setVisibility(View.VISIBLE);
                holder.hashTag_lpa.setText(model.getCtcValue()+" "+model.getCtcType());
            }

            holder.hashtag_realted.setText("#"+model.getRelatedTo());
            holder.hashtag_company.setText("#"+ model.getCompanyTo());
            holder.post_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.post_desc.setMaxLines(Integer.MAX_VALUE);
                }
            });

            holder.getUserInfo(userId, model);
            holder.getPostViews(pId, holder);

            checkBookmarkPost(holder, pId);


            edit_l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(bookmarkedActivity.this, EditPostActivity.class);
                    intent.putExtra("PostId",pId);
                    intent.putExtra("UserId",userId);
                    intent.putExtra("PostUrl",postUrl);
                    startActivity(intent);
                }
            });


            holder.user_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(bookmarkedActivity.this, userProfileActivity.class);
                    intent.putExtra("userid", model.getUserId());
                    startActivity(intent);
                }
            });

            holder.userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(bookmarkedActivity.this, userProfileActivity.class);
                    intent.putExtra("userid", model.getUserId());
                    startActivity(intent);
                }
            });


            holder.bookmark_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookmarkProcess = true;

                    DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
                    bookmarkREf.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (bookmarkProcess == true){
                                if (snapshot.child(CurrentUserId).hasChild(pId)){
                                    bookmarkREf.child(CurrentUserId).child(pId).removeValue();
                                    bookmarkProcess = false;
                                }
                                else {
                                    bookmarkREf.child(CurrentUserId).child(pId).child("postId").setValue(pId);
                                    bookmarkProcess = false;

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });

            holder.more_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookmark_l.setVisibility(View.GONE);

                    report_post_l.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(bookmarkedActivity.this);
                            builder.setTitle("Delete");
                            builder.setMessage("Are you sure to report this post?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    reportPost(model.getTimeStamp());
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
                            postId = model.getTimeStamp();
                            postImageUrl = model.getPostURL();

                            postShareType = model.getFileType();
//                        loadShareBottomSheetdata(pId);
                            sharebottomsheet.show();
                            sharebottomsheetDataWork();
                        }
                    });

                    if (userId.equals(CurrentUserId)){
                        delete_l.setVisibility(View.GONE);
                        edit_l.setVisibility(View.GONE);

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deletePost(pId, postUrl);
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

            holder.share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postId = model.getTimeStamp();
                    postImageUrl = model.getPostURL();

                    postShareType = model.getFileType();
//                        loadShareBottomSheetdata(pId);
                    sharebottomsheet.show();
                    sharebottomsheetDataWork();
                }
            });

            if (type.equals("image")){
                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(model.getTextBoxData());
                holder.post_image.setVisibility(View.VISIBLE);
                Picasso.get().load(model.getPostURL()).into(holder.post_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.video_progress_bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                holder.video_view.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.GONE);
                holder.video_pic.setVisibility(View.GONE);
                holder.play_icon.setVisibility(View.GONE);

            }
            else if (type.equals("video")){
                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(model.getTextBoxData());
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.GONE);
                holder.play_icon.setVisibility(View.VISIBLE);
                holder.video_pic.setVisibility(View.VISIBLE);

                // set video

                holder.video_view.setVisibility(View.GONE);
                holder.video_progress_bar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(model.getPostURL())
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.video_progress_bar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.video_progress_bar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.video_pic);
//                holder.setVideo(postUrl, bookmarkedActivity.this);

            }
            else if (type.equals("pdf")){
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(model.getTextBoxData());
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.VISIBLE);
                holder.fileName.setText(model.getFileName());
                holder.doc_img.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
                holder.video_progress_bar.setVisibility(View.GONE);
                holder.video_pic.setVisibility(View.GONE);
                holder.play_icon.setVisibility(View.GONE);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        startActivity(browserIntent);
                    }
                });
            }
            else if (type.equals("ppt") || type.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||type.equals("vnd.ms-powerpoint")){
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(model.getTextBoxData());
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.VISIBLE);
                holder.fileName.setText(model.getFileName());
                holder.doc_img.setImageResource(R.drawable.ppt);
                holder.video_progress_bar.setVisibility(View.GONE);
                holder.video_pic.setVisibility(View.GONE);
                holder.play_icon.setVisibility(View.GONE);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        startActivity(browserIntent);
                    }
                });
            }
            else if (type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| type.equals("xlsx") || type.equals("application/vnd.ms-excel")){
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(model.getTextBoxData());
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.VISIBLE);
                holder.fileName.setText(model.getFileName());
                holder.doc_img.setImageResource(R.drawable.excel);
                holder.video_progress_bar.setVisibility(View.GONE);
                holder.video_pic.setVisibility(View.GONE);
                holder.play_icon.setVisibility(View.GONE);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        startActivity(browserIntent);
                    }
                });
            }
            else if (type.equals("application/msword") || type.equals("doc") || type.equals("docx") || type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(model.getTextBoxData());
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.VISIBLE);
                holder.fileName.setText(model.getFileName());
                holder.doc_img.setImageResource(R.drawable.word);
                holder.video_progress_bar.setVisibility(View.GONE);
                holder.video_pic.setVisibility(View.GONE);
                holder.play_icon.setVisibility(View.GONE);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        startActivity(browserIntent);
                    }
                });
            }
            else if (type.equals("none")){

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(model.getTextBoxData());
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.GONE);
                holder.video_progress_bar.setVisibility(View.GONE);
                holder.video_pic.setVisibility(View.GONE);
                holder.play_icon.setVisibility(View.GONE);
            }
            else {
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(model.getTextBoxData());
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.VISIBLE);
                holder.fileName.setText(model.getFileName());
                holder.doc_img.setImageResource(R.drawable.zip);
                holder.video_progress_bar.setVisibility(View.GONE);
                holder.video_pic.setVisibility(View.GONE);
                holder.play_icon.setVisibility(View.GONE);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        startActivity(browserIntent);
                    }
                });
            }

            holder.setLikes(pId, CurrentUserId);
            holder.setComment(pId, CurrentUserId);
            holder.getActionLikeBtn(pId, CurrentUserId, model);

            holder.play_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(bookmarkedActivity.this, openVideoActivity.class);
                    intent.putExtra("videoUrl", model.getPostURL());
                    startActivity(intent);
                }
            });

            holder.comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, postDetailsActivity.class);
                    intent.putExtra("pId", pId);
                    context.startActivity(intent);
                }
            });

            holder.viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, postDetailsActivity.class);
                    intent.putExtra("pId", pId);
                    context.startActivity(intent);
                }
            });
            holder.post_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


//                            ZoomInImageViewAttacher mIvAttacter = new ZoomInImageViewAttacher(imagebottom);
                    Picasso.get().load(model.getPostURL()).into(imagebottom);
                    imageBottomSheet.show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class Myviewholder extends RecyclerView.ViewHolder {

            CircleImageView user_profile;
            TextView userName, post_time, post_desc, fileName, like_count, comment_count, viewDetails, copyUrl, share, bookmark, edit, delete, post_views, hashTag_lpa, hashtag_company, hashtag_realted, profession;
            ImageView bookmark_btn, post_image, download_btn, like_btn, comment_btn, share_btn, more_btn, doc_img, fullScreen, video_pic, play_icon;
            PlayerView video_view;
            CardView documentView;
            SimpleExoPlayer exoPlayer;
            boolean mPrecessLikes = false;
            private BottomSheetDialog moreBottomSheet;
            private LinearLayout delete_l, edit_l;
            ProgressBar video_progress_bar;

            public Myviewholder(@NonNull View itemView) {
                super(itemView);

                user_profile = itemView.findViewById(R.id.user_profile);
                userName = itemView.findViewById(R.id.userName);
                post_time = itemView.findViewById(R.id.post_time);
                post_desc = itemView.findViewById(R.id.post_desc);
                fileName = itemView.findViewById(R.id.fileName);
                like_count = itemView.findViewById(R.id.like_count);
                comment_count = itemView.findViewById(R.id.comment_count);
                viewDetails = itemView.findViewById(R.id.viewDetails);
                bookmark_btn = itemView.findViewById(R.id.bookmark_btn);
                post_image = itemView.findViewById(R.id.post_image);
                download_btn = itemView.findViewById(R.id.download_btn);
                like_btn = itemView.findViewById(R.id.like_btn);
                comment_btn = itemView.findViewById(R.id.comment_btn);
                share_btn = itemView.findViewById(R.id.share_btn);
                more_btn = itemView.findViewById(R.id.more_btn);
                video_view = itemView.findViewById(R.id.video_view);
                documentView = itemView.findViewById(R.id.documentView);
                doc_img = itemView.findViewById(R.id.doc_img);
                post_views = itemView.findViewById(R.id.post_views);
                hashTag_lpa = itemView.findViewById(R.id.hashTag_lpa);
                hashtag_company = itemView.findViewById(R.id.hashtag_company);
                hashtag_realted = itemView.findViewById(R.id.hashtag_realted);
                profession = itemView.findViewById(R.id.profession);
                fullScreen = video_view.findViewById(R.id.fullScreen);
                video_progress_bar = itemView.findViewById(R.id.video_progress_bar);
                video_pic = itemView.findViewById(R.id.video_pic);
                play_icon = itemView.findViewById(R.id.play_icon);

            }

            public void getUserInfo(String userId, timeLine_model model) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child("profile_image").exists()){
                            String pUrl = snapshot.child("profile_image").getValue().toString();
                            model.setUserProfile(pUrl);
                            Picasso.get().load(model.getUserProfile()).into(user_profile);
                        }
                        else {
                            user_profile.setImageResource(R.drawable.profile);
                        }

                        String uName = snapshot.child("userName").getValue().toString();
                        model.setUserName(uName);
                        userName.setText(model.getUserName());
                        getDataFromUserDetails(userId, model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            private void getDataFromUserDetails(String userId, timeLine_model model) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserDetails");
                reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child("user_bio").exists()){
                            profession.setVisibility(View.VISIBLE);
                            String userBio = snapshot.child("user_bio").getValue().toString();
                            model.setUserBio(userBio);
                            profession.setText(model.getUserBio());
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

            public String getFormateDate(Context context, String postTime) {
                Calendar smsTime = Calendar.getInstance();
                smsTime.setTimeInMillis(Long.parseLong(postTime));

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

            public void setLikes(String pId, String currentUserId) {

                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(pId).hasChild(currentUserId)){

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

            public void setComment(String pId, String currentUserId) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles");
                ref.child(pId).child("Comment").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = (int) snapshot.getChildrenCount();
                        comment_count.setText(""+count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            public void getActionLikeBtn(String pId, String currentUserId, timeLine_model model) {
                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

                like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mPrecessLikes = true;
                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (mPrecessLikes == true){

                                    if (snapshot.child(pId).hasChild(currentUserId)){
                                        likeRef.child(pId).child(currentUserId).removeValue();
                                        deleteNotification(model.getUserId(), pId, CurrentUserId);
                                        mPrecessLikes = false;
                                    }
                                    else {
                                        likeRef.child(pId).child(currentUserId).child("uId").setValue(currentUserId);
                                        mPrecessLikes = false;

                                        if (!model.getUserId().equals(currentUserId)){

                                            sendPushNotificationToUser(model.getUserId(), currentUserId, context, model.getTextBoxData(), model.getPostURL(), model.getTimeStamp(), "LikeP");

                                            addToHisNotification(model.getUserId(), pId, "Liked Your Post", currentUserId, "likeP");
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

            public void setVideo(String postUrl, Context context) {

                try {

                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                    Uri videouri = Uri.parse(postUrl);

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
                                video_progress_bar.setVisibility(View.VISIBLE);
                            }
                            else if (playbackState == Player.STATE_READY){
                                video_progress_bar.setVisibility(View.GONE);
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

            public void getPostViews(String pId, Myviewholder holder) {
                DatabaseReference postviewsRef = FirebaseDatabase.getInstance().getReference("PostViews");
                postviewsRef.child(pId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            int countViews = (int) snapshot.getChildrenCount();
                            holder.post_views.setText(""+countViews);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

    }

    private void reportPost(String post_id) {
        String t = String.valueOf(System.currentTimeMillis());
        HashMap map = new HashMap();
        map.put("reportBy", CurrentUserId);
        map.put("postId", post_id);

        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("ReportPost");
        reportRef.child(t).setValue(map);
    }

    private void sendPushNotificationToUser(String userId, String currentUserId, Context context, String textBoxData, String postURL, String pid, String type) {
        DatabaseReference userREf = FirebaseDatabase.getInstance().getReference("Users");
        userREf.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String state = snapshot.child("NotifyState").getValue().toString();
                String token = snapshot.child("token").getValue().toString();
                String name = snapshot.child("userName").getValue().toString();

                if (state.equals("1")) {
                    userREf.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String user_n = dataSnapshot.child("userName").getValue().toString();

                            if (dataSnapshot.child("profile_image").exists()){
                                userProfile = dataSnapshot.child("profile_image").getValue().toString();
                            }
                            String message = user_n + " Liked your post";

                            JSONObject to = new JSONObject();
                            JSONObject data = new JSONObject();
                            try {
                                data.put("title", message);
                                data.put("body", textBoxData);
                                data.put("pid", pid);
                                data.put("sender", currentUserId);
                                data.put("type", "LikeP");
                                data.put("imageUrl", postURL);
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

    private void addToHisNotification(String hisUid, String pId, String message, String currentUserId, String type) {
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

    private void deletePost(String pId, String postUrl) {
        if (postUrl.equals("none")){
            deleteWithoutImage(pId);
        }
        else {
            deleteWithImage(pId, postUrl);
        }
    }

    private void deleteWithImage(String pId, String postUrl) {

        final ProgressDialog pd = new ProgressDialog(bookmarkedActivity.this);
        pd.setMessage("Deleting...");
        pd.show();
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(postUrl);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query query = FirebaseDatabase.getInstance().getReference("POSTFiles").orderByChild("timeStamp").equalTo(pId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(bookmarkedActivity.this, "Deleted succefully..", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void deleteWithoutImage(String pId) {

        final ProgressDialog pd = new ProgressDialog(bookmarkedActivity.this);
        pd.setMessage("Deleting...");
        pd.show();
        Query query = FirebaseDatabase.getInstance().getReference("POSTFiles").orderByChild("timeStamp").equalTo(pId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(bookmarkedActivity.this, "Deleted succefully..", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkBookmarkPost(filterPostAdapter.Myviewholder holder, String pId) {
        DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
        bookmarkREf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(CurrentUserId).hasChild(pId)){
                    holder.bookmark_btn.setImageResource(R.drawable.bookmark_fill);
                }
                else {
                    holder.bookmark_btn.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // share post adapter

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
        messageTextBody.put("message", postId);
        messageTextBody.put("type", "post");
        messageTextBody.put("from", CurrentUserId);
        messageTextBody.put("postShareType", postShareType);
        messageTextBody.put("to", to);
        messageTextBody.put("messageID", messagePushID);
        messageTextBody.put("timestamp", timestamp);
        messageTextBody.put("time", CurrentTime);
        messageTextBody.put("date", CurrentDate);
        messageTextBody.put("isSeen", "0");

        Map messageTextBody1 = new HashMap();
        messageTextBody1.put("message", postId);
        messageTextBody1.put("type", "post");
        messageTextBody1.put("from", CurrentUserId);
        messageTextBody.put("postShareType", postShareType);
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
//                    shareCount = 1;

                    sendPushNotificationOfSahrePost(to);
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
                                data.put("imageUrl", postImageUrl);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}