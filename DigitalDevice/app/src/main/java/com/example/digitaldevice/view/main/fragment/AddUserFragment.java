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
import com.example.digitaldevice.data.model.Users;
import com.example.digitaldevice.utils.DataUserLocal;
import com.example.digitaldevice.utils.SessionManager;
import com.example.digitaldevice.view.main.adapter.MemberAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserFragment extends Fragment {
    private SessionManager sessionManager;
    private DataUserLocal dataUserLocal;
    private MemberAdapter memberAdapter;
    private RecyclerView rcvMember;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_fragment , container , false);
        rcvMember = view.findViewById(R.id.rcvMember);
        rcvMember.setLayoutManager(new LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL , false));
        if(requireContext() != null){
            sessionManager = new SessionManager(requireContext());
            dataUserLocal = new DataUserLocal(requireContext());
        }
        ApiService.apiService.GetUserByHomeID("Bearer " +sessionManager.getToken() , dataUserLocal.getHomeId()).enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(requireContext() != null){
                        memberAdapter = new MemberAdapter(requireContext() , response.body());
                        rcvMember.setAdapter(memberAdapter);
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {

            }
        });

        return view;
    }
}
