package com.example.digitaldevice.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.digitaldevice.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        // Xử lý sự kiện chọn menu trong BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.itemDashboard) {
                viewPager.setCurrentItem(0);
            } else if (item.getItemId() == R.id.itemVehicle) { // Đổi tên ID nếu cần
                viewPager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.itemSetting) {
                viewPager.setCurrentItem(2);
            }
            return true;
        });

        setUpViewPager();
    }

    private void setUpViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Ngăn chặn vuốt sang MapFragment
        viewPager.setUserInputEnabled(true);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    bottomNavigationView.getMenu().findItem(R.id.itemDashboard).setChecked(true);
                } else if (position == 1) {
                    bottomNavigationView.getMenu().findItem(R.id.itemVehicle).setChecked(true);
                } else if (position == 2) {
                    bottomNavigationView.getMenu().findItem(R.id.itemSetting).setChecked(true);
                }
            }
        });
    }

    private void initialize() {
        bottomNavigationView = findViewById(R.id.navigationViewMain);
        viewPager = findViewById(R.id.viewPagerMain);
    }

    // 👉 Mở MapFragment khi bấm nút từ VehicleFragment
    public void openMapFragment() {
        viewPagerAdapter.addMapFragment();
        viewPager.setCurrentItem(3, true);
    }

    // 👉 Đóng MapFragment khi quay lại
    public void closeMapFragment() {
        viewPagerAdapter.removeMapFragment();
        viewPager.setCurrentItem(1, true); // Quay về VehicleFragment
    }
}
