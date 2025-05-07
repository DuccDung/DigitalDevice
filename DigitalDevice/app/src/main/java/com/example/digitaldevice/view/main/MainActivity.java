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
import com.example.digitaldevice.view.main.fragment.MapFragment;
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
    private MqttHandler mqttHandler;
    private SessionManager sessionManager;
    private LinearLayout itemLoad;
    private DashBoardFragment dashBoardFragment;
    private VehicleFragment vehicleFragment;
    private SettingFragment settingFragment;
    private Fragment currentFragment;

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
        // init fragment
        dashBoardFragment = new DashBoardFragment();
        vehicleFragment = new VehicleFragment();
        settingFragment = new SettingFragment();

        InitializeApp(new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

                itemLoad.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                currentFragment = dashBoardFragment;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, dashBoardFragment)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, vehicleFragment)
                        .hide(vehicleFragment)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, settingFragment)
                        .hide(settingFragment)
                        .commit();
                // ========================
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    if (item.getItemId() == R.id.itemDashboard) {
                        showFragment(dashBoardFragment);
                    } else if (item.getItemId() == R.id.itemVehicle) {
                        showFragment(vehicleFragment);
                    } else if (item.getItemId() == R.id.itemSetting) {
                        showFragment(settingFragment);
                    }
                    return true;
                });

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("InitApp", "Initialization failed: " + t.getMessage());
            }
        });
    }
    private void showFragment(Fragment fragment) {
        if (currentFragment != fragment){
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .show(fragment)
                    .commit();
            currentFragment = fragment;
        }
    }

    public void openMapFragment(double latitude, double longitude) {
        MapFragment mapFragment = MapFragment.newInstance(latitude, longitude);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mapFragment) // dùng ID của FrameLayout
                .addToBackStack(null)
                .commit();
    }
    public void closeMapFragment() {
        getSupportFragmentManager().popBackStack();
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
        mqttHandler.subscribe("device/status");
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
