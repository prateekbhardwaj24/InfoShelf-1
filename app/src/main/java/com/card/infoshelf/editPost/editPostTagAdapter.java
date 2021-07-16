package com.card.infoshelf.editPost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.TagAdapter;
import com.card.infoshelf.bottomfragment.TagModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class editPostTagAdapter extends RecyclerView.Adapter<editPostTagAdapter.MyviewHolder> {
    private Context context;
    private List<TagModel> tagPersonList;
    private final List<String> CheckBoxTags = new ArrayList<>();
    private List<String> CheckedTags = new ArrayList<>();
    public static  final String SHARED_PREFS = "sharedPrefs";
    public static  final String TEXT = "text";
    private final List<String> SharedList = new ArrayList<>();
    public List<String> getArrayTags(){
        return CheckBoxTags;
    }

    public editPostTagAdapter(List<TagModel> tagPersonList, List<String> checkedTags) {
        this.tagPersonList = tagPersonList;
        CheckedTags = checkedTags;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        TagModel tagModel = tagPersonList.get(position);
        holder.PersonName.setText(tagModel.getUserName());
        String imageUri = tagModel.getProfile_image();
        Picasso.get().load(imageUri).into(holder.personimg);
        if (tagModel.isChecker() == true){
            holder.checkBox.setChecked(true);
        }

        String TagiId = tagModel.getUserId();
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setSelected(tagModel.isHold());
        if (!CheckedTags.isEmpty()){

            for (int i=0; i<CheckedTags.size(); i++){
                if (tagModel.getUserId().equals(CheckedTags.get(i))){
                    holder.checkBox.setChecked(true);
                    CheckBoxTags.add(tagModel.getUserId());

                }
            }


        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tagModel.setHold(true);
                    CheckBoxTags.add(tagModel.getUserId());
                    tagModel.setChecker(true);
                }else {
                    CheckBoxTags.remove(tagModel.getUserId());
                    tagModel.setHold(false);
                    tagModel.setChecker(false);
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
            checkBox = itemView.findViewById(R.id.Tagcheckbox);

            personimg = itemView.findViewById(R.id.PersonImage);
        }
    }
}
