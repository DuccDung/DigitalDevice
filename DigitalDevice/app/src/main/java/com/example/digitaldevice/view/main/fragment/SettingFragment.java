package com.example.digitaldevice.view.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.digitaldevice.R;
import com.example.digitaldevice.view.select_home.SelectHomeActivity;

public class SettingFragment extends Fragment {
    LinearLayout ln1,ln2,ln3,ln4,ln5,ln6 ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment , container , false);

        ln1 = view.findViewById(R.id.ln1);
        ln2 = view.findViewById(R.id.ln2);
        ln3 = view.findViewById(R.id.ln3);
        ln4 = view.findViewById(R.id.ln4);
        ln5 = view.findViewById(R.id.ln5);
        ln6 = view.findViewById(R.id.ln6);
        ln1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserFragment userFragment = new UserFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,userFragment)
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

        ln5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageFragment languageFragment = new LanguageFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container3,languageFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
