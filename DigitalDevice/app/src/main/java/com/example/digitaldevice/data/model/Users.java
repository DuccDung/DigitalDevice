package com.example.digitaldevice.data.model;

import android.os.Parcel;
import android.os.Parcelable;

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

    // Constructor mặc định
    public Users() {}

    // Constructor từ Parcel
    protected Users(Parcel in) {
        userId = in.readString();
        name = in.readString();
        photoPath = in.readString();
        phone = in.readString();
    }

    public static final Parcelable.Creator<Users> CREATOR = new Parcelable.Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };



    // Getters và Setters
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
}