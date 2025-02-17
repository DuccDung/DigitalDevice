package com.example.smarthome.View.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.R;

public class SelectHouseAdapter extends RecyclerView.Adapter<SelectHouseAdapter.ItemHouse>{
    @NonNull
    @Override
    public ItemHouse onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHouse holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ItemHouse extends RecyclerView.ViewHolder{
    private TextView txtItemHouse;
    public ItemHouse(@NonNull View itemView) {
        super(itemView);
        txtItemHouse = itemView.findViewById(R.id.txt_item_house);
    }

}
}
