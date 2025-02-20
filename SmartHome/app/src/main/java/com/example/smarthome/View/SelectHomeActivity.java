package com.example.smarthome.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.smarthome.R;
import com.example.smarthome.View.Adapter.SelectHouseAdapter;
import com.example.smarthome.api_service.ApiService;
import com.example.smarthome.api_service.DataCallback;
import com.example.smarthome.model.HomeUser;
import com.example.smarthome.utils.DataUserLocal;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectHomeActivity extends AppCompatActivity {
    private List<HomeUser> DataHouses = new ArrayList<>();
    private SelectHouseAdapter HouseAdapter;
    private RecyclerView rcvChoseHouse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_home);
        rcvChoseHouse = findViewById(R.id.rcvSelectHouses);
        rcvChoseHouse.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        HouseAdapter = new SelectHouseAdapter(this);

        InitData(new DataCallback<List<HomeUser>>() {
            @Override
            public void onSuccess(List<HomeUser> data) {
                DataHouses.addAll(data);
                HouseAdapter = new SelectHouseAdapter(DataHouses);
                rcvChoseHouse.setAdapter(HouseAdapter);
                HouseAdapter.notifyDataSetChanged();
            }


            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    private void InitData(DataCallback<List<HomeUser>> callback){
        // Lấy userID trong sqlie
        DataUserLocal dataUserLocal = DataUserLocal.getInstance(this);
        String userId = dataUserLocal.getUserId();

        ApiService.apiService.GetHomesByUserID(userId).enqueue(new Callback<List<HomeUser>>() {
            @Override
            public void onResponse(Call<List<HomeUser>> call, Response<List<HomeUser>> response) {
                if(response.isSuccessful() && response.body() != null){
                    callback.onSuccess(response.body());
                }
                else {
                    Toast.makeText(SelectHomeActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HomeUser>> call, Throwable t) {
                Log.d("Lỗi ở Select house : " , t.getMessage());
            }
        });
    }
    public void IntentDashboard(){
        Intent intent = new Intent(SelectHomeActivity.this , DashBoardActivity.class);
        startActivity(intent);
    }
}