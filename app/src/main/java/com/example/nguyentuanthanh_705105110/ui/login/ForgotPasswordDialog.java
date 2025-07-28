package com.example.nguyentuanthanh_705105110.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.nguyentuanthanh_705105110.R;

public class ForgotPasswordDialog extends DialogFragment {

    private EditText emailEditText;
    private Button sendButton;
    private Button cancelButton;
    private ProgressBar progressBar;
    private TextView titleText;
    private TextView messageText;
    
    private OnPasswordResetListener listener;

    public interface OnPasswordResetListener {
        void onSendPasswordResetEmail(String email);
    }

    public static ForgotPasswordDialog newInstance() {
        return new ForgotPasswordDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPasswordResetListener) {
            listener = (OnPasswordResetListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnPasswordResetListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_forgot_password, null);

        // Initialize views
        emailEditText = view.findViewById(R.id.et_email);
        sendButton = view.findViewById(R.id.btn_send);
        cancelButton = view.findViewById(R.id.btn_cancel);
        progressBar = view.findViewById(R.id.progress_bar);
        titleText = view.findViewById(R.id.tv_title);
        messageText = view.findViewById(R.id.tv_message);

        // Setup text watcher for email validation
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });

        // Setup button clicks
        sendButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (isEmailValid(email)) {
                sendPasswordResetEmail(email);
            } else {
                emailEditText.setError(getString(R.string.error_invalid_email));
            }
        });

        cancelButton.setOnClickListener(v -> {
            if (cancelButton.getText().toString().equals(getString(R.string.ok))) {

                dismiss();
            } else {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void validateEmail() {
        String email = emailEditText.getText().toString().trim();
        sendButton.setEnabled(isEmailValid(email));
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendPasswordResetEmail(String email) {
        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);
        emailEditText.setEnabled(false);

        // Call listener
        if (listener != null) {
            listener.onSendPasswordResetEmail(email);
        }
    }

    public void onResetEmailSent(boolean success, String message) {
        progressBar.setVisibility(View.GONE);
        
        if (success) {
            titleText.setText(getString(R.string.reset_password_sent));
            messageText.setText(message);
            messageText.setTextColor(requireContext().getColor(R.color.colorSuccess));

            emailEditText.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
            cancelButton.setText(getString(R.string.ok));

            requireView().postDelayed(() -> {
                if (isVisible()) {
                    dismiss();
                }
            }, 2000);
        } else {
            emailEditText.setError(message);
            sendButton.setEnabled(true);
            emailEditText.setEnabled(true);
        }
    }
} 