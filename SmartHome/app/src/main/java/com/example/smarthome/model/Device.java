package com.example.smarthome.model;

import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("deviceId")
    private String deviceID;

    @SerializedName("nameDevice")
    private String nameDevice;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }
}
