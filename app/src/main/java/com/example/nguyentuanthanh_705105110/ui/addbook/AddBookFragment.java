package com.example.nguyentuanthanh_705105110.ui.addbook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nguyentuanthanh_705105110.Book;
import com.example.nguyentuanthanh_705105110.DatabaseHelper;
import com.example.nguyentuanthanh_705105110.QRCodeParser;
import com.example.nguyentuanthanh_705105110.QRScannerActivity;
import com.example.nguyentuanthanh_705105110.R;
import com.example.nguyentuanthanh_705105110.databinding.FragmentAddBookBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddBookFragment extends Fragment {

    private FragmentAddBookBinding binding;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int QR_SCAN_REQUEST = 2;
    private Uri selectedImageUri = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddBookBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(requireContext());
        calendar = Calendar.getInstance();

        setupViews();
        setupEvents();
        setCurrentDate();

        return root;
    }

    private void setupViews() {
        // Views sẽ được khởi tạo trong layout
    }

    private void setupEvents() {
        // Save button
        binding.btnSave.setOnClickListener(v -> saveBook());

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> {
            // Navigate back to Home tab
            if (getActivity() != null) {
                getActivity().findViewById(R.id.navigation_home).performClick();
            }
        });

        // Date picker
        binding.etDate.setOnClickListener(v -> showDatePicker());

        // Image picker
        binding.btnPickImage.setOnClickListener(v -> openImagePicker());
        binding.ivBookImage.setOnClickListener(v -> openImagePicker());
        
        // QR Scan button
        binding.btnScanQr.setOnClickListener(v -> openQRScanner());
    }

    private void setCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        binding.etDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    binding.etDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_book_image)), PICK_IMAGE_REQUEST);
    }

    private void openQRScanner() {
        Intent intent = new Intent(requireContext(), QRScannerActivity.class);
        startActivityForResult(intent, QR_SCAN_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedUri = data.getData();
            try {
                String savedImagePath = copyImageToInternalStorage(selectedUri);
                if (savedImagePath != null) {
                    selectedImageUri = Uri.parse(savedImagePath);
                    binding.ivBookImage.setImageURI(selectedImageUri);
                    binding.etImageUrl.setText(savedImagePath);
                } else {
                    Toast.makeText(requireContext(), getString(R.string.image_save_error), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), getString(R.string.image_process_error) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == QR_SCAN_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            String qrData = data.getStringExtra("qr_data");
            if (qrData != null && !qrData.isEmpty()) {
                // Parse QR data and populate form
                QRCodeParser.BookInfo bookInfo = QRCodeParser.parseQRData(qrData);
                if (QRCodeParser.isValidBookInfo(bookInfo)) {
                    // Populate form fields with scanned data
                    binding.etCode.setText(bookInfo.code);
                    binding.etTitle.setText(bookInfo.title);
                    binding.etAuthor.setText(bookInfo.author);
                    binding.etCategory.setText(bookInfo.category);
                    if (!bookInfo.price.isEmpty()) {
                        binding.etPrice.setText(bookInfo.price);
                    }
                    if (!bookInfo.imageUrl.isEmpty()) {
                        binding.etImageUrl.setText(bookInfo.imageUrl);
                    }
                    
                    Toast.makeText(requireContext(), getString(R.string.qr_scan_success), Toast.LENGTH_SHORT).show();
                } else {
                    // If only code is available, just populate the code field
                    if (!bookInfo.code.isEmpty()) {
                        binding.etCode.setText(bookInfo.code);
                        Toast.makeText(requireContext(), "Đã quét mã sách: " + bookInfo.code, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.invalid_qr_data), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.qr_scan_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String copyImageToInternalStorage(Uri sourceUri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "book_image_" + timeStamp + ".jpg";

            File internalDir = new File(requireContext().getFilesDir(), "book_images");
            if (!internalDir.exists()) {
                internalDir.mkdirs();
            }

            File destFile = new File(internalDir, fileName);

            InputStream inputStream = requireContext().getContentResolver().openInputStream(sourceUri);
            OutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return destFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveBook() {
        if (!validateInput()) {
            return;
        }

        String code = binding.etCode.getText().toString().trim();
        String title = binding.etTitle.getText().toString().trim();
        String category = binding.etCategory.getText().toString().trim();
        String author = binding.etAuthor.getText().toString().trim();
        String date = binding.etDate.getText().toString().trim();
        double price = Double.parseDouble(binding.etPrice.getText().toString().trim());
        String imageUrl = binding.etImageUrl.getText().toString().trim();

        if (selectedImageUri != null) {
            imageUrl = selectedImageUri.toString();
        }

        Book newBook = new Book(code, title, category, author, date, price, imageUrl);
        long result = dbHelper.addBook(newBook);
        
        if (result > 0) {
            Toast.makeText(requireContext(), getString(R.string.add_book_success), Toast.LENGTH_SHORT).show();
            // Navigate back to Home tab
            if (getActivity() != null) {
                getActivity().findViewById(R.id.navigation_home).performClick();
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.add_book_error), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(binding.etCode.getText().toString().trim())) {
            binding.etCode.setError("Vui lòng nhập mã sách");
            binding.etCode.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(binding.etTitle.getText().toString().trim())) {
            binding.etTitle.setError("Vui lòng nhập tên sách");
            binding.etTitle.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(binding.etCategory.getText().toString().trim())) {
            binding.etCategory.setError("Vui lòng nhập thể loại");
            binding.etCategory.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(binding.etAuthor.getText().toString().trim())) {
            binding.etAuthor.setError("Vui lòng nhập tác giả");
            binding.etAuthor.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(binding.etDate.getText().toString().trim())) {
            binding.etDate.setError("Vui lòng chọn ngày nhập kho");
            binding.etDate.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(binding.etPrice.getText().toString().trim())) {
            binding.etPrice.setError("Vui lòng nhập giá");
            binding.etPrice.requestFocus();
            return false;
        }

        try {
            double price = Double.parseDouble(binding.etPrice.getText().toString().trim());
            if (price < 0) {
                binding.etPrice.setError("Giá phải lớn hơn 0");
                binding.etPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            binding.etPrice.setError("Giá không hợp lệ");
            binding.etPrice.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 