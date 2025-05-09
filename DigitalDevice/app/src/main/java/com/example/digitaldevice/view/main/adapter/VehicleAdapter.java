package com.example.digitaldevice.view.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.SSLUtils;
import com.example.digitaldevice.data.model.DeviceFunction;
import com.example.digitaldevice.data.model.DeviceVehicle;
import com.example.digitaldevice.utils.MqttHandler;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.main.fragment.VehicleFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleHolder> {
    private List<DeviceVehicle> devicesVehicle;
    private VehicleOnClick context;
    private Map<String, String> latestData = new HashMap<>();
    private MqttHandler mqttHandler; // MQTT Handler
    private Context mContext;
    public void updateData(String topic, String data) {
        SharedPreferences prefs = mContext.getSharedPreferences("monitor_prefs", Context.MODE_PRIVATE);
        boolean isMonitored = prefs.getBoolean("monitor_" + topic, false);

        if (isMonitored) {
            Log.d("MQTT_MONITOR", "Đang theo dõi xe: " + topic);
            notifyDataSetChanged();
        } else {
            Log.d("MQTT_MONITOR", "Không theo dõi xe: " + topic);
        }
        latestData.put(topic, data);
    }


    public VehicleAdapter(Context _mContext, VehicleOnClick _context, List<DeviceVehicle> deviceVehicle) {
        this.devicesVehicle = deviceVehicle;
        context = _context;
        mContext = _mContext;
    }

    public interface VehicleOnClick {
        void btnDetailOnClick(double latitude, double longitude);
    }

    @NonNull
    @Override
    public VehicleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vehical, parent, false);
        return new VehicleHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull VehicleHolder holder, int position) {
        VehicleHolder vehicleHolder = (VehicleHolder) holder;
        // real time load data
        DeviceVehicle deviceVehicle = devicesVehicle.get(position); // deviceVehicle

        holder.switchMonitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("monitor_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("monitor_" + deviceVehicle.getDeviceID(), isChecked);  // lưu theo deviceID
                editor.apply();

                if (isChecked) {

                } else {

                }
            }
        });
        SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("monitor_prefs", Context.MODE_PRIVATE);
        boolean isMonitored = prefs.getBoolean("monitor_" + deviceVehicle.getDeviceID(), false);
        holder.switchMonitor.setChecked(isMonitored);

        holder.txtName.setText(deviceVehicle.getNameDevice());
        OkHttpClient client = SSLUtils.getUnsafeOkHttpClient();

        if (holder.imgVehicle.getTag() == null) {
            Glide.with(holder.itemView.getContext())
                    .load(ApiService.getBaseDomain() + deviceVehicle.getPhotoPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(holder.imgVehicle);
            holder.imgVehicle.setTag(true);
        }

        String jsonString = latestData.get(deviceVehicle.getDeviceID()); // get json data MQTT return
        double _lat = 0;
        double _lng = 0;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            double lat = jsonObject.getDouble("lat");
            double lng = jsonObject.getDouble("lng");
            int speed = (int) jsonObject.getDouble("speed");
            int accel = (int) jsonObject.getDouble("accel");
            _lat = lat;
            _lng = lng;
            if (speed == 0) {
                holder.txtSpeed.setText("stand still!");
                // --------------disconnect----------------------
                holder.overlayView.setVisibility(View.GONE);
            } else {
                holder.txtSpeed.setText(speed + "km/h");
                // --------------disconnect----------------------
                holder.overlayView.setVisibility(View.GONE);
            }
            if (speed <= 50) {
                holder.txtSpeed.setTextColor(R.color.green);
                holder.txtCondition.setText("Safe");
            } else if (speed >= 51 && speed <= 80) {
                holder.txtSpeed.setTextColor(R.color.yellow);
                holder.txtCondition.setText("Warning");
            } else if (speed >= 81) {
                holder.txtSpeed.setTextColor(R.color.red);
                holder.txtCondition.setText("Dangerous");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ==================================================================
        double final_lng = _lng;
        double final_lat = _lat;
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (final_lat != 0 && final_lng != 0) {
                    context.btnDetailOnClick(final_lat, final_lng);
                } else {
                    Log.d("Map", "Không có dữ liệu");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return devicesVehicle.size();
    }

    public class VehicleHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private ImageView imgVehicle;
        private TextView txtCondition;
        private TextView txtSpeed;
        private LinearLayout btnDetail;
        private View overlayView;
        private Switch switchMonitor;

        public VehicleHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_Name);
            imgVehicle = itemView.findViewById(R.id.imgVehicle);
            txtCondition = itemView.findViewById(R.id.txtCondition);
            txtSpeed = itemView.findViewById(R.id.txtKm);
            btnDetail = itemView.findViewById(R.id.btnVehicleDetails);
            overlayView = itemView.findViewById(R.id.overlayViewInVehicle);
            switchMonitor = itemView.findViewById(R.id.switchMonitor);
        }
    }
}
