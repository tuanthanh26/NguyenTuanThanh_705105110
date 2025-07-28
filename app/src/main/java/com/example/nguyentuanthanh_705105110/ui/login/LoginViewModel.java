package com.example.nguyentuanthanh_705105110.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;
import com.example.nguyentuanthanh_705105110.data.LoginRepository;
import com.example.nguyentuanthanh_705105110.data.Result;
import com.example.nguyentuanthanh_705105110.data.model.LoggedInUser;
import com.example.nguyentuanthanh_705105110.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<ResetPasswordResult> resetPasswordResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    LiveData<ResetPasswordResult> getResetPasswordResult() {
        return resetPasswordResult;
    }

    public void login(String username, String password) {
        // Reset result
        loginResult.setValue(null);
        
        loginRepository.login(username, password, new LoginRepository.ResultCallback<LoggedInUser>() {
            @Override
            public void onResult(Result<LoggedInUser> result) {
                if (result instanceof Result.Success) {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    loginResult.postValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
                } else {
                    String errorMessage = ((Result.Error) result).getError().getMessage();
                    loginResult.postValue(new LoginResult(errorMessage));
                }
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) return false;
        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 6;
    }

    public void sendPasswordResetEmail(String email) {
        // Reset result
        resetPasswordResult.setValue(null);
        
        loginRepository.sendPasswordResetEmail(email, new LoginRepository.ResultCallback<Void>() {
            @Override
            public void onResult(Result<Void> result) {
                if (result instanceof Result.Success) {
                    resetPasswordResult.postValue(new ResetPasswordResult(true, null));
                } else {
                    String errorMessage = ((Result.Error) result).getError().getMessage();
                    resetPasswordResult.postValue(new ResetPasswordResult(false, errorMessage));
                }
            }
        });
    }
}