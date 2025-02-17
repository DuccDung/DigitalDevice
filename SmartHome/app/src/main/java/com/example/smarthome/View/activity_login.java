package com.example.smarthome.View;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.api_service.ApiService;
import com.example.smarthome.api_service.DataCallback;
import com.example.smarthome.model.Users;
import com.example.smarthome.utils.DbUserHelper;
import com.example.smarthome.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class activity_login extends AppCompatActivity {

    private Button btnLogin;
    private TextView txtAcc;
    private TextView txtPass;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btnLogin);
        txtAcc = findViewById(R.id.txtAccountInput);
        txtPass = findViewById(R.id.txtPasswordInput);
        // Khởi tạo database Sqlite
        DbUserHelper dbUserHelper = new DbUserHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(new DataCallback<Users>() {
                    @Override
                    public void onSuccess(Users data) {
                        Toast.makeText(activity_login.this, data.getName(), Toast.LENGTH_SHORT).show();

                        boolean isInserted = dbUserHelper.insertOrUpdateUser(
                                data.getUserId(), // userId
                                data.getName(), // name
                                data.getPassWord(), // password
                                data.getPhotoPath(), // photoPath
                                null // homeId
                        );

                        if (isInserted) {
                            Log.d("sqlite" , "insert user on success");
                        } else {
                            Log.d("sqlite" , "insert user on failure");
                        }

                        // chuyển activity
                        Intent intent = new Intent(activity_login.this , DashBoardActivity.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });

            }
        });
    }

    public void login(DataCallback<Users> usersDataCallback) {
        String name = txtAcc.getText().toString();
        String pass = txtPass.getText().toString();
        ApiService.apiService.loginUser(name , pass).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(activity_login.this, "connect Success and data", Toast.LENGTH_SHORT).show();
                    Users user = response.body();
                    usersDataCallback.onSuccess(user);
                }
                else {
                    Toast.makeText(activity_login.this, "connect fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(activity_login.this, "Lỗi" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}