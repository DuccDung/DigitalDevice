package com.example.digitaldevice.view.main.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaldevice.R;
import com.example.digitaldevice.data.api_service.ApiService;
import com.example.digitaldevice.data.model.UserResponse;
import com.example.digitaldevice.data.model.Users;
import com.example.digitaldevice.view.adapter.UserAdapter;
import com.example.digitaldevice.view.main.adapter.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUserFragment extends Fragment {
    private RecyclerView rcvUser;
    private UserAdapter userAdapter;
    private List<Users> userList = new ArrayList<>();
    private TextView tvAddMember;
    private SearchView searchView;
    private OnUsersSelectedListener usersSelectedListener;
    private TextView tvNoResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);


        // 1. Khởi tạo danh sách trước
        userList = new ArrayList<>();

        // 2. Khởi tạo Adapter với danh sách trống
        userAdapter = new UserAdapter(getContext(), userList);


        // 3. Setup RecyclerView
        rcvUser = view.findViewById(R.id.rcvUser);
        tvAddMember = view.findViewById(R.id.textView4);
        rcvUser.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvUser.setAdapter(userAdapter); // Gắn adapter ngay lúc này

        // 4. Các bước khác
        searchView = view.findViewById(R.id.searchUser);
        tvNoResults = view.findViewById(R.id.tvNoResults);
        fetchUsers();
        setupSearchView();

        /// Trong onCreateView
        rcvUser.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvUser.setAdapter(userAdapter);

        // Xóa tất cả ItemDecoration hiện có trước khi thêm mới
                while (rcvUser.getItemDecorationCount() > 0) {
                    rcvUser.removeItemDecorationAt(0);
                }

        // Thêm ItemDecoration với khoảng cách nhỏ
        rcvUser.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 4; // 4dp giữa các item
                outRect.bottom = 4;
            }
        });

        setupClickListener();
        setupRecyclerView();
        return view;

    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(getContext(), userList);
        rcvUser.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvUser.setAdapter(userAdapter);
    }




    private void searchUsers(String keyword) {
        ApiService apiService = RetrofitClient.getApiService();

        Call<UserResponse> call = apiService.searchUsers(keyword);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Users> users = response.body().getData();
                    if (users.isEmpty()) {
                        Toast.makeText(getContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                        // Load lại danh sách ban đầu
                        fetchUsers();
                    } else {
                        userAdapter.updateList(users);
                    }
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    fetchUsers(); // Load lại danh sách ban đầu
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tìm kiếm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                fetchUsers(); // Load lại danh sách ban đầu khi có lỗi
            }
        });



    }



    private void filter(String text) {
        List<Users> filteredList = new ArrayList<>();
        for (Users user : userList) {
            if ((user.getName() != null && user.getName().toLowerCase().contains(text.toLowerCase())) ||
                    (user.getUserId() != null && user.getUserId().toLowerCase().contains(text.toLowerCase()))) {
                filteredList.add(user);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        }
        userAdapter.updateList(filteredList);
    }


    private void setupClickListener() {
        tvAddMember.setOnClickListener(v -> {
            List<Users> selectedUsers = userAdapter.getSelectedUsers();
            if (!selectedUsers.isEmpty()) {
                Fragment parent = getParentFragment();
                if (parent instanceof SettingFragment) {
                    // Log kiểm tra dữ liệu trước khi gửi
                    Log.d("SearchUserFragment", "Sending selected users: " + selectedUsers.size());

                    ((SettingFragment) parent).switchFragment(true, selectedUsers);
                }
            } else {
                Toast.makeText(getContext(), "Please select members first", Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void fetchUsers() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Users>> call = apiService.getUsers();

        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(@NonNull Call<List<Users>> call,
                                   @NonNull Response<List<Users>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESPONSE", "Received " + response.body().size() + " users");
                    userList.clear();
                    userList.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to get users", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Users>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    fetchUsers(); // Load lại toàn bộ danh sách nếu search rỗng
                } else {
                    searchUsers(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    fetchUsers(); // Load lại toàn bộ danh sách khi xóa hết text
                } else {
                    filter(newText);
                }
                return true;
            }
        });

        // Thêm listener khi đóng search view
        searchView.setOnCloseListener(() -> {
            fetchUsers(); // Load lại danh sách ban đầu khi đóng search
            return false;
        });
    }




    public interface OnUsersSelectedListener {
        void onUsersSelected(List<Users> selectedUsers);
    }

}