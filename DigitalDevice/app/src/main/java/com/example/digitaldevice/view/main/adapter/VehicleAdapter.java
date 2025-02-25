package com.example.digitaldevice.view.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.model.DeviceFunction;

import java.util.List;

public class VehicleAdapter  extends RecyclerView.Adapter<VehicleAdapter.VehicleHolder>{
     private List<DeviceFunction> devicesVehicle;

    public VehicleAdapter(List<DeviceFunction> deviceVehicle) {
        this.devicesVehicle = deviceVehicle;
    }

    @NonNull
    @Override
    public VehicleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vehical , parent , false);
        return new VehicleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleHolder holder, int position) {
        VehicleHolder vehicleHolder = (VehicleHolder) holder;
        // truyền dữ liệu liên tục

    }

    @Override
    public int getItemCount() {
        return devicesVehicle.size();
    }

    public class VehicleHolder extends RecyclerView.ViewHolder{
        private ImageView imgVehicle;
        private TextView txtCondition;
        private TextView txtKm;
        private LinearLayout btnDetail;
        public VehicleHolder(@NonNull View itemView) {
            super(itemView);
            imgVehicle = itemView.findViewById(R.id.imgVehicle);
            txtCondition = itemView.findViewById(R.id.txtCondition);
            txtKm = itemView.findViewById(R.id.txtKm);
            btnDetail = itemView.findViewById(R.id.btnVehicleDetails);
        }
    }
}
