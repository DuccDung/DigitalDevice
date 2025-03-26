package com.example.digitaldevice.view.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.model.Users;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    private static final String CURRENT_FRAGMENT_KEY = "current_fragment";
    private boolean isAddUserFragment = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adduser_fragment, container, false);

        // Khôi phục trạng thái nếu có
        if (savedInstanceState != null) {
            isAddUserFragment = savedInstanceState.getBoolean(CURRENT_FRAGMENT_KEY, true);
        }

        // Thiết lập fragment ban đầu
        setupInitialFragment();

        ImageView imageView = view.findViewById(R.id.imageView5);
        imageView.setOnClickListener(v -> switchFragment());

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CURRENT_FRAGMENT_KEY, isAddUserFragment);
    }

    private void setupInitialFragment() {
        Fragment initialFragment = isAddUserFragment ? new AdduserFragment() : new SearchUserFragment();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, initialFragment)
                .commitNow();
    }

    // Thêm biến lưu trữ dữ liệu
    private List<Users> selectedUsers = new ArrayList<>();

    // Sửa lại switchFragment
    // Phiên bản mới với 2 tham số
    public void switchFragment(boolean backToAddUser, List<Users> users) {
        if (backToAddUser) {
            this.selectedUsers = new ArrayList<>(users); // Sửa từ selectedMembers thành selectedUsers

            // Tìm AdduserFragment đã tồn tại hoặc tạo mới
            AdduserFragment adduserFragment = (AdduserFragment) getChildFragmentManager()
                    .findFragmentByTag("add_user_fragment");

            if (adduserFragment == null) {
                adduserFragment = new AdduserFragment();
            }

            // Truyền dữ liệu trực tiếp qua phương thức
            adduserFragment.updateMembers(selectedUsers); // Sửa thành selectedUsers

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, adduserFragment)
                    .commit();
        } else {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SearchUserFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    // Phiên bản cũ không tham số (dành cho imageView)
    public void switchFragment() {
        // Gọi phiên bản có tham số với giá trị mặc định
        switchFragment(false, new ArrayList<>());
    }

    // Xử lý nút back để tránh thoát app khi đang ở fragment con
    public boolean onBackPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            getChildFragmentManager().popBackStack();
            isAddUserFragment = !isAddUserFragment;
            return true;
        }
        return false;
    }
}