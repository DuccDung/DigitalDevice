package com.example.digitaldevice.data.api_service;


public interface DataCallback<T> {
    void onSuccess(T data);         // Callback khi thành công, trả về kiểu dữ liệu T
    void onFailure(Throwable t);   // Callback khi thất bại
}


