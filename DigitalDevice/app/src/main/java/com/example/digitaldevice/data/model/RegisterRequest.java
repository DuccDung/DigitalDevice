package com.example.digitaldevice.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("Name")
    private String name;
    @SerializedName("Password")
    private String password;
    @SerializedName("Phone")
    private String phone;

}
