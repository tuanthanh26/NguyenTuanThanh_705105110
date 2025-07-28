package com.example.nguyentuanthanh_705105110.ui.login;


public class ResetPasswordResult {
    private boolean success;
    private String errorMessage;

    ResetPasswordResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
} 