package com.example.smarthome.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthome.R;
import com.example.smarthome.ui.intro.IntroActivity;

public class SettingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Ánh xạ layout của Fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // lấy button trong xml
        Button btnGoToActivity = view.findViewById(R.id.btnGoToActivity);

        // bắt sự kiện click
        btnGoToActivity.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), IntroActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
