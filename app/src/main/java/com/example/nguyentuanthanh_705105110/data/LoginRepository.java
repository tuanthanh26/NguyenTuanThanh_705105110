package com.example.nguyentuanthanh_705105110.data;

import android.util.Log;
import com.example.nguyentuanthanh_705105110.data.model.LoggedInUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ActionCodeSettings;

public class LoginRepository {

    private static volatile LoginRepository instance;
    private final FirebaseAuth firebaseAuth;
    private static final String TAG = "LoginRepository";

    public interface ResultCallback<T> {
        void onResult(Result<T> result);
    }

    private LoginRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public void login(String email, String password, final ResultCallback<LoggedInUser> callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            LoggedInUser loggedInUser = new LoggedInUser(user.getUid(), user.getEmail());
                            callback.onResult(new Result.Success<>(loggedInUser));
                        } else {
                            callback.onResult(new Result.Error(new Exception("User is null")));
                        }
                    } else {
                        Exception exception = task.getException();
                        String errorMessage = getFirebaseErrorMessage(exception);
                        Log.e(TAG, "Login failed: " + errorMessage, exception);
                        callback.onResult(new Result.Error(new Exception(errorMessage)));
                    }
                });
    }

    public void register(String email, String password, final ResultCallback<LoggedInUser> callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            LoggedInUser loggedInUser = new LoggedInUser(user.getUid(), user.getEmail());
                            callback.onResult(new Result.Success<>(loggedInUser));
                        } else {
                            callback.onResult(new Result.Error(new Exception("User is null")));
                        }
                    } else {
                        Exception exception = task.getException();
                        String errorMessage = getFirebaseErrorMessage(exception);
                        Log.e(TAG, "Registration failed: " + errorMessage, exception);
                        callback.onResult(new Result.Error(new Exception(errorMessage)));
                    }
                });
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void sendPasswordResetEmail(String email, final ResultCallback<Void> callback) {
        // Tạo ActionCodeSettings để deep link vào app
        // Thay thế "your-app" bằng tên domain thực tế của bạn
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://nguyentuanthanh.page.link/reset-password")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.nguyentuanthanh_705105110", true, null)
                .build();
        
        firebaseAuth.sendPasswordResetEmail(email, actionCodeSettings)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Password reset email sent successfully");
                        callback.onResult(new Result.Success<>(null));
                    } else {
                        Exception exception = task.getException();
                        String errorMessage = getFirebaseErrorMessage(exception);
                        Log.e(TAG, "Password reset failed: " + errorMessage, exception);
                        callback.onResult(new Result.Error(new Exception(errorMessage)));
                    }
                });
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    private String getFirebaseErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            return "Tài khoản không tồn tại";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Mật khẩu không đúng";
        } else if (exception != null) {
            String message = exception.getMessage();
            if (message != null) {
                if (message.contains("email address is already in use")) {
                    return "Email đã được sử dụng";
                } else if (message.contains("network")) {
                    return "Lỗi kết nối mạng";
                } else if (message.contains("invalid email")) {
                    return "Email không hợp lệ";
                } else if (message.contains("user not found")) {
                    return "Email không tồn tại trong hệ thống";
                } else if (message.contains("too many requests")) {
                    return "Quá nhiều yêu cầu. Vui lòng thử lại sau";
                }
            }
        }
        return "Đã xảy ra lỗi không xác định";
    }
}