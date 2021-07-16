package com.card.infoshelf.profileFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.card.infoshelf.R;
import com.card.infoshelf.postDetailsActivity;
import com.squareup.picasso.Picasso;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder> {
    private final Context context;
    public List<GridModel> fileArray;

    public FileAdapter(Context context, List<GridModel> fileArray) {
        this.context = context;
        this.fileArray = fileArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_for_file,parent,false);
        return new FileAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GridModel gridModel = fileArray.get(position);
        String videoUrl = gridModel.getPostURL();
        String extensionType = gridModel.getFileType();

        String Postdate = gridModel.getTimeStamp();

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy         hh:mm aa");

        long milliSeconds= Long.parseLong(Postdate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String date = formatter.format(calendar.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, postDetailsActivity.class);
                intent.putExtra("pId",gridModel.getTimeStamp());
                context.startActivity(intent);
            }
        });

        if (!extensionType.equals("image") && !extensionType.equals("video") && !extensionType.equals("none")){
            holder.Post_image.setVisibility(View.VISIBLE);
                     if (extensionType.equals("pdf")){
                         holder.Post_image.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
                     }else if(extensionType.equals("ppt") || extensionType.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||extensionType.equals("vnd.ms-powerpoint")){
                         holder.Post_image.setImageResource(R.drawable.ppt);
                     }else if(extensionType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| extensionType.equals("xlsx") || extensionType.equals("application/vnd.ms-excel")){
                         holder.Post_image.setImageResource(R.drawable.excel);
                     }else if (extensionType.equals("application/msword") || extensionType.equals("doc") || extensionType.equals("docx") || extensionType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                         holder.Post_image.setImageResource(R.drawable.word);
                     }else {
                         holder.Post_image.setImageResource(R.drawable.zip);
                     }

            holder.post_text.setVisibility(View.VISIBLE);
            holder.post_text.setText(gridModel.getFileName());
            holder.posted_by.setText("Posted On:   "+ date);
        }
    }

    @Override
    public int getItemCount() {
        return fileArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Post_image;
        TextView post_text,posted_by;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Post_image = itemView.findViewById(R.id.gridImageview);
            post_text = itemView.findViewById(R.id.gridTextview);
            posted_by = itemView.findViewById(R.id.gridTextview2);
        }
    }
}
