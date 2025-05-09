package com.example.digitaldevice.view.main.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.digitaldevice.utils.MqttEvent;
import com.example.digitaldevice.utils.MqttHandler;
import com.example.digitaldevice.utils.SessionManager;
import com.example.digitaldevice.view.main.MainActivity;
import com.example.digitaldevice.view.main.adapter.VehicleAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleFragment extends Fragment implements VehicleAdapter.VehicleOnClick{
    private RecyclerView rcvVehicle;
    private VehicleAdapter vehicleAdapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SessionManager sessionManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vehicle_fragment, container, false);
        rcvVehicle = view.findViewById(R.id.rcv_vehicle);
        rcvVehicle.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            Log.d("VehicleFragment", "EventBus registered!");
            EventBus.getDefault().register(this);
        } else {
            Log.d("VehicleFragment", "EventBus already registered!");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            Log.d("VehicleFragment", "EventBus unregistered!");
        }
    }


    // Nhận dữ liệu MQTT từ EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMqttMessageReceived(@NonNull MqttEvent event) {
        Log.d("VehicleFragment", "Nhận MQTT: " + event.topic + " - " + event.payload);
        vehicleAdapter.updateData(event.topic , event.payload);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        sessionManager.CheckRefreshToken();
        String homeId = DataUserLocal.getInstance(requireContext()).getHomeId(); // Lấy dữ liệu homeId từ local
        LoadData(new DataCallback<List<DeviceVehicle>>() {
            @Override
            public void onSuccess(List<DeviceVehicle> data) {
                vehicleAdapter = new VehicleAdapter(requireContext(),VehicleFragment.this, data);
                rcvVehicle.setAdapter(vehicleAdapter);
                vehicleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error API Vehicle", t.getMessage());
            }
        }, homeId);
    }

    private void LoadData(DataCallback<List<DeviceVehicle>> dataCallback, String homeId) {
        String token ="Bearer " + sessionManager.getToken();
        ApiService.apiService.GetAllVehicleByHome(token , homeId).enqueue(new Callback<List<DeviceVehicle>>() {
            @Override
            public void onResponse(Call<List<DeviceVehicle>> call, Response<List<DeviceVehicle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dataCallback.onSuccess(response.body());
                } else {
                    Log.d("Error API", "No data in api getAllDeviceVehicle!");
                }
            }

            @Override
            public void onFailure(Call<List<DeviceVehicle>> call, Throwable t) {
                dataCallback.onFailure(t);
            }
        });
    }

    @Override
    public void btnDetailOnClick(@NonNull double latitude, double longitude) {
        ((MainActivity) requireContext()).openMapFragment(latitude , longitude);
    }
}
