package com.card.infoshelf.Block;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.TagModel;
import com.card.infoshelf.bottomfragment.networkModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class block_adapter extends RecyclerView.Adapter<block_adapter.Myviewholder> {

    private final Context context;
    private final ArrayList<TagModel> list;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private Dialog blockDialog;
    private Button yes, no;
    private ImageView blockIcon;
    private TextView hint;

    public block_adapter(Context context, ArrayList<TagModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.blocked_user_layout,parent,false);
        return new Myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder holder, int position) {

        blockDialog = new Dialog(context);
        blockDialog.setContentView(R.layout.block_dialog);
        blockDialog.setCancelable(false);

        yes = blockDialog.findViewById(R.id.yes);
        no = blockDialog.findViewById(R.id.no);
        blockIcon = blockDialog.findViewById(R.id.blockIcon);
        hint= blockDialog.findViewById(R.id.hint);

        TagModel user = list.get(position);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        Picasso.get().load(user.getProfile_image()).into(holder.userProfile);
        holder.username.setText(user.getUserName());

        holder.unblock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hint.setText("Are You Sure To Unblock This Person");

                blockDialog.show();

            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference BlockedREf = FirebaseDatabase.getInstance().getReference("BlockList");
                BlockedREf.child(CurrentUserId).child(user.getUserId()).removeValue();

                DatabaseReference FriendsREf = FirebaseDatabase.getInstance().getReference("Friends");
                FriendsREf.child(CurrentUserId).child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            FriendsREf.child(CurrentUserId).child(user.getUserId()).child("Friends").setValue("UnFriend");
                            FriendsREf.child(user.getUserId()).child(CurrentUserId).child("Friends").setValue("UnFriend");
                            Toast.makeText(context, "Unblock", Toast.LENGTH_SHORT).show();
                            blockDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockDialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Myviewholder extends RecyclerView.ViewHolder {

        CircleImageView userProfile;
        TextView username, unblock_btn;

        public Myviewholder(@NonNull View itemView) {
            super(itemView);

            userProfile = itemView.findViewById(R.id.userProfile);
            username = itemView.findViewById(R.id.username);
            unblock_btn = itemView.findViewById(R.id.unblock_btn);
        }
    }
}
