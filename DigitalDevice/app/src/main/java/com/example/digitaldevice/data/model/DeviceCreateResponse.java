package com.example.digitaldevice.data.model;

import com.google.gson.annotations.SerializedName;

public class DeviceCreateResponse {
    @SerializedName("DeviceId")
    private String DeviceId;

    @SerializedName("FunctionId")
    private String FunctionId;

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        this.DeviceId = deviceId;
    }

    public String getFunctionId() {
        return FunctionId;
    }

    public void setFunctionId(String functionId) {
        this.FunctionId = functionId;
    }
} 