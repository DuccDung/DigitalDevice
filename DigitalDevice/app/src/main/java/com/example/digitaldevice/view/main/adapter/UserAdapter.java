package com.example.digitaldevice.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.model.Users;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<Users> userList;
    private List<Users> selectedUsers = new ArrayList<>();

    public UserAdapter(Context context, List<Users> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Users user = userList.get(position);

        holder.tvName.setText(user.getName());
        holder.tvStatus.setText(user.getUserId());

        if (user.getPhotoPath() != null) {
            Glide.with(context)
                    .load(user.getPhotoPath())
                    .placeholder(R.drawable.avt)
                    .into(holder.avatar);
        }

        holder.checkAddu.setOnCheckedChangeListener(null);
        holder.checkAddu.setChecked(selectedUsers.contains(user));

        holder.checkAddu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedUsers.contains(user)) {
                    selectedUsers.add(user);
                }
            } else {
                selectedUsers.remove(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Thêm phương thức updateList này
    public void updateList(List<Users> newList) {
        this.userList.clear();
        this.userList.addAll(newList);
        this.selectedUsers.clear(); // Xóa các lựa chọn trước đó
        notifyDataSetChanged();
    }


    public List<Users> getSelectedUsers() {
        return selectedUsers;
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkAddu;
        ShapeableImageView avatar;
        TextView tvName, tvStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            checkAddu = itemView.findViewById(R.id.checkAddu);
            avatar = itemView.findViewById(R.id.avtAddu);
            tvName = itemView.findViewById(R.id.tvNameAu);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}