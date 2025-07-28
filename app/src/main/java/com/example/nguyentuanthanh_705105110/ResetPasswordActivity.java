package com.example.nguyentuanthanh_705105110;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nguyentuanthanh_705105110.databinding.ActivityResetPasswordBinding;
import com.example.nguyentuanthanh_705105110.ui.login.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    private String actionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        
        setupViews();
        handleIntent();
    }

    private void setupViews() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đặt lại mật khẩu");
        }

        // Setup password input
        binding.passwordInputLayout.setStartIconDrawable(R.drawable.ic_lock);
        binding.passwordInputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        // Setup confirm password input
        binding.confirmPasswordInputLayout.setStartIconDrawable(R.drawable.ic_lock);
        binding.confirmPasswordInputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        // Setup reset button
        binding.btnResetPassword.setOnClickListener(v -> resetPassword());

        // Setup back to login button
        binding.btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            // Handle deep link
            String data = intent.getDataString();
            if (data != null && data.contains("mode=resetPassword")) {
                // Extract action code from URL
                actionCode = extractActionCode(data);
                if (actionCode != null) {
                    binding.tvStatus.setText("Vui lòng nhập mật khẩu mới");
                    binding.passwordInputLayout.setVisibility(View.VISIBLE);
                    binding.confirmPasswordInputLayout.setVisibility(View.VISIBLE);
                    binding.btnResetPassword.setVisibility(View.VISIBLE);
                } else {
                    showError("Link không hợp lệ hoặc đã hết hạn");
                }
            } else {
                showError("Link không hợp lệ");
            }
        }
    }

    private String extractActionCode(String url) {
        try {
            // Firebase reset password URL format: 
            // https://your-app.firebaseapp.com/__/auth/action?mode=resetPassword&oobCode=ACTION_CODE&apiKey=API_KEY&continueUrl=CONTINUE_URL&lang=vi
            if (url.contains("oobCode=")) {
                int startIndex = url.indexOf("oobCode=") + 8;
                int endIndex = url.indexOf("&", startIndex);
                if (endIndex == -1) {
                    endIndex = url.length();
                }
                return url.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void resetPassword() {
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(newPassword)) {
            binding.etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            binding.etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            binding.etNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            binding.etNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            binding.etConfirmPassword.requestFocus();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnResetPassword.setEnabled(false);

        // Confirm password reset
        firebaseAuth.confirmPasswordReset(actionCode, newPassword)
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnResetPassword.setEnabled(true);

                    if (task.isSuccessful()) {
                        showSuccess("Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
                        binding.btnResetPassword.setVisibility(View.GONE);
                        binding.btnBackToLogin.setVisibility(View.VISIBLE);
                    } else {
                        String errorMessage = getErrorMessage(task.getException());
                        showError("Lỗi: " + errorMessage);
                    }
                });
    }

    private String getErrorMessage(Exception exception) {
        if (exception != null) {
            String message = exception.getMessage();
            if (message != null) {
                if (message.contains("expired")) {
                    return "Link đã hết hạn. Vui lòng yêu cầu link mới.";
                } else if (message.contains("invalid")) {
                    return "Link không hợp lệ.";
                } else if (message.contains("weak-password")) {
                    return "Mật khẩu quá yếu.";
                }
            }
        }
        return "Đã xảy ra lỗi không xác định";
    }

    private void showSuccess(String message) {
        binding.tvStatus.setText(message);
        binding.tvStatus.setTextColor(getResources().getColor(R.color.colorSuccess, null));
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showError(String message) {
        binding.tvStatus.setText(message);
        binding.tvStatus.setTextColor(getResources().getColor(R.color.colorError, null));
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 