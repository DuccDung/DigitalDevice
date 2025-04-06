package com.example.digitaldevice.AddUser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.model.Users;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;


public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.MemberViewHolder> {
   public interface MemberClick{
       public void MemberOnClick(String userID);
   }
    private Context context;
   private MemberClick memberClick;
    private List<Users> memberList;

    public AddMemberAdapter(Context context, List<Users> memberList , MemberClick memberClick) {
        this.context = context;
        this.memberList = new ArrayList<>(memberList);
        this.memberClick = memberClick;
    }
    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_add, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Users member = memberList.get(position);
        holder.txtName.setText(member.getName());
        holder.txtIdUser.setText(member.getUserId());
        try {
            if (member.getPhotoPath() != null && !member.getPhotoPath().isEmpty()) {
                Glide.with(context)
                        .load(member.getPhotoPath())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(holder.avUser);
            } else {
                holder.avUser.setImageResource(R.drawable.avatar);
            }
        } catch (Exception e) {
            holder.avUser.setImageResource(R.drawable.avatar);
        }
        holder.txtName.setOnClickListener(v->{
            memberClick.MemberOnClick(member.getUserId());
        });

    }

    @Override
    public int getItemCount() {
        int count = memberList.size();
        return count;
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView avUser;
        TextView txtName;
        TextView txtIdUser;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            avUser = itemView.findViewById(R.id.av_user_add);
            txtName = itemView.findViewById(R.id.txtName);
            txtIdUser = itemView.findViewById(R.id.txtIdUser);
        }
    }
}