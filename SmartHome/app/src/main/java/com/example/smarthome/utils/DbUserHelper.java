package com.example.smarthome.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbUserHelper extends SQLiteOpenHelper {

    // Tên database
    private static final String DATABASE_NAME = "DbUser.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng
    private static final String TABLE_USERS = "users";

    // Các cột trong bảng
    private static final String COLUMN_ID = "userId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHOTO = "photoPath";
    private static final String COLUMN_HOME_ID = "homeId";

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_PASSWORD + " TEXT, "
            + COLUMN_PHOTO + " TEXT, "
            + COLUMN_HOME_ID + " TEXT)";

    public DbUserHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Kiểm tra xem có user nào trong database chưa
    public boolean isUserExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Thêm user mới (Chỉ lưu 1 user duy nhất)
    public boolean insertOrUpdateUser(String userId, String name, String password, String photoPath, String homeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, userId);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHOTO, photoPath);
        values.put(COLUMN_HOME_ID, homeId);

        if (isUserExists()) {
            // Nếu đã có user, cập nhật user hiện tại
            int result = db.update(TABLE_USERS, values, null, null);
            db.close();
            return result > 0;
        } else {
            // Nếu chưa có user, thêm mới
            long result = db.insert(TABLE_USERS, null, values);
            db.close();
            return result != -1;
        }
    }

    // Lấy thông tin user duy nhất
    public Cursor getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    // Xóa user (Chỉ xóa user duy nhất)
    public boolean deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, null, null);
        db.close();
        return result > 0;
    }
}
