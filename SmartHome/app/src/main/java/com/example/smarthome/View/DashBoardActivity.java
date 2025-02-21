package com.example.smarthome.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.smarthome.View.Adapter.DashBoardDeviceAdapter;
import com.example.smarthome.View.Adapter.RoomAdapter;
import com.example.smarthome.api_service.ApiService;
import com.example.smarthome.api_service.DataCallback;
import com.example.smarthome.model.Device;
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

public class DashBoardActivity extends AppCompatActivity implements MqttHandler.MqttListener, RoomAdapter.OnRoomClickListener {
    private RoomAdapter roomAdapter;
    private RecyclerView rcvRooms;
    private DashBoardDeviceAdapter dashBoardDeviceAdapter;
    private RecyclerView rcvDashboardDevice;
    private MqttHandler mqttHandler;
    private List<DeviceFunction> devicesDashboard = new ArrayList<>();
    private String roomId;
    @Override
    public void onRoomClick(String _roomId) {
        roomId = _roomId;
        Log.d("RoomClick", "Clicked room ID: " + _roomId);

        // Gọi API lấy danh sách thiết bị trong phòng vừa chọn
        connectApiDevice(new DataCallback<List<DeviceFunction>>() {
            @Override
            public void onSuccess(List<DeviceFunction> data) {
                devicesDashboard.clear();
                devicesDashboard.addAll(data);

               // ConnectMQTT();
                dashBoardDeviceAdapter = new DashBoardDeviceAdapter(devicesDashboard , mqttHandler);
                rcvDashboardDevice.setAdapter(dashBoardDeviceAdapter);
                dashBoardDeviceAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu
            }

            @Override
            public void onFailure(Throwable t) {

            }
        } , DataUserLocal.getInstance(DashBoardActivity.this).getHomeId() , roomId);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        InitializeView();
        InitializeMQTT();
    }
    private void InitializeMQTT() {
        String homeId = DataUserLocal.getInstance(this).getHomeId();
        ApiService.apiService.GetALlDeviceByHome(homeId).enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Device> devices = response.body();
                    // Khi lấy data về và connect MQTT
                    ConnectMQTT(new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            fetchData();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("InitializeMQTT" , "fetchData");
                        }
                    }, devices);
                }
                else Log.d("in dashboard" , "bug Mqtt call api");
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                Log.e("bug mess" , t.getMessage());
            }
        });
    }
    public void connectApiRoom(DataCallback<List<Room>> roomDataCallback , String homeId){
        ApiService.apiService.GetRooms(homeId).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if(response.isSuccessful() && response.body() != null){
                  //  Toast.makeText(DashBoardActivity.this, "Connect Success", Toast.LENGTH_SHORT).show();
                    List<Room> rooms = response.body();
                    roomDataCallback.onSuccess(rooms);
                }
                else {
                   // Toast.makeText(DashBoardActivity.this, "Connect Fail", Toast.LENGTH_SHORT).show();
                    Log.d("Lỗi Dashboard " , "API Endpoint Issue");
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
    private void ConnectMQTT(DataCallback<Boolean> callback ,List<Device> devices) {
        // ✅ Đảm bảo `MainActivity` triển khai `MqttHandler.MqttListener`
        mqttHandler = new MqttHandler( this);

        // Thông số kết nối
        String brokerUrl = DataUserLocal.getInstance(this).getUrlMqtt();
        int port = 8883;
        String clientId = DataUserLocal.getInstance(this).getHomeId();
        String username = DataUserLocal.getInstance(this).getUserMqtt();
        String password = DataUserLocal.getInstance(this).getPasswordMqtt();

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
    @Override
    public void onMessageReceived(String topic, String payload) {
        runOnUiThread(() -> {
           // Log.d("MQTT", "Dữ liệu MQTT nhận được là: " + payload);
            dashBoardDeviceAdapter.updateData(topic, payload);
        });
    }
    private void InitializeView (){
        rcvRooms = findViewById(R.id.rcvRoomDashboard);
        rcvRooms.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvDashboardDevice = findViewById(R.id.rcvDeviceDashBoard);
        rcvDashboardDevice.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }
    private void fetchData(){
        DataUserLocal dataUserLocal = DataUserLocal.getInstance(DashBoardActivity.this);
        connectApiRoom(new DataCallback<List<Room>>() {
            @Override
            public void onSuccess(List<Room> data) {
                roomAdapter = new RoomAdapter(DashBoardActivity.this ,data);
                rcvRooms.setAdapter(roomAdapter);
                roomAdapter.notifyDataSetChanged();

                InitializeDeviceFirst(String.valueOf(data.get(0).getRoomId()));  // init first data device
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

                dashBoardDeviceAdapter = new DashBoardDeviceAdapter(devicesDashboard , mqttHandler);
                rcvDashboardDevice.setAdapter(dashBoardDeviceAdapter);
                dashBoardDeviceAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu
            }

            @Override
            public void onFailure(Throwable t) {

            }
        } , dataUserLocal.getHomeId() , roomId); // Xử lý lấy device trong room
    }
    private void InitializeDeviceFirst(String RoomId){
        DataUserLocal dataUserLocal = DataUserLocal.getInstance(DashBoardActivity.this);
        connectApiDevice(new DataCallback<List<DeviceFunction>>() {
            @Override
            public void onSuccess(List<DeviceFunction> data) {
                devicesDashboard.clear();
                devicesDashboard.addAll(data);

                dashBoardDeviceAdapter = new DashBoardDeviceAdapter(devicesDashboard , mqttHandler);
                rcvDashboardDevice.setAdapter(dashBoardDeviceAdapter);
                dashBoardDeviceAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu
            }

            @Override
            public void onFailure(Throwable t) {

            }
        } , dataUserLocal.getHomeId() , RoomId); // Xử lý lấy device trong room
    }
}
