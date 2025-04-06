package com.example.digitaldevice.view.main.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.model.Room;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.SessionManager;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.main.adapter.SelectRoomAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectRoomFragment extends Fragment implements 
    SelectRoomAdapter.OnRoomClickListener,
    SelectRoomAdapter.OnOptionsClickListener {

    private RecyclerView recyclerView;
    private SelectRoomAdapter adapter;
    private SessionManager sessionManager;

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
        if (requireContext() != null){
            sessionManager = new SessionManager(requireContext());
        }
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
            
            // Gọi phương thức navigateToFragment từ MainActivity
            ((MainActivity) requireActivity()).navigateToFragment(addNewRoomFragment);
        });

        // Load danh sách phòng
        loadRooms();

        return view;
    }

    private void loadRooms() {
        String homeId = DataUserLocal.getInstance(requireContext()).getHomeId();
        sessionManager.CheckRefreshToken();
        ApiService.apiService.GetRooms("Bearer " + sessionManager.getToken() , homeId).enqueue(new Callback<List<Room>>() {
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

    @Override
    public void onRoomClick(Room room) {
        // Tạo instance của SelectDeviceFragment
        SelectDeviceFragment selectDeviceFragment = new SelectDeviceFragment();
        
        // Tạo Bundle để truyền dữ liệu
        Bundle args = new Bundle();
        args.putString("roomId", room.getRoomId());
        //args.putInt("deviceCount", room.getDeviceCount());
        
        // Set arguments cho Fragment
        selectDeviceFragment.setArguments(args);
        
        // Gọi phương thức navigateToFragment từ MainActivity
        ((MainActivity) requireActivity()).navigateToFragment(selectDeviceFragment);
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
//        dialogView.findViewById(R.id.btnYes).setOnClickListener(v -> {
//            // Gọi API xóa phòng
//            ApiService.apiService.DeleteRoom(room.getRoomId())
//                .enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        if (response.isSuccessful()) {
//                            Toast.makeText(requireContext(), "Xóa phòng thành công", Toast.LENGTH_SHORT).show();
//                            // Tải lại danh sách phòng
//                            loadRooms();
//                        } else {
//                            Toast.makeText(requireContext(),
//                                "Không thể xóa phòng: " + response.errorBody(),
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
}
