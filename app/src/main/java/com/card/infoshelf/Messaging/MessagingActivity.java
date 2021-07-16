package com.card.infoshelf.Messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.card.infoshelf.Messenger.MessengerActivity;
import com.card.infoshelf.R;
import com.card.infoshelf.userProfileActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    ImageView iv_back, btn_send, select_file, menu;
    CircleImageView profile_image;
    TextView user_name, status, tv_msg, tv_add;
    EditText et_msg;
    private RecyclerView msg_rv;
    LinearLayoutManager linearLayoutManager;
    String name, image, MessageSenderID, MessageReceiverID , state;
    private ArrayList<MessageModel> list;
    private MessageAdaptor adaptor;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, ref, typingRef , rr;
    private final int i = 0;
    private RelativeLayout l2, rl;

    private BottomSheetDialog bottomSheetDialog;

    private Uri FileUri;
    private String myUrl = "";
    private String checker, Extension, displayName, size, profileImage , FileType;
    private int displaysize = 0;
    private StorageTask uploadTask;

    private ValueEventListener seenListener, notificationListener;

    private final boolean isTyping = false;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        getSupportActionBar().hide();

        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);
        status = findViewById(R.id.status);
        iv_back = findViewById(R.id.iv_back);
        btn_send = findViewById(R.id.btn_send);
        et_msg = findViewById(R.id.et_msg);
        select_file = findViewById(R.id.select_file);
        menu = findViewById(R.id.menu);
        l2 = findViewById(R.id.l2);
        rl = findViewById(R.id.rl);
        tv_add = findViewById(R.id.tv_add);
        tv_msg = findViewById(R.id.tv_msg);

        linearLayoutManager = new LinearLayoutManager(this);
        msg_rv = findViewById(R.id.msg_rv);
        msg_rv.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);


        list = new ArrayList<>();
        adaptor = new MessageAdaptor(this, list);
        msg_rv.setAdapter(adaptor);

        progressDialog = new ProgressDialog(MessagingActivity.this);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog1 = new ProgressDialog(MessagingActivity.this);
        progressDialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog1.setCanceledOnTouchOutside(false);


        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        ProgressBar progressBar = progressDialog.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);




        MessageReceiverID = getIntent().getStringExtra("userid");
        state = getIntent().getStringExtra("state");


        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagingActivity.this, userProfileActivity.class);
                intent.putExtra("userid", MessageReceiverID);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MessagingActivity.this, v);
                popupMenu.inflate(R.menu.menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view_profile:
                                Intent intent = new Intent(MessagingActivity.this, userProfileActivity.class);
                                intent.putExtra("userid", MessageReceiverID);
                                startActivity(intent);
                                return true;
                            case R.id.clear_chat:
                                AlertDialog builder1 = new AlertDialog.Builder(MessagingActivity.this).create();
                                View view = LayoutInflater.from(MessagingActivity.this).inflate(R.layout.clear_chat_dialog , null);
                                builder1.setView(view);

                                builder1.show();
                                TextView clear_chat = view.findViewById(R.id.clear_chat);
                                TextView cancel = view.findViewById(R.id.cancel);
                                clear_chat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ClearChat();
                                        builder1.dismiss();

                                    }
                                });
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        builder1.dismiss();
                                    }
                                });
                                return true;


                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });

        mAuth = FirebaseAuth.getInstance();
        MessageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        typingRef = FirebaseDatabase.getInstance().getReference("typing");

        RootRef.child("Users").child(MessageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String name = snapshot.child("userName").getValue().toString();
                    user_name.setText(name);
                    if (snapshot.child("profile_image").exists())
                    {
                        String p_image = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(p_image).placeholder(R.drawable.profile).into(profile_image);
                    }else {
                        Picasso.get().load(R.drawable.profile).placeholder(R.drawable.profile).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        user_name.setText(name);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagingActivity.this, MessengerActivity.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Typing(false);
                SendMessage();
            }
        });

        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MessagingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MessagingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    return;
                }
                ShowBottomSheet();
            }
        });

        RootRef.child("Users").child(MessageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    typingRef.child(MessageSenderID).child(MessageReceiverID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot s) {
                            if (s.exists()) {
                                Boolean sts = (Boolean) s.child("isTyping").getValue();
                                if (sts) {
                                    status.setText("typing...");
                                } else {
                                    String st = snapshot.child("status").getValue().toString();
                                    String time = snapshot.child("timeStamp").getValue().toString();
                                    if (st.equals("online")) {
                                        status.setText(st);
                                    }
                                    if (st.equals("offline")) {
                                        String lastSeen = getFormateDate(getApplicationContext() , time);
                                        status.setText("Last Seen : "+lastSeen);
                                    }
                                }
                            } else {
                                String stt = snapshot.child("status").getValue().toString();
                               String time = snapshot.child("timeStamp").getValue().toString();
                                if (stt.equals("online")) {
                                    status.setText(stt);
                                }
                                if (stt.equals("offline")) {
                                    String lastSeen = getFormateDate(getApplicationContext() , time);
                                    status.setText("Last Seen : "+lastSeen);
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






        TypingStatus();


    }

    private String getFormateDate(Context context, String msgTime) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(Long.parseLong(msgTime));

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today at " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday at " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }
    private void ClearChat() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(MessageReceiverID).child(MessageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    rootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).removeValue();
                    rootRef.child("ChatList").child(MessageSenderID).child(MessageReceiverID).removeValue();
                    Toast.makeText(MessagingActivity.this, "Chat Deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MessagingActivity.this, MessengerActivity.class);
                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    rootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String type = ds.child("type").getValue().toString();
                                String msg = ds.child("message").getValue().toString();
                                if (type.equals("image") || type.equals("doc")) {
                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference ref = firebaseStorage.getReferenceFromUrl(msg);
                                    ref.delete();
                                    ds.getRef().removeValue();

                                } else {

                                    ds.getRef().removeValue();
                                }
                            }
                            rootRef.child("ChatList").child(MessageSenderID).child(MessageReceiverID).removeValue();
                            Toast.makeText(MessagingActivity.this, "Chat Deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MessagingActivity.this, MessengerActivity.class);
                            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


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




    private void TypingStatus() {
        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Typing(s.toString().trim().length() != 0);


            }

            @Override
            public void afterTextChanged(Editable s) {



            }
        });
    }

    private void Typing(boolean b) {
        typingRef.child(MessageReceiverID).child(MessageSenderID).child("isTyping").setValue(b);
    }

    private void isSeen() {
        ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(MessageReceiverID).child(MessageSenderID);
        seenListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MessageModel model = dataSnapshot.getValue(MessageModel.class);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isSeen", "1");
                        dataSnapshot.getRef().updateChildren(map);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rr = FirebaseDatabase.getInstance().getReference().child("LastMessage").child(MessageSenderID).child(MessageReceiverID);
        notificationListener = rr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    rr.child("isSeen").setValue("1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.message_bottom_sheet, null);

        view.findViewById(R.id.r1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Select Document"), 1);

                checker = "Doc";

                bottomSheetDialog.dismiss();
            }
        });

        view.findViewById(R.id.r2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

                checker = "image";

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);


        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog = null;
            }
        });
        bottomSheetDialog.show();
    }
    private void getExtensionFromUri(Uri uri) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        FileType = mimeTypeMap.getExtensionFromMimeType(MessagingActivity.this.getContentResolver().getType(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            progressDialog1.show();
            progressDialog1.setContentView(R.layout.progress_dialog);
            TextView tv = progressDialog.findViewById(R.id.tvtv);
            tv.setText("Sending..");

            ProgressBar progressBar1 = progressDialog1.findViewById(R.id.spin_kit);
            Sprite doubleBounce = new Wave();
            progressBar1.setIndeterminateDrawable(doubleBounce);

            FileUri = data.getData();
            String uriString = FileUri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();


            Cursor cursor = null;
            try {
                cursor = getApplication().getContentResolver().query(FileUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    displaysize = cursor.getColumnIndex(OpenableColumns.SIZE);
                    size = String.valueOf(displaysize);


                }

            } finally {
                cursor.close();
            }


            if (!checker.equals("image")) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                et_msg.setText("");
                final String CurrentTime, CurrentDate;
                final String messageSenderRef = "Messages/" + MessageSenderID + "/" + MessageReceiverID;
                final String messageReceiverRef = "Messages/" + MessageReceiverID + "/" + MessageSenderID;

                Calendar date = Calendar.getInstance();
                SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("dd MMM , yyyy");
                CurrentDate = CurrentDateFormat.format(date.getTime());


                Calendar time = Calendar.getInstance();
                SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
                CurrentTime = CurrentTimeFormat.format(time.getTime());

                String timestamp = String.valueOf(System.currentTimeMillis());


                DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).push();


                final String messagePushID = userMessageKeyRef.getKey();



                final StorageReference filePath = storageReference.child(timestamp).child(displayName);

                filePath.putFile(FileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", task.getResult().toString());
                                    messageTextBody.put("name", displayName);
                                    messageTextBody.put("size", size);
                                    messageTextBody.put("type", "doc");
                                    messageTextBody.put("from", MessageSenderID);
                                    messageTextBody.put("to", MessageReceiverID);
                                    messageTextBody.put("messageID", messagePushID);
                                    messageTextBody.put("timestamp", timestamp);
                                    messageTextBody.put("time", CurrentTime);
                                    messageTextBody.put("date", CurrentDate);
                                    messageTextBody.put("isSeen", "0");

                                    Map messageTextBody1 = new HashMap();
                                    messageTextBody1.put("message", task.getResult().toString());
                                    messageTextBody1.put("name", displayName);
                                    messageTextBody1.put("size", size);
                                    messageTextBody1.put("type", "doc");
                                    messageTextBody1.put("from", MessageSenderID);
                                    messageTextBody1.put("to", MessageReceiverID);
                                    messageTextBody1.put("messageID", messagePushID);
                                    messageTextBody1.put("timestamp", timestamp);
                                    messageTextBody1.put("time", CurrentTime);
                                    messageTextBody1.put("date", CurrentDate);
                                    messageTextBody1.put("isSeen", "1");

                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                    messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody1);

                                    RootRef.updateChildren(messageBodyDetails);
                                    UpdateChatList(timestamp);
                                    progressDialog1.dismiss();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog1.dismiss();
                        Toast.makeText(MessagingActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                    }
                });
                UpdateChatListAndLastMessage("Send a Document");

                RootRef.child("OnActivity").child(MessageSenderID).child(MessageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String state = snapshot.child("state").getValue().toString();
                            if (state.equals("0"))
                            {
                                getToken("Send a Document" , MessageSenderID , MessageReceiverID);
                            }
                            else {

                            }
                        }else {
                            getToken("Send a Document" , MessageSenderID , MessageReceiverID);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
            else if (checker.equals("image")) {


                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                et_msg.setText("");
                final String CurrentTime, CurrentDate;
                final String messageSenderRef = "Messages/" + MessageSenderID + "/" + MessageReceiverID;
                final String messageReceiverRef = "Messages/" + MessageReceiverID + "/" + MessageSenderID;

                Calendar date = Calendar.getInstance();
                SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("dd MMM , yyyy");
                CurrentDate = CurrentDateFormat.format(date.getTime());


                Calendar time = Calendar.getInstance();
                SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
                CurrentTime = CurrentTimeFormat.format(time.getTime());

                String timestamp = String.valueOf(System.currentTimeMillis());


                DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).push();


                final String messagePushID = userMessageKeyRef.getKey();


                final StorageReference filePath = storageReference.child(timestamp).child(displayName);

                final File file = new File(SiliCompressor.with(MessagingActivity.this).compress(FileUtils.getPath(MessagingActivity.this, FileUri), new File(MessagingActivity.this.getCacheDir(), "temp")));
                Uri outputUri = Uri.fromFile(file);

                uploadTask = filePath.putFile(outputUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();


                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", displayName);
                            messageTextBody.put("size", size);
                            messageTextBody.put("type", "image");
                            messageTextBody.put("from", MessageSenderID);
                            messageTextBody.put("to", MessageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("timestamp", timestamp);
                            messageTextBody.put("time", CurrentTime);
                            messageTextBody.put("date", CurrentDate);
                            messageTextBody.put("isSeen", "0");


                            Map messageTextBody1 = new HashMap();
                            messageTextBody1.put("message", myUrl);
                            messageTextBody1.put("name", displayName);
                            messageTextBody1.put("size", size);
                            messageTextBody1.put("type", "image");
                            messageTextBody1.put("from", MessageSenderID);
                            messageTextBody1.put("to", MessageReceiverID);
                            messageTextBody1.put("messageID", messagePushID);
                            messageTextBody1.put("timestamp", timestamp);
                            messageTextBody1.put("time", CurrentTime);
                            messageTextBody1.put("date", CurrentDate);
                            messageTextBody1.put("isSeen", "1");

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody1);

                            UpdateChatList(timestamp);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        progressDialog1.dismiss();
                                    } else {
                                        progressDialog1.dismiss();
                                        Toast.makeText(MessagingActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    }
                });

                RootRef.child("OnActivity").child(MessageSenderID).child(MessageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String state = snapshot.child("state").getValue().toString();
                            if (state.equals("0"))
                            {
                                getToken("Send a Image" , MessageSenderID , MessageReceiverID);
                            }
                            else {

                            }
                        }else {
                            getToken("Send a Image" , MessageSenderID , MessageReceiverID);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                UpdateChatListAndLastMessage("Send a Image");


            }


        }


    }

    private void ShowMessages() {
        RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        MessageModel model = ds.getValue(MessageModel.class);
                        list.add(model);
                    }
                    adaptor.notifyDataSetChanged();
                    msg_rv.smoothScrollToPosition(msg_rv.getAdapter().getItemCount() - 1);
                    progressDialog.dismiss();

                }else {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SendMessage() {

        String MessageText = et_msg.getText().toString().trim();


        if (TextUtils.isEmpty(MessageText)) {
            Toast.makeText(this, "Write Message First.....", Toast.LENGTH_SHORT).show();
        } else {
            et_msg.setText("");

            String CurrentTime, CurrentDate;
            String messageSenderRef = "Messages/" + MessageSenderID + "/" + MessageReceiverID;
            String messageReceiverRef = "Messages/" + MessageReceiverID + "/" + MessageSenderID;

            Calendar date = Calendar.getInstance();
            SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("dd MMM , yyyy");
            CurrentDate = CurrentDateFormat.format(date.getTime());


            Calendar time = Calendar.getInstance();
            SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
            CurrentTime = CurrentTimeFormat.format(time.getTime());

            String timestamp = String.valueOf(System.currentTimeMillis());


            DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).push();


            String messagePushID = userMessageKeyRef.getKey();



            Map messageTextBody = new HashMap();
            messageTextBody.put("message", MessageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", MessageSenderID);
            messageTextBody.put("to", MessageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("timestamp", timestamp);
            messageTextBody.put("time", CurrentTime);
            messageTextBody.put("date", CurrentDate);
            messageTextBody.put("isSeen", "0");

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);


            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        UpdateChatList(timestamp);

                    } else {


                    }

                }
            });
            UpdateChatListAndLastMessage(MessageText);

            RootRef.child("OnActivity").child(MessageSenderID).child(MessageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        String state = snapshot.child("state").getValue().toString();
                        if (state.equals("0"))
                        {
                            getToken(MessageText , MessageSenderID , MessageReceiverID);

                        }
                        else {

                        }
                    }else {
                        getToken(MessageText , MessageSenderID , MessageReceiverID);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    private void UpdateChatList(String timestamp) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatList");
        HashMap map = new HashMap();
        map.put("userId", MessageReceiverID);
        map.put("time", timestamp);
        ref.child(MessageSenderID).child(MessageReceiverID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap map1 = new HashMap();
                map1.put("userId", MessageSenderID);
                map1.put("time", timestamp);

                ref.child(MessageReceiverID).child(MessageSenderID).setValue(map1);
            }
        });
    }


    private void UpdateChatListAndLastMessage(String data){
        String CurrentTime  , MessageText;
        String timestamp = String.valueOf(System.currentTimeMillis());
        MessageText = data;
        Calendar time = Calendar.getInstance();
        SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
        CurrentTime = CurrentTimeFormat.format(time.getTime());

        HashMap map = new HashMap();
        map.put("timestamp", timestamp);
        map.put("lastMessag", data);
        map.put("sender", MessageSenderID);
        map.put("receiver", MessageReceiverID);
        map.put("isSeen", "0");
        map.put("count", "0");

        RootRef.child("LastMessage").child(MessageReceiverID).child(MessageSenderID).setValue(map);
        updateMessageCountReceiver();


        HashMap map1 = new HashMap();
        map1.put("timestamp", timestamp);
        map1.put("lastMessag", data);
        map1.put("sender", MessageSenderID);
        map1.put("receiver", MessageReceiverID);
        map1.put("isSeen", "1");
        map1.put("count", "0");

        RootRef.child("LastMessage").child(MessageSenderID).child(MessageReceiverID).setValue(map1);
        updateMessageCountSender();

    }

    private void updateMessageCountSender() {
        RootRef.child("Messages").child(MessageReceiverID).child(MessageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RootRef.child("Messages").child(MessageReceiverID).child(MessageSenderID).orderByChild("to").equalTo(MessageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                RootRef.child("Messages").child(MessageReceiverID).child(MessageSenderID).orderByChild("isSeen").equalTo("0").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        long count = s.getChildrenCount();
                                        String c = String.valueOf(count);
                                        RootRef.child("LastMessage").child(MessageSenderID).child(MessageReceiverID).child("count").setValue(c);


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

    private void updateMessageCountReceiver() {
        RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).orderByChild("to").equalTo(MessageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).orderByChild("isSeen").equalTo("0").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        long count = s.getChildrenCount();
                                        String c = String.valueOf(count);
                                        RootRef.child("LastMessage").child(MessageReceiverID).child(MessageSenderID).child("count").setValue(c);


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


    private void  status (String status){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("timeStamp", timeStamp);
        onlineState.put("status", status);

        ref.updateChildren(onlineState);
    }

    private void DataChange() {
        RootRef.child("LastMessage").child(MessageSenderID).child(MessageReceiverID).child("isSeen").setValue("1");
        RootRef.child("LastMessage").child(MessageSenderID).child(MessageReceiverID).child("count").setValue("0");

    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowMessages();
        status("online");
        isSeen();
        DataChange();
        CurrentUser(MessageSenderID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(seenListener);
        rr.removeEventListener(notificationListener);
        status("offline");
        Typing(false);
        CurrentUser("none");
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference();
        ref.child("OnActivity").child(MessageReceiverID).child(MessageSenderID).child("state").setValue("0");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (state.equals("UnFriend"))
        {
            rl.setVisibility(View.GONE);
            et_msg.setVisibility(View.GONE);
            btn_send.setVisibility(View.GONE);
            tv_msg.setVisibility(View.VISIBLE);
            tv_add.setVisibility(View.VISIBLE);

        }else if (status.equals("Blocked"))
        {
            rl.setVisibility(View.GONE);
            et_msg.setVisibility(View.GONE);
            btn_send.setVisibility(View.GONE);
            tv_msg.setVisibility(View.VISIBLE);
            tv_msg.setText("You Blocked This Person \n To start Conversation Again , UnBlock Him");
            tv_add.setVisibility(View.VISIBLE);
            tv_add.setText("UnBlock");



        }else if (state.equals("BlockedBy"))
        {
            rl.setVisibility(View.GONE);
            et_msg.setVisibility(View.GONE);
            btn_send.setVisibility(View.GONE);
            tv_msg.setVisibility(View.VISIBLE);
            tv_msg.setText("You are Blocked By This Person \n Can't Message Him Now");

        }

        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference();
        ref.child("OnActivity").child(MessageReceiverID).child(MessageSenderID).child("state").setValue("1");

    }

    private void CurrentUser(String uid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentUser", MessageSenderID);
        editor.apply();

    }

    private void getToken (String message , String hisId , String ChatId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(ChatId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();

                RootRef.child("Users").child(hisId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String senderName = snapshot.child("userName").getValue().toString();

                        if (snapshot.child("profile_image").exists()){
                            profileImage = snapshot.child("profile_image").getValue().toString();
                        }

                        JSONObject to = new JSONObject();
                        JSONObject data = new JSONObject();

                        try {
                            data.put("title" , senderName);
                            data.put("body" , message);
                            data.put("sender1" , hisId);
                            data.put("type", "chat");
                            data.put("state", state);
                            data.put("userProfile",profileImage);


                            to.put("to" , token);
                            to.put("data" , data);

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST , "https://fcm.googleapis.com/fcm/send" , to , response -> {

        },error -> {

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> map = new HashMap<>();
                map.put("Authorization" , "key = AAAAXb76uxw:APA91bFE4oCMF8hCE9JLM_R_aLOAT838pI0-GHtpcZPB876r8bHF_aXQ2dOb9LCCytmN_G8UOxfTZx69HZrnB1aZjEAs8etZOYmFdokaGj3azt5OKI0sO01PrKZMXTrad2UbNLEnGFlo");
                map.put("Content+Type" , "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(0 ,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}