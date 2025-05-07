package com.example.digitaldevice.view.register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.view.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText txtUsername, txtPhone, txtPassword, txtPassword2;
    private TextView txtIntentLogin;
    private LinearLayout dialogSuccess;
    private ScrollView formLayout;
    private Button btnRegister , btnIntent;
    private ImageView imgPass1 , imgPass2;
    private boolean isPasswordVisible1 = false;
    private boolean isPasswordVisible2 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        makeStatusBarTransparent();
        applyTopPadding();
        initView();
        imgPass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible1) {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPass1.setImageResource(R.drawable.ic_hide);
                } else {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgPass1.setImageResource(R.drawable.ic_show);
                }
                isPasswordVisible1 = !isPasswordVisible1;
            }
        });
        imgPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible2) {
                    txtPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPass2.setImageResource(R.drawable.ic_hide);
                } else {
                    txtPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgPass2.setImageResource(R.drawable.ic_show);
                }
                isPasswordVisible2 = !isPasswordVisible2;
            }
        });
        btnRegister.setOnClickListener(v -> validateForm());
        txtIntentLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
            startActivity(intent);
            finish();
        });
        btnIntent.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
            intent.putExtra("username", txtUsername.getText().toString());
            intent.putExtra("password", txtPassword.getText().toString());
            startActivity(intent);
            finish();
        });
    }
    private void registerSuccess(){
        formLayout.setVisibility(View.GONE);
        dialogSuccess.setVisibility(View.VISIBLE);
    }

    private void registerRequest(String Name, String Password, String Phone) {
        ApiService.apiService.registerUser(Name, Password, Phone).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                  // register success
                    registerSuccess();
                }else {
                    Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initView() {
        txtUsername = findViewById(R.id.txtAccountInputRegister);
        txtPhone = findViewById(R.id.txtPhoneInputRegister);
        txtPassword = findViewById(R.id.txtPasswordInputRegister);
        txtPassword2 = findViewById(R.id.txtPasswordInputRegister2);
        btnRegister = findViewById(R.id.btnRegister);
        txtIntentLogin = findViewById(R.id.txt_intent_login);
        dialogSuccess = findViewById(R.id.dialog_success_register);
        formLayout = findViewById(R.id.formRegister);
        btnIntent = findViewById(R.id.btn_intent_register_login);
        imgPass1 = findViewById(R.id.togglePasswordVisibility);
        imgPass2 = findViewById(R.id.togglePasswordVisibility2);
    }
    private void validateForm() {
        String username = txtUsername.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String password = txtPassword.getText().toString();
        String password2 = txtPassword2.getText().toString();

        if (username.isEmpty()) {
            txtUsername.setError("Please enter a username");
            txtUsername.requestFocus();
            return;
        }

        if (phone.isEmpty() || !phone.matches("^\\d{10}$")) {
            txtPhone.setError("Please enter a valid 10-digit phone number");
            txtPhone.requestFocus();
            return;
        }

        if (password.length() < 6) {
            txtPassword.setError("Password must be at least 6 characters");
            txtPassword.requestFocus();
            return;
        }
        if (!password.equals(password2)) {
            txtPassword2.setError("Passwords do not match");
            txtPassword2.requestFocus();
            return;
        }
        registerRequest(username, password, phone);
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