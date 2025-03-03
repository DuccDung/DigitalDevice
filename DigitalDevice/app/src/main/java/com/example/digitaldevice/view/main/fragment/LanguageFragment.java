package com.example.digitaldevice.view.main.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.digitaldevice.R;


public class LanguageFragment extends Fragment {

    private ImageView imgBacklg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_fragment, container, false);

        TextView textView = view.findViewById(R.id.textView18);
        Drawable drawable = getResources().getDrawable(R.drawable.st_right);
        drawable.setBounds(0, 0, 20, 20); // Thay đổi kích thước width, height
        textView.setCompoundDrawables(null, null, drawable, null); // Set drawableRight

        // Ánh xạ ImageView đúng vị trí
        imgBacklg = view.findViewById(R.id.imgBacklg);

        // Bắt sự kiện click để quay lại fragment trước
        imgBacklg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

}