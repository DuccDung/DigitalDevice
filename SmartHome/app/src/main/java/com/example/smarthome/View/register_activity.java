package com.example.smarthome.View;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smarthome.api_service.ApiService;
import com.example.smarthome.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register_activity extends AppCompatActivity {

    private Button btnRegister;
    private EditText txtAcc;
    private EditText txtPass;
    private EditText txtPhone;
    private EditText txtEmail;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtAcc = findViewById(R.id.txtAccountInput);
        txtPass = findViewById(R.id.txtPasswordInput);
        txtEmail = findViewById(R.id.txtEmailInput);
        txtPhone = findViewById(R.id.txtPhoneInput);

        btnRegister = findViewById(R.id.btnRegisterButton);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });
    }

    private void Register(){
        String NameAcc = txtAcc.getText().toString();
        String Password = txtPass.getText().toString();
        String Phone = txtPhone.getText().toString();
        String Email = txtEmail.getText().toString();
        ApiService.apiService.registerUser(NameAcc , Password , Email ,Phone).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(register_activity.this, "Register Success " + response.code(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(register_activity.this, "Register fail" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(register_activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}