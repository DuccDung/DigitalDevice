package com.example.digitaldevice.view.main.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.digitaldevice.R;
import com.example.digitaldevice.utils.DataUserLocal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNewRoomFragment extends Fragment {
    private EditText editTextRoomName;
    private Button btnSave, btnCancel;
    private ImageView imageRoomPhoto;
    private String homeId;
    private String savedImagePath;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_newroom_fragment, container, false);
        
        // Lấy homeId từ DataUserLocal
        homeId = DataUserLocal.getInstance(requireContext()).getHomeId();
        
        // Ánh xạ các view
        editTextRoomName = view.findViewById(R.id.editTextRoomName);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        imageRoomPhoto = view.findViewById(R.id.imageRoomPhoto);

        // Xử lý click vào ảnh để chọn ảnh mới
        imageRoomPhoto.setOnClickListener(v -> {
            if (checkPermission()) {
                openGallery();
            } else {
                requestPermission();
            }
        });

        // Xử lý sự kiện click button Save
        btnSave.setOnClickListener(v -> {
            String roomName = editTextRoomName.getText().toString();
            
            // Kiểm tra dữ liệu đầu vào
            if (roomName.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập tên phòng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (savedImagePath == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn ảnh cho phòng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Gọi hàm thêm phòng mới
            addNewRoom(roomName, homeId);
        });

        // Xử lý sự kiện click button Cancel
        btnCancel.setOnClickListener(v -> {
            // Quay lại Fragment trước đó (SelectRoomFragment)
            requireActivity().onBackPressed();
        });

        return view;
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) 
            == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            PERMISSION_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    // Lưu ảnh vào thư mục assets
                    savedImagePath = saveImageToAssets(selectedImageUri);
                    // Hiển thị ảnh đã chọn
                    imageRoomPhoto.setImageURI(Uri.fromFile(new File(savedImagePath)));
                } catch (IOException e) {
                    Toast.makeText(requireContext(), "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String saveImageToAssets(Uri imageUri) throws IOException {
        // Tạo tên file với timestamp để tránh trùng lặp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "ROOM_" + timeStamp + ".jpg";

        // Tạo thư mục images/rooms trong assets nếu chưa tồn tại
        File assetsDir = new File(requireContext().getFilesDir(), "assets/images/rooms");
        if (!assetsDir.exists()) {
            assetsDir.mkdirs();
        }

        // Tạo file mới
        File imageFile = new File(assetsDir, imageFileName);

        // Copy ảnh từ Uri vào file mới
        try (InputStream in = requireContext().getContentResolver().openInputStream(imageUri);
             FileOutputStream out = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        // Trả về đường dẫn tương đối từ assets
        return "images/rooms/" + imageFileName;
    }

    private void addNewRoom(String roomName, String homeId) {
        // TODO: Implement API call to add new room
        // 1. Tạo roomId mới
        // 2. Gọi API CreateRoom với các tham số:
        //    - RoomId (cái này sẽ đc tự gen)
        //    - Name (roomName)
        //    - HomeId (homeId)
        //    - PhotoPath (savedImagePath) - đường dẫn tương đối từ assets
        // 3. Xử lý response:
        //    - Nếu thành công: Quay lại SelectRoomFragment và refresh danh sách
        //    - Nếu thất bại: Hiển thị thông báo lỗi
    }
}
