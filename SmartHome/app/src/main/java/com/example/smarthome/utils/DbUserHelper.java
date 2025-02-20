package com.example.smarthome.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbUserHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DbUser.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_USERS = "users";

    public static final String COLUMN_ID = "userId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHOTO = "photoPath";
    public static final String COLUMN_HOME_ID = "homeId";
    public static final String COLUMN_URL_MQTT = "urlMqtt";
    public static final String COLUMN_USER_MQTT = "userMqtt";
    public static final String COLUMN_PASSWORD_MQTT = "passwordMqtt";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_PASSWORD + " TEXT, "
            + COLUMN_PHOTO + " TEXT, "
            + COLUMN_HOME_ID + " TEXT, "
            + COLUMN_URL_MQTT + " TEXT, "
            + COLUMN_USER_MQTT + " TEXT, "
            + COLUMN_PASSWORD_MQTT + " TEXT)";

    public DbUserHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_URL_MQTT + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_USER_MQTT + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PASSWORD_MQTT + " TEXT");
        }
    }

    public boolean isUserExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertOrUpdateUser(String userId, String name, String password, String photoPath, String homeId, String urlMqtt, String userMqtt, String passwordMqtt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, userId);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHOTO, photoPath);
        values.put(COLUMN_HOME_ID, homeId);
        values.put(COLUMN_URL_MQTT, urlMqtt);
        values.put(COLUMN_USER_MQTT, userMqtt);
        values.put(COLUMN_PASSWORD_MQTT, passwordMqtt);

        if (isUserExists()) {
            int result = db.update(TABLE_USERS, values, null, null);
            db.close();
            return result > 0;
        } else {
            long result = db.insert(TABLE_USERS, null, values);
            db.close();
            return result != -1;
        }
    }

    public Cursor getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    public boolean deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, null, null);
        db.close();
        return result > 0;
    }
    public boolean isUrlMqttExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_URL_MQTT + " FROM " + TABLE_USERS + " WHERE " + COLUMN_URL_MQTT + " IS NOT NULL AND " + COLUMN_URL_MQTT + " != ''", null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
