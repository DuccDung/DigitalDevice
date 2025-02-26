package com.example.digitaldevice.view.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.SSLUtils;
import com.example.digitaldevice.data.model.DeviceFunction;
import com.example.digitaldevice.data.model.DeviceVehicle;

import java.util.List;

import okhttp3.OkHttpClient;

public class VehicleAdapter  extends RecyclerView.Adapter<VehicleAdapter.VehicleHolder>{
     private List<DeviceVehicle> devicesVehicle;
     private VehicleOnClick context;

    public VehicleAdapter( VehicleOnClick _context, List<DeviceVehicle> deviceVehicle) {
        this.devicesVehicle = deviceVehicle;
        context = _context;
    }
    public interface VehicleOnClick{
        void btnDetailOnClick(DeviceVehicle deviceVehicle);
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
        DeviceVehicle deviceVehicle = devicesVehicle.get(position); // deviceVehicle

        holder.txtName.setText(deviceVehicle.getNameDevice());
        OkHttpClient client = SSLUtils.getUnsafeOkHttpClient();

        Glide.with(holder.itemView.getContext()).load(deviceVehicle.getPhotoPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error).into(holder.imgVehicle);

        // ==================================================================

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.btnDetailOnClick(deviceVehicle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devicesVehicle.size();
    }

    public class VehicleHolder extends RecyclerView.ViewHolder{
        private TextView txtName;
        private ImageView imgVehicle;
        private TextView txtCondition;
        private TextView txtKm;
        private LinearLayout btnDetail;
        public VehicleHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_Name);
            imgVehicle = itemView.findViewById(R.id.imgVehicle);
            txtCondition = itemView.findViewById(R.id.txtCondition);
            txtKm = itemView.findViewById(R.id.txtKm);
            btnDetail = itemView.findViewById(R.id.btnVehicleDetails);
        }
    }
}
