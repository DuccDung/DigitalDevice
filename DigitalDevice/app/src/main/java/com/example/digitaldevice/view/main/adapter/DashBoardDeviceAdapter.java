package com.example.digitaldevice.view.main.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.model.DeviceFunction;
import com.example.digitaldevice.utils.MqttEvent;
import com.example.digitaldevice.utils.MqttHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DashBoardDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DeviceFunction> deviceFunctionList;
    private Map<String, String> latestData = new HashMap<>();
    private MqttHandler _mqttHandler; // MQTT Handler


    public DashBoardDeviceAdapter(List<DeviceFunction> deviceFunctionList, MqttHandler mqttHandler) {
        this.deviceFunctionList = deviceFunctionList;
        this._mqttHandler = mqttHandler;
    }

    public void updateData(String topic, String data) {
        Log.d("MQTT Update", "Topic: " + topic + ", Data: " + data);

        // Kiểm tra nếu dữ liệu là JSON, lưu lại
        if (data.startsWith("{") && data.endsWith("}")) {
            latestData.put(topic, data);  // Chỉ cập nhật khi là JSON
        } else {
            Log.d("MQTT Update", "Ignoring non-JSON data: " + data);
        }

        notifyDataSetChanged(); // Cập nhật UI
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater Inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1) {

            View view = Inflater.inflate(R.layout.item_air, parent, false);
            return new DeviceAirHolder(view);

        } else if (viewType == 3) {

            View view = Inflater.inflate(R.layout.item_lamp, parent, false);
            return new DeviceLampHolder(view);

        } else {
            View view = Inflater.inflate(R.layout.item_water, parent, false);
            return new DeviceWaterHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof DeviceAirHolder) {
            DeviceAirHolder airHolder = (DeviceAirHolder) holder;
            airHolder.txtAirName.setText(deviceFunctionList.get(position).getNameDevice());

            airHolder.swAirConditioner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    handleSwitchChange(deviceFunctionList.get(position), isChecked);

                    if (isChecked == true)
                        airHolder.swAirConditioner.setBackgroundResource(R.drawable.custom_switch_track);

                    else
                        airHolder.swAirConditioner.setBackgroundResource(R.drawable.custom_switch_init);
                }
            });

            // Cập nhật nhiệt độ từ dữ liệu MQTT
            String topic = deviceFunctionList.get(position).getDeviceID();
            String payload = latestData.get(topic);
            double temperature = 0.0; // Giá trị mặc định

            if (payload != null && !payload.isEmpty()) {
                Log.d("MQTT Payload", "Received: " + payload); // Log để kiểm tra dữ liệu đầu vào

                if ("ON".equals(payload) || "OFF".equals(payload)) {
                    Log.d("MQTT Payload", "Received ON/OFF command: " + payload);
                } else if (payload.startsWith("{") && payload.endsWith("}")) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(payload);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (jsonObject.has("temperature")) {
                        try {
                            temperature = jsonObject.getDouble("temperature");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            } else {
                Log.e("MQTT Payload", "No data received for topic: " + topic);
            }

            // Hiển thị giá trị nhiệt độ hoặc thông báo chờ dữ liệu
            if (temperature != 0.0) {
                airHolder.txtAirTemperature.setText(temperature + "°C");
            } else {
                airHolder.txtAirTemperature.setText("Đang chờ dữ liệu...");
            }
        } else if (holder instanceof DeviceLampHolder) {
            DeviceLampHolder lampHolder = (DeviceLampHolder) holder;
            lampHolder.txtNameLamp.setText(deviceFunctionList.get(position).getNameDevice());

            lampHolder.swLamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    handleSwitchChange(deviceFunctionList.get(position), isChecked);
                    if (isChecked == true) {
                        lampHolder.swLamp.setBackgroundResource(R.drawable.custom_switch_track);
                        lampHolder.txtLampCondition.setText("Device On");
                    } else {
                        lampHolder.swLamp.setBackgroundResource(R.drawable.custom_switch_init);
                        lampHolder.txtLampCondition.setText("Device Off");
                    }
                }
            });


            // Đặt full span cho Lamp (chiếm toàn bộ hàng)
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }

            // condition
        } else if (holder instanceof DeviceWaterHolder) {
            DeviceWaterHolder waterHolder = (DeviceWaterHolder) holder;
            waterHolder.txtWaterHeaterName.setText(deviceFunctionList.get(position).getNameDevice());

            waterHolder.swWaterHeater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    handleSwitchChange(deviceFunctionList.get(position), isChecked);
                    if (isChecked == true){
                        waterHolder.swWaterHeater.setBackgroundResource(R.drawable.custom_switch_track);
                        waterHolder.txtWaterHeaterCondition.setText("Device On");
                    }
                    else{
                        waterHolder.swWaterHeater.setBackgroundResource(R.drawable.custom_switch_init);
                        waterHolder.txtWaterHeaterCondition.setText("Device Off");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return deviceFunctionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String functionID = deviceFunctionList.get(position).getFunctionID();

        switch (functionID) {
            case "F_Air":
                return 1;
            case "F_WaterHeater":
                return 2;
            case "F_lamp":
                return 3;
            default:
                return 0; // Default view type
        }
    }


    public class DeviceLampHolder extends RecyclerView.ViewHolder {
        private TextView txtNameLamp;
        private TextView txtLampCondition;
        private Switch swLamp;

        public DeviceLampHolder(@NonNull View itemView) {
            super(itemView);
            txtNameLamp = itemView.findViewById(R.id.txtDeviceLampName);
            txtLampCondition = itemView.findViewById(R.id.txtDeviceLampCondition);
            swLamp = itemView.findViewById(R.id.switch_lamp);
        }
    }

    public class DeviceAirHolder extends RecyclerView.ViewHolder {
        private TextView txtAirName;
        private TextView txtAirTemperature;
        private Switch swAirConditioner;

        public DeviceAirHolder(@NonNull View itemView) {
            super(itemView);
            txtAirName = itemView.findViewById(R.id.txtDeviceAirName);
            txtAirTemperature = itemView.findViewById(R.id.txtDeviceAirTemperature);
            swAirConditioner = itemView.findViewById(R.id.switch_Air);
        }
    }

    public class DeviceWaterHolder extends RecyclerView.ViewHolder {

        private TextView txtWaterHeaterName;
        private TextView txtWaterHeaterCondition;
        private Switch swWaterHeater;

        public DeviceWaterHolder(@NonNull View itemView) {
            super(itemView);
            txtWaterHeaterName = itemView.findViewById(R.id.txtDeviceWaterName);
            txtWaterHeaterCondition = itemView.findViewById(R.id.txtDeviceLampCondition);
            swWaterHeater = itemView.findViewById(R.id.switch_water);
        }
    }

    private void handleSwitchChange(@NonNull DeviceFunction device, boolean isChecked) {
        String deviceID = device.getDeviceID();
        String status = isChecked ? "ON" : "OFF";
        // Gửi dữ liệu MQTT hoặc cập nhật trạng thái
        Log.d(deviceID, "click " + status);  // Kiểm tra xem có đúng ID không
        _mqttHandler.publish(deviceID, status);
    }

}
