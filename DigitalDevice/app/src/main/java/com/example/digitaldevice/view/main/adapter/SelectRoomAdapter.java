package com.example.digitaldevice.view.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.model.Room;

import java.util.ArrayList;
import java.util.List;

public class SelectRoomAdapter extends RecyclerView.Adapter<SelectRoomAdapter.RoomViewHolder> {
    private List<Room> rooms = new ArrayList<>();
    private OnRoomClickListener roomClickListener;
    private OnOptionsClickListener optionsClickListener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public interface OnOptionsClickListener {
        void onOptionsClick(Room room);
    }

    public SelectRoomAdapter(OnRoomClickListener roomClickListener, OnOptionsClickListener optionsClickListener) {
        this.roomClickListener = roomClickListener;
        this.optionsClickListener = optionsClickListener;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
        private TextView txtRoomName;
        private TextView txtDeviceCount;
        private ImageView imageOptions;
        private ImageView imageRoom;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRoomName = itemView.findViewById(R.id.txtRoomName);
            txtDeviceCount = itemView.findViewById(R.id.textDeviceCount);
            imageOptions = itemView.findViewById(R.id.imageOptions);
            imageRoom = itemView.findViewById(R.id.imageRoom);

            // Xử lý click vào item
            itemView.setOnClickListener(v -> {
                // Kiểm tra xem click có phải vào imageOptions không
                if (v != imageOptions) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && roomClickListener != null) {
                        roomClickListener.onRoomClick(rooms.get(position));
                    }
                }
            });

            // Xử lý click vào nút options
            imageOptions.setOnClickListener(v -> {

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && optionsClickListener != null) {
                    optionsClickListener.onOptionsClick(rooms.get(position));
                }
            });
        }

        public void bind(Room room) {
            txtRoomName.setText(room.getName());
            txtDeviceCount.setText(room.getDeviceCount() + " devices");

            // Hiển thị ảnh từ server
            String imageUrl = ApiService.getBaseDomain() + room.getPhotoPath();
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(imageRoom);
        }
    }
}
