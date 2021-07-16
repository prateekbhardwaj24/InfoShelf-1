package com.card.infoshelf.bottomfragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.card.infoshelf.MainActivity;
import com.card.infoshelf.Messenger.MessengerActivity;


import com.card.infoshelf.R;
import com.card.infoshelf.editPost.EditPostActivity;
import com.card.infoshelf.openVideoActivity;
import com.card.infoshelf.postDetailsActivity;
import com.card.infoshelf.storyAdaptor;
import com.card.infoshelf.storyModel;
import com.card.infoshelf.userProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.ads.nativetemplates.TemplateView;
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
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TimelineFragment extends Fragment {

    private RecyclerView storiesRecycler, postRecycler, allPostRecyclerView;
    private storyAdaptor adaptor;
    private List<storyModel> mstory;
    //    private ArrayList<timeLine_model>t;
    ArrayList<Object> allPostList, filterPostList;
    private static filterPostAdapter allPostAdapter;
    private filterPostAdapter postFilterAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private static BottomSheetDialog moreBottomSheet;
    private static BottomSheetDialog sharebottomsheet;
    private static BottomSheetDialog imageBottomSheet;
    private ImageView filter, messenger;
    private  static ImageView imagebottom;
    private TextView all, tv_count;
    CheckBox allcheck, check_2, check_3;
    Spinner company_tag_spin, interest_tag_spin;
    String getInterest, getCompany;
    Integer getCheck, reference_val_filter = 0;
    TextView donebtn;
    static TextView bookmark;
    TextView share;
    TextView copyUrl;
    static TextView delete;
    TextView edit;
    TextView resetbtn;
    TextView errorTextView;
    DataBaseHelper myDb;
    DataBaseHelper myDb2;
    DataBaseHelper myDb3;
    private List<String> userList, PostTypeList, PostRefList;
    private FirebaseRecyclerAdapter<timeLine_model, timeline_adaptor> postAdaptor;
    private DatabaseReference postRef;
    private DatabaseReference PostType;
    private DatabaseReference PostRefChild;
    private static DatabaseReference shareRef;
    private DatabaseReference RootRef;
    private DatabaseReference reference;
    private DatabaseReference blockListRef;
    private FirebaseAuth mAuth;
    private static String CurrentUserId;
    private static LinearLayout delete_l;
    private static LinearLayout edit_l, report_post_l;
    private LinearLayout company_layout;
    private LinearLayout interest_layout;
    private static LinearLayout share_l;
    private LinearLayout bookmark_l;
    private final boolean bookmarkProcess = false;
    private final int c = 0;
    private int count = 0;
    private int i = 0;
    private int click = 0 , noLoad =0;
    private String key;
    private EditText searchView;
    private static EditText userinput;
    static List<TagModel> contacts = new ArrayList<>();
    private TextView done;
    private RecyclerView recyclerView;
    static shareBottomSheetAdapter shareAdapter;
    static String postId, user_image, postImageUrl, postShareType;
    private boolean checkBoxState = true;
    int shareCount = 0;
    private List<String> sendCkeckLitData;
    ArrayList<String> listBlock;
    private static Context contextForIntent;
    public static int ITEM_PER_AD =4 ;
    private static String userProfile;
    private static final String BANNER_AD = "ca-app-pub-3940256099942544/6300978111";
    private static final String fcmServerKey = "AAAAXb76uxw:APA91bFE4oCMF8hCE9JLM_R_aLOAT838pI0-GHtpcZPB876r8bHF_aXQ2dOb9LCCytmN_G8UOxfTZx69HZrnB1aZjEAs8etZOYmFdokaGj3azt5OKI0sO01PrKZMXTrad2UbNLEnGFlo";
    private CardView new_Post;
    SharedPreferences pref;
    private static Boolean Visible = false;
    public static int id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_timeline, container, false);

        contextForIntent = getActivity();

        storiesRecycler = root.findViewById(R.id.storiesRecycler);
        filter = root.findViewById(R.id.filter);
        messenger = root.findViewById(R.id.messeger);
        postRecycler = root.findViewById(R.id.postRecycler);
        allPostRecyclerView = root.findViewById(R.id.allPostRecyclerView);
        tv_count = root.findViewById(R.id.tv_count);
        new_Post = root.findViewById(R.id.newPost);



        storiesRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        storiesRecycler.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        allPostRecyclerView.setLayoutManager(lm);
        postRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);

        mstory = new ArrayList<>();
        PostTypeList = new ArrayList<>();
        PostRefList = new ArrayList<>();
        filterPostList = new ArrayList<>();
        allPostList = new ArrayList<>();

        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544/2247696110");

        adaptor = new storyAdaptor(getActivity(), mstory);
        postFilterAdapter = new filterPostAdapter(getActivity(), filterPostList);
        allPostAdapter = new filterPostAdapter(getActivity(), allPostList);
        storiesRecycler.setAdapter(adaptor);
        allPostRecyclerView.setAdapter(allPostAdapter);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference("POSTFiles");
        PostType = FirebaseDatabase.getInstance().getReference("allCompanies");
        reference = FirebaseDatabase.getInstance().getReference("allCompanies");
        shareRef = FirebaseDatabase.getInstance().getReference();
        RootRef = FirebaseDatabase.getInstance().getReference();
        blockListRef = FirebaseDatabase.getInstance().getReference("BlockList");

        postRecycler.setAdapter(postFilterAdapter);

        // bottom sheet
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);
        moreBottomSheet = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);
        sharebottomsheet = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);
        imageBottomSheet = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.timeline_filter_bottom_sheet, root.findViewById(R.id.sheet2));
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.more_bottom_sheet, root.findViewById(R.id.sheet2));
        View viewShare = LayoutInflater.from(getActivity()).inflate(R.layout.share_bottom_sheet, root.findViewById(R.id.sharebottomlayout));
        View imageview = LayoutInflater.from(getActivity()).inflate(R.layout.image_bottom_sheet, root.findViewById(R.id.ImagebottomSheet));

        bottomSheetDialog.setContentView(view);
        moreBottomSheet.setContentView(v);
        sharebottomsheet.setContentView(viewShare);
        imageBottomSheet.setContentView(imageview);

        allcheck = bottomSheetDialog.findViewById(R.id.all_check);
        check_2 = bottomSheetDialog.findViewById(R.id.checkbox_2);
        all = bottomSheetDialog.findViewById(R.id.All);
        errorTextView = bottomSheetDialog.findViewById(R.id.error_text);
        resetbtn = bottomSheetDialog.findViewById(R.id.ResetFilter);
        company_tag_spin = bottomSheetDialog.findViewById(R.id.comapny_tag_spinner);
        interest_tag_spin = bottomSheetDialog.findViewById(R.id.interest_tag_spinner);
        company_layout = bottomSheetDialog.findViewById(R.id.comapny_layout);

        // more bottom item
        edit_l = moreBottomSheet.findViewById(R.id.edit_l);
        delete_l = moreBottomSheet.findViewById(R.id.delete_l);
        share = moreBottomSheet.findViewById(R.id.share);
        share_l = moreBottomSheet.findViewById(R.id.share_l);
        bookmark = moreBottomSheet.findViewById(R.id.bookmark);
        bookmark_l = moreBottomSheet.findViewById(R.id.bookmark_l);
        delete = moreBottomSheet.findViewById(R.id.delete);
        edit = moreBottomSheet.findViewById(R.id.edit);
        report_post_l = moreBottomSheet.findViewById(R.id.report_post_l);

        //share sheet irtem
        recyclerView = sharebottomsheet.findViewById(R.id.shareRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        shareAdapter = new shareBottomSheetAdapter(contacts);
        recyclerView.setAdapter(shareAdapter);

        userinput = sharebottomsheet.findViewById(R.id.TypeChipsTag);
        done = sharebottomsheet.findViewById(R.id.DoneTag);

        //imagebottomsheet items
        imagebottom = imageBottomSheet.findViewById(R.id.imageInSheet);


        //adds

        //-----------------------------interest adapter (casual,Internship,Placement----------------------------//
        ArrayAdapter<String> interestAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, PostTypeList);
        //set the spinners adapter to the previously created one.
        interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interest_tag_spin.setAdapter(interestAdapter);

        //-----------------------------company adapter (amzon,google----------------------------//
        ArrayAdapter<String> company_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, PostRefList);
        //set the spinners adapter to the previously created one.
        company_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        company_tag_spin.setAdapter(company_adapter);

        //--------------------------- getting childs of allCompanies node-----------------------

        pref = getActivity().getPreferences(Context.MODE_PRIVATE);

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Visible = false;
                if (i == 0) {
                    count = (int) snapshot.getChildrenCount();
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putInt("count", count);
                    edt.commit();
                    i = 1;
                }

                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("count", (int) snapshot.getChildrenCount());
                edit.apply();

                id = pref.getInt("count", 0);

                if (snapshot.getChildrenCount() == id){
                    MainActivity.posBadge.setVisible(false);
                }

                if (id < (int) snapshot.getChildrenCount()) {
                    if (!Visible) {
                        new_Post.startAnimation(MainActivity.animation);
                        new_Post.setVisibility(View.VISIBLE);
                        Visible = true;
                    }
                    new_Post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment fragment = new TimelineFragment();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                            click = 1;
                            Visible = false;
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putInt("count", (int) snapshot.getChildrenCount());
                            edit.apply();

                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        PostType.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String val = ds.getKey();
                        PostTypeList.add(val);
                    }
                    interestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // getting childs of spinner value ref node
        interest_tag_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PostRefList.clear();
                PostRefChild = FirebaseDatabase.getInstance().getReference("allCompanies").child(parent.getItemAtPosition(position).toString());

                PostRefChild.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String RefVal = ds.getKey();
                                PostRefList.add(RefVal);
                            }
                            company_adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //-------------------validate search fields---------------
        validateSearch();

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessengerActivity.class);
                startActivity(intent);
            }
        });

//sqlite data -----------------
        myDb = new DataBaseHelper(getContext());
        donebtn = bottomSheetDialog.findViewById(R.id.DoneFilter);
        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//----------------------------getting selected value of filters------------------------//
                if (reference_val_filter == 0) {

                    allPostRecyclerView.setVisibility(View.VISIBLE);
                    postRecycler.setVisibility(View.GONE);
                    getAllPosts();

//                    loadPost();
                    //---it means only shuffled post checkbox is checked and rest are gone

                } else {
                    //----it means both spinners are selected and shuffled checkbox is not checked
                    checkBoxState = false;
                    allPostRecyclerView.setVisibility(View.GONE);
                    postRecycler.setVisibility(View.VISIBLE);
                    String typeOfPost = interest_tag_spin.getSelectedItem().toString();
                    String whichCompany = company_tag_spin.getSelectedItem().toString();

                    getPostREf(typeOfPost, whichCompany);

                }
                bottomSheetDialog.cancel();


            }
        });
        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company_layout.setVisibility(View.GONE);
                check_2.setChecked(false);
                allcheck.setChecked(true);
            }
        });

        if (checkBoxState == true) {
            allcheck.setChecked(checkBoxState);
            allPostRecyclerView.setVisibility(View.VISIBLE);
            postRecycler.setVisibility(View.GONE);
            getAllPosts();

        }

        checkUserList();


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCkeckLitData = shareAdapter.getArraySend();
                if (!sendCkeckLitData.isEmpty()) {
                    for (int i = 0; i < sendCkeckLitData.size(); i++) {
                        sharePost(sendCkeckLitData.get(i));

                    }
                    sendCkeckLitData.clear();

                }
                sharebottomsheet.dismiss();

            }

        });



        return root;
    }



    private void getAllPosts() {

        listBlock = new ArrayList<>();

        blockListRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                for (DataSnapshot ds : dataSnapshot1.getChildren()) {
                    String key = ds.getKey();
                    listBlock.add(key);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    allPostList.clear();
                    int countPosts = (int) snapshot.getChildrenCount();
                    if (countPosts > 20) {
                        postRef.orderByChild("timeStamp").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    String id = ds.child("UserId").getValue().toString();

                                    if (listBlock.contains(id)) {
                                    } else {
                                        timeLine_model user = ds.getValue(timeLine_model.class);
                                        allPostList.add(user);
                                    }
                                }
                                Collections.reverse(allPostList);
                                shuffledPostAdd(countPosts);

                                allPostAdapter.notifyDataSetChanged();
//                                if(noLoad == 0)
//                                {
//                                    allPostAdapter.notifyDataSetChanged();
//                                    noLoad = 1;
//                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String id = ds.child("UserId").getValue().toString();

                            if (listBlock.contains(id)) {
                            } else {
                                timeLine_model user = ds.getValue(timeLine_model.class);
                                allPostList.add(user);
                            }
                        }
                        LoadAds();
                        Collections.reverse(allPostList);
                        allPostAdapter.notifyDataSetChanged();
//                        if(noLoad == 0)
//                        {
//                            allPostAdapter.notifyDataSetChanged();
//                            noLoad = 1;
//                        }
                    }
                }
                else {
                    MainActivity.progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadAds() {
        //check array size and increase ITEM_PER_AD value

        if (allPostList.size() >=5 && allPostList.size() <=8){
            ITEM_PER_AD = 5;
        }else if (allPostList.size() >=9 && allPostList.size() <=16){
            ITEM_PER_AD = 6;
        }else if (allPostList.size() >=17 && allPostList.size() <=32){
            ITEM_PER_AD = 7;
        }else if (allPostList.size() >=33 && allPostList.size() <=66){
            ITEM_PER_AD = 8;
        }else if (allPostList.size() >=67 && allPostList.size() <=100){
            ITEM_PER_AD = 9;
        }else {
            ITEM_PER_AD = 11;
        }
        for (int i =0 ; i < allPostList.size(); i += ITEM_PER_AD) {
            if (i != 0) {

                String k = "none";
                allPostList.add(i, k);
            }
        }
//        //        after ads in allpost we should load all ads
//        for (int i = 0; i < allPostList.size(); i++) {
//            Object item = allPostList.get(i);
//            if (item instanceof AdView) {
//                final AdView adView = (AdView) item;
//                adView.loadAd(new AdRequest.Builder().build());
//            }
//
//        }
    }

    private void shuffledPostAdd(int countPosts) {
        int lessVal = countPosts - 20;
        ArrayList<timeLine_model> shuffledArray = new ArrayList<>();
        postRef.orderByChild("timeStamp").limitToFirst(lessVal).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.child("UserId").getValue().toString();

                    if (listBlock.contains(id)) {
                    } else {
                        timeLine_model user = ds.getValue(timeLine_model.class);
                        shuffledArray.add(user);
                    }
                }
                Collections.shuffle(shuffledArray);
                // now add shuffled array to original array
                for (int i = 0; i < shuffledArray.size(); i++) {
                    allPostList.add(shuffledArray.get(i));
                }
                LoadAds();

//                allPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void sharebottomsheetDataWork() {
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

    private void getPostREf(String typeOfPost, String whichCompany) {


        ArrayList<String> listBlock = new ArrayList<>();

        blockListRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                for (DataSnapshot ds : dataSnapshot1.getChildren()) {
                    String key = ds.getKey();
                    listBlock.add(key);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child(typeOfPost).child(whichCompany).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filterPostList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key1 = "" + ds.getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles");
                    ref.orderByChild("timeStamp").equalTo(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds1 : dataSnapshot.getChildren()) {

                                String id = ds1.child("UserId").getValue().toString();

                                if (listBlock.contains(id)) {
                                } else {
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostViews(String pId, timeline_adaptor holder) {
        DatabaseReference postviewsRef = FirebaseDatabase.getInstance().getReference("PostViews");
        postviewsRef.child(pId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int countViews = (int) snapshot.getChildrenCount();
                    holder.post_views.setText("" + countViews);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void CountViews(String pId) {
        DatabaseReference postviewsRef = FirebaseDatabase.getInstance().getReference("PostViews");
        postviewsRef.child(pId).child(CurrentUserId).child("userId").setValue(CurrentUserId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public class shareBottomSheetAdapter extends RecyclerView.Adapter<shareBottomSheetAdapter.MyviewHolder> {

        private List<TagModel> tagPersonList;
        private final List<String> sendList = new ArrayList<>();

        public shareBottomSheetAdapter(List<TagModel> tagPersonList) {
            this.tagPersonList = tagPersonList;
        }

        public List<String> getArraySend() {
            return sendList;
        }

        @NonNull
        @Override
        public shareBottomSheetAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_view, parent, false);
            return new shareBottomSheetAdapter.MyviewHolder(view);
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
                    if (isChecked) {
                        sendList.add(tagModel.getUserId());
//                            tagModel.setChecker(true);
                    } else {
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

        class MyviewHolder extends RecyclerView.ViewHolder {

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
        messageTextBody1.put("postShareType", postShareType);
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
                } else {

                }

            }
        });
        UpdateChatListAndLastMessage("Shared a Post" , to);

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

    //    }
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

    private void checkBookmarkPost(timeline_adaptor holder, String pId) {
        DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
        bookmarkREf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(CurrentUserId).hasChild(pId)) {
                    holder.bookmark_btn.setImageResource(R.drawable.bookmark_fill);
                } else {
                    holder.bookmark_btn.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void deletePost(String pId, String postUrl, String userId, String c_To, String r_To, int position, ArrayList<Object> a_list) {

        if (postUrl.equals("none")) {
            deleteWithoutImage(pId, userId, c_To, r_To, position, a_list);
        } else {
            deleteWithImage(pId, postUrl, userId, c_To, r_To, position, a_list);
        }
    }

    private static void deleteWithImage(String pId, String postUrl, String userId, String c_To, String r_To, int position, ArrayList<Object> a_list) {
        TextView tv = MainActivity.progressDialog.findViewById(R.id.tvtv);
        tv.setText("Deleting..");
     MainActivity.progressDialog.show();
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(postUrl);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference query = FirebaseDatabase.getInstance().getReference("POSTFiles");
                query.child(pId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteDataFromPostViews(pId, MainActivity.progressDialog);
                        deleteDataFromallCompanies(pId, r_To, c_To, MainActivity.progressDialog);
                        deleteDataFromUserData(pId, userId, MainActivity.progressDialog);
                        deleteDataFromLikes(pId, MainActivity.progressDialog);
                        deleteDataFromCommentLikes(pId, MainActivity.progressDialog);
                        deleteDataFromNotification(userId, pId, MainActivity.progressDialog);
                        deleteDataFromReplyNotification(userId, pId);
                        MainActivity.progressDialog.dismiss();
                        a_list.remove(position);
                        allPostAdapter.notifyItemRemoved(position);
                        allPostAdapter.notifyItemRangeChanged(position, a_list.size());

                    }
                });
            }
        });

    }

    private static void deleteDataFromReplyNotification(String userId, String pId) {
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

    private static void deleteDataFromNotification(String userId, String pId, ProgressDialog pd) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference("Users");
        ref5.child(userId).child("Notifications").orderByChild("pId").equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                       for (DataSnapshot ds5 : snapshot.getChildren()) {
                    ds5.getRef().removeValue();
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void deleteDataFromCommentLikes(String pId, ProgressDialog pd) {
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

    private static void deleteDataFromLikes(String pId, ProgressDialog pd) {
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

    private static void deleteDataFromUserData(String pId, String userId, ProgressDialog pd) {
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("UserPostData");
        ref2.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                      if (snapshot.hasChild(pId)){
                    ref2.child(userId).child(pId).removeValue();

                }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private static void deleteDataFromallCompanies(String pId, String r_to, String c_to, ProgressDialog pd) {

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

    private static void deleteDataFromPostViews(String pId, ProgressDialog pd) {
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

    private static void deleteWithoutImage(String pId, String userId, String c_To, String r_To, int position, ArrayList<Object> a_list) {

        TextView tv = MainActivity.progressDialog.findViewById(R.id.tvtv);
        tv.setText("Deleting..");
        MainActivity.progressDialog.show();
        DatabaseReference query = FirebaseDatabase.getInstance().getReference("POSTFiles");
        query.child(pId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteDataFromPostViews(pId, MainActivity.progressDialog);
                deleteDataFromallCompanies(pId, r_To, c_To,  MainActivity.progressDialog);
                deleteDataFromUserData(pId, userId,  MainActivity.progressDialog);
                deleteDataFromLikes(pId,  MainActivity.progressDialog);
                deleteDataFromCommentLikes(pId,  MainActivity.progressDialog);
                deleteDataFromNotification(userId, pId,  MainActivity.progressDialog);
                deleteDataFromReplyNotification(userId, pId);
                MainActivity.progressDialog.dismiss();
                a_list.remove(position);
                allPostAdapter.notifyItemRemoved(position);
                allPostAdapter.notifyItemRangeChanged(position, a_list.size());
            }
        });
    }

    private void checkUserList() {

        userList = new ArrayList<>();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    userList.add(ds.getKey());
                }
//                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validateSearch() {
        interest_tag_spin.setEnabled(false);
        allcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allcheck.isChecked()) {
                    reference_val_filter = 0;
                    check_2.setChecked(false);
                    company_layout.setVisibility(View.GONE);
                    interest_tag_spin.setEnabled(false);
                }
            }
        });

        check_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check_2.isChecked()) {
                    reference_val_filter = 1;
                    company_layout.setVisibility(View.VISIBLE);
                    allcheck.setChecked(false);
                    interest_tag_spin.setEnabled(true);
                }

            }
        });
    }

    private void readStory() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timeCurent = System.currentTimeMillis();
                mstory.clear();
                mstory.add(new storyModel("", 0, 0, "", FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : userList) {
                    int countStory = 0;
                    storyModel story = null;
                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()) {
                        story = snapshot1.getValue(storyModel.class);
                        if (timeCurent > story.getTimestart() && timeCurent < story.getTimeend()) {
                            countStory++;
                        }
                    }
                    if (countStory > 0) {
                        mstory.add(story);
                    }
                }
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // filter post adapter

    public static class filterPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;
        private final ArrayList<Object> list;
        private boolean bookmarkProcess = false;

        private static final int item_data = 0;
        private static final int item_Banner = 1;

        private ScaleGestureDetector scaleGestureDetector;
        private float mScaleFactor = 1.0f;

        public filterPostAdapter(Context context, ArrayList<Object> list) {
            ArrayList<Object>  newlist= list;

            for (int i =0 ; i < list.size(); i += ITEM_PER_AD) {
                if (i != 0) {

                    AdView adView = new AdView(context);
                    adView.setAdSize(AdSize.SMART_BANNER);
                    adView.setAdUnitId(BANNER_AD);
                    newlist.add(i, adView);
                }
            }
            for (int i = 0; i < newlist.size(); i++) {
                Object item = newlist.get(i);
                if (item instanceof AdView) {
                    final AdView adView = (AdView) item;
                    adView.loadAd(new AdRequest.Builder().build());
                }

            }
            this.context = context;
            this.list = newlist;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (viewType) {
                case item_data:
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
                    return new Myviewholder(v);

                case item_Banner:
                default:
                    View bannerview = LayoutInflater.from(parent.getContext()).inflate(R.layout.admob_ad_one, parent, false);
                    return new bannerADDViewholder(bannerview);

            }


        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            int viewtype = getItemViewType(position);
            switch (viewtype) {

                case item_data:
                    scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
                    Myviewholder mvh = (Myviewholder) holder;
                    timeLine_model model = (timeLine_model) list.get(position);

                    String postTime = model.getTimeStamp();
                    String pId = model.getTimeStamp();

                    String showPostTime = mvh.getFormateDate(context, postTime);
                    String postUrl = model.getPostURL();
                    String postDesc = model.getTextBoxData();
                    String type = model.getFileType();
                    String userId = model.getUserId();
                    String C_To = model.getCompanyTo();
                    String R_To = model.getRelatedTo();
                    String ctcType = model.getCtcType();

                    if (ctcType.equals("none")) {
                        mvh.hashTag_lpa.setVisibility(View.GONE);
                    } else {
                        mvh.hashTag_lpa.setVisibility(View.VISIBLE);
                        mvh.hashTag_lpa.setText(model.getCtcValue() + " " + model.getCtcType());
                    }

                    mvh.hashtag_realted.setText("#" + model.getRelatedTo());
                    mvh.hashtag_company.setText("#" + model.getCompanyTo());
                    mvh.post_desc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mvh.post_desc.setMaxLines(Integer.MAX_VALUE);
                        }
                    });

                    mvh.getUserInfo(userId, model);

                    mvh.checkBookmarkPost(mvh, pId);

                    mvh.getPostViews(pId, mvh);
                    CountViews(pId);

                    edit_l.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(contextForIntent, EditPostActivity.class);
                            intent.putExtra("PostId", model.getTimeStamp());
                            intent.putExtra("UserId", model.getUserId());
                            intent.putExtra("PostUrl", model.getPostURL());
                            contextForIntent.startActivity(intent);
                            moreBottomSheet.dismiss();
                        }
                    });

                    mvh.user_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(contextForIntent, userProfileActivity.class);
                            intent.putExtra("userid", model.getUserId());
                            contextForIntent.startActivity(intent);
                        }
                    });

                    mvh.userName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(contextForIntent, userProfileActivity.class);
                            intent.putExtra("userid", model.getUserId());
                            contextForIntent.startActivity(intent);
                        }
                    });

                    mvh.bookmark_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bookmarkProcess = true;
                            DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("POSTFiles");

                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(pId)){

                                        bookmarkREf.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (bookmarkProcess == true) {
                                                    if (snapshot.child(CurrentUserId).hasChild(pId)) {
                                                        bookmarkREf.child(CurrentUserId).child(pId).removeValue();
                                                        bookmarkProcess = false;
                                                    } else {
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
                                    else {
                                        list.remove(position);
                                        allPostAdapter.notifyItemRemoved(position);
                                        allPostAdapter.notifyItemRangeChanged(position, list.size());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        }
                    });

                    mvh.more_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("POSTFiles");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(pId)){

                                        // for report post
                                        report_post_l.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(contextForIntent);
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
                                                moreBottomSheet.dismiss();

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
                                                moreBottomSheet.dismiss();
                                            }
                                        });

                                        bookmark.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                moreBottomSheet.dismiss();
                                                bookmarkProcess = true;

                                                DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
                                                bookmarkREf.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (bookmarkProcess == true) {
                                                            if (snapshot.child(CurrentUserId).hasChild(pId)) {
                                                                bookmarkREf.child(CurrentUserId).child(pId).removeValue();
                                                                bookmarkProcess = false;
                                                            } else {
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

                                        if (userId.equals(CurrentUserId)) {
                                            delete_l.setVisibility(View.VISIBLE);
                                            edit_l.setVisibility(View.VISIBLE);

                                            delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    moreBottomSheet.dismiss();

                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(contextForIntent);
                                                    builder.setTitle("Delete");
                                                    builder.setMessage("Are you sure to delete this post?");
                                                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            deletePost(pId, postUrl, userId, C_To, R_To, position, list);
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
                                            });

                                        } else {
                                            delete_l.setVisibility(View.GONE);
                                            edit_l.setVisibility(View.GONE);

                                        }

                                        moreBottomSheet.show();

                                    }
                                    else {
                                        list.remove(position);
                                        allPostAdapter.notifyItemRemoved(position);
                                        allPostAdapter.notifyItemRangeChanged(position, list.size());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });

                    mvh.share_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("POSTFiles");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(pId)){

                                        postId = model.getTimeStamp();
                                        postImageUrl = model.getPostURL();

                                        postShareType = model.getFileType();
//                        loadShareBottomSheetdata(pId);

                                        sharebottomsheet.show();
                                        sharebottomsheetDataWork();

                                        moreBottomSheet.dismiss();
                                    }
                                    else {
                                        list.remove(position);
                                        allPostAdapter.notifyItemRemoved(position);
                                        allPostAdapter.notifyItemRangeChanged(position, list.size());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });

                    if (type.equals("image")) {

                        if (Myviewholder.is_video == true){
                            Myviewholder.exoPlayer.stop();
                        }
                        mvh.post_image.setVisibility(View.VISIBLE);
                        mvh.post_time.setText(showPostTime);
                        mvh.post_desc.setText(model.getTextBoxData());
                        Picasso.get().load(model.getPostURL()).into(mvh.post_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                mvh.video_progress_bar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                        mvh.video_view.setVisibility(View.GONE);
                        mvh.documentView.setVisibility(View.GONE);
                        mvh.video_pic.setVisibility(View.GONE);
                        mvh.play_icon.setVisibility(View.GONE);


                    } else if (type.equals("video")) {
                        if (Myviewholder.is_video == true){
                            Myviewholder.exoPlayer.stop();
                        }
                        mvh.post_time.setText(showPostTime);
                        mvh.post_desc.setText(model.getTextBoxData());
                        mvh.post_image.setVisibility(View.GONE);
                        mvh.documentView.setVisibility(View.GONE);
                        mvh.play_icon.setVisibility(View.VISIBLE);

                        mvh.video_pic.setVisibility(View.VISIBLE);
                        // set video
                        mvh.video_view.setVisibility(View.GONE);
                        mvh.video_progress_bar.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(model.getPostURL())
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        mvh.video_progress_bar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        mvh.video_progress_bar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(mvh.video_pic);
//                        mvh.setVideo(postUrl, contextForIntent);

                    } else if (type.equals("pdf")) {
                        if (Myviewholder.is_video == true){
                            Myviewholder.exoPlayer.stop();
                        }
                        String filename = model.getFileName();

                        mvh.post_time.setText(showPostTime);
                        mvh.post_desc.setText(model.getTextBoxData());
                        mvh.video_view.setVisibility(View.GONE);
                        mvh.post_image.setVisibility(View.GONE);
                        mvh.documentView.setVisibility(View.VISIBLE);
                        mvh.fileName.setText(model.getFileName());
                        mvh.doc_img.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
                        mvh.video_progress_bar.setVisibility(View.GONE);
                        mvh.video_pic.setVisibility(View.GONE);
                        mvh.play_icon.setVisibility(View.GONE);

                        // set document

                        mvh.download_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                                contextForIntent.startActivity(browserIntent);
                            }
                        });
                    } else if (type.equals("ppt") || type.equals("vnd.openxmlformats-officedocument.presentationml.presentation") || type.equals("vnd.ms-powerpoint")) {
                        String filename = model.getFileName();

                        if (Myviewholder.is_video == true){
                            Myviewholder.exoPlayer.stop();
                        }
                        mvh.post_time.setText(showPostTime);
                        mvh.post_desc.setText(model.getTextBoxData());
                        mvh.video_view.setVisibility(View.GONE);
                        mvh.post_image.setVisibility(View.GONE);
                        mvh.documentView.setVisibility(View.VISIBLE);
                        mvh.fileName.setText(model.getFileName());
                        mvh.doc_img.setImageResource(R.drawable.ppt);
                        mvh.video_progress_bar.setVisibility(View.GONE);
                        mvh.video_pic.setVisibility(View.GONE);
                        mvh.play_icon.setVisibility(View.GONE);

                        // set document

                        mvh.download_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                                contextForIntent.startActivity(browserIntent);
                            }
                        });
                    } else if (type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") || type.equals("xlsx") || type.equals("application/vnd.ms-excel")) {
                        String filename = model.getFileName();

                        if (Myviewholder.is_video == true){
                            Myviewholder.exoPlayer.stop();
                        }
                        mvh.post_time.setText(showPostTime);
                        mvh.post_desc.setText(model.getTextBoxData());
                        mvh.video_view.setVisibility(View.GONE);
                        mvh.post_image.setVisibility(View.GONE);
                        mvh.documentView.setVisibility(View.VISIBLE);
                        mvh.fileName.setText(model.getFileName());
                        mvh.doc_img.setImageResource(R.drawable.excel);
                        mvh.video_progress_bar.setVisibility(View.GONE);
                        mvh.video_pic.setVisibility(View.GONE);
                        mvh.play_icon.setVisibility(View.GONE);

                        // set document

                        mvh.download_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                                contextForIntent.startActivity(browserIntent);
                            }
                        });
                    } else if (type.equals("application/msword") || type.equals("doc") || type.equals("docx") || type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                        String filename = model.getFileName();

                        if (Myviewholder.is_video == true){
                            Myviewholder.exoPlayer.stop();
                        }
                        mvh.post_time.setText(showPostTime);
                        mvh.post_desc.setText(model.getTextBoxData());
                        mvh.video_view.setVisibility(View.GONE);
                        mvh.post_image.setVisibility(View.GONE);
                        mvh.documentView.setVisibility(View.VISIBLE);
                        mvh.fileName.setText(model.getFileName());
                        mvh.doc_img.setImageResource(R.drawable.word);
                        mvh.video_progress_bar.setVisibility(View.GONE);
                        mvh.video_pic.setVisibility(View.GONE);
                        mvh.play_icon.setVisibility(View.GONE);

                        // set document

                        mvh.download_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                                contextForIntent.startActivity(browserIntent);
                            }
                        });
                    } else if (type.equals("none")) {

                        if (Myviewholder.is_video == true){
                            Myviewholder.exoPlayer.stop();
                        }
                        mvh.post_time.setText(showPostTime);
                        mvh.post_desc.setText(model.getTextBoxData());
                        mvh.video_view.setVisibility(View.GONE);
                        mvh.post_image.setVisibility(View.GONE);
                        mvh.documentView.setVisibility(View.GONE);
                        mvh.video_progress_bar.setVisibility(View.GONE);
                        mvh.video_pic.setVisibility(View.GONE);
                        mvh.play_icon.setVisibility(View.GONE);
                    } else {

                        if (Myviewholder.is_video == true){
                            Myviewholder.exoPlayer.stop();
                        }
                        String filename = model.getFileName();
                        mvh.post_time.setText(showPostTime);
                        mvh.post_desc.setText(model.getTextBoxData());
                        mvh.video_view.setVisibility(View.GONE);
                        mvh.post_image.setVisibility(View.GONE);
                        mvh.documentView.setVisibility(View.VISIBLE);
                        mvh.fileName.setText(model.getFileName());
                        mvh.doc_img.setImageResource(R.drawable.zip);
                        mvh.video_progress_bar.setVisibility(View.GONE);
                        mvh.video_pic.setVisibility(View.GONE);
                        mvh.play_icon.setVisibility(View.GONE);

                        // set document
                        mvh.download_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                                contextForIntent.startActivity(browserIntent);
                            }
                        });
                    }

                    mvh.setLikes(pId, CurrentUserId);
                    mvh.setComment(pId, CurrentUserId);
                    mvh.getActionLikeBtn(pId, CurrentUserId, model, contextForIntent, position, list);

                    mvh.play_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("POSTFiles");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(pId)){
                                        Intent intent = new Intent(contextForIntent, openVideoActivity.class);
                                        intent.putExtra("videoUrl", model.getPostURL());
                                        context.startActivity(intent);
                                    }
                                    else {
                                        list.remove(position);
                                        allPostAdapter.notifyItemRemoved(position);
                                        allPostAdapter.notifyItemRangeChanged(position, list.size());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });

                    mvh.comment_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("POSTFiles");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(pId)){
                                        Intent intent = new Intent(context, postDetailsActivity.class);
                                        intent.putExtra("pId", pId);
                                        context.startActivity(intent);
                                    }
                                    else {
                                        list.remove(position);
                                        allPostAdapter.notifyItemRemoved(position);
                                        allPostAdapter.notifyItemRangeChanged(position, list.size());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });

                    mvh.viewDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("POSTFiles");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(pId)){

                                        Intent intent = new Intent(context, postDetailsActivity.class);
                                        intent.putExtra("pId", pId);
                                        context.startActivity(intent);
                                    }
                                    else {
                                        list.remove(position);
                                        allPostAdapter.notifyItemRemoved(position);
                                        allPostAdapter.notifyItemRangeChanged(position, list.size());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });

                    mvh.post_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("POSTFiles");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(pId)){
                                        imageBottomSheet.show();
//                            ZoomInImageViewAttacher mIvAttacter = new ZoomInImageViewAttacher(imagebottom);
                                        Picasso.get().load(model.getPostURL()).into(imagebottom);
                                    }
                                    else {
                                        list.remove(position);
                                        allPostAdapter.notifyItemRemoved(position);
                                        allPostAdapter.notifyItemRangeChanged(position, list.size());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });


                    break;

                case item_Banner:
                    if (list.get(position) instanceof String) {

                        bannerADDViewholder bvh = (bannerADDViewholder) holder;
                        AdLoader.Builder builder = new AdLoader.Builder(
                                context, "ca-app-pub-3940256099942544/2247696110");

                        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                bvh.templateView.setNativeAd(unifiedNativeAd);
                            }
                        });

                        final AdLoader adLoader = builder.build();
                        adLoader.loadAd(new AdRequest.Builder().build());

                        break;

                    }

            }

            MainActivity.progressDialog.dismiss();


        }

        private void reportPost(String post_id) {

            String t = String.valueOf(System.currentTimeMillis());
            HashMap map = new HashMap();
            map.put("reportBy", CurrentUserId);
            map.put("postId", post_id);

            DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("ReportPost");
            reportRef.child(t).setValue(map);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        @Override
        public int getItemViewType(int position) {

            if (list.get(position) instanceof timeLine_model) {

                return item_data;

            } else {
                return item_Banner;
            }
        }

        public static class Myviewholder extends RecyclerView.ViewHolder {

            CircleImageView user_profile;
            TextView userName, post_time, post_desc, fileName, like_count, comment_count, viewDetails, copyUrl, share, bookmark, edit, delete, post_views, hashTag_lpa, hashtag_company, hashtag_realted, profession;
            ImageView bookmark_btn, post_image, download_btn, like_btn, comment_btn, share_btn, more_btn, doc_img, fullScreen, video_pic, play_icon;
            PlayerView video_view;
            CardView documentView;
            public static SimpleExoPlayer exoPlayer;
            boolean mPrecessLikes = false;
            private BottomSheetDialog moreBottomSheet;
            private LinearLayout delete_l, edit_l;
            TemplateView templateView;
            ProgressBar video_progress_bar;
            public static boolean is_video = false;

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

            public String getFormateDate(Context context, String postTime) {
                Calendar smsTime = Calendar.getInstance();
                smsTime.setTimeInMillis(Long.parseLong(postTime));

                Calendar now = Calendar.getInstance();

                final String timeFormatString = "h:mm aa";
                final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
                final long HOURS = 60 * 60 * 60;
                if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
                    return "" + DateFormat.format(timeFormatString, smsTime);
                } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
                    return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
                    return DateFormat.format(dateTimeFormatString, smsTime).toString();
                } else {
                    return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
                }
            }

            public void getUserInfo(String userId, timeLine_model model) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child("profile_image").exists()) {
                            String pUrl = snapshot.child("profile_image").getValue().toString();
                            model.setUserProfile(pUrl);
                            Picasso.get().load(model.getUserProfile()).into(user_profile);
                        } else {
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

                        if (snapshot.child("user_bio").exists()) {
                            profession.setVisibility(View.VISIBLE);
                            String userBio = snapshot.child("user_bio").getValue().toString();
                            model.setUserBio(userBio);
                            profession.setText(model.getUserBio());
                        } else {
                            profession.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            public void checkBookmarkPost(Myviewholder holder, String pId) {
                DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
                bookmarkREf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).hasChild(pId)) {
                            bookmark_btn.setImageResource(R.drawable.bookmark_fill);
                        } else {
                            bookmark_btn.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            public void setLikes(String pId, String currentUserId) {

                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(pId).hasChild(currentUserId)) {

                            int likeCount = (int) snapshot.child(pId).getChildrenCount();
                            like_count.setText(likeCount + " ");
                            like_btn.setImageResource(R.drawable.favorite);

                        } else {
                            int likeCount = (int) snapshot.child(pId).getChildrenCount();
                            like_count.setText(likeCount + " ");
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
                        comment_count.setText("" + count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            public void getActionLikeBtn(String pId, String currentUserId, timeLine_model model, Context context, int position, ArrayList<Object> a_list) {

                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");
                DatabaseReference re  = FirebaseDatabase.getInstance().getReference("POSTFiles");

                like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mPrecessLikes = true;

                        re.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(pId)){

                                    likeRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if (mPrecessLikes == true) {

                                                if (snapshot.child(pId).hasChild(currentUserId)) {
                                                    likeRef.child(pId).child(currentUserId).removeValue();
                                                    deleteNotification(model.getUserId(), pId, currentUserId);
                                                    mPrecessLikes = false;
                                                } else {
                                                    likeRef.child(pId).child(currentUserId).child("uId").setValue(currentUserId);
                                                    mPrecessLikes = false;

                                                    if (!model.getUserId().equals(currentUserId)) {

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
                                else {

                                    a_list.remove(position);
                                    allPostAdapter.notifyItemRemoved(position);
                                    allPostAdapter.notifyItemRangeChanged(position, a_list.size());
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
                                is_video = false;
                            }
                            else if (playbackState == Player.STATE_READY){
                                video_progress_bar.setVisibility(View.GONE);
                                is_video = true;
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
                postviewsRef.child(pId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int countViews = (int) snapshot.getChildrenCount();
                            holder.post_views.setText("" + countViews);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        public static class bannerADDViewholder extends RecyclerView.ViewHolder {
            TemplateView templateView;

            public bannerADDViewholder(@NonNull View itemView) {
                super(itemView);
                templateView = itemView.findViewById(R.id.my_template);

            }
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
                imagebottom.setScaleX(mScaleFactor);
                imagebottom.setScaleY(mScaleFactor);
                return true;
            }
        }
    }

    private static void sendPushNotificationToUser(String userId, String currentUserId, Context context, String textBoxData, String postURL, String pid, String type) {
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

                            if (dataSnapshot.child("profile_image").exists()) {
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

    private static void sendNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", to, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
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

        RequestQueue queue = Volley.newRequestQueue(contextForIntent);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    private static void deleteNotification(String userId, String pId, String currentUserId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userId).child("Notifications").orderByChild("sUid").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String message = "" + ds.child("type").getValue().toString();
                    String postid = "" + ds.child("pId").getValue().toString();

                    if (postid.equals(pId) && message.equals("likeP")) {
                        ds.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void addToHisNotification(String hisUid, String pId, String message, String currentUserId, String type) {
        String timestamp = "" + System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();

        hashMap.put("pId", pId);
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

    private void CheckNewPost() {
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                RootRef.child("PostViews").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String key = ds.getKey();
                            RootRef.child("PostViews").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.child(CurrentUserId).exists()) {

                                        SharedPreferences.Editor edit = pref.edit();
                                        edit.putInt("count", (int) s.getChildrenCount());
                                        edit.apply();

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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        RootRef.child("PostViews").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    RootRef.child("PostViews").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(CurrentUserId).exists()) {
                                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int p_count = (int) snapshot.getChildrenCount();
                                        int c_count = pref.getInt("count", 0);
                                        if (c_count == p_count) {
                                            if (Visible != false && click != 1) {

                                                new_Post.startAnimation(MainActivity.anime);
                                                new_Post.setVisibility(View.GONE);
                                                Visible = false;
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        CheckNewPost();

        RootRef.child("LastMessage").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("isSeen").getValue().toString().equals("0")) {
                            i++;
                        }

                    }
                    if (i != 0) {
                        tv_count.setVisibility(View.VISIBLE);
                        tv_count.setText("" + i);
                    } else if (i == 0) {
                        tv_count.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                                RootRef.child("Messages").child(data).child(CurrentUserId).orderByChild("isSeen").equalTo("0").addListenerForSingleValueEvent(new ValueEventListener() {
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
                                RootRef.child("Messages").child(CurrentUserId).child(data).orderByChild("isSeen").equalTo("0").addListenerForSingleValueEvent(new ValueEventListener() {
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

}