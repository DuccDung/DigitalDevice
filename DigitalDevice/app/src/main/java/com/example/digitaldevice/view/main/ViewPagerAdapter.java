package com.example.digitaldevice.view.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.digitaldevice.view.main.fragment.DashBoardFragment;
import com.example.digitaldevice.view.main.fragment.MapFragment;
import com.example.digitaldevice.view.main.fragment.SettingFragment;
import com.example.digitaldevice.view.main.fragment.VehicleFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        // Ban đầu chỉ chứa 3 Fragment, không có MapFragment
        fragmentList.add(new DashBoardFragment()); // Case 0
        fragmentList.add(new VehicleFragment());   // Case 1
        fragmentList.add(new SettingFragment());   // Case 2
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    // ✅ Thêm MapFragment (khi bấm Button từ VehicleFragment)
    public void addMapFragment(double latitude, double longitude) {
        if (fragmentList.size() == 3) { // Đảm bảo MapFragment chỉ thêm 1 lần
            MapFragment mapFragment = MapFragment.newInstance(latitude, longitude);
            fragmentList.add(mapFragment);
            notifyItemInserted(3); // Cập nhật ViewPager2
        }
    }

    // ✅ Xóa MapFragment (khi quay lại)
    public void removeMapFragment() {
        if (fragmentList.size() == 4) { // Kiểm tra nếu đã thêm MapFragment
            fragmentList.remove(3);
            notifyItemRemoved(3);
        }
    }
}
