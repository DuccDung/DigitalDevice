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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DashBoardDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DeviceFunction> deviceFunctionList;
    private Map<String, String> latestData = new HashMap<>();
    private MqttHandler _mqttHandler;


    public DashBoardDeviceAdapter(List<DeviceFunction> deviceFunctionList, MqttHandler mqttHandler) {
        this.deviceFunctionList = deviceFunctionList;
        this._mqttHandler = mqttHandler;
    }

    public void updateData(String topic, String data) {
        Log.d("MQTT Update", "Topic: " + topic + ", Data: " + data);
        if (data.startsWith("{") && data.endsWith("}")) {
            latestData.put(topic, data);
        } else {
            Log.d("MQTT Update", "Ignoring non-JSON data: " + data);
        }

        if (topic.equals("device/status") && data.startsWith("{") && data.endsWith("}")) {
            try {
                JSONObject jsonObject = new JSONObject(data);

                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String deviceId = keys.next();
                    String state = jsonObject.optString(deviceId);
                    latestData.put(deviceId, state);
                    Log.d("MQTT_STATE", "Thiết bị " + deviceId + " đang " + state);
                }

            } catch (JSONException e) {
                Log.e("JSON", "Lỗi phân tích JSON: " + e.getMessage());
            }

        }
        notifyDataSetChanged();
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

            String topic = deviceFunctionList.get(position).getDeviceID();
            String state = latestData.get(topic);
            String payload = latestData.get(topic);
            double temperature = 0.0;

            if (state != null) {
                boolean isOn = state.equals("ON");
                airHolder.swAirConditioner.setChecked(isOn);
                airHolder.swAirConditioner.setBackgroundResource(isOn ? R.drawable.custom_switch_track : R.drawable.custom_switch_init);
            }

            airHolder.swAirConditioner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    handleSwitchChange(deviceFunctionList.get(position), isChecked);
                    if (isChecked == true) {
                        latestData.put(topic, "ON");
                        airHolder.swAirConditioner.setBackgroundResource(R.drawable.custom_switch_track);
                    } else {
                        latestData.put(topic, "OFF");
                        airHolder.swAirConditioner.setBackgroundResource(R.drawable.custom_switch_init);
                    }
                }
            });

            if (payload != null && !payload.isEmpty()) {
                Log.d("MQTT Payload", "Received: " + payload);

                if ("ON".equals(payload) || "OFF".equals(payload)) {
                    Log.d("MQTT Payload", "Received ON/OFF command: " + payload);
                } else if (payload.startsWith("{") && payload.endsWith("}")) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(payload);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (jsonObject.has("temperature") && !jsonObject.isNull("temperature")) {
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
                airHolder.txtAirTemperature.setText("27°C");
            }

        } else if (holder instanceof DeviceLampHolder) {
            DeviceLampHolder lampHolder = (DeviceLampHolder) holder;
            lampHolder.txtNameLamp.setText(deviceFunctionList.get(position).getNameDevice());
            String topic = deviceFunctionList.get(position).getDeviceID();
            String state = latestData.get(topic);
            lampHolder.swLamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    handleSwitchChange(deviceFunctionList.get(position), isChecked);
                    if (isChecked == true) {
                        latestData.put(topic, "ON");
                        lampHolder.swLamp.setBackgroundResource(R.drawable.custom_switch_track);
                        lampHolder.txtLampCondition.setText("Device On");
                    } else {
                        latestData.put(topic, "OFF");
                        lampHolder.swLamp.setBackgroundResource(R.drawable.custom_switch_init);
                        lampHolder.txtLampCondition.setText("Device Off");
                    }
                }
            });
            if (state != null) {
                boolean isOn = state.equals("ON");
                lampHolder.swLamp.setChecked(isOn);
                lampHolder.txtLampCondition.setText(isOn ? "Device On" : "Device Off");
                lampHolder.swLamp.setBackgroundResource(isOn ? R.drawable.custom_switch_track : R.drawable.custom_switch_init);
            }

            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }

            // condition
        } else if (holder instanceof DeviceWaterHolder) {
            DeviceWaterHolder waterHolder = (DeviceWaterHolder) holder;
            waterHolder.txtWaterHeaterName.setText(deviceFunctionList.get(position).getNameDevice());
            String topic = deviceFunctionList.get(position).getDeviceID();
            String state = latestData.get(topic);

            if (state != null) {
                boolean isOn = state.equals("ON");
                waterHolder.swWaterHeater.setChecked(isOn);
                waterHolder.txtWaterHeaterCondition.setText(isOn ? "Device On" : "Device Off");
                waterHolder.swWaterHeater.setBackgroundResource(isOn ? R.drawable.custom_switch_track : R.drawable.custom_switch_init);
            }
            waterHolder.swWaterHeater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    handleSwitchChange(deviceFunctionList.get(position), isChecked);
                    if (isChecked == true) {
                        latestData.put(topic , "ON");
                        waterHolder.swWaterHeater.setBackgroundResource(R.drawable.custom_switch_track);
                        waterHolder.txtWaterHeaterCondition.setText("Device On");
                    } else {
                        latestData.put(topic , "OFF");
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
        Log.d(deviceID, "click " + status);
        _mqttHandler.publish(deviceID, status);
    }
}
