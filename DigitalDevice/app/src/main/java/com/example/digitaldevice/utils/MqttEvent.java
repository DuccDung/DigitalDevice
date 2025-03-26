package com.example.digitaldevice.utils;

public class MqttEvent {
    public final String topic;
    public final String payload;

    public MqttEvent(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }
}
