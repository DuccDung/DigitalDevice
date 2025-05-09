package com.example.digitaldevice.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.Device;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.notification.NotifyActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

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
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
        startForeground(1, notification);
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
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMqttMessageReceived(MqttEvent event) {
        // Ví dụ: kiểm tra tai nạn và ghi log
        try {
            JSONObject json = new JSONObject(event.payload);
            double accel = json.getDouble("accel");
            double speed = json.getDouble("speed");

            SharedPreferences prefs = getSharedPreferences("monitor_prefs", MODE_PRIVATE);
            if (!prefs.getBoolean("monitor_" + event.topic, false)) return;
            if (accel >= 80) {
                sendAccidentNotification2(event.topic, speed, accel);
            }
            if (accel >= 2 && speed > 3) {
                sendAccidentNotification(event.topic, speed, accel);
            }
        } catch (Exception e) {
            Log.e("MQTT_PARSE", "Lỗi xử lý dữ liệu MQTT", e);
        }
    }
    private void sendAccidentNotification(String topic, double speed, double accel) {
        String channelId = "mqtt_channel_alert";

        Intent intent = new Intent(this, NotifyActivity.class);
        intent.putExtra("device_id", topic);
        intent.putExtra("speed", speed);
        intent.putExtra("accel", accel);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo channel mới có âm thanh
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Cảnh báo xe",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setSound(soundUri, audioAttributes);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("🚨 Cảnh báo xe di chuyển")
                .setContentText("Xe " + topic + " có tốc độ " + speed + " km/h, gia tốc " + accel)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void sendAccidentNotification2(String topic, double speed, double accel) {
        String channelId = "mqtt_channel_alert";

        Intent intent = new Intent(this, NotifyActivity.class);
        intent.putExtra("device_id", topic);
        intent.putExtra("speed", speed);
        intent.putExtra("accel", accel);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo channel mới có âm thanh
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Cảnh báo xe",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setSound(soundUri, audioAttributes);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("🚨 Cảnh báo xe tai nạn")
                .setContentText("Xe " + topic + " có tốc độ " + speed + " km/h, gia tốc " + accel)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}

