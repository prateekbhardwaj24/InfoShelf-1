package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.card.infoshelf.bottomfragment.nearByModel;
import com.card.infoshelf.bottomfragment.networkModel;
import com.card.infoshelf.bottomfragment.network_adaptor;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class allUserActivity extends AppCompatActivity {

    private RecyclerView allUserRecycler, nearByRecycler;
    private ArrayList<networkModel> list;
    private allUserAdaptor adaptor;
    private allNearByUserAdapter nearByAdapter;
    private ArrayList<nearByModel> nearList;
    private DatabaseReference userInfoRef, blockListRef;
    private String type;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AdView mAdView;
    private TextView no_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        type = getIntent().getStringExtra("type");


        if (type.equals("recommended")){
            setTitle("Recommended For You");
        }
        else if (type.equals("umk")){
            setTitle("Peoples You May Know");
        }
        else {
            if (type.equals("nearBy")){
                setTitle("Peoples Near By");
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUserRecycler = findViewById(R.id.allUserRecycler);
        nearByRecycler = findViewById(R.id.nearByRecycler);
        no_user = findViewById(R.id.no_user);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");
        blockListRef = FirebaseDatabase.getInstance().getReference("BlockList");

        allUserRecycler.setHasFixedSize(true);
        allUserRecycler.setLayoutManager(new LinearLayoutManager(this));
        nearByRecycler.setHasFixedSize(true);
        nearByRecycler.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        nearList = new ArrayList<>();
        adaptor = new allUserAdaptor(this,list);
        nearByAdapter = new allNearByUserAdapter(this, nearList);
        allUserRecycler.setAdapter(adaptor);
        nearByRecycler.setAdapter(nearByAdapter);

        if (type.equals("recommended")){

            allUserRecycler.setVisibility(View.VISIBLE);
            nearByRecycler.setVisibility(View.GONE);


            ArrayList<String> listBlock1 = new ArrayList<>();

            blockListRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                    for (DataSnapshot ds : dataSnapshot1.getChildren()){
                        String key = ds.getKey();
                        listBlock1.add(key);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            userInfoRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String getProf = snapshot.child("profession").getValue().toString();

                    userInfoRef.orderByChild("profession").equalTo(getProf).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){


                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                    String id = ""+dataSnapshot.child("userId").getValue().toString();

                                    if (!id.equals(CurrentUserId)){

                                        if (listBlock1.contains(id)) {
                                        }
                                        else {
                                            networkModel user = dataSnapshot.getValue(networkModel.class);
                                            list.add(user);
                                        }
                                    }
                                }
                                if (list.size() > 0){
                                    no_user.setVisibility(View.GONE);
                                }
                                else {
                                    no_user.setVisibility(View.VISIBLE);
                                }
                                Collections.shuffle(list);
                                adaptor.notifyDataSetChanged();
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
        }
        else if (type.equals("umk")){

            allUserRecycler.setVisibility(View.VISIBLE);
            nearByRecycler.setVisibility(View.GONE);


            ArrayList<String> listBlock2 = new ArrayList<>();

            blockListRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                    for (DataSnapshot ds : dataSnapshot1.getChildren()){
                        String key = ds.getKey();
                        listBlock2.add(key);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String id = ""+dataSnapshot.child("userId").getValue().toString();

                        if (!id.equals(CurrentUserId)){

                            if (listBlock2.contains(id)) {
                            }
                            else {
                                networkModel user = dataSnapshot.getValue(networkModel.class);
                                list.add(user);
                            }
                        }
                    }
                    Collections.shuffle(list);
                    adaptor.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            if (type.equals("nearBy")){

                allUserRecycler.setVisibility(View.GONE);
                nearByRecycler.setVisibility(View.VISIBLE);
                getUsersLocation();
            }
        }
        // applying banner ads
        BannerAds();
    }

    private void BannerAds() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void getUsersLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();

        } else {

//            ActivityCompat.requestPermissions(SendCurrentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

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

                            double latitude = location1.getLatitude();
                            double longitude = location1.getLongitude();

                            getNearByUsers(latitude, longitude);
                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                }
            });
        }else {
            this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }

    private void getNearByUsers(double mlatitude, double mlongitude) {

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


        DatabaseReference userREf = FirebaseDatabase.getInstance().getReference("Users");
        userREf.orderByChild("permission").equalTo("Granted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    for (DataSnapshot ds : snapshot.getChildren()){

                        double lat = Double.parseDouble(""+ds.child("latitude").getValue().toString());
                        double longi = Double.parseDouble(""+ds.child("longitude").getValue().toString());
                        String id = ""+ds.child("userId").getValue().toString();

                        if (!id.equals(CurrentUserId)){

                            float[] results = new float[1];
                            Location.distanceBetween(mlatitude, mlongitude, lat, longi, results);
                            float distance = results[0];

                            if (distance < 10000){

                                if (listBlock.contains(id)) {
                                }
                                else {
                                    nearByModel model = ds.getValue(nearByModel.class);
                                    nearList.add(model);
                                }
                            }
                        }
                    }
                    if (nearList.size() > 0){
                        no_user.setVisibility(View.GONE);
                    }
                    else {
                        no_user.setText("There are no user in radius of 10kms");
                        no_user.setVisibility(View.VISIBLE);
                    }
                    nearByAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (type.equals("nearBy")){
                    filterNearBy(newText);
                }
                else {
                    filter(newText);
                }

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterNearBy(String newText) {
        ArrayList<nearByModel> filterList = new ArrayList<>();

        for (nearByModel item : nearList){
            if (item.getUserName().toLowerCase().contains(newText.toLowerCase())){
                filterList.add(item);
            }
        }
        nearByAdapter.filterList(filterList);
    }

    private void filter(String newText) {

        ArrayList<networkModel> filterList = new ArrayList<>();

        for (networkModel item : list){
            if (item.getUserName().toLowerCase().contains(newText.toLowerCase())){
                filterList.add(item);
            }
        }
        adaptor.filterList(filterList);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
}