package com.example.nguyentuanthanh_705105110;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nguyentuanthanh_705105110.data.LoginRepository;
import com.example.nguyentuanthanh_705105110.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000; // 1 giây
    private LoginRepository loginRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loginRepository = LoginRepository.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (loginRepository.isUserLoggedIn()) {
                // Người dùng đã đăng nhập, chuyển đến MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Người dùng chưa đăng nhập, chuyển đến LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
} 