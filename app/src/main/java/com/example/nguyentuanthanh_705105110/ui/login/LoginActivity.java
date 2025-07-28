package com.example.nguyentuanthanh_705105110.ui.login;

import android.content.Intent;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nguyentuanthanh_705105110.MainActivity;
import com.example.nguyentuanthanh_705105110.R;
import com.example.nguyentuanthanh_705105110.databinding.ActivityLoginBinding;
import com.example.nguyentuanthanh_705105110.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity implements ForgotPasswordDialog.OnPasswordResetListener {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private ForgotPasswordDialog forgotPasswordDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final TextView registerLink = binding.registerLink;
        final TextView forgotPasswordLink = binding.forgotPasswordLink;

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());

            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            } else if (loginResult.getErrorMessage() != null) {
                showLoginFailed(loginResult.getErrorMessage());
            }
            
            if (loginResult.getSuccess() != null) {
                if (loginResult.getSuccessMessage() != null) {
                    Toast.makeText(getApplicationContext(), loginResult.getSuccessMessage(), Toast.LENGTH_SHORT).show();
                }
                updateUiWithUser(loginResult.getSuccess());
                // Chuyển đến MainActivity sau khi đăng nhập thành công
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // Setup forgot password link
        forgotPasswordLink.setOnClickListener(v -> showForgotPasswordDialog());

        // Observe reset password result
        loginViewModel.getResetPasswordResult().observe(this, resetPasswordResult -> {
            if (resetPasswordResult != null) {
                if (resetPasswordResult.isSuccess()) {
                    showResetPasswordSuccess();
                    // Tắt dialog khi gửi thành công
                    if (forgotPasswordDialog != null && forgotPasswordDialog.isVisible()) {
                        forgotPasswordDialog.onResetEmailSent(true, getString(R.string.reset_password_sent));
                    }
                } else {
                    showResetPasswordError(resetPasswordResult.getErrorMessage());
                    // Hiển thị lỗi trong dialog
                    if (forgotPasswordDialog != null && forgotPasswordDialog.isVisible()) {
                        forgotPasswordDialog.onResetEmailSent(false, resetPasswordResult.getErrorMessage());
                    }
                }
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + " " + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void showForgotPasswordDialog() {
        forgotPasswordDialog = ForgotPasswordDialog.newInstance();
        forgotPasswordDialog.show(getSupportFragmentManager(), "forgot_password_dialog");
    }

    @Override
    public void onSendPasswordResetEmail(String email) {
        loginViewModel.sendPasswordResetEmail(email);
    }

    private void showResetPasswordSuccess() {
        Toast.makeText(this, getString(R.string.reset_password_sent), Toast.LENGTH_LONG).show();
    }

    private void showResetPasswordError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}