package com.example.digitaldevice.view.main.fragment;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.Users;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private TextView UserID , txtName , txtPass , txtPhone;
    private SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        UserID = view.findViewById(R.id.txtIdInAccount);
        txtName = view.findViewById(R.id.txtNameAccount);
        txtPass = view.findViewById(R.id.txtPasswordInAcount);
        txtPhone = view.findViewById(R.id.txtPhoneInAccount);
        sessionManager = new SessionManager(requireContext());
        InitData(new DataCallback<Users>() {
            @Override
            public void onSuccess(Users data) {
                UserID.setText(data.getUserId().toString());
                txtName.setText(data.getName());
                txtPass.setText(data.getPassWord());
                txtPhone.setText(data.getPhone());

                ImageView imageView = view.findViewById(R.id.avatarImageInAccount);
                if (imageView != null) {
                    Context context = imageView.getContext();
                    Glide.with(context).load(ApiService.BASE_URL + data.getPhotoPath())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.avatar).into(imageView);
                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void InitData(DataCallback<Users> callback){
        if (!isAdded()) return;
        DataUserLocal dataUserLocal = DataUserLocal.getInstance(requireContext());
        sessionManager.CheckRefreshToken();
        ApiService.apiService.GetUser("Bearer " +sessionManager.getToken() , dataUserLocal.getUserId()).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.isSuccessful() && response.body() != null){
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.e("bug", "onFailure: " + t.getMessage());
            }
        });

    }

}