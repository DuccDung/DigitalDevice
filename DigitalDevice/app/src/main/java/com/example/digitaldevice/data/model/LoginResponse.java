package com.example.digitaldevice.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("user")
    private Users user;

    @SerializedName("token")
    private String token;

    // Getters và Setters
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}