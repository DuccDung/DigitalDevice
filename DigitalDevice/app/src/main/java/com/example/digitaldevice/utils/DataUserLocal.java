package com.example.digitaldevice.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataUserLocal {
    private static DataUserLocal instance;
    private String userId;
    private String homeId;
    private String urlMqtt;
    private String userMqtt;
    private String passwordMqtt;

    @SuppressLint("Range")
    public DataUserLocal(Context context) {
        DbUserHelper dbUserHelper = new DbUserHelper(context);
        SQLiteDatabase db = dbUserHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbUserHelper.TABLE_USERS, null);

        if (cursor != null && cursor.moveToFirst()) {
            this.userId = cursor.getString(cursor.getColumnIndex(DbUserHelper.COLUMN_ID));
            this.homeId = cursor.getString(cursor.getColumnIndex(DbUserHelper.COLUMN_HOME_ID));
            this.urlMqtt = cursor.getString(cursor.getColumnIndex(DbUserHelper.COLUMN_URL_MQTT));
            this.userMqtt = cursor.getString(cursor.getColumnIndex(DbUserHelper.COLUMN_USER_MQTT));
            this.passwordMqtt = cursor.getString(cursor.getColumnIndex(DbUserHelper.COLUMN_PASSWORD_MQTT));
            cursor.close();
        }
    }

    public static synchronized DataUserLocal getInstance(Context context) {
        if (instance == null) {
            instance = new DataUserLocal(context);
        }
        return instance;
    }

    public String getHomeId() {
        return homeId;
    }
    public String getUserId() {
        return userId;
    }

    public String getUrlMqtt() {
        return urlMqtt;
    }

    public String getUserMqtt() {
        return userMqtt;
    }

    public String getPasswordMqtt() {
        return passwordMqtt;
    }

    public void setUserId(Context context, String userId) {
        updateField(context, DbUserHelper.COLUMN_ID, userId);
        this.userId = userId;
    }

    public void setUrlMqtt(Context context, String urlMqtt) {
        updateField(context, DbUserHelper.COLUMN_URL_MQTT, urlMqtt);
        this.urlMqtt = urlMqtt;
    }
    public void setHomeId(Context context , String homeId){
        updateField(context ,DbUserHelper.COLUMN_HOME_ID , homeId);
        this.homeId = homeId;
    }

    public void setUserMqtt(Context context, String userMqtt) {
        updateField(context, DbUserHelper.COLUMN_USER_MQTT, userMqtt);
        this.userMqtt = userMqtt;
    }

    public void setPasswordMqtt(Context context, String passwordMqtt) {
        updateField(context, DbUserHelper.COLUMN_PASSWORD_MQTT, passwordMqtt);
        this.passwordMqtt = passwordMqtt;
    }

    private void updateField(Context context, String column, String value) {
        DbUserHelper dbUserHelper = new DbUserHelper(context);
        SQLiteDatabase db = dbUserHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, value);
        db.update(DbUserHelper.TABLE_USERS, values, null, null);
        db.close();
    }
}
