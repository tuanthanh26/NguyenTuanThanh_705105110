package com.example.nguyentuanthanh_705105110;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nguyentuanthanh_705105110.data.LoginRepository;
import com.example.nguyentuanthanh_705105110.databinding.ActivityMainBinding;
import com.example.nguyentuanthanh_705105110.ProfileImageHelper;
import android.graphics.Bitmap;
import com.example.nguyentuanthanh_705105110.ui.addbook.AddBookFragment;
import com.example.nguyentuanthanh_705105110.ui.books.BooksFragment;
import com.example.nguyentuanthanh_705105110.ui.home.HomeFragment;
import com.example.nguyentuanthanh_705105110.ui.login.LoginActivity;
import com.example.nguyentuanthanh_705105110.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import android.net.Uri;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LoginRepository loginRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginRepository = LoginRepository.getInstance();

        // Check if user is logged in
        if (!loginRepository.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setupBottomNavigation();
        setupToolbar();
        loadProfileImage();
        
        // Handle Dynamic Links
        handleDynamicLinks();
        
        // Load default fragment (Home)
        loadFragment(new HomeFragment());
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            
            if (item.getItemId() == R.id.navigation_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.navigation_books) {
                fragment = new BooksFragment();
            } else if (item.getItemId() == R.id.navigation_add) {
                fragment = new AddBookFragment();
            } else if (item.getItemId() == R.id.navigation_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide default title
        }
        
        // Setup profile image click
        binding.ivToolbarProfile.setOnClickListener(v -> {
            // Navigate to profile tab
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_profile);
        });
    }

    private void loadProfileImage() {
        FirebaseUser currentUser = loginRepository.getCurrentUser();
        if (currentUser != null) {
            Bitmap profileImage = ProfileImageHelper.getProfileImage(this, currentUser.getUid());
            if (profileImage != null) {
                binding.ivToolbarProfile.setImageBitmap(profileImage);
                binding.ivToolbarProfile.setPadding(0, 0, 0, 0);
                binding.ivToolbarProfile.setBackgroundResource(R.drawable.toolbar_profile_background);
            } else {
                binding.ivToolbarProfile.setImageResource(R.drawable.ic_person);
                binding.ivToolbarProfile.setPadding(10, 10, 10, 10);
                binding.ivToolbarProfile.setBackgroundResource(R.drawable.toolbar_profile_background);
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        loginRepository.logout();
        Toast.makeText(this, R.string.logout_successful, Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null && deepLink.toString().contains("reset-password")) {
                            // Navigate to ResetPasswordActivity
                            Intent intent = new Intent(this, ResetPasswordActivity.class);
                            intent.setData(deepLink);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.w("MainActivity", "getDynamicLink:onFailure", e);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile image
        loadProfileImage();
        
        // Refresh current fragment if needed
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof BooksFragment) {
            // Refresh books list
            ((BooksFragment) currentFragment).onResume();
        } else if (currentFragment instanceof HomeFragment) {
            // Refresh home statistics
            ((HomeFragment) currentFragment).onResume();
        }
    }
}