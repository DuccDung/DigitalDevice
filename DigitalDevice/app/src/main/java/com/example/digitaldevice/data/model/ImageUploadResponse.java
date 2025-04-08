package com.example.digitaldevice.data.model;

import com.google.gson.annotations.SerializedName;

public class ImageUploadResponse {
    @SerializedName("path")
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
} 