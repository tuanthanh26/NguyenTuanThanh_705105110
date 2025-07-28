package com.example.nguyentuanthanh_705105110.ui.register;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

class RegisterFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer confirmPasswordError;
    private boolean isDataValid;

    RegisterFormState(@Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer confirmPasswordError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.confirmPasswordError = confirmPasswordError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.emailError = null;
        this.passwordError = null;
        this.confirmPasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() { return emailError; }

    @Nullable
    Integer getPasswordError() { return passwordError; }

    @Nullable
    Integer getConfirmPasswordError() { return confirmPasswordError; }

    boolean isDataValid() { return isDataValid; }
} 