package com.example.digitaldevice.view.main.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.model.Users;

import java.util.ArrayList;
import java.util.List;


public class AdduserFragment extends Fragment {
    private RecyclerView rcvMember;
    private com.example.digitaldevice.view.adapter.MemberAdapter memberAdapter;
    private List<Users> memberList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adduser_fragment, container, false);
        rcvMember = view.findViewById(R.id.rcvMember);

        // Debug 1: Kiểm tra RecyclerView
        if (rcvMember == null) {
            Log.e("AdduserFragment", "RecyclerView is null - Check your layout file");
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy RecyclerView", Toast.LENGTH_LONG).show();
            return view;
        }

        // Debug 2: Đặt màu nền để kiểm tra
        //rcvMember.setBackgroundColor(Color.YELLOW);

        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        // 1. Thiết lập LayoutManager với context từ requireActivity()
        rcvMember.setLayoutManager(new LinearLayoutManager(requireActivity()));

        // 2. Khởi tạo Adapter với dữ liệu thực
        memberAdapter = new com.example.digitaldevice.view.adapter.MemberAdapter(requireActivity(), memberList);
        rcvMember.setAdapter(memberAdapter);

        // 3. Thêm ItemDecoration
        rcvMember.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        Log.d("AdduserFragment", "RecyclerView setup completed");
    }

    public void updateMembers(List<Users> newMembers) {
        if (newMembers == null || rcvMember == null) {
            Log.w("AdduserFragment", "Invalid update data");
            return;
        }

        requireActivity().runOnUiThread(() -> {
            try {
                // 1. Cập nhật dữ liệu
                memberList.clear();
                memberList.addAll(newMembers);

                // 2. Debug: Kiểm tra dữ liệu thực tế
                Log.d("AdduserFragment", "Updating with " + newMembers.size() + " members");
                for (Users user : newMembers) {
                    Log.d("AdduserFragment", "Member: " + user.getName());
                }

                // 3. Đảm bảo Adapter tồn tại
                if (memberAdapter == null) {
                    memberAdapter = new com.example.digitaldevice.view.adapter.MemberAdapter(requireActivity(), memberList);
                    rcvMember.setAdapter(memberAdapter);
                    Log.d("AdduserFragment", "New adapter created");
                } else {
                    memberAdapter.notifyDataSetChanged();
                    Log.d("AdduserFragment", "Adapter notified");
                }

                // 4. Kiểm tra lại RecyclerView
                rcvMember.post(() -> {
                    Log.d("AdduserFragment", "RecyclerView dimensions: " +
                            rcvMember.getWidth() + "x" + rcvMember.getHeight());
                });
            } catch (Exception e) {
                Log.e("AdduserFragment", "Update error", e);
            }
        });
    }
}