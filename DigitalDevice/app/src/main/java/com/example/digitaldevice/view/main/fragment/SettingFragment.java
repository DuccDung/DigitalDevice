package com.example.digitaldevice.view.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.digitaldevice.R;
import com.example.digitaldevice.view.main.MainActivity;

public class SettingFragment extends Fragment {
    private Button btnViewRooms;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        
        // Ánh xạ view
        btnViewRooms = view.findViewById(R.id.btnViewRooms);
        
        // Xử lý sự kiện click vào nút
        btnViewRooms.setOnClickListener(v -> {
            // Tạo instance của SelectRoomFragment
            SelectRoomFragment selectRoomFragment = new SelectRoomFragment();
            
            // Gọi phương thức navigateToFragment từ MainActivity
            ((MainActivity) requireActivity()).navigateToFragment(selectRoomFragment);
        });
        
        return view;
    }
}
