package com.example.smarthome.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smarthome.ui.main.AssistantFragment;
import com.example.smarthome.ui.main.HomeFragment;
import com.example.smarthome.ui.main.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new AssistantFragment();
            case 2: return new SettingFragment();
        }
        return new HomeFragment(); // Trả về mặc định nếu không khớp với case nào
    }

    @Override
    public int getItemCount() {
        return 3; // có số lượng 3 fragment
    }
}
