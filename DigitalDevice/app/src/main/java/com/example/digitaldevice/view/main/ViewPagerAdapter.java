package com.example.digitaldevice.view.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.digitaldevice.view.main.fragment.DashBoardFragment;
import com.example.digitaldevice.view.main.fragment.MapFragment;
import com.example.digitaldevice.view.main.fragment.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new DashBoardFragment();
        }
        else if(position == 1){
            return new MapFragment();
        }
        else return new SettingFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
