package com.example.digitaldevice.data.model;

import com.google.gson.annotations.SerializedName;

public class Room {

    public Room(String roomId, String homeId, String name, String photoPath) {
        this.roomId = roomId;
        this.homeId = homeId;
        this.name = name;
        this.photoPath = photoPath;
    }

    @SerializedName("roomId")
    private String roomId;

    @SerializedName("homeId")
    private String homeId;

    @SerializedName("name")
    private String name;

    @SerializedName("photoPath")
    private String photoPath;



    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
