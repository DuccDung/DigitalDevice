package com.example.digitaldevice.view.main.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.Device;
import com.example.digitaldevice.data.model.DeviceFunction;
import com.example.digitaldevice.data.model.Room;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.MqttHandler;
import com.example.digitaldevice.view.main.adapter.DashBoardDeviceAdapter;
import com.example.digitaldevice.view.main.adapter.RoomAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardFragment extends Fragment implements MqttHandler.MqttListener, RoomAdapter.OnRoomClickListener{
    private RoomAdapter roomAdapter;
    private RecyclerView rcvRooms;
    private DashBoardDeviceAdapter dashBoardDeviceAdapter;
    private RecyclerView rcvDashboardDevice;
    private MqttHandler mqttHandler;
    private List<DeviceFunction> devicesDashboard = new ArrayList<>();
    private String roomId;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onRoomClick(String _roomId) {
        roomId = _roomId;
        // cập nhật lại dữ liệu
        devicesDashboard.clear();
        dashBoardDeviceAdapter.notifyDataSetChanged();

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
        } , DataUserLocal.getInstance(requireContext()).getHomeId() , roomId);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("DashBoardFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.dash_board_fragment , container , false);
        rcvRooms = view.findViewById(R.id.rcvRoomDashboard);
        rcvRooms.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvDashboardDevice = view.findViewById(R.id.rcvDeviceDashBoard);
        rcvDashboardDevice.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // khởi chạy....
        InitializeApp();


    }
    private void InitializeApp() {
        String homeId = DataUserLocal.getInstance(requireContext()).getHomeId();
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
    private void ConnectMQTT(DataCallback<Boolean> callback ,List<Device> devices) {
        // ✅ Đảm bảo `MainActivity` triển khai `MqttHandler.MqttListener`
        mqttHandler = new MqttHandler( this);

        // Thông số kết nối
        String brokerUrl = DataUserLocal.getInstance(requireContext()).getUrlMqtt();
        int port = 8883;
        String clientId = DataUserLocal.getInstance(requireContext()).getHomeId();
        String username = DataUserLocal.getInstance(requireContext()).getUserMqtt();
        String password = DataUserLocal.getInstance(requireContext()).getPasswordMqtt();

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
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(requireContext(), "Connect Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DeviceFunction>> call, Throwable t) {
                Toast.makeText(requireContext(), "Connect Fail: "+ t.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onMessageReceived(String topic, String payload) {
        handler.post(() -> {
            // Log.d("MQTT", "Dữ liệu MQTT nhận được là: " + payload);
            dashBoardDeviceAdapter.updateData(topic, payload);
        });
    }
    private void fetchData(){
        DataUserLocal dataUserLocal = DataUserLocal.getInstance(requireContext());
        connectApiRoom(new DataCallback<List<Room>>() {
            @Override
            public void onSuccess(List<Room> data) {
                roomAdapter = new RoomAdapter(DashBoardFragment.this ,data);
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
        DataUserLocal dataUserLocal = DataUserLocal.getInstance(requireContext());
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
