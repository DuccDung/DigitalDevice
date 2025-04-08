package com.example.digitaldevice.data.model;

import com.google.gson.annotations.SerializedName;

public class RoomCreateResponse {
    @SerializedName("roomId")
    private String roomId;

    @SerializedName("name")
    private String name;

    @SerializedName("homeId")
    private String homeId;

    @SerializedName("photoPath")
    private String photoPath;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
} 