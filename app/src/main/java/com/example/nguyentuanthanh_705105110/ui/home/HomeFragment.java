package com.example.nguyentuanthanh_705105110.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nguyentuanthanh_705105110.EditBookActivity;
import com.example.nguyentuanthanh_705105110.Book;
import com.example.nguyentuanthanh_705105110.DatabaseHelper;
import com.example.nguyentuanthanh_705105110.QRScannerActivity;
import com.example.nguyentuanthanh_705105110.R;
import com.example.nguyentuanthanh_705105110.databinding.FragmentHomeBinding;
import com.example.nguyentuanthanh_705105110.ui.books.BooksFragment;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseHelper dbHelper;
    private static final int QR_SCAN_REQUEST = 100;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(requireContext());
        
        setupViews();
        setupEvents();
        loadStatistics();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void setupViews() {
        // Views sẽ được khởi tạo trong layout
    }

    private void setupEvents() {
        // Add Book Card Click
        binding.cardAddBook.setOnClickListener(v -> {
            // Navigate to Add Book tab
            if (getActivity() != null) {
                getActivity().findViewById(R.id.navigation_add).performClick();
            }
        });

        // View Books Card Click
        binding.cardViewBooks.setOnClickListener(v -> {
            // Navigate to Books tab
            if (getActivity() != null) {
                getActivity().findViewById(R.id.navigation_books).performClick();
            }
        });
        
        // QR Scan Card Click
        binding.cardScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), QRScannerActivity.class);
            startActivityForResult(intent, QR_SCAN_REQUEST);
        });
    }

    private void loadStatistics() {
        List<com.example.nguyentuanthanh_705105110.Book> allBooks = dbHelper.getAllBooks();
        binding.tvTotalBooks.setText(String.valueOf(allBooks.size()));

        List<com.example.nguyentuanthanh_705105110.Book> recentBooks = dbHelper.getRecentBooks(7);
        binding.tvRecentBooks.setText(String.valueOf(recentBooks.size()));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_SCAN_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            String qrData = data.getStringExtra("qr_data");
            if (qrData != null && !qrData.isEmpty()) {
                // Navigate to Add Book tab with QR data
                if (getActivity() != null) {
                    getActivity().findViewById(R.id.navigation_add).performClick();
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