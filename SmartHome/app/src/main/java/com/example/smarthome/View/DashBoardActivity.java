package com.example.smarthome.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.smarthome.View.Adapter.DashBoardDeviceAdapter;
import com.example.smarthome.View.Adapter.RoomAdapter;
import com.example.smarthome.api_service.ApiService;
import com.example.smarthome.api_service.DataCallback;
import com.example.smarthome.model.DeviceFunction;
import com.example.smarthome.model.Room;
import com.example.smarthome.utils.DataUserLocal;
import com.example.smarthome.utils.MqttHandler;
import com.example.smarthome.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardActivity extends AppCompatActivity implements MqttHandler.MqttListener {
    private RoomAdapter roomAdapter;
    private RecyclerView rcvRooms;

    private DashBoardDeviceAdapter dashBoardDeviceAdapter;
    private  RecyclerView rcvDashboardDevice;

    private MqttHandler mqttHandler;
    private List<DeviceFunction> devicesDashboard = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        rcvRooms = findViewById(R.id.rcvRoomDashboard);
        rcvRooms.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        rcvDashboardDevice = findViewById(R.id.rcvDeviceDashBoard);
        rcvDashboardDevice.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        DataUserLocal dataUserLocal = new DataUserLocal(this);


        connectApiRoom(new DataCallback<List<Room>>() {
            @Override
            public void onSuccess(List<Room> data) {
                roomAdapter = new RoomAdapter(data);
                rcvRooms.setAdapter(roomAdapter);
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Adapter room" , t.getMessage());
            }
        } ,dataUserLocal.getHomeId()); // homeId từ local
        connectApiDevice(new DataCallback<List<DeviceFunction>>() {
            @Override
            public void onSuccess(List<DeviceFunction> data) {
                devicesDashboard.clear();
                devicesDashboard.addAll(data);

                ConnectMQTT();
                dashBoardDeviceAdapter = new DashBoardDeviceAdapter(devicesDashboard , mqttHandler);
                rcvDashboardDevice.setAdapter(dashBoardDeviceAdapter);
                dashBoardDeviceAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu
            }

            @Override
            public void onFailure(Throwable t) {

            }
        } , "" , ""); // Xử lý lấy device trong room

    }

    public void connectApiRoom(DataCallback<List<Room>> roomDataCallback , String homeId){
        ApiService.apiService.GetRooms(homeId).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(DashBoardActivity.this, "Connect Success", Toast.LENGTH_SHORT).show();
                    List<Room> rooms = response.body();
                    roomDataCallback.onSuccess(rooms);
                }
                else {
                    Toast.makeText(DashBoardActivity.this, "Connect Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(DashBoardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                roomDataCallback.onFailure(t);
            }
        });
    };
    public void connectApiDevice(DataCallback<List<DeviceFunction>> devicesCallback , String homeId , String roomId){
        ApiService.apiService.GetDeviceFunction(roomId , homeId).enqueue(new Callback<List<DeviceFunction>>() {
            @Override
            public void onResponse(Call<List<DeviceFunction>> call, Response<List<DeviceFunction>> response) {
                if(response.isSuccessful() && response.body() != null){
                    Log.d("API Device" , "Success");
                    List<DeviceFunction> devices = response.body();
                    devicesCallback.onSuccess(devices);
                }
                else {
                    Toast.makeText(DashBoardActivity.this, "Connect Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DeviceFunction>> call, Throwable t) {
                Toast.makeText(DashBoardActivity.this, "Connect Fail: "+ t.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ConnectMQTT() {
        // ✅ Đảm bảo `MainActivity` triển khai `MqttHandler.MqttListener`
        mqttHandler = new MqttHandler( this);

        // Thông số kết nối
        String brokerUrl = "bbb04b2c528242358401e2add20b296a.s1.eu.hivemq.cloud";
        int port = 8883;
        String clientId = "device_13233434";
        String username = "hivemq.webclient.1735043576854";
        String password = "rAhT10mH$8Btos;6I,Z.";

        // ✅ Kết nối với MQTT broker
        mqttHandler.connect(brokerUrl, port, clientId, username, password);

        // ✅ Đăng ký topic để lắng nghe dữ liệu b
        // mqttHandler.subscribe("airConditioner_1");

        // ✅ Kiểm tra danh sách trước khi subscribe
        if (devicesDashboard == null || devicesDashboard.isEmpty()) {
            Log.e("MQTT", "Device list is empty, skipping subscription.");
            return;
        }
        // ✅ Subscribe tất cả các device ID trong danh sách
        for (DeviceFunction device : devicesDashboard) {
            String topic = device.getDeviceID();
            Log.d("MQTT", "Subscribing to topic: " + topic);  // Kiểm tra xem có đúng ID không
            mqttHandler.subscribe(topic);
        }

    }

    @Override
    public void onMessageReceived(String topic, String payload) {
        runOnUiThread(() -> {
            Log.d("MQTT", "Dữ liệu MQTT nhận được là: " + payload);
            dashBoardDeviceAdapter.updateData(topic, payload);
        });
    }
    }
