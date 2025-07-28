package com.example.nguyentuanthanh_705105110.ui.register;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.example.nguyentuanthanh_705105110.ui.login.LoggedInUserView;

class RegisterResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;
    @Nullable
    private String errorMessage;
    @Nullable
    private Integer successMessage;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    RegisterResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    RegisterResult(@Nullable LoggedInUserView success, @Nullable @StringRes Integer successMessage) {
        this.success = success;
        this.successMessage = successMessage;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    @Nullable
    String getErrorMessage() {
        return errorMessage;
    }

    @Nullable
    Integer getSuccessMessage() { 
        return successMessage; 
    }
} 