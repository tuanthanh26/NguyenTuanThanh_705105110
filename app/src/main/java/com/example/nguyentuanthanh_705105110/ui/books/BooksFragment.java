package com.example.nguyentuanthanh_705105110.ui.books;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nguyentuanthanh_705105110.EditBookActivity;
import com.example.nguyentuanthanh_705105110.Book;
import com.example.nguyentuanthanh_705105110.BookAdapter;
import com.example.nguyentuanthanh_705105110.DatabaseHelper;
import com.example.nguyentuanthanh_705105110.R;
import com.example.nguyentuanthanh_705105110.databinding.FragmentBooksBinding;

import java.util.List;

public class BooksFragment extends Fragment {

    private FragmentBooksBinding binding;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private EditText searchEditText;
    private Button searchButton, filter2025Button;
    private boolean isFiltering2025 = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(requireContext());
        initViews();
        setupRecyclerView();
        setupEvents();
        loadBooks();

        return root;
    }

    private void initViews() {
        recyclerView = binding.recyclerView;
        searchEditText = binding.searchEditText;
        searchButton = binding.searchButton;
        filter2025Button = binding.filter2025Button;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookAdapter = new BookAdapter(requireContext(), dbHelper);
        recyclerView.setAdapter(bookAdapter);
    }

    private void setupEvents() {
        searchButton.setOnClickListener(v -> searchBooks());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            searchBooks();
            return true;
        });
        
        filter2025Button.setOnClickListener(v -> toggleFilter2025());
    }

    private void searchBooks() {
        String query = searchEditText.getText().toString().trim();
        if (query.isEmpty()) {
            loadBooks();
        } else {
            List<Book> books = dbHelper.searchBooks(query);
            bookAdapter.updateBooks(books);
        }
    }

    private void loadBooks() {
        List<Book> books = dbHelper.getAllBooks();
        bookAdapter.updateBooks(books);
    }

    private void toggleFilter2025() {
        if (!isFiltering2025) {
            List<Book> allBooks = dbHelper.getAllBooks();
            List<Book> filtered = new java.util.ArrayList<>();
            for (Book b : allBooks) {
                String date = b.getEntryDate();
                if (date != null && date.matches(".*2025.*")) {
                    filtered.add(b);
                }
            }
            bookAdapter.updateBooks(filtered);
            isFiltering2025 = true;
            filter2025Button.setText("Hiện tất cả");
            Toast.makeText(requireContext(), "Đã lọc sách năm 2025", Toast.LENGTH_SHORT).show();
        } else {
            loadBooks();
            isFiltering2025 = false;
            filter2025Button.setText("Lọc sách 2025");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBooks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 