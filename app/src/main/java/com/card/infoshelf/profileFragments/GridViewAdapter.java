package com.card.infoshelf.profileFragments;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.card.infoshelf.postDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyViewHolder> {
    private final Context context;
    public List<GridModel> imageArray;

    public GridViewAdapter(Context context, List<GridModel> imageArray) {
        this.context = context;
        this.imageArray = imageArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_for_profile_image,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        GridModel gridModel = imageArray.get(position);
        String imageUri = gridModel.getPostURL();
        if (!imageUri.equals("none")){
            holder.Post_image.setVisibility(View.VISIBLE);
            holder.post_text.setVisibility(View.GONE);
            Picasso.get().load(imageUri).into(holder.Post_image);
        }else{
            if (position%5==0){
                holder.post_text.setBackgroundResource(R.drawable.grad1);
            }else  if (position%5==1){
                holder.post_text.setBackgroundResource(R.drawable.grad2);
            }else if (position%5==2){
                holder.post_text.setBackgroundResource(R.drawable.grad3);
            }else if (position%5==3){
                holder.post_text.setBackgroundResource(R.drawable.grad4);
            }
            else{
                holder.post_text.setBackgroundResource(R.drawable.grade5);
            }
            holder.Post_image.setVisibility(View.GONE);
            holder.post_text.setVisibility(View.VISIBLE);
            holder.post_text.setText(gridModel.getTextBoxData());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, postDetailsActivity.class);
                intent.putExtra("pId",gridModel.getTimeStamp());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView Post_image;
        TextView post_text;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Post_image = itemView.findViewById(R.id.gridImageview);
            post_text = itemView.findViewById(R.id.gridTextview);

        }
    }
}
