package com.example.smarthome.model;

import com.google.gson.annotations.SerializedName;

public class Users {

    @SerializedName("userId")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String passWord;
    @SerializedName("photoPath")
    private String photoPath;
    @SerializedName("phone")
    private String phone;

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Users(String name, String passWord, String photoPath, String phone) {
        this.name = name;
        this.passWord = passWord;
        this.photoPath = photoPath;
        this.phone = phone;
    }
}
