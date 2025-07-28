package com.example.nguyentuanthanh_705105110.ui.register;

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
import com.example.nguyentuanthanh_705105110.databinding.ActivityRegisterBinding;
import com.example.nguyentuanthanh_705105110.ui.login.LoggedInUserView;
import com.example.nguyentuanthanh_705105110.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private ActivityRegisterBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final EditText confirmPasswordEditText = binding.confirmPassword;
        final Button registerButton = binding.register;
        final ProgressBar loadingProgressBar = binding.loading;
        final TextView loginLink = binding.loginLink;

        registerViewModel.getRegisterFormState().observe(this, registerFormState -> {
            if (registerFormState == null) {
                return;
            }
            registerButton.setEnabled(registerFormState.isDataValid());

            if (registerFormState.getEmailError() != null) {
                emailEditText.setError(getString(registerFormState.getEmailError()));
            }
            if (registerFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(registerFormState.getPasswordError()));
            }
            if (registerFormState.getConfirmPasswordError() != null) {
                confirmPasswordEditText.setError(getString(registerFormState.getConfirmPasswordError()));
            }
        });

        registerViewModel.getRegisterResult().observe(this, registerResult -> {
            if (registerResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            
            if (registerResult.getError() != null) {
                showRegisterFailed(registerResult.getError());
            } else if (registerResult.getErrorMessage() != null) {
                showRegisterFailed(registerResult.getErrorMessage());
            }
            
            if (registerResult.getSuccess() != null) {
                if (registerResult.getSuccessMessage() != null) {
                    Toast.makeText(getApplicationContext(), registerResult.getSuccessMessage(), Toast.LENGTH_SHORT).show();
                }
                updateUiWithUser(registerResult.getSuccess());
                // Chuyển đến MainActivity sau khi đăng ký thành công
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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
                registerViewModel.registerDataChanged(
                    emailEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    confirmPasswordEditText.getText().toString()
                );
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);
        
        confirmPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                registerViewModel.register(
                    emailEditText.getText().toString(),
                    passwordEditText.getText().toString()
                );
            }
            return false;
        });

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            registerViewModel.register(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString()
            );
        });

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + " " + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showRegisterFailed(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
} 