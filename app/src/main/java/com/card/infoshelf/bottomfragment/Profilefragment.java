package com.card.infoshelf.bottomfragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.card.infoshelf.AboutActivity;
import com.card.infoshelf.Block.BlockedActivity;
import com.card.infoshelf.CridentialsActivity;
import com.card.infoshelf.Friends.FriendsActivity;
import com.card.infoshelf.MainActivity;
import com.card.infoshelf.Messaging.MessagingActivity;
import com.card.infoshelf.R;
import com.card.infoshelf.ReportAndSuggestions.reportActivity;
import com.card.infoshelf.Requests.RequestsActivity;
import com.card.infoshelf.bookmarkedActivity;
import com.card.infoshelf.loginandsignIn.LogInActivity;
import com.card.infoshelf.myProfileTabaccessAdaptor;
import com.card.infoshelf.userProfileActivity;
import com.card.infoshelf.whoViewedProfile.whoViewedProfileActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Profilefragment extends Fragment {

    private TextView userName, userinfo, viewProfile, uploadprofile , f_count , r_count, removeProfile, timeline_count;
    private LinearLayout network , requests, ProfileUploadLayout, blocked_l, credentials_l, about_l, logout_l, wvp_l, bookmarked_l, removeProfile_l, view_l, report_l, edit_l;
    private String userid;
    private ImageView coverPic, dialogImage, ProfileOptionBtn,imagebottom;
    private CircleImageView userProfile;
    private BottomSheetDialog bottomSheetDialog, profile_More_bottom_sheet;
    private StorageReference UserProfileImageRef, uploadCoverPic;
    private DatabaseReference databaseReference , Ref;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private Dialog mDialog, logout_dialog;
    String profession;
    String[] cameraPermission;
    String[] storagePermission;
    private boolean coverPicIsClicked = false;
    private boolean profilePicIsClicked = false;
    private  BottomSheetDialog imageBottomSheet;
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleImage";

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private myProfileTabaccessAdaptor tabaccessAdaptor;

    private RewardedAd mRewardedAd;
    private ProgressDialog progressDialog;
    private Boolean adloaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profilefragment, container, false);

        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544/5224354917");
        loadRewardAds();

        userinfo = root.findViewById(R.id.userinfo);
        userName = root.findViewById(R.id.userName);
        coverPic = root.findViewById(R.id.coverPic);
        userProfile = root.findViewById(R.id.userProfile);
        network = root.findViewById(R.id.network);
        f_count = root.findViewById(R.id.f_count);
        requests = root.findViewById(R.id.requests);
        r_count = root.findViewById(R.id.r_count);
        ProfileOptionBtn = root.findViewById(R.id.profileoptionBTN);
        timeline_count = root.findViewById(R.id.timeline_count);

        logout_dialog = new Dialog(getActivity());
        logout_dialog.setContentView(R.layout.logout_dialog);
        logout_dialog.setCancelable(false);

        Button yes = logout_dialog.findViewById(R.id.yes);
        Button no = logout_dialog.findViewById(R.id.no);

        cameraPermission = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(getActivity(), new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
               }, 1);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        uploadCoverPic = FirebaseStorage.getInstance().getReference().child("Cover_pic");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Ref = FirebaseDatabase.getInstance().getReference("Friends");

        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.show_profile_cover_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imageBottomSheet = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);
        View imageview = LayoutInflater.from(getActivity()).inflate(R.layout.image_bottom_sheet, root.findViewById(R.id.ImagebottomSheet));
        imageBottomSheet.setContentView(imageview);

        //imagebottomsheet items
        imagebottom = imageBottomSheet.findViewById(R.id.imageInSheet);

        dialogImage = mDialog.findViewById(R.id.dialog_image);


        myViewPager = root.findViewById(R.id.my_profile_pager);
        tabaccessAdaptor = new myProfileTabaccessAdaptor(getActivity().getSupportFragmentManager());
        myViewPager.setAdapter(tabaccessAdaptor);

        myTabLayout = root.findViewById(R.id.my_profile_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        myTabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_picture_in_picture_24);
        myTabLayout.getTabAt(1).setIcon(R.drawable.video);
        myTabLayout.getTabAt(2).setIcon(R.drawable.document);
//        myTabLayout.getTabAt(3).setIcon(R.drawable.about);
        int color = Color.parseColor("#007a4a");
        int unselectedColor = Color.parseColor("#59090909");
        myTabLayout.getTabAt(0).getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        myTabLayout.getTabAt(1).getIcon().setColorFilter(unselectedColor, PorterDuff.Mode.SRC_IN);
        myTabLayout.getTabAt(2).getIcon().setColorFilter(unselectedColor, PorterDuff.Mode.SRC_IN);
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
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);
        profile_More_bottom_sheet = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottomsheet_dialog, root.findViewById(R.id.sheet));

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.profile_more_bottom_sheet, root.findViewById(R.id.more_sheet));
        bottomSheetDialog.setContentView(view);
        profile_More_bottom_sheet.setContentView(v);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);


        viewProfile = bottomSheetDialog.findViewById(R.id.viewProfile);
        uploadprofile = bottomSheetDialog.findViewById(R.id.uploadProfile);
        TextView titleProfile = bottomSheetDialog.findViewById(R.id.titleProfile);
        ProfileUploadLayout = bottomSheetDialog.findViewById(R.id.uploadProfileLayout);
        blocked_l = profile_More_bottom_sheet.findViewById(R.id.blocked_l);
        bookmarked_l = profile_More_bottom_sheet.findViewById(R.id.bookmarked_l);
        about_l = profile_More_bottom_sheet.findViewById(R.id.about_l);
        credentials_l = profile_More_bottom_sheet.findViewById(R.id.credentials_l);
        logout_l = profile_More_bottom_sheet.findViewById(R.id.logiut_l);
        wvp_l = profile_More_bottom_sheet.findViewById(R.id.wvp_l);
        removeProfile_l = bottomSheetDialog.findViewById(R.id.removeProfile_l);
        removeProfile = bottomSheetDialog.findViewById(R.id.removeProfile);
        view_l = bottomSheetDialog.findViewById(R.id.view_l);
        report_l = profile_More_bottom_sheet.findViewById(R.id.report_l);
        edit_l = profile_More_bottom_sheet.findViewById(R.id.edit_l);


        logout_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_dialog.show();
                profile_More_bottom_sheet.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserOffline();
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(),
                        LogInActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                logout_dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_dialog.dismiss();
            }
        });

        credentials_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CridentialsActivity.class);
                startActivity(intent);
            }
        });
        wvp_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);

                ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
                Sprite doubleBounce = new Wave();
                progressBar.setIndeterminateDrawable(doubleBounce);

                TextView tv = progressDialog.findViewById(R.id.tvtv);
                tv.setText("Fetching data..");

adloaded = true;
                showRewadedAds();

            }
        });
        blocked_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BlockedActivity.class);
                startActivity(intent);
            }
        });

        report_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), reportActivity.class);
                startActivity(intent);
            }
        });

        about_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        Ref.child(CurrentUserId).orderByChild("Friends").equalTo("Saved").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    int count = (int) snapshot.getChildrenCount();
                    f_count.setText(""+count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       requests.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext() , RequestsActivity.class);
               startActivity(intent);
           }
       });

        coverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkUserCoverPicExixt("coverPic");

                profilePicIsClicked = false;

                titleProfile.setText("Cover Photo");
                viewProfile.setText("View Cover Photo");
                removeProfile.setText("Remove Cover Photo");
                ProfileUploadLayout.setVisibility(View.VISIBLE);
                uploadprofile.setText("Upload Cover Photo");

                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage("cover");

                    }
                });


                uploadprofile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                            return;
                            Toast.makeText(getContext(), "Give Storage Permission", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            coverPicIsClicked = true;
                            starCrop();
                        }


                    }
                });

                removeProfile_l.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Remove");
                        builder.setMessage("Are you sure to remove this post?");
                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeProfile("cover");
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

                bottomSheetDialog.show();

            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                coverPicIsClicked = false;

                checkUserCoverPicExixt("profilePic");

                titleProfile.setText("Profile Photo");
                viewProfile.setText("View Profile Photo");
                removeProfile.setText("Remove Profile Photo");
                ProfileUploadLayout.setVisibility(View.VISIBLE);
                uploadprofile.setText("Upload Profile Photo");

                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage("profile");

                    }
                });

                uploadprofile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                            return;
                            Toast.makeText(getContext(), "Give Storage Permission", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            profilePicIsClicked = true;
                            starProfileCrop();
                        }

                    }
                });

                removeProfile_l.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Remove");
                        builder.setMessage("Are you sure to delete this post?");
                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeProfile("profile");
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

                bottomSheetDialog.show();
            }
        });

        ProfileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profile_More_bottom_sheet.show();

                edit_l.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EditProfile.class);
                        startActivity(intent);

                        profile_More_bottom_sheet.dismiss();
                    }
                });
            }
        });

        bookmarked_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), bookmarkedActivity.class);
                startActivity(intent);
                profile_More_bottom_sheet.dismiss();
            }
        });


        network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , FriendsActivity.class);
                intent.putExtra("userid" , CurrentUserId);
                startActivity(intent);
            }
        });

        getUserInfo();

        CountTimeline();

        return root;
    }

    private void setUserOffline() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap map = new HashMap();
        map.put("timeStamp" , timeStamp);
        map.put("status" , "offline");

        ref.updateChildren(map);
    }

    private void CountTimeline() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserPostData");
        reference.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void checkUserCoverPicExixt(String type) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (type.equals("profilePic")){
                    if (snapshot.child("profile_image").exists()){
                        removeProfile_l.setVisibility(View.VISIBLE);
                        view_l.setVisibility(View.VISIBLE);
                    }
                    else {
                        removeProfile_l.setVisibility(View.GONE);
                        view_l.setVisibility(View.GONE);
                    }
                }
                else {
                    if (type.equals("coverPic")){
                        if (snapshot.child("cover_pic").exists()){
                            removeProfile_l.setVisibility(View.VISIBLE);
                            view_l.setVisibility(View.VISIBLE);
                        }
                        else {
                            removeProfile_l.setVisibility(View.GONE);
                            view_l.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeProfile(String type) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        TextView tv = progressDialog.findViewById(R.id.tvtv);
        tv.setText("Removing..");

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (type.equals("profile")){
                    String image_url = snapshot.child("profile_image").getValue().toString();

                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image_url);
                    picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseReference query = FirebaseDatabase.getInstance().getReference("Users");
                            query.child(CurrentUserId).child("profile_image").removeValue();
                            progressDialog.dismiss();
                            bottomSheetDialog.dismiss();
                        }
                    });
                }
                else {
                    if (type.equals("cover")){

                        String image_url = snapshot.child("cover_pic").getValue().toString();

                        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image_url);
                        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DatabaseReference query = FirebaseDatabase.getInstance().getReference("Users");
                                query.child(CurrentUserId).child("profile_image").removeValue();
                                progressDialog.dismiss();
                                bottomSheetDialog.dismiss();
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadRewardAds() {

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(getActivity(), "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.

                        mRewardedAd = null;

                        if (adloaded == true){
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Something went wrong/Check your internet connection", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                       if (adloaded ==  true){
                           showRewadedAds();
                       }

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.


                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.

showRewadedAds();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.



                                loadRewardAds();
                            }
                        });

                    }
                });
    }
    private void showRewadedAds(){
        if (mRewardedAd != null) {
            progressDialog.dismiss();
            adloaded = false;
            mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    Intent intent = new Intent(getActivity(), whoViewedProfileActivity.class);
                    startActivity(intent);

                }
            });

        }else {
            loadRewardAds();


        }
    }

    private void starProfileCrop() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop Image")
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(100)
                .start(getContext(), this);
    }

    private void openImage(String cover) {
        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
        userNameRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (cover.equals("cover")){
                    //  for cover pic
                    if (snapshot.child("cover_pic").exists()){
                        String user_cover = snapshot.child("cover_pic").getValue().toString();
                        Picasso.get().load(user_cover).into(imagebottom);
                        imageBottomSheet.show();
                        bottomSheetDialog.dismiss();

                    }
                }else
                {
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(user_Profile).into(imagebottom);
                        imageBottomSheet.show();
                        bottomSheetDialog.dismiss();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void starCrop() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop Image")
                .setAspectRatio(16, 9)
                .setOutputCompressQuality(100)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                final File file = new File(SiliCompressor.with(getActivity()).compress(FileUtils.getPath(getActivity(), resultUri), new File(getActivity().getCacheDir(), "temp")));
                Uri outputUri = Uri.fromFile(file);

                if (profilePicIsClicked == true){
                    userProfile.setImageURI(outputUri);
                    uploadProfileImage(outputUri, "Profile Pic");
                }
                if (coverPicIsClicked == true){
                    coverPic.setImageURI(outputUri);
                    uploadCoverImage(outputUri, "Cover Pic");
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadCoverImage(Uri resultUri, String cover_pic) {

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);

        final StorageReference filepath = uploadCoverPic.child(CurrentUserId + ".jpg");

        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String uri = task.getResult().toString();

                                    databaseReference.child(CurrentUserId).child("cover_pic").setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                bottomSheetDialog.dismiss();
                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                bottomSheetDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    });

                }
                else
                {

                    progressDialog.dismiss();
                    String msg = task.getException().toString();

                }
            }
        });
    }

    private void uploadProfileImage(Uri resultUri, String type) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);

        final StorageReference filepath = UserProfileImageRef.child(CurrentUserId + ".jpg");

        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {

                    filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String uri = task.getResult().toString();

                                    databaseReference.child(CurrentUserId).child("profile_image").setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                bottomSheetDialog.dismiss();

                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                bottomSheetDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    });

                }
                else
                {

                    progressDialog.dismiss();
                    String msg = task.getException().toString();

                }
            }
        });
    }

    private void getUserInfo() {

        DatabaseReference infoREf = FirebaseDatabase.getInstance().getReference("UserDetails");
        infoREf.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               profession = snapshot.child("user_bio").getValue().toString();
                userinfo.setText(profession);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getUserName(CurrentUserId);
    }


    public void getUserName(String userId) {

        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
        userNameRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user_name = snapshot.child("userName").getValue().toString();
//                userName.setText(user_name);
                String email = snapshot.child("userEmail").getValue().toString();

                int extra = profession.length()/5;
                String random = email.concat(user_name).concat(String.valueOf(extra));


                    userName.setText(user_name);

//                for profile pic

                if (snapshot.child("profile_image").exists()){
                    String user_Profile = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(user_Profile).into(userProfile);
                }
                else {
                    userProfile.setImageResource(R.drawable.profile);
                }

//                for cover pic
                if (snapshot.child("cover_pic").exists()){
                    String user_cover = snapshot.child("cover_pic").getValue().toString();

                    Picasso.get().load(user_cover).into(coverPic);
                }
                else {

                    if (random.length() % 5 == 0){
                        coverPic.setBackgroundResource(R.drawable.back1);
                    }else if (random.length()%5 ==1){
                        coverPic.setBackgroundResource(R.drawable.back2);

                    }else if (random.length()%5==2){
                        coverPic.setBackgroundResource(R.drawable.back3);

                    }else if (random.length()%5 == 3){
                        coverPic.setBackgroundResource(R.drawable.back4);

                    }else {
                        coverPic.setBackgroundResource(R.drawable.back5);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}