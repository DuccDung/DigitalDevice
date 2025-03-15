package com.example.digitaldevice.view.login;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.LoginResponse;
import com.example.digitaldevice.data.model.Users;
import com.example.digitaldevice.utils.DbUserHelper;
import com.example.digitaldevice.R;
import com.example.digitaldevice.utils.SessionManager;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.select_home.SelectHomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

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
        // Init database Sqlite
        DbUserHelper dbUserHelper = new DbUserHelper(this);

        dbUserHelper.deleteUser();

        // check dataUserLocal
        if(dbUserHelper.isUserExists() && dbUserHelper.isUrlMqttExists()){
            // The user already exists and has selected a house.
            // if user and home already exists then transfer activity
            Intent intent = new Intent(LoginActivity.this , MainActivity.class);
            startActivity(intent);
        }
        else if(dbUserHelper.isUserExists() && !dbUserHelper.isUrlMqttExists() ){
            // if user already exist and home null then transfer SelectHomeActivity
            Intent intent = new Intent(LoginActivity.this , SelectHomeActivity.class);
            startActivity(intent);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(new DataCallback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse loginResponse) {
                        Users data = loginResponse.getUser();
                        Toast.makeText(LoginActivity.this, data.getName(), Toast.LENGTH_SHORT).show();

                        if (!dbUserHelper.isUserExists()) {
                            boolean isInserted = dbUserHelper.insertOrUpdateUser(
                                    data.getUserId(), // userId
                                    data.getName(), // name
                                    data.getPassWord(), // password
                                    data.getPhotoPath(),
                                    null,
                                    null,
                                    null,
                                    null
                            );

                            if (isInserted) {
                                Log.d("sqlite" , "insert user on success");
                                SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                sessionManager.saveToken(loginResponse.getToken());

                            } else {
                                Log.d("sqlite" , "insert user on failure");
                            }
                        }
                        // chuyển activity
                        Intent intent = new Intent(LoginActivity.this , SelectHomeActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("Bug login" , t.getMessage());
                    }
                });
            }
        });
    }
    // login processing function
    public void login(DataCallback<LoginResponse> usersDataCallback) {
        String name = txtAcc.getText().toString();
        String pass = txtPass.getText().toString();
        ApiService.apiService.loginUser(name , pass).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(LoginActivity.this, "connect Success and data", Toast.LENGTH_SHORT).show();
                    LoginResponse loginResponse = response.body();
                    usersDataCallback.onSuccess(loginResponse);
                }
                else {
                    Toast.makeText(LoginActivity.this, "connect fail", Toast.LENGTH_SHORT).show();
                    Log.d("bug call api" , "no data!");
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                usersDataCallback.onFailure(t);
            }
        });
    }
}