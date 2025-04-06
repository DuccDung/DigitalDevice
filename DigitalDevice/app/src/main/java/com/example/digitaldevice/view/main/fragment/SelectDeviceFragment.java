package com.example.digitaldevice.view.main.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.model.Device;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.main.adapter.SelectDeviceAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectDeviceFragment extends Fragment implements SelectDeviceAdapter.OnOptionsClickListener {
    private RecyclerView recyclerView;
    private SelectDeviceAdapter adapter;
    private TextView txtDeviceCount;
    private String roomId;
    private int deviceCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_device_fragment, container, false);
        
        // Lấy roomId và deviceCount từ SelectRoomFragment
        if (getArguments() != null) {
            roomId = getArguments().getString("roomId");
            deviceCount = getArguments().getInt("deviceCount", 0);
        }

        // Khởi tạo các view
        recyclerView = view.findViewById(R.id.recyclerViewRooms);
        txtDeviceCount = view.findViewById(R.id.txtDeviceCount);
        ImageView imageBack = view.findViewById(R.id.imageBack);
        ImageView imageAddDevice = view.findViewById(R.id.imageAddDevice);

        // Hiển thị số lượng thiết bị
        txtDeviceCount.setText(deviceCount + " devices");

        // Xử lý click vào nút back
        imageBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Xử lý click vào nút thêm thiết bị
        imageAddDevice.setOnClickListener(v -> {
            // Tạo instance của AddNewDeviceFragment
            AddNewDeviceFragment addNewDeviceFragment = new AddNewDeviceFragment();
            
            // Tạo Bundle để truyền dữ liệu
            Bundle args = new Bundle();
            args.putString("roomId", roomId); // Truyền roomId để biết thêm thiết bị vào phòng nào
            
            // Set arguments cho Fragment
            addNewDeviceFragment.setArguments(args);
            
            // Gọi phương thức navigateToFragment từ MainActivity
            ((MainActivity) requireActivity()).navigateToFragment(addNewDeviceFragment);
        });

        // Khởi tạo RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SelectDeviceAdapter(this);
        recyclerView.setAdapter(adapter);

        // Load danh sách thiết bị
        loadDevices();

        return view;
    }

    private void loadDevices() {
        String homeId = DataUserLocal.getInstance(requireContext()).getHomeId();
        
        ApiService.apiService.GetDevice(roomId).enqueue(new Callback<List<Device>>() {

            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Device> devices = response.body();
                    adapter.setDevices(devices);
                } else {
                    Toast.makeText(requireContext(), "Không thể tải danh sách thiết bị", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOptionsClick(Device device) {
        // Hiển thị PopupMenu
        PopupMenu popup = new PopupMenu(requireContext(), recyclerView);
        popup.getMenuInflater().inflate(R.menu.menu_device_options, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_device_edit) {
                showEditDeviceDialog(device);
            } else if (itemId == R.id.menu_device_delete) {
                showDeleteDeviceDialog(device);
            }
            return true;
        });

        popup.show();
    }

    private void showDeleteDeviceDialog(Device device) {
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.deletedevice_confirmation_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create();

        // Xử lý click vào button YES
//        dialogView.findViewById(R.id.btnYes).setOnClickListener(v -> {
//            // Gọi API xóa thiết bị
//            ApiService.apiService.DeleteDevice(device.getDeviceID())
//                .enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        if (response.isSuccessful()) {
//                            Toast.makeText(requireContext(), "Xóa thiết bị thành công", Toast.LENGTH_SHORT).show();
//                            // Tải lại danh sách thiết bị
//                            loadDevices();
//                        } else {
//                            Toast.makeText(requireContext(),
//                                "Không thể xóa thiết bị: " + response.errorBody(),
//                                Toast.LENGTH_SHORT).show();
//                        }
//                        dialog.dismiss(); // Đóng dialog sau khi xử lý xong
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//                        Toast.makeText(requireContext(),
//                            "Lỗi kết nối: " + t.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                        dialog.dismiss(); // Đóng dialog nếu có lỗi
//                    }
//                });
//        });

        // Xử lý click vào button NO
        dialogView.findViewById(R.id.btnNo).setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog khi click NO
        });
        dialog.show();
    }

    private void showEditDeviceDialog(Device device) {
        // TODO: Xử lý sửa thiết bị
    }
}
