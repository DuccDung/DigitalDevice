package com.example.digitaldevice.view.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.model.Device;
import com.example.digitaldevice.data.model.DeviceFunction;

import java.util.ArrayList;
import java.util.List;

public class SelectDeviceAdapter extends RecyclerView.Adapter<SelectDeviceAdapter.DeviceViewHolder> {
    private List<Device> devices = new ArrayList<>();
    private OnOptionsClickListener optionsClickListener;

    public interface OnOptionsClickListener {
        void onOptionsClick(Device device);
    }

    public SelectDeviceAdapter(OnOptionsClickListener optionsClickListener) {
        this.optionsClickListener = optionsClickListener;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.bind(device);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDeviceName;
        private ImageView imageMoreOptions;
        private ImageView imageDevice;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDeviceName = itemView.findViewById(R.id.txtDeviceName);
            imageMoreOptions = itemView.findViewById(R.id.imageMoreOptions);
            imageDevice = itemView.findViewById(R.id.imageDevice);

            // Xử lý click vào nút options
            imageMoreOptions.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && optionsClickListener != null) {
                    optionsClickListener.onOptionsClick(devices.get(position));
                }
            });
        }

        public void bind(Device device) {
            txtDeviceName.setText(device.getNameDevice());

            // Hiển thị ảnh dựa vào categoryDevice
            String category = device.getCategoryDeviceID();
            int imageResource;

            if (category != null) {
                switch (category) {
                    case "cat_lamp":
                        imageResource = R.drawable.lamp;
                        break;
                    case "cat_aircon":
                        imageResource = R.drawable.icon_airconditioner;
                        break;
                    case "cat_device":
                        imageResource = R.drawable.icon_device;
                        break;
                    default:
                        imageResource = R.drawable.icon_device; // Ảnh mặc định
                        break;
                }
            } else {
                imageResource = R.drawable.icon_device; // Ảnh mặc định nếu category là null
            }

            imageDevice.setImageResource(imageResource);
        }
    }
}
