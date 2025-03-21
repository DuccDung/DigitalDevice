package com.example.digitaldevice.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.model.LoginResponse;
import com.example.digitaldevice.data.model.Users;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_TOKEN = "token";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context _context;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        _context = context;
    }

    // Lưu token
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // Lấy token
    public String getToken() {
        return  sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Xóa token (khi đăng xuất)
    public void clearToken() {
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public boolean isTokenExpired() {
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        if (token == null) return true; // Nếu token rỗng → coi như hết hạn

        try {
            JWT jwt = new JWT(token);
            Date expiresAt = jwt.getExpiresAt(); // Lấy thời gian hết hạn
            return expiresAt != null && expiresAt.before(new Date()); // So sánh với thời gian hiện tại
        } catch (Exception e) {
            return true; // Nếu token không hợp lệ, coi như hết hạn
        }
    }

    public void CheckRefreshToken(){
        if(isTokenExpired()){
            DbUserHelper dbUserHelper = new DbUserHelper(_context);
            Users user = dbUserHelper.getUserLocal();

             ApiService.apiService.loginUser(user.getName() , user.getPassWord()).enqueue(new Callback<LoginResponse>() {
                 @Override
                 public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                     if(response.isSuccessful() && response.body() != null){
                        LoginResponse loginResponse = response.body();
                        saveToken(loginResponse.getToken());
                        Log.d("Save token" , loginResponse.getToken());
                     }
                 }

                 @Override
                 public void onFailure(Call<LoginResponse> call, Throwable t) {
                     Log.e("Error" , t.getMessage());
                 }
             });
        }
    }

}
