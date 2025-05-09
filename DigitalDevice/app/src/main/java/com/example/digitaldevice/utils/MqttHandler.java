package com.example.digitaldevice.utils;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.greenrobot.eventbus.EventBus;

import java.util.LinkedHashMap;
import java.util.Map;

public class MqttHandler {

    private static MqttHandler instance;
    private MqttClient client;
    private MqttHandler() {} // private constructor

    public interface MqttConnectionListener {
        void onDisconnected();
    }
    private MqttConnectionListener connectionListener;
    public  void setConnectionListener(MqttConnectionListener listener){
        this.connectionListener = listener;
    }

    public static synchronized MqttHandler getInstance() {
        if (instance == null) {
            instance = new MqttHandler();
        }
        return instance;
    }
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public interface MqttListener {
        void onMessageReceived(String topic, String payload);
    }

    // Lưu trữ dữ liệu nhận về
    private static final int MAX_CACHE_SIZE = 1000;
    private final LinkedHashMap<String, String> topicData = new LinkedHashMap<String, String>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    public void connect(String brokerUrl, int port, String clientId, String username, String password) {
        try {
            String completeBrokerUrl = "ssl://" + brokerUrl + ":" + port;
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(completeBrokerUrl, clientId, persistence);

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setUserName(username);
            connectOptions.setPassword(password.toCharArray());

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("MQTT", "Mất kết nối: " + cause.getMessage());
                    if (connectionListener != null) {
                        connectionListener.onDisconnected();
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    Log.d("MQTT", "Nhận topic: " + topic + ", payload: " + payload);

                    synchronized (topicData) {
                        topicData.put(topic, payload);
                    }
                    EventBus.getDefault().post(new MqttEvent(topic, payload));

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Gửi thành công.");
                }
            });

            client.connect(connectOptions);
            Log.d("MQTT", "Kết nối thành công tới broker: " + completeBrokerUrl);
        } catch (MqttException e) {
            Log.e("MQTT", "Lỗi khi kết nối MQTT", e);
        }
    }

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                Log.d("MQTT", "Ngắt kết nối thành công.");
            }
        } catch (MqttException e) {
            Log.e("MQTT", "Lỗi khi ngắt kết nối.", e);
        }
    }

    public void publish(String topic, String message) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                client.publish(topic, mqttMessage);
                Log.d("MQTT", "Đã gửi tới topic: " + topic);
            } else {
                Log.w("MQTT", "Không thể gửi - chưa kết nối.");
            }
        } catch (MqttException e) {
            Log.e("MQTT", "Lỗi khi gửi.", e);
        }
    }

    public void subscribe(String topic) {
        try {
            if (client != null && client.isConnected()) {
                client.subscribe(topic);
                Log.d("MQTT", "Đăng ký topic: " + topic);
            } else {
                Log.w("MQTT", "Không thể đăng ký - chưa kết nối.");
            }
        } catch (MqttException e) {
            Log.e("MQTT", "Lỗi khi đăng ký.", e);
        }
    }

    private void reconnect() {
        new Thread(() -> {
            while (client != null && !client.isConnected()) {
                try {
                    Log.d("MQTT", "Đang thử kết nối lại...");
                    client.reconnect();
                    Log.d("MQTT", "Kết nối lại thành công.");
                    break;
                } catch (MqttException e) {
                    Log.e("MQTT", "Thất bại, thử lại sau 5s", e);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }).start();
    }

    public String getLatestMessage(String topic) {
        synchronized (topicData) {
            return topicData.get(topic);
        }
    }
}
