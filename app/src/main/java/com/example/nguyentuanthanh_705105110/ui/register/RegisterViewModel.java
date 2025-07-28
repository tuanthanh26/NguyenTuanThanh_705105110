package com.example.nguyentuanthanh_705105110.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;
import com.example.nguyentuanthanh_705105110.data.LoginRepository;
import com.example.nguyentuanthanh_705105110.data.Result;
import com.example.nguyentuanthanh_705105110.data.model.LoggedInUser;
import com.example.nguyentuanthanh_705105110.R;
import com.example.nguyentuanthanh_705105110.ui.login.LoggedInUserView;

public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String email, String password) {
        // Reset result
        registerResult.setValue(null);
        
        loginRepository.register(email, password, new LoginRepository.ResultCallback<LoggedInUser>() {
            @Override
            public void onResult(Result<LoggedInUser> result) {
                if (result instanceof Result.Success) {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    registerResult.postValue(new RegisterResult(new LoggedInUserView(data.getDisplayName()), R.string.registration_successful));
                } else {
                    String errorMessage = ((Result.Error) result).getError().getMessage();
                    registerResult.postValue(new RegisterResult(errorMessage));
                }
            }
        });
    }

    public void registerDataChanged(String email, String password, String confirmPassword) {
        if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null));
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.password_mismatch));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        if (email == null) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 6;
    }

    private boolean isConfirmPasswordValid(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
} 