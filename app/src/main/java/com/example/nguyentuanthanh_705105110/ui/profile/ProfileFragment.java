package com.example.nguyentuanthanh_705105110.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nguyentuanthanh_705105110.ProfileImageHelper;
import com.example.nguyentuanthanh_705105110.R;
import com.example.nguyentuanthanh_705105110.data.LoginRepository;
import com.example.nguyentuanthanh_705105110.databinding.FragmentProfileBinding;
import com.example.nguyentuanthanh_705105110.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseUser;
import android.view.animation.AnimationUtils;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private LoginRepository loginRepository;
    private static final int PICK_PROFILE_IMAGE_REQUEST = 100;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loginRepository = LoginRepository.getInstance();
        initViews();
        displayUserInfo();
        setupEvents();

        return root;
    }

    private void initViews() {
        // Views sẽ được khởi tạo trong layout
    }

    private void displayUserInfo() {
        FirebaseUser currentUser = loginRepository.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            String userEmail = currentUser.getEmail();
            binding.tvUserEmail.setText(userEmail);
            binding.tvUserEmailDetail.setText(userEmail);
            
            // Display user name if available
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                binding.tvUserEmail.setText(displayName);
            }
            
            // Load profile image
            loadProfileImage(currentUser.getUid());
        } else {
            binding.tvUserEmail.setText("Không xác định");
            binding.tvUserEmailDetail.setText("Không xác định");
        }
    }

    private void setupEvents() {
        binding.btnLogout.setOnClickListener(v -> logout());
        
        // Setup profile image click events
        binding.ivProfileImage.setOnClickListener(v -> openImagePicker());
        binding.ivCameraIcon.setOnClickListener(v -> openImagePicker());
    }

    private void logout() {
        loginRepository.logout();
        Toast.makeText(requireContext(), R.string.logout_successful, Toast.LENGTH_SHORT).show();
        
        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void loadProfileImage(String userId) {
        Bitmap profileImage = ProfileImageHelper.getProfileImage(requireContext(), userId);
        if (profileImage != null) {
            binding.ivProfileImage.setImageBitmap(profileImage);
            binding.ivProfileImage.setPadding(0, 0, 0, 0); // Remove padding for actual image
            binding.ivProfileImage.setBackgroundResource(R.drawable.profile_image_background);
        } else {
            binding.ivProfileImage.setImageResource(R.drawable.ic_person);
            binding.ivProfileImage.setPadding(24, 24, 24, 24); // Add padding for default icon
            binding.ivProfileImage.setBackgroundResource(R.drawable.profile_image_background);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_profile_image)), PICK_PROFILE_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PROFILE_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            FirebaseUser currentUser = loginRepository.getCurrentUser();
            if (currentUser != null) {
                // Save profile image
                String savedImagePath = ProfileImageHelper.saveProfileImage(requireContext(), selectedImageUri, currentUser.getUid());
                if (savedImagePath != null) {
                    // Load and display the new profile image with animation
                    loadProfileImage(currentUser.getUid());
                    binding.ivProfileImage.startAnimation(
                        AnimationUtils.loadAnimation(requireContext(), R.anim.profile_image_fade)
                    );
                    Toast.makeText(requireContext(), getString(R.string.profile_image_updated), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), getString(R.string.profile_image_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 