package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.card.infoshelf.bottomfragment.AddTimeline;
import com.card.infoshelf.bottomfragment.NetworkFragment;
import com.card.infoshelf.bottomfragment.NotificationFragment;
import com.card.infoshelf.bottomfragment.Profilefragment;
import com.card.infoshelf.bottomfragment.TimelineFragment;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DatabaseReference userREf;
    private FirebaseAuth mAuth;
    private String CurrentUserId, token;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final Handler mHandler = new Handler();
    public static Animation animation, anime;
    public static BadgeDrawable badge, posBadge;
    private SharedPreferences pref;
    private AppUpdateManager appUpdateManager;
    private static final int RC_APP_UPDATE = 100;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("notification_all");

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        animation = AnimationUtils.loadAnimation(this, R.anim.new_timeline_animation);
        anime = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);

        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            token = Objects.requireNonNull(task.getResult()).getToken();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(CurrentUserId).child("token").setValue(token);

                        }

                    }
                });

//        mFunctionRepeat.run();

        getSupportActionBar().hide();

//        ActivityCompat.requestPermissions(this, new String[] {
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.INTERNET,
//                Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        int menuItemId = bottomNavigationView.getMenu().getItem(3).getItemId();
        int postItem = bottomNavigationView.getMenu().getItem(0).getItemId();
        badge = bottomNavigationView.getOrCreateBadge(menuItemId);
        posBadge = bottomNavigationView.getOrCreateBadge(postItem);
        badge.setVisible(false);
        posBadge.setVisible(false);

//        badge.setNumber(2);
//        badge.clearNumber();
        mAuth = FirebaseAuth.getInstance();
        userREf = FirebaseDatabase.getInstance().getReference("Users");
        CurrentUserId = mAuth.getCurrentUser().getUid();

        pref = getPreferences(Context.MODE_PRIVATE);

        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new TimelineFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.timeline);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                switch (item.getItemId()){

                    case R.id.timeline:
                        fragment = new TimelineFragment();
//                        updateBadge();
                        break;
                    case R.id.network:
                        fragment = new NetworkFragment();
                        break;
                    case R.id.addTimeline:
                        fragment = new AddTimeline();
                        break;
                    case R.id.notification:
                        fragment = new NotificationFragment();
//                        Intent intent = new Intent(MainActivity.this, InternalNotificationActivity.class);
//                        startActivity(intent);
                        break;
                    case R.id.profile:
                        fragment = new Profilefragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                return true;
            }
        });

        getUserLocation();


//        appUpdateManager.registerListener(installStateUpdatedListener);

    }

    private void updateBadge() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("count", (int) snapshot.getChildrenCount());
                edit.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED){
                showCompletedUpdate();
            }
        }
    };

    private void showCompletedUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New app is ready", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RC_APP_UPDATE && resultCode != RESULT_OK){

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getPostBadge() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    int c = (int) snapshot.getChildrenCount();
                    int id = TimelineFragment.id;
                    Log.d("shareId", "id"+String.valueOf(id)+" postId"+ snapshot.getChildrenCount());

                    if (c > id){
                        int post_badgeC = c-id;
                        posBadge.setNumber(post_badgeC);
                        posBadge.setVisible(true);
                    }
                    else {
                        posBadge.setVisible(false);
                    }
                }
                else {
                    posBadge.setVisible(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Runnable mFunctionRepeat = new Runnable() {
        @Override
        public void run() {
           getUserLocation();
//            Log.d(SCREEN_TOGGLE_TAG, "Code Repeat");
            mHandler.postDelayed(this, 5000);
        }
    };

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(MainActivity.this);

        } else {
//            ActivityCompat.requestPermissions(SendCurrentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(MainActivity mainActivity) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

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

                            uploadLocation(latitude, longitude);

                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                }
            });
        }else {
            this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }

    private void uploadLocation(double latitude, double longitude) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId);
        userRef.child("longitude").setValue(longitude);
        userRef.child("latitude").setValue(latitude);
        userRef.child("permission").setValue("Granted");
    }

    private void checkNotification(BadgeDrawable badge) {
        userREf.child(CurrentUserId).child("Notifications").orderByChild("status").equalTo("0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    int noti_badge = (int) snapshot.getChildrenCount();

                    if (noti_badge > 0){
                        badge.setNumber(noti_badge);
                        badge.setVisible(true);

                    }
                    else {
                        badge.setVisible(false);
                    }
                }
                else {
                    badge.setVisible(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void  status (String status){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user !=null){

            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", CurrentUserId);
            editor.apply();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap map = new HashMap();
        map.put("timeStamp" , timeStamp);
        map.put("status" , status);

        ref.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkUserStatus();
        status("online");
        getPostBadge();
        checkNotification(badge);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            status("offline");
        }

    }

}