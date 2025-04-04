package com.example.digitaldevice.view.activity_first;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldevice.R;
import com.example.digitaldevice.utils.DbUserHelper;
import com.example.digitaldevice.view.login.LoginActivity;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.select_home.SelectHomeActivity;

public class FirstActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000; // 2 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        makeStatusBarTransparent();
        applyTopPadding();
         DbUserHelper dbUserHelper = new DbUserHelper(this); // Init database Sqlite
         //dbUserHelper.deleteUser();

        // check dataUserLocal
//        if (dbUserHelper.isUserExists() && dbUserHelper.isUrlMqttExists()) {
//            // The user already exists and has selected a house.
//            // if user and home already exists then transfer activity
//            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
//            startActivity(intent);
//        } else if (dbUserHelper.isUserExists() && !dbUserHelper.isUrlMqttExists()) {
//            // if user already exist and home null then transfer SelectHomeActivity
//            Intent intent = new Intent(FirstActivity.this, SelectHomeActivity.class);
//            startActivity(intent);
//        }
        // Sau 2 giây chuyển sang MainActivity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(FirstActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_TIME);
    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void applyTopPadding() {
        View contentContainer = findViewById(R.id.fragment_container);
        if (contentContainer != null) {
            int statusBarHeight = getStatusBarHeight();
            contentContainer.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
