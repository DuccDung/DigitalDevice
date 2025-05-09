package com.example.digitaldevice.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.Device;
import com.example.digitaldevice.view.main.MainActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForegroundMqttService extends Service implements MqttHandler.MqttConnectionListener {
    private SessionManager sessionManager;
    public static boolean isRunning = false;
    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "mqtt_channel",
                    "MQTT Foreground Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Hiển thị notification
        Notification notification = new NotificationCompat.Builder(this, "mqtt_channel")
                .setContentTitle("MQTT Service")
                .setContentText("Đang kết nối MQTT...")
                .setSmallIcon(R.drawable.logo)
                .build();

        MqttHandler mqttHandler = MqttHandler.getInstance();
        MqttHandler.getInstance().setConnectionListener(this);

        if (!mqttHandler.isConnected()) {
            ReconnectAndResubscribe(new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    Log.d("Kết nối mqtt lại", "onSuccess");
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("lỗi ", "onFailure: Kết nối thất bại" );
                }
            });
        }
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
    private void ReconnectAndResubscribe(DataCallback<Boolean> callback) {
        String homeId = DataUserLocal.getInstance(getApplicationContext()).getHomeId();

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
                else Log.d("in foregroundService" , "bug call api");
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                Log.e("bug mess" , t.getMessage());
            }
        });
    }
    private void ConnectMQTT( DataCallback<Boolean> callback,List<Device> devices) {
        MqttHandler mqttHandler = MqttHandler.getInstance();
                if (!ForegroundMqttService.isRunning) {
                    mqttHandler.connect(
                            DataUserLocal.getInstance(getApplicationContext()).getUrlMqtt(),
                            8883,
                            DataUserLocal.getInstance(getApplicationContext()).getHomeId(),
                            DataUserLocal.getInstance(getApplicationContext()).getUserMqtt(),
                            DataUserLocal.getInstance(getApplicationContext()).getPasswordMqtt()
                    );

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
        }

        callback.onSuccess(true);
    }

    @Override
    public void onDisconnected() {
        ReconnectAndResubscribe(new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                Log.d("Kết nối mqtt lại", "onSuccess");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("lỗi ", "onFailure: Kết nối thất bại" );
            }
        });
    }
}

