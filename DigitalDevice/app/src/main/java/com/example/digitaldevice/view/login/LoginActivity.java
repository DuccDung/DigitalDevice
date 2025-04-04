package com.example.digitaldevice.view.login;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.digitaldevice.view.register.RegisterActivity;
import com.example.digitaldevice.view.select_home.SelectHomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextView txtAcc;
    private TextView txtPass;
    private ImageView togglePasswordVisibility;
    private boolean isPasswordVisible = false;
    private TextView txtIntentRegister;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        makeStatusBarTransparent();
        applyTopPadding();
        InitializeView(); // Initialize find
        DbUserHelper dbUserHelper = new DbUserHelper(this); // Init database Sqlite
       // dbUserHelper.deleteUser();
        // check dataUserLocal
        if (dbUserHelper.isUserExists() && dbUserHelper.isUrlMqttExists()) {
            // The user already exists and has selected a house.
            // if user and home already exists then transfer activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (dbUserHelper.isUserExists() && !dbUserHelper.isUrlMqttExists()) {
            // if user already exist and home null then transfer SelectHomeActivity
            Intent intent = new Intent(LoginActivity.this, SelectHomeActivity.class);
            startActivity(intent);
            finish();
        }
        txtIntentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(new DataCallback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse loginResponse) {
                        Users data = loginResponse.getUser();
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
                                // save token Success
                                SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                sessionManager.saveToken(loginResponse.getToken());
                            }
                        }
                        // chuyển activity
                        Intent intent = new Intent(LoginActivity.this, SelectHomeActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("Bug login", t.getMessage());
                    }
                });
            }
        });
        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (isPasswordVisible) {
                        txtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        togglePasswordVisibility.setImageResource(R.drawable.ic_hide);
                    } else {
                        txtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        togglePasswordVisibility.setImageResource(R.drawable.ic_show);
                    }
                    isPasswordVisible = !isPasswordVisible;
            }
        });
    }

    // login processing function
    public void login(DataCallback<LoginResponse> usersDataCallback) {
        String name = txtAcc.getText().toString();
        String pass = txtPass.getText().toString();
        ApiService.apiService.loginUser(name, pass).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    usersDataCallback.onSuccess(loginResponse);
                } else {
                    Log.e("bug call api", "no data!");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                usersDataCallback.onFailure(t);
            }
        });
    }

    public void InitializeView() {
        btnLogin = findViewById(R.id.btnLogin);
        txtAcc = findViewById(R.id.txtAccountInput);
        txtPass = findViewById(R.id.txtPasswordInput);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        txtIntentRegister = findViewById(R.id.txt_intent_register);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        txtAcc.setText(username);
        txtPass.setText(password);
    }
    private void makeStatusBarTransparent() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        window.setStatusBarColor(Color.TRANSPARENT);
    }


    private void applyTopPadding() {
        View contentContainer = findViewById(R.id.fragment_container);

        if (contentContainer != null) {
            int statusBarHeight = getStatusBarHeight();
            contentContainer.setPadding(0, statusBarHeight, 0, 0);
        }
    }


    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}