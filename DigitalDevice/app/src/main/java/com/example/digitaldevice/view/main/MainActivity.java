package com.example.digitaldevice.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.digitaldevice.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//            // Làm trong suốt Status Bar
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().setStatusBarColor(Color.TRANSPARENT);
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            }


        Innitialize();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.itemDashboard){
                    viewPager.setCurrentItem(0);
                } else if (item.getItemId() == R.id.itemMap) {
                    viewPager.setCurrentItem(1);
                } else if (item.getItemId() == R.id.itemSetting) {
                    viewPager.setCurrentItem(2);
                }
                return true;
            }
        });

        setUpViewPager();
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if(position == 0){
                    bottomNavigationView.getMenu().findItem(R.id.itemDashboard).setChecked(true);
                }
                else if(position == 1){
                    bottomNavigationView.getMenu().findItem(R.id.itemMap).setChecked(true);
                }
                else if(position == 2){
                    bottomNavigationView.getMenu().findItem(R.id.itemSetting).setChecked(true);
                }
            }
        });
    }

    private void Innitialize(){
        bottomNavigationView = findViewById(R.id.navigationViewMain);
        viewPager = findViewById(R.id.viewPagerMain);
    }
}