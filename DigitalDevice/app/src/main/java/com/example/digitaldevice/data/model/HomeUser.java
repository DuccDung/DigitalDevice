package com.example.digitaldevice.data.model;

import com.google.gson.annotations.SerializedName;

public class HomeUser {
    // User properties
    @SerializedName("userId")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;
    @SerializedName("phone")
    private String phone;
    @SerializedName("photoPath")
    private String photoPath;

    @SerializedName("homeId")
    // Home properties
    private String homeId;
    @SerializedName("address")
    private String address;
    @SerializedName("urlMqtt")
    private String urlMqtt;
    @SerializedName("userMQTT")
    private String userMQTT;
    @SerializedName("passwordMQTT")
    private String passwordMQTT;

    // Constructor
    public HomeUser(String userId, String name, String password, String homeId) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.homeId = homeId;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrlMqtt() {
        return urlMqtt;
    }

    public void setUrlMqtt(String urlMqtt) {
        this.urlMqtt = urlMqtt;
    }

    public String getUserMQTT() {
        return userMQTT;
    }

    public void setUserMQTT(String userMQTT) {
        this.userMQTT = userMQTT;
    }

    public String getPasswordMQTT() {
        return passwordMQTT;
    }

    public void setPasswordMQTT(String passwordMQTT) {
        this.passwordMQTT = passwordMQTT;
    }
}
