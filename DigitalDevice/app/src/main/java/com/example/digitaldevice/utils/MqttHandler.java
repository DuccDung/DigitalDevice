package com.example.digitaldevice.utils;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.LinkedHashMap;

public class MqttHandler {

    private MqttClient client;
    private MqttListener mqttListener;

    public interface MqttListener {
        void onMessageReceived(String topic, String payload);
    }

    public MqttHandler(MqttListener listener) {
        this.mqttListener = listener;
    }
    // Lưu trữ dữ liệu nhận về
    private static final int MAX_CACHE_SIZE = 1000;
    private LinkedHashMap<String, String> topicData = new LinkedHashMap<String, String>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Entry<String, String> eldest) {
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
                    System.err.println("Mất kết nối với broker: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("Nhận được tin nhắn từ topic " + topic + ": " + new String(message.getPayload()));

                    String payload = new String(message.getPayload());
                    Log.d("MQTT", "Nhận tin nhắn từ topic: " + topic + " - " + payload);

                    synchronized (topicData) {
                        topicData.put(topic, payload);
                    }
                    if (mqttListener != null) {
                        mqttListener.onMessageReceived(topic, payload);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Tin nhắn đã được gửi thành công.");
                }
            });

            client.connect(connectOptions);
            System.out.println("Kết nối thành công tới broker: " + completeBrokerUrl);
        } catch (MqttException e) {
            System.err.println("Không thể kết nối tới broker: " + brokerUrl);
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                System.out.println("Ngắt kết nối thành công.");
            }
        } catch (MqttException e) {
            System.err.println("Lỗi khi ngắt kết nối.");
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                client.publish(topic, mqttMessage);
                System.out.println("Tin nhắn đã được gửi đến topic: " + topic);
            } else {
                System.err.println("Không thể gửi tin nhắn vì MQTT client chưa kết nối.");
            }
        } catch (MqttException e) {
            System.err.println("Không thể gửi tin nhắn.");
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            if (client != null && client.isConnected()) {
                client.subscribe(topic);
                System.out.println("Đã đăng ký lắng nghe topic: " + topic);
            } else {
                System.err.println("Không thể đăng ký topic vì MQTT client chưa kết nối.");
            }
        } catch (MqttException e) {
            System.err.println("Không thể đăng ký topic.");
            e.printStackTrace();
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
                    Log.e("MQTT", "Kết nối lại thất bại, thử lại sau 5 giây...");
                    try {
                        Thread.sleep(5000); // Chờ 5 giây trước khi thử lại
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }).start();
    }

    public String getLatestMessage(String topic) {
        synchronized (topicData) {
            return topicData.get(topic); // Lấy dữ liệu mới nhất của một topic
        }
    }

}