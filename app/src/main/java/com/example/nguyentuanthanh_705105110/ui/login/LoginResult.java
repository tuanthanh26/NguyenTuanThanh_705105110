package com.example.nguyentuanthanh_705105110.ui.login;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

class LoginResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;
    @Nullable
    private String errorMessage;
    @Nullable
    private Integer successMessage;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    LoginResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    LoginResult(@Nullable LoggedInUserView success, @Nullable @StringRes Integer successMessage) {
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