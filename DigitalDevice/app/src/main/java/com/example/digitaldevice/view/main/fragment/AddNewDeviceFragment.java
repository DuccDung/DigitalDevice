package com.example.digitaldevice.view.main.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.model.DeviceCreateResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.digitaldevice.R;
import com.example.digitaldevice.utils.SessionManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewDeviceFragment extends Fragment {
    private Spinner spinnerDeviceType;
    private EditText edtDeviceName, edtDeviceId;
    private Button btnSave, btnCancel;
    private ImageView imageDevicePhoto;
    private String roomId;
    private String homeId;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_newdevice_fragment, container, false);
        //lấy homeId từ DataUserLocal
        homeId = DataUserLocal.getInstance(requireContext()).getHomeId();
        // Lấy roomId từ SelectDeviceFragment
        if (getArguments() != null) {
            roomId = getArguments().getString("roomId");
        }
        if(requireContext() != null){
            sessionManager = new SessionManager(requireContext());
        }
        // Ánh xạ các view
        spinnerDeviceType = view.findViewById(R.id.spinnerDeviceType);
        edtDeviceName = view.findViewById(R.id.edtDeviceName);
        edtDeviceId = view.findViewById(R.id.edtDeviceId);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        imageDevicePhoto = view.findViewById(R.id.imageDevicePhoto);

        // Thiết lập sự kiện khi chọn item trong Spinner
        spinnerDeviceType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                updateDeviceImage(selectedItem);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Không làm gì khi không có item nào được chọn
            }
        });

        // Xử lý sự kiện click button Save
        btnSave.setOnClickListener(v -> {
            String deviceName = edtDeviceName.getText().toString();
            String deviceId = edtDeviceId.getText().toString();
            String deviceType = spinnerDeviceType.getSelectedItem().toString();
            String deviceFunction;

            if(deviceType.equals("Air Conditioner")){

                deviceFunction = "F_Air";
            }
            else if(deviceType.equals("Different Device")){
                deviceFunction = "F_Different";
            }
            else if(deviceType.equals("Lamp")){
                deviceFunction = "F_lamp";
            }
            else if(deviceType.equals("Water Heater")){
                deviceFunction = "F_WaterHeater";
            }
            else {
                deviceFunction = "";
            }
            // Kiểm tra dữ liệu đầu vào
            if (deviceName.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập tên thiết bị", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi hàm thêm thiết bị mới
            addNewDevice(deviceName, deviceId, deviceFunction, roomId, homeId);
        });

        // Xử lý sự kiện click button Cancel
        btnCancel.setOnClickListener(v -> {
            // Quay lại Fragment trước đó (SelectDeviceFragment)
            requireActivity().onBackPressed();
        });

        return view;
    }

    // Hàm cập nhật hình ảnh dựa trên loại thiết bị được chọn
    private void updateDeviceImage(String deviceType) {
        int imageResource;

        switch (deviceType) {
            case "Air Conditioner":
                imageResource = R.drawable.icon_airconditioner;
                break;
            case "Lamp":
                imageResource = R.drawable.lamp;
                break;
            case "Water Heater":
                imageResource = R.drawable.icon_warterheater;
                break;
            case "Different Device":
            default:
                imageResource = R.drawable.icon_defferent;
                break;
        }

        imageDevicePhoto.setImageResource(imageResource);
    }

    private void addNewDevice(String deviceName, String deviceId, String deviceFunction, String roomId, String homeId) {
        ApiService.apiService.CreateDevice("Bearer " + sessionManager.getToken(),deviceName, deviceId, roomId, deviceFunction).enqueue(new Callback<DeviceCreateResponse>() {
            @Override
            public void onResponse(Call<DeviceCreateResponse> call, Response<DeviceCreateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Thêm thiết bị thành công với ID: " + response.body().getDeviceId(), Toast.LENGTH_SHORT).show();

                    // Tìm SelectDeviceFragment bằng tag
                    SelectDeviceFragment selectDeviceFragment = (SelectDeviceFragment) requireActivity()
                            .getSupportFragmentManager()
                            .findFragmentByTag("SelectDeviceFragment");

                    if (selectDeviceFragment != null) {
                        selectDeviceFragment.refreshDeviceList();
                    }

                    // Quay lại Fragment trước đó
                    requireActivity().onBackPressed();
                } else {
                    try {
                        String errorMsg = response.errorBody().string();
                        Log.e("API_ERROR", errorMsg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeviceCreateResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 