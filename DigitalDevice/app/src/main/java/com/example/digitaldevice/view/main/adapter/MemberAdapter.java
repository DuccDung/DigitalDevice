package com.example.digitaldevice.view.main.adapter;

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

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private Context context;
    private List<Users> memberList;

    public MemberAdapter(Context context, List<Users> memberList) {
        this.context = context;
        this.memberList = new ArrayList<>(memberList);
        Log.d("MemberAdapter", "Adapter created with " + memberList.size() + " members");
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        Log.d("MemberAdapter", "Creating new ViewHolder");
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Users member = memberList.get(position);
        Log.d("MemberAdapter", "Binding position " + position + ": " + member.getName());

        holder.tvNameu.setText(member.getName());
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
            Log.e("MemberAdapter", "Error loading image", e);
            holder.avUser.setImageResource(R.drawable.avatar);
        }
        holder.imgDelete.setOnClickListener(v ->{

        });
    }

    @Override
    public int getItemCount() {
        int count = memberList.size();
        Log.d("MemberAdapter", "Current item count: " + count);
        return count;
    }

    public void updateMembers(List<Users> newMembers) {
        Log.d("MemberAdapter", "Updating with " + (newMembers != null ? newMembers.size() : 0) + " new members");

        this.memberList.clear();
        if (newMembers != null) {
            this.memberList.addAll(newMembers);
        }
        notifyDataSetChanged();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView avUser;
        TextView tvNameu;
        ImageView imgDelete;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            avUser = itemView.findViewById(R.id.av_user);
            tvNameu = itemView.findViewById(R.id.tvNameu);
            imgDelete = itemView.findViewById(R.id.deleteMember);
            if (avUser == null || tvNameu == null) {
                Log.e("MemberViewHolder", "Some views not found in item layout");
            }
        }
    }
}