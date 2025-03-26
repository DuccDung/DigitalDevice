package com.example.digitaldevice.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.Device;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.MqttEvent;
import com.example.digitaldevice.utils.MqttHandler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitializeApp(new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
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

            @Override
            public void onFailure(Throwable t) {

            }
        }); // Khởi tạo kết nối MQTT

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
    public void openMapFragment(double latitude, double longitude) {
        viewPagerAdapter.addMapFragment(latitude , longitude);
        viewPager.setCurrentItem(3, true);
    }

    // 👉 Đóng MapFragment khi quay lại
    public void closeMapFragment() {
        viewPagerAdapter.removeMapFragment();
        viewPager.setCurrentItem(1, true); // Quay về VehicleFragment
    }
    private void InitializeApp(DataCallback<Boolean> callback) {
        String homeId = DataUserLocal.getInstance(MainActivity.this).getHomeId();
        ApiService.apiService.GetALlDeviceByHome(homeId).enqueue(new Callback<List<Device>>() {
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
        // ✅ Đảm bảo `MainActivity` triển khai `MqttHandler.MqttListener`
        mqttHandler = new MqttHandler( MainActivity.this);

        // Thông số kết nối
        String brokerUrl = DataUserLocal.getInstance(MainActivity.this).getUrlMqtt();
        int port = 8883;
        String clientId = DataUserLocal.getInstance(MainActivity.this).getHomeId();
        String username = DataUserLocal.getInstance(MainActivity.this).getUserMqtt();
        String password = DataUserLocal.getInstance(MainActivity.this).getPasswordMqtt();

        // ✅ Kết nối với MQTT broker
        mqttHandler.connect(brokerUrl, port, clientId, username, password);

        // ✅ Kiểm tra danh sách trước khi subscribe
        if (devices == null || devices.isEmpty()) {
            Log.e("MQTT", "Device list is empty, skipping subscription.");
            return;
        }
        // ✅ Subscribe tất cả các device ID trong danh sách
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
            Log.d("Main" , topic);
        //Gửi dữ liệu MQTT đến tất cả Fragment đang lắng nghe
        EventBus.getDefault().post(new MqttEvent(topic, payload));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
