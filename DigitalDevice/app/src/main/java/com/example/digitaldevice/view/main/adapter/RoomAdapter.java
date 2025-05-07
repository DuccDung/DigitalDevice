package com.example.digitaldevice.view.main.adapter;

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
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.SSLUtils;
import com.example.digitaldevice.data.model.Room;

import java.util.List;

import okhttp3.OkHttpClient;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ItemRoomHolder> {
    public interface OnRoomClickListener {
        void onRoomClick(String roomId);
    }

    private List<Room> rooms;

    private OnRoomClickListener onRoomClickListener;

    public RoomAdapter(OnRoomClickListener listener, List<Room> _rooms) {
        this.rooms = _rooms;
        this.onRoomClickListener = listener;
    }

    @NonNull
    @Override
    public ItemRoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater Inflater = LayoutInflater.from(parent.getContext());
        View view = Inflater.inflate(R.layout.item_room, parent, false);
        return new ItemRoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRoomHolder holder, int position) {
        ItemRoomHolder roomHolder = (ItemRoomHolder) holder;
        Room room = rooms.get(position);
        String UrlImg = ApiService.getBaseDomain() + room.getPhotoPath();


        roomHolder.imgRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRoomClickListener != null) {
                    Log.d("RoomAdapter", "Click detected on Room: " + room.getRoomId());
                    onRoomClickListener.onRoomClick(room.getRoomId());
                } else {
                    Log.e("RoomAdapter", "onRoomClickListener is NULL!");
                }
            }
        });
        Glide.with(holder.itemView.getContext()).load(UrlImg)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error).into(holder.imgRoom);
        roomHolder.txtNameRoom.setText(room.getName());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ItemRoomHolder extends RecyclerView.ViewHolder {
        private TextView txtNameRoom;
        private ImageView imgRoom;

        public ItemRoomHolder(@NonNull View itemView) {
            super(itemView);
            txtNameRoom = itemView.findViewById(R.id.txtName_item_room);
            imgRoom = itemView.findViewById(R.id.imgIcon_item_room);
        }
    }
}
