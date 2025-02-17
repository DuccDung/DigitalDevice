package com.example.smarthome.View.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarthome.api_service.ApiService;
import com.example.smarthome.api_service.SSLUtils;
import com.example.smarthome.model.Room;
import com.example.smarthome.R;

import java.util.List;

import okhttp3.OkHttpClient;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ItemRoomHolder>{

    private List<Room> rooms;
    public RoomAdapter(List<Room > _rooms){
        rooms = _rooms;
    }

    @NonNull
    @Override
    public ItemRoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater Inflater = LayoutInflater.from(parent.getContext());
        View view = Inflater.inflate(R.layout.item_room , parent,false);
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
                Log.d("room" , room.getName());
            }
        });



        OkHttpClient client = SSLUtils.getUnsafeOkHttpClient();

             Glide.with(holder.itemView.getContext()).load(UrlImg)
                        .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error).into(holder.imgRoom);

        roomHolder.txtNameRoom.setText(room.getName());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ItemRoomHolder extends RecyclerView.ViewHolder{
    private TextView txtNameRoom;
    private ImageView imgRoom;
    public ItemRoomHolder(@NonNull View itemView) {
        super(itemView);
        txtNameRoom = itemView.findViewById(R.id.txtName_item_room);
        imgRoom = itemView.findViewById(R.id.imgIcon_item_room);
    }
}
}
