package com.example.digitaldevice.view.main.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.digitaldevice.R;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.model.RoomCreateResponse;
import com.example.digitaldevice.data.model.ImageUploadResponse;
import com.example.digitaldevice.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewRoomFragment extends Fragment {
    private EditText editTextRoomName;
    private Button btnSave, btnCancel;
    private ImageView imageRoomPhoto;
    private String homeId;
    private String savedImagePath;

    private static final int PICK_IMAGE_REQUEST = 1;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_newroom_fragment, container, false);
        if (requireContext() != null) {
            sessionManager = new SessionManager(requireContext());
        }
        // Lấy homeId từ DataUserLocal
        homeId = DataUserLocal.getInstance(requireContext()).getHomeId();

        // Ánh xạ các view
        editTextRoomName = view.findViewById(R.id.editTextRoomName);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        imageRoomPhoto = view.findViewById(R.id.imageRoomPhoto);

        // Xử lý click vào ảnh để chọn ảnh mới
        imageRoomPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
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
            addNewRoom(roomName, homeId, savedImagePath);
        });

        // Xử lý sự kiện click button Cancel
        btnCancel.setOnClickListener(v -> {
            // Quay lại Fragment trước đó (SelectRoomFragment)
            requireActivity().onBackPressed();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    // Hiển thị ảnh đã chọn
                    imageRoomPhoto.setImageURI(selectedImageUri);

                    // Tạo file tạm thời từ Uri
                    File tempFile = createTempFileFromUri(selectedImageUri);

                    // Tạo MultipartBody.Part cho file ảnh
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

                    // Gọi API để upload ảnh
                    ApiService.apiService.uploadRoomImage("Bearer " + sessionManager.getToken() ,imagePart)
                            .enqueue(new Callback<ImageUploadResponse>() {
                                @Override
                                public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        // Lưu đường dẫn ảnh để sử dụng khi tạo room
                                        savedImagePath = response.body().getPath();
                                        Toast.makeText(requireContext(), "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                                    Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Lỗi khi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        // Tạo file tạm thời
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getCacheDir();
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Sao chép dữ liệu từ Uri vào file tạm thời
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return imageFile;
    }

    private void addNewRoom(String roomName, String homeId, String photoPath) {
//        // Log để debug
//        android.util.Log.d("AddNewRoomFragment", "Creating room with: " +
//                "\nName: " + roomName +
//                "\nHomeId: " + homeId +
//                "\nPhotoPath: " + photoPath);

        ApiService.apiService.CreateRoom("Bearer " + sessionManager.getToken(), roomName, homeId, photoPath)
                .enqueue(new Callback<RoomCreateResponse>() {
                    @Override
                    public void onResponse(Call<RoomCreateResponse> call, Response<RoomCreateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(requireContext(), "Tạo phòng thành công", Toast.LENGTH_SHORT).show();

                            // Tìm SelectRoomFragment để refresh danh sách
                            SelectRoomFragment selectRoomFragment = (SelectRoomFragment) requireActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentByTag("SelectRoomFragment");

                            if (selectRoomFragment != null) {
                                selectRoomFragment.refreshRoomList();
                            }

                            // Quay lại SelectRoomFragment
                            requireActivity().onBackPressed();
                        } else {
                            // Log lỗi chi tiết
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                    // Log toàn bộ response để debug
                                    android.util.Log.e("AddNewRoomFragment", "Full error response: " + errorBody);
                                    android.util.Log.e("AddNewRoomFragment", "Error code: " + response.code());
                                    android.util.Log.e("AddNewRoomFragment", "Error message: " + response.message());
                                } else {
                                    errorBody = "Unknown error";
                                }
                            } catch (IOException e) {
                                errorBody = "Error reading error body: " + e.getMessage();
                            }
                            Toast.makeText(requireContext(), "Lỗi khi tạo phòng: " + errorBody, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RoomCreateResponse> call, Throwable t) {
                        android.util.Log.e("AddNewRoomFragment", "Network error: " + t.getMessage());
                        Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
