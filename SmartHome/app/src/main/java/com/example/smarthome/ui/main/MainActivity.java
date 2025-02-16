package com.example.smarthome.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarthome.R;
import com.example.smarthome.adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mViewPager2;
    BottomNavigationView mNavigationView;

    private boolean doubleBackToExitPressedOnce = false;
    private final Handler handler = new Handler();
    private final Runnable resetBackPress = () -> doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();


    }

    private void initUI() {
        // ánh xạ id
        mViewPager2 = findViewById(R.id.view_pager);
        mNavigationView = findViewById(R.id.bottom_nav);

        // khi vuốt màn hình sang ngang thay đổi màn hình tương ứng
        setUpViewPager();

        // menu bottom nav = khi click vào từng item thay đổi fragment tương ứng
        setupBottomNav();

        // Kích hoạt trình nghe sự kiện Back2lan
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    // Setup view Pager
    private void setUpViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        mViewPager2.setAdapter(adapter);

        // khi vuốt màn hình sang ngang thì di chuyển fragment
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position) {
                    case 0:
                        mNavigationView.setSelectedItemId(R.id.action_home);
                        break;
                    case 1:
                        mNavigationView.setSelectedItemId(R.id.action_assistant);
                        break;
                    case 2:
                        mNavigationView.setSelectedItemId(R.id.action_setting);
                        break;
                }
            }
        });
    }

    // click vào item nav bottom thay đổi fragment tương ứng
    private void setupBottomNav() {
        mNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                mViewPager2.setCurrentItem(0);
                return true;
            }
            if (item.getItemId() == R.id.action_assistant) {
                mViewPager2.setCurrentItem(1);
                return true;
            }
            if (item.getItemId() == R.id.action_setting) {
                mViewPager2.setCurrentItem(2);
                return true;
            }
            return false;
        });
    }

    // Callback xử lý sự kiện Back2 lần
    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                finishAffinity(); // Thoát hoàn toàn ứng dụng
            } else {
                doubleBackToExitPressedOnce = true;
                Toast.makeText(MainActivity.this, "Nhấn BACK lần nữa để thoát", Toast.LENGTH_SHORT).show();
                handler.postDelayed(resetBackPress, 2000);
            }
        }
    };
}