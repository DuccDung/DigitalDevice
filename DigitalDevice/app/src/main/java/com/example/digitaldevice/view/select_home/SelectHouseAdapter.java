package com.example.digitaldevice.view.select_home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.model.HomeUser;
import com.example.digitaldevice.utils.DataUserLocal;

import java.util.ArrayList;
import java.util.List;

public class SelectHouseAdapter extends RecyclerView.Adapter<SelectHouseAdapter.ItemHouse>{
    private List<HomeUser> DataListHome = new ArrayList<>();
    private Context _context;
    public SelectHouseAdapter(Context context, List<HomeUser> data){
        _context = context;
        DataListHome = data;
    }

    @NonNull
    @Override
    public ItemHouse onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_house , parent , false);
        return new ItemHouse(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHouse holder, int position) {
        ItemHouse houseHolder = (ItemHouse) holder;
        HomeUser home = DataListHome.get(position);
        houseHolder.txtItemHouse.setText(home.getName());

        houseHolder.itemHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMqttLocal(home);
            }
        });
    }

    @Override
    public int getItemCount() {
        return DataListHome.size();
    }

    public class ItemHouse extends RecyclerView.ViewHolder{
    private TextView txtItemHouse;
    private View itemHouse;
    public ItemHouse(@NonNull View itemView) {
        super(itemView);
        txtItemHouse = itemView.findViewById(R.id.txt_item_house);
        itemHouse = itemView.findViewById(R.id.itemHouse);
    }

}
private void AddMqttLocal(@NonNull HomeUser Home){
        if(_context != null){
            DataUserLocal userLocal = DataUserLocal.getInstance(_context);
            userLocal.setHomeId(_context , Home.getHomeId());
            userLocal.setUrlMqtt(_context , Home.getUrlMqtt());
            userLocal.setUserMqtt(_context , Home.getUserMQTT());
            userLocal.setPasswordMqtt(_context , Home.getPasswordMQTT());

            // =====================================

            Intent intent = new Intent(_context , MainActivity.class);
            _context.startActivity(intent);
        }
}
}
