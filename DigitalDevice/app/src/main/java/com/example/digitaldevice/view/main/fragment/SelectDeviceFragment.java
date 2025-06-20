package com.example.digitaldevice.view.main.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.example.digitaldevice.data.model.DeviceFunction;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.SessionManager;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.main.adapter.SelectDeviceAdapter;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectDeviceFragment extends Fragment implements SelectDeviceAdapter.OnOptionsClickListener {
    private RecyclerView recyclerView;
    private SelectDeviceAdapter adapter;
    private TextView txtDeviceCount;
    private String roomId;
    private int deviceCount;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_device_fragment, container, false);
        if (requireContext() != null) {
            sessionManager = new SessionManager(requireContext());
        }
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

            // Thay thế fragment hiện tại bằng fragment mới
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, addNewDeviceFragment)
                    .addToBackStack(null) // Thêm vào back stack để có thể quay lại
                    .commit();
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

        ApiService.apiService.GetDevice_2("Bearer " + sessionManager.getToken(), roomId).enqueue(new Callback<List<Device>>() {

            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Device> devices = response.body();
                    adapter.setDevices(devices);
                    // Cập nhật số lượng thiết bị dựa trên danh sách thực tế
                    txtDeviceCount.setText(devices.size() + " devices");
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
        dialogView.findViewById(R.id.btnYes).setOnClickListener(v -> {
            Log.d("SelectDeviceFragment", "Đang xóa thiết bị với ID: " + device.getDeviceID());

            // Gọi API xóa thiết bị
            ApiService.apiService.DeleteDevice("Bearer " + sessionManager.getToken(), device.getDeviceID())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d("SelectDeviceFragment", "Xóa thiết bị thành công");
                                Toast.makeText(requireContext(), "Xóa thiết bị thành công", Toast.LENGTH_SHORT).show();
                                // Tải lại danh sách thiết bị
                                loadDevices();
                            } else {
                                try {
                                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                                    Log.e("SelectDeviceFragment", "Lỗi API DeleteDevice: " + errorBody);
                                    Log.e("SelectDeviceFragment", "Code: " + response.code());
                                    Log.e("SelectDeviceFragment", "Message: " + response.message());
                                } catch (IOException e) {
                                    Log.e("SelectDeviceFragment", "Lỗi khi đọc error body: " + e.getMessage());
                                }
                                Toast.makeText(requireContext(),
                                        "Không thể xóa thiết bị",
                                        Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss(); // Đóng dialog sau khi xử lý xong
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("SelectDeviceFragment", "Lỗi kết nối khi gọi DeleteDevice: " + t.getMessage());
                            Log.e("SelectDeviceFragment", "Stack trace: ", t);
                            Toast.makeText(requireContext(),
                                    "Lỗi kết nối: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); // Đóng dialog nếu có lỗi
                        }
                    });
        });

        // Xử lý click vào button NO
        dialogView.findViewById(R.id.btnNo).setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog khi click NO
        });

        dialog.show();
    }

    private void showEditDeviceDialog(Device device) {
        // TODO: Xử lý sửa thiết bị
    }

    public void refreshDeviceList() {
        // Gọi lại phương thức loadDevices để tải lại danh sách thiết bị
        loadDevices();
    }
}
