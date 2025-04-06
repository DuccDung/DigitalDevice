package com.example.digitaldevice.view.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaldevice.AddUser.AddUserActivity;
import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.api_service.DataCallback;
import com.example.digitaldevice.data.model.Users;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.SessionManager;
import com.example.digitaldevice.view.main.adapter.MemberAdapter;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserFragment extends Fragment {
    private SessionManager sessionManager;
    private DataUserLocal dataUserLocal;
    private MemberAdapter memberAdapter;
    private RecyclerView rcvMember;
    private LinearLayout formDialog;
    private Button btnDelete , btnBack;
    private TextView txtIntentAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_fragment, container, false);
        rcvMember = view.findViewById(R.id.rcvMember);
        formDialog = view.findViewById(R.id.formDialogInAdd);
        btnDelete = view.findViewById(R.id.btnDeleteInAddUser);
        btnBack = view.findViewById(R.id.btnCancelInAddUser);
        txtIntentAdd = view.findViewById(R.id.txtIntentAddUser);
        rcvMember.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        if (requireContext() != null) {
            sessionManager = new SessionManager(requireContext());
            dataUserLocal = new DataUserLocal(requireContext());
        }
        InitView(new DataCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> data) {
                memberAdapter = new MemberAdapter(requireContext(), data, new MemberAdapter.MemberClick() {
                    @Override
                    public void MemberOnClick(String UserID) {
                        formDialog.setVisibility(View.VISIBLE);
                        btnBack.setOnClickListener(v -> {
                            formDialog.setVisibility(View.GONE);
                        });
                        btnDelete.setOnClickListener(v->{
                            ApiService.apiService.DeleteUserByID("Bearer " + sessionManager.getToken() , UserID , dataUserLocal.getHomeId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.isSuccessful()){
                                        memberAdapter.RefreshView(UserID);
                                        formDialog.setVisibility(View.GONE);
                                        memberAdapter.notifyDataSetChanged();
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
                                    Log.d("looix", t.getMessage());
                                }
                            });
                        });
                    }
                });
                rcvMember.setAdapter(memberAdapter);
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
        txtIntentAdd.setOnClickListener(v->{
            Intent intent = new Intent(requireContext() , AddUserActivity.class);
            startActivity(intent);
        });
    }

    public void InitView(DataCallback<List<Users>> callback) {
        ApiService.apiService.GetUserByHomeID("Bearer " + sessionManager.getToken(), dataUserLocal.getHomeId()).enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (requireContext() != null) {
                        callback.onSuccess(response.body());
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {

            }
        });
    }
}
