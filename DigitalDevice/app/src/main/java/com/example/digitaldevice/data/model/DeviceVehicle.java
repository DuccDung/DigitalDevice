package com.example.digitaldevice.data.model;

import com.google.gson.annotations.SerializedName;

public class DeviceVehicle {
    @SerializedName("deviceId")
    private String deviceID;

    @SerializedName("nameDevice")
    private String nameDevice;

    @SerializedName("photoPath")
    private String  photoPath;

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

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        photoPath = photoPath;
    }
}
