package com.example.digitaldevice.view.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.digitaldevice.R;

public class AddNewDeviceFragment extends Fragment {
    private Spinner spinnerDeviceType;
    private EditText edtDeviceName;
    private Button btnSave, btnCancel;
    private String roomId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_newdevice_fragment, container, false);
        
        // Lấy roomId từ SelectDeviceFragment
        if (getArguments() != null) {
            roomId = getArguments().getString("roomId");
        }
        
        // Ánh xạ các view
        spinnerDeviceType = view.findViewById(R.id.spinnerDeviceType);
        edtDeviceName = view.findViewById(R.id.edtDeviceName);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

       

        // Xử lý sự kiện click button Save
        btnSave.setOnClickListener(v -> {
            String deviceName = edtDeviceName.getText().toString();
            String deviceType = spinnerDeviceType.getSelectedItem().toString();
            
            // Kiểm tra dữ liệu đầu vào
            if (deviceName.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập tên thiết bị", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Gọi hàm thêm thiết bị mới
            addNewDevice(deviceName, deviceType);
        });

        // Xử lý sự kiện click button Cancel
        btnCancel.setOnClickListener(v -> {
            // Quay lại Fragment trước đó (SelectDeviceFragment)
            requireActivity().onBackPressed();
        });

        return view;
    }

    private void addNewDevice(String deviceName, String deviceType) {
        // TODO: Implement API call to add new device
        // 1. Tạo deviceId mới
        // 2. Gọi API CreateDevice với các tham số:
        //    - DeviceId
        //    - Name (deviceName)
        //    - RoomId (roomId)
        //    - FunctionId (dựa vào deviceType)
        // 3. Xử lý response:
        //    - Nếu thành công: Quay lại SelectDeviceFragment và refresh danh sách
        //    - Nếu thất bại: Hiển thị thông báo lỗi
    }
} 