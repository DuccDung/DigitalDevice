package com.example.digitaldevice.view.main.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.DeviceVehicle;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.main.adapter.VehicleAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleFragment extends Fragment implements VehicleAdapter.VehicleOnClick {
    private RecyclerView rcvVehicle;
    private VehicleAdapter vehicleAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vehicle_fragment , container , false);
        rcvVehicle = view.findViewById(R.id.rcv_vehicle);
        rcvVehicle.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String homeId = DataUserLocal.getInstance(requireContext()).getHomeId(); // Lấy dữ liệu homeId từ local

        LoadData(new DataCallback<List<DeviceVehicle>>() {
            @Override
            public void onSuccess(List<DeviceVehicle> data) {
                vehicleAdapter = new VehicleAdapter(VehicleFragment.this , data);
                rcvVehicle.setAdapter(vehicleAdapter);
                vehicleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error API Vehicle" , t.getMessage());
            }
        } , homeId);
    }
    private void LoadData(DataCallback<List<DeviceVehicle>> dataCallback , String homeId){
        ApiService.apiService.GetAllVehicleByHome(homeId).enqueue(new Callback<List<DeviceVehicle>>() {
            @Override
            public void onResponse(Call<List<DeviceVehicle>> call, Response<List<DeviceVehicle>> response) {
                if(response.isSuccessful() && response.body() != null){
                    dataCallback.onSuccess(response.body());
                }
                else {
                    Log.d("Error API" , "No data in api getAllDeviceVehicle!");
                }
            }

            @Override
            public void onFailure(Call<List<DeviceVehicle>> call, Throwable t) {
                dataCallback.onFailure(t);
            }
        });
    }

    @Override
    public void btnDetailOnClick(@NonNull DeviceVehicle deviceVehicle) {
        ((MainActivity) requireContext()).openMapFragment();
    }
}
