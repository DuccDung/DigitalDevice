package com.example.digitaldevice.AddUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.model.Users;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.SessionManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserActivity extends AppCompatActivity {
    private EditText searchInput;
    private Button btnSearch , btnAdd , btnBack;
    private RecyclerView rcv;
    private SessionManager sessionManager;
    private DataUserLocal dataUserLocal;
    private AddMemberAdapter addMemberAdapter;
    private LinearLayout formDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        searchInput = findViewById(R.id.edtSearchUser);
        btnSearch = findViewById(R.id.btnSearchInAdd);
        sessionManager = new SessionManager(this);
        dataUserLocal = new DataUserLocal(this);
        btnAdd = findViewById(R.id.btnThemInAddUser);
        btnBack = findViewById(R.id.btnCancelInSearchUser);
        formDialog = findViewById(R.id.formDialogInSearch);
        rcv = findViewById(R.id.rcvUser);
        rcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        btnBack.setOnClickListener(v->{
            formDialog.setVisibility(View.GONE);
        });
        btnSearch.setOnClickListener(v -> {
            ApiService.apiService.GetUserByID("Bearer " + sessionManager.getToken(), searchInput.getText().toString()).enqueue(new Callback<List<Users>>() {
                @Override
                public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                    addMemberAdapter = new AddMemberAdapter(AddUserActivity.this, response.body(), new AddMemberAdapter.MemberClick() {
                        @Override
                        public void MemberOnClick(String userID) {
                            Log.d("đaa", "MemberOnClick: " + userID);
                            formDialog.setVisibility(View.VISIBLE);
                            btnAdd.setOnClickListener(v->{
                                ApiService.apiService.AddUser("Bearer " + sessionManager.getToken() , dataUserLocal.getHomeId() , userID).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.isSuccessful()){
                                            formDialog.setVisibility(View.GONE);
                                            Toast.makeText(AddUserActivity.this, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        else {
                                            Log.e("ErrorCode", "Code: " + response.code());
                                            try {
                                                Log.e("ErrorBody", "Error: " + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

                                    }
                                });
                            });
                        }
                    });
                    rcv.setAdapter(addMemberAdapter);
                }

                @Override
                public void onFailure(Call<List<Users>> call, Throwable t) {

                }
            });
        });
    }
}