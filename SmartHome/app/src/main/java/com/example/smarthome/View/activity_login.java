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
import com.example.smarthome.utils.DataUserLocal;
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
        // Init database Sqlite
        DbUserHelper dbUserHelper = new DbUserHelper(this);
       // check dataUserLocal
        if(dbUserHelper.isUserExists() && dbUserHelper.isUrlMqttExists()){
            // The user already exists and has selected a house.
            // if user and home already exists then transfer activity
            Intent intent = new Intent(activity_login.this , DashBoardActivity.class);
            startActivity(intent);
        }
        else if(dbUserHelper.isUserExists() && !dbUserHelper.isUrlMqttExists() ){
            // if user already exist and home null then transfer SelectHomeActivity
            Intent intent = new Intent(activity_login.this , SelectHomeActivity.class);
            startActivity(intent);
        }



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(new DataCallback<Users>() {
                    @Override
                    public void onSuccess(Users data) {
                        Toast.makeText(activity_login.this, data.getName(), Toast.LENGTH_SHORT).show();

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
                            } else {
                                Log.d("sqlite" , "insert user on failure");
                            }
                        }
                        // chuyển activity
                        Intent intent = new Intent(activity_login.this , DashBoardActivity.class);
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
                    Log.d("bug call api" , "no data!");
                }
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                usersDataCallback.onFailure(t);
            }
        });
    }
}