package com.example.digitaldevice.view.main.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.digitaldevice.R;
import com.example.digitaldevice.utils.MqttHandler;
import com.example.digitaldevice.view.main.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private boolean isSatellite = false; // Biến kiểm tra chế độ bản đồ
    private FloatingActionButton btnToggleMap; // Khai báo nút FAB
    private MqttHandler mqttHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageView imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> {
            ((MainActivity) requireContext()).closeMapFragment();
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLastLocation();

        // Tìm SupportMapFragment sau khi layout đã sẵn sàng
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        //Lấy FloatingActionButton từ layout
        FloatingActionButton btnToggleMap = view.findViewById(R.id.btn_toggle_map);
        btnToggleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMap != null) {
                    isSatellite = !isSatellite; // Đảo trạng thái

                    if (isSatellite) {
                        myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        btnToggleMap.setImageResource(R.drawable.ic_earth); // Đổi icon sang trái đất
                    } else {
                        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        btnToggleMap.setImageResource(R.drawable.ic_map); // Đổi icon về bản đồ
                    }
                }
            }
        });
    }
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_PERMISSION_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                updateMap(location);
            } else {
                Toast.makeText(requireContext(), "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
            }
        });

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation =location;

                    //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    //mapFragment.getMapAsync(MapFragment.this);
                }
            }
        });
    }

    private void updateMap(Location location) {
        if (myMap != null) {
            LatLng locate = new LatLng(location.getLatitude(), location.getLongitude());
            myMap.addMarker(new MarkerOptions().position(locate).title("Vị trí của bạn"));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate, 15));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;
        //
        if (currentLocation != null) {
            LatLng locate = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            myMap.addMarker(new MarkerOptions().position(locate).title("Vị trí của bạn"));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate, 15));
        } else {
            Toast.makeText(requireContext(), "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
        }

        // Tọa độ Hà Nội
        LatLng hanoi = new LatLng(21.0285,105.8542);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi,15));
        MarkerOptions options = new MarkerOptions().position(hanoi).title("Hà Nội");
        //Đổi màu biểu tượng điểm -> hồng cánh sen (magenta).
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        myMap.addMarker(options);

        // Bật các cử chỉ thu phóng (zoom)
        myMap.getUiSettings().setZoomControlsEnabled(true); // Hiển thị nút zoom
        myMap.getUiSettings().setZoomGesturesEnabled(true); // Cho phép zoom bằng tay


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(requireContext(), "Location permission is denied, please allow the permission to access", Toast.LENGTH_SHORT).show();
            }
        }
    }
}