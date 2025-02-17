package com.example.smarthome.model;

import com.google.gson.annotations.SerializedName;

public class DeviceFunction {
    @SerializedName("deviceID")
    private String deviceID;

    @SerializedName("nameDevice")
    private String nameDevice;

    @SerializedName("functionID")
    private String functionID;

    @SerializedName("description")
    private String description;

    public DeviceFunction(String deviceID, String nameDevice, String functionID, String description) {
        this.deviceID = deviceID;
        this.nameDevice = nameDevice;
        this.functionID = functionID;
        this.description = description;
    }

    // Getters
    public String getDeviceID() {
        return deviceID;
    }

    public String getNameDevice() {
        return nameDevice;
    }


    public String getFunctionID() {
        return functionID;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }


    public void setFunctionID(String functionID) {
        this.functionID = functionID;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
