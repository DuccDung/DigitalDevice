package com.example.digitaldevice.view.main.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.digitaldevice.R;
import com.example.digitaldevice.utils.DbUserHelper;
import com.example.digitaldevice.view.login.LoginActivity;
import com.example.digitaldevice.view.select_home.SelectHomeActivity;

public class SettingFragment extends Fragment {
    LinearLayout ln1,ln2,ln3,ln4,ln5,ln6 ;
    TextView txtLogout;
    private TextView txtMember , txtRoomAndDevice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment , container , false);
        makeStatusBarTransparent();
        applyTopPadding(view);
        ln1 = view.findViewById(R.id.ln1);
        ln2 = view.findViewById(R.id.ln2);
        ln3 = view.findViewById(R.id.ln3);
        ln4 = view.findViewById(R.id.ln4);
        ln5 = view.findViewById(R.id.ln5);
        ln6 = view.findViewById(R.id.ln6);
        txtMember = view.findViewById(R.id.txtmember);
        txtLogout = view.findViewById(R.id.txtLogout);
        txtRoomAndDevice = view.findViewById(R.id.btnViewRooms);
        txtMember.setOnClickListener(v -> {
            AddUserFragment addUserFragment = new AddUserFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_setting,addUserFragment)
                    .addToBackStack(null)
                    .commit();
        });
        txtRoomAndDevice.setOnClickListener(v->{
            SelectRoomFragment selectRoomFragment = new SelectRoomFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_setting,selectRoomFragment)
                    .addToBackStack(null)
                    .commit();
        });
        ln1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserFragment userFragment = new UserFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_setting,userFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ln2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectHomeActivity.class);
                startActivity(intent);
            }
        });
        txtLogout.setOnClickListener(v -> {
            if(requireContext() != null){
                DbUserHelper dbUserHelper = new DbUserHelper(requireContext()); // Init database Sqlite
                dbUserHelper.deleteUser();
                Intent intent = new Intent(requireContext() , LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;
    }
    private void makeStatusBarTransparent() {
        if (getActivity() != null) {
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );

            requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    private void applyTopPadding(View view) {
        View contentContainer = view.findViewById(R.id.fragment_container_setting);

        if (contentContainer != null) {
            int statusBarHeight = getStatusBarHeight();
            contentContainer.setPadding(0, statusBarHeight, 0, 0);
        }
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
