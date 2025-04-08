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
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.Room;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.main.adapter.SelectRoomAdapter;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class SelectRoomFragment extends Fragment implements
        SelectRoomAdapter.OnRoomClickListener,
        SelectRoomAdapter.OnOptionsClickListener {

    private RecyclerView recyclerView;
    private SelectRoomAdapter adapter;
    private TextView txtRoomCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_room_fragment, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo adapter
        adapter = new SelectRoomAdapter(this, this);
        recyclerView.setAdapter(adapter);

        // Khởi tạo TextView hiển thị số lượng phòng
        txtRoomCount = view.findViewById(R.id.txtRoomCount);

        // Xử lý click vào nút back
        ImageView imageBack = view.findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Xử lý click vào nút thêm phòng mới
        ImageView imageAddRoom = view.findViewById(R.id.imageAddRoom);
        imageAddRoom.setOnClickListener(v -> {
            // Tạo instance của AddNewRoomFragment
            AddNewRoomFragment addNewRoomFragment = new AddNewRoomFragment();

            // Tạo Bundle để truyền dữ liệu
            Bundle args = new Bundle();
            args.putString("homeId", DataUserLocal.getInstance(requireContext()).getHomeId());

            // Set arguments cho Fragment
            addNewRoomFragment.setArguments(args);

            // Thay thế fragment hiện tại bằng fragment mới
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_setting, addNewRoomFragment)
                    .addToBackStack(null) // Thêm vào back stack để có thể quay lại
                    .commit();
        });

        // Load danh sách phòng và số lượng phòng
        loadRooms();
        loadRoomCount();

        return view;
    }

    private void loadRooms() {
        String homeId = DataUserLocal.getInstance(requireContext()).getHomeId();

        ApiService.apiService.GetRooms("" ,homeId).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setRooms(response.body());
                } else {
                    Toast.makeText(requireContext(), "Không thể tải danh sách phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRoomCount() {
        String homeId = DataUserLocal.getInstance(requireContext()).getHomeId();
        Log.d("SelectRoomFragment", "Đang gọi API GetRoomCount với homeId: " + homeId);

        ApiService.apiService.GetRoomCount(homeId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body();
                    Log.d("SelectRoomFragment", "Nhận được số lượng phòng: " + count);
                    txtRoomCount.setText(count + " rooms");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e("SelectRoomFragment", "Lỗi API GetRoomCount: " + errorBody);
                        Log.e("SelectRoomFragment", "Code: " + response.code());
                        Log.e("SelectRoomFragment", "Message: " + response.message());
                    } catch (IOException e) {
                        Log.e("SelectRoomFragment", "Lỗi khi đọc error body: " + e.getMessage());
                    }
                    Toast.makeText(requireContext(), "Không thể tải số lượng phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("SelectRoomFragment", "Lỗi kết nối khi gọi GetRoomCount: " + t.getMessage());
                Log.e("SelectRoomFragment", "Stack trace: ", t);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRoomClick(Room room) {
        // Tạo instance của SelectDeviceFragment
        SelectDeviceFragment selectDeviceFragment = new SelectDeviceFragment();

        // Tạo Bundle để truyền dữ liệu
        Bundle args = new Bundle();
        args.putString("roomId", room.getRoomId());
        args.putInt("deviceCount", room.getDeviceCount());
        selectDeviceFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_setting,selectDeviceFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onOptionsClick(Room room) {
        // Hiển thị PopupMenu
        PopupMenu popup = new PopupMenu(requireContext(), recyclerView);
        popup.getMenuInflater().inflate(R.menu.menu_room_options, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_edit) {
                //xử lý sự kiện edit
                showEditRoomDialog(room);
            } else if (itemId == R.id.menu_delete) {
                //xử lý sự kiện delete
                showDeleteRoomDialog(room);
            }
            return true;
        });

        popup.show();
    }

    private void showEditRoomDialog(Room room) {
    }

    private void showDeleteRoomDialog(Room room) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.deleteroom_confirmation_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        // Xử lý click vào button YES
        dialogView.findViewById(R.id.btnYes).setOnClickListener(v -> {
            Log.d("SelectRoomFragment", "Đang xóa phòng với ID: " + room.getRoomId());

            // Gọi API xóa phòng
            ApiService.apiService.DeleteRoom(room.getRoomId())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d("SelectRoomFragment", "Xóa phòng thành công");
                                Toast.makeText(requireContext(), "Xóa phòng thành công", Toast.LENGTH_SHORT).show();
                                // Tải lại danh sách phòng
                                loadRooms();
                                loadRoomCount();
                            } else {
                                try {
                                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                                    Log.e("SelectRoomFragment", "Lỗi API DeleteRoom: " + errorBody);
                                    Log.e("SelectRoomFragment", "Code: " + response.code());
                                    Log.e("SelectRoomFragment", "Message: " + response.message());
                                } catch (IOException e) {
                                    Log.e("SelectRoomFragment", "Lỗi khi đọc error body: " + e.getMessage());
                                }
                                Toast.makeText(requireContext(),
                                        "Không thể xóa phòng",
                                        Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss(); // Đóng dialog sau khi xử lý xong
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("SelectRoomFragment", "Lỗi kết nối khi gọi DeleteRoom: " + t.getMessage());
                            Log.e("SelectRoomFragment", "Stack trace: ", t);
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

    public void refreshRoomList() {
        loadRooms();
        loadRoomCount();
    }
}
