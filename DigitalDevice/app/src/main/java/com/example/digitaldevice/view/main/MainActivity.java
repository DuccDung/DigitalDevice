package com.example.digitaldevice.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.Device;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.MqttEvent;
import com.example.digitaldevice.utils.MqttHandler;
import com.example.digitaldevice.utils.SessionManager;
import com.example.digitaldevice.view.main.fragment.DashBoardFragment;
import com.example.digitaldevice.view.main.fragment.SettingFragment;
import com.example.digitaldevice.view.main.fragment.VehicleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  implements MqttHandler.MqttListener{
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private MqttHandler mqttHandler;
    private SessionManager sessionManager;
    private LinearLayout itemLoad;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeStatusBarTransparent();
        applyTopPadding();
        sessionManager = new SessionManager(MainActivity.this);
        sessionManager.CheckRefreshToken();
        bottomNavigationView = findViewById(R.id.navigationViewMain);
        itemLoad = findViewById(R.id.itemLoading);
        itemLoad.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
        InitializeApp(new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

                itemLoad.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);

                loadFragment(new DashBoardFragment());
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    Fragment selectedFragment = null;
                    if (item.getItemId() == R.id.itemDashboard) {
                        selectedFragment = new DashBoardFragment();
                    } else if (item.getItemId() == R.id.itemVehicle) {
                        selectedFragment = new VehicleFragment();
                    } else if (item.getItemId() == R.id.itemSetting) {
                        selectedFragment = new SettingFragment();
                    }
                    if (selectedFragment != null) {
                        loadFragment(selectedFragment);
                    }
                    return true;
                });
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    private void initializeView() {

    }
    public void openMapFragment(double latitude, double longitude) {
        viewPagerAdapter.addMapFragment(latitude , longitude);
        viewPager.setCurrentItem(3, true);
    }
    public void closeMapFragment() {
        viewPagerAdapter.removeMapFragment();
        loadFragment(new VehicleFragment());
    }
    private void InitializeApp(DataCallback<Boolean> callback) {
        String homeId = DataUserLocal.getInstance(MainActivity.this).getHomeId();

        sessionManager.CheckRefreshToken();
        String token = "Bearer " + sessionManager.getToken();
        ApiService.apiService.GetALlDeviceByHome(token , homeId).enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {

                if(response.isSuccessful() && response.body() != null){
                    // Khi lấy data về và connect MQTT
                    ConnectMQTT(new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            callback.onSuccess(true);
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    }, response.body());
                }
                else Log.d("in dashboard" , "bug call api");
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                Log.e("bug mess" , t.getMessage());
            }
        });
    }
    public void navigateToFragment(Fragment fragment) {
        // Ẩn ViewPager, hiện container
        viewPager.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);

        // Thêm Fragment mới vào stack
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_setting, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void ConnectMQTT( DataCallback<Boolean> callback,List<Device> devices) {
        mqttHandler = new MqttHandler( MainActivity.this);

        // Thông số kết nối
        String brokerUrl = DataUserLocal.getInstance(MainActivity.this).getUrlMqtt();
        int port = 8883;
        String clientId = DataUserLocal.getInstance(MainActivity.this).getHomeId();
        String username = DataUserLocal.getInstance(MainActivity.this).getUserMqtt();
        String password = DataUserLocal.getInstance(MainActivity.this).getPasswordMqtt();

        mqttHandler.connect(brokerUrl, port, clientId, username, password);

        if (devices == null || devices.isEmpty()) {
            Log.e("MQTT", "Device list is empty, skipping subscription.");
            return;
        }
        for (Device device : devices) {
            String topic = device.getDeviceID();
            Log.d("MQTT", "Subscribing to topic: " + topic);  // Kiểm tra xem có đúng ID không
            mqttHandler.subscribe(topic);
        }
        callback.onSuccess(true);
    }
    public MqttHandler getMqttHandler() {
        return mqttHandler;
    }
    @Override
    public void onMessageReceived(String topic, String payload) {
        EventBus.getDefault().post(new MqttEvent(topic, payload));
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
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
