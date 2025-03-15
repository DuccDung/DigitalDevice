package com.example.digitaldevice.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_TOKEN = "token";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Lưu token
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // Lấy token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Xóa token (khi đăng xuất)
    public void clearToken() {
        editor.remove(KEY_TOKEN);
        editor.apply();
    }
}
