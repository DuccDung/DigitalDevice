package com.example.smarthome.api_service;

import com.example.smarthome.model.DeviceFunction;
import com.example.smarthome.model.Room;
import com.example.smarthome.model.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    //https://192.168.0.107:7012/api/Leanners/get-leanners

    // Base URL
    String BASE_URL = "https://192.168.1.177:7012/";
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(SSLUtils.getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    // Static method to get the base domain
    static String getBaseDomain() {
        return BASE_URL;
    }

    @GET("api/Users/GetLogin")
    Call<Users> loginUser(@Query("Name") String name , @Query("Password") String password);

    @GET("api/Users/Register")
    Call<Void> registerUser(@Query("Name") String Name , @Query("Password") String Password , @Query("Email") String Email , @Query("Phone") String Phone);

    @GET("/api/Rooms/GetRoomsByHomeID")
    Call<List<Room>> GetRooms(@Query("HomeID") String HomeID);

    @GET("/api/Devices/GetDevicesByRoomID")
    Call<List<DeviceFunction>> GetDeviceFunction(@Query("RoomID") String RoomID , @Query("HomeID") String HomeID);
}
