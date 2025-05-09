package com.example.digitaldevice.data.api_service;

import com.example.digitaldevice.data.model.Device;
import com.example.digitaldevice.data.model.DeviceCreateResponse;
import com.example.digitaldevice.data.model.DeviceFunction;
import com.example.digitaldevice.data.model.DeviceVehicle;
import com.example.digitaldevice.data.model.HomeUser;
import com.example.digitaldevice.data.model.ImageUploadResponse;
import com.example.digitaldevice.data.model.LoginResponse;
import com.example.digitaldevice.data.model.Room;
import com.example.digitaldevice.data.model.RoomCreateResponse;
import com.example.digitaldevice.data.model.Users;
import com.example.digitaldevice.data.model.WeatherResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    //https://api.openweathermap.org/data/2.5/weather?q=Hanoi&appid=db7e8be2cd9133533090f6e5c64f6bc7&units=metric
    //https://192.168.0.107:7012/api/Leanners/get-leanners
    //https://api.openweathermap.org/data/2.5/weather?q=Hanoi,VN&appid=db7e8be2cd9133533090f6e5c64f6
    // Base URL https://be0f-1-55-142-179.ngrok-free.app
    String BASE_URL = "https://84de-171-224-179-15.ngrok-free.app/";
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(SSLUtils.getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);
    ApiService apiWeather = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(SSLUtils.getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    // Static method to get the base domain
    static String getBaseDomain() {
        return BASE_URL;
    }

    @GET("api/Auth/GetLogin") // get the token and user
    Call<LoginResponse> loginUser(@Query("Name") String name , @Query("Password") String password);
    @POST("api/Auth/Register")
    Call<Void> registerUser(@Query("Name") String Name , @Query("Password") String Password , @Query("Phone") String Phone);
    @GET("/api/Rooms/GetRoomsByHomeID")
    Call<List<Room>> GetRooms(@Header("Authorization") String token,@Query("HomeID") String HomeID);
    @GET("/api/Devices/GetDevicesFunctionByRoomID")
    Call<List<DeviceFunction>> GetDeviceFunction(@Header("Authorization") String token,@Query("RoomID") String RoomID , @Query("HomeID") String HomeID);
    @GET("/api/Users/GetHomeUsersByUserId")
    Call<List<HomeUser>> GetHomesByUserID(@Header("Authorization") String token, @Query("userId") String UserID);
    @GET("/api/Devices/GetAllDevice")
    Call<List<Device>> GetALlDeviceByHome(@Header("Authorization") String token,@Query("homeId") String homeId);
    @GET("/api/Devices/GetAllVehicleByHome")
    Call<List<DeviceVehicle>> GetAllVehicleByHome(@Header("Authorization") String token , @Query("homeId") String homeId);
    @GET("https://api.openweathermap.org/data/2.5/weather?q=Hanoi&appid=db7e8be2cd9133533090f6e5c64f6bc7&units=metric")
    Call<WeatherResponse> GetWeather();
    @GET("api/Users/GetUser")
    Call<Users> GetUser(@Header("Authorization") String token , @Query("UserID") String UserID);
    @GET("api/Users/GetUsersByHomeId")
    Call<List<Users>> GetUserByHomeID(@Header("Authorization") String token , @Query("homeId") String HomeID);
    @DELETE("api/Users/RemoveUserFromHome")
    Call<Void> DeleteUserByID(@Header("Authorization") String token , @Query("userId") String userId , @Query("homeId") String HomeID );
    @GET("api/Users/SearchUsers")
    Call<List<Users>> GetUserByID(@Header("Authorization") String token , @Query("keyword") String userId);
    @POST("api/Users/AddUserToHome")
    Call<Void> AddUser(@Header("Authorization") String token , @Query("HomeID") String homeId , @Query("UserID") String userID );
    @GET("/api/Devices/GetDevicesFunctionByRoomID")
    Call<List<Device>> GetDevice(@Header("Authorization") String token ,@Query("RoomID") String RoomID);
    @GET("/api/Devices/GetDevicesByRoomID")
    Call<List<Device>> GetDevice_2(@Header("Authorization") String token , @Query("RoomID") String RoomID);
    @POST("/api/Devices/CreateDevice")
    Call<DeviceCreateResponse> CreateDevice(@Header("Authorization") String token, @Query("Name") String Name, @Query("DeviceId") String DeviceId, @Query("RoomId") String RoomId,@Query("FunctionId") String FunctionId);
    @POST("/api/Rooms/CreateRoom")
    Call<RoomCreateResponse> CreateRoom( @Header("Authorization") String token, @Query("Name") String Name, @Query("HomeId") String HomeId,  @Query("PhotoPath") String PhotoPath);
    @Multipart
    @POST("/api/Rooms/UploadRoomImage")
    Call<ImageUploadResponse> uploadRoomImage(@Header("Authorization") String token,@Part MultipartBody.Part file);
    @GET("/api/Admin/RoomCount")
    Call<Integer> GetRoomCount(@Header("Authorization") String token , @Query("homeId") String homeId);
    @DELETE("/api/Rooms/DeleteRoom")
    Call<ResponseBody> DeleteRoom(@Header("Authorization") String token , @Query("RoomId") String RoomId);
    @DELETE("/api/Devices/DeleteDevice")
    Call<ResponseBody> DeleteDevice(@Header("Authorization") String token , @Query("DeviceId") String DeviceId);
}
