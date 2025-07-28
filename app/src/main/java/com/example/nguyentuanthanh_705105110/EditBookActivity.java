package com.example.nguyentuanthanh_705105110;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditBookActivity extends AppCompatActivity {

    private EditText etCode, etTitle, etCategory, etAuthor, etDate, etPrice, etImageUrl;
    private Button btnSave, btnCancel;
    private DatabaseHelper dbHelper;
    private Book editingBook;
    private boolean isEditMode = false;
    private Calendar calendar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivBookImage;
    private Button btnPickImage;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();


        initViews();


        checkEditMode();


        setupEvents();
    }

    private void initViews() {
        etCode = findViewById(R.id.etCode);
        etTitle = findViewById(R.id.etTitle);
        etCategory = findViewById(R.id.etCategory);
        etAuthor = findViewById(R.id.etAuthor);
        etDate = findViewById(R.id.etDate);
        etPrice = findViewById(R.id.etPrice);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        ivBookImage = findViewById(R.id.ivBookImage);
        btnPickImage = findViewById(R.id.btnPickImage);
    }

    private void checkEditMode() {
        int bookId = getIntent().getIntExtra("book_id", -1);
        if (bookId != -1) {
            isEditMode = true;
            getSupportActionBar().setTitle("Sửa sách");
            editingBook = dbHelper.getBookById(bookId);
            if (editingBook != null) {
                fillBookData();
            } else {
                // Nếu không tìm thấy sách, đóng activity
                Toast.makeText(this, getString(R.string.book_not_found), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Nếu không có book_id, đóng activity vì chỉ dùng cho edit
            Toast.makeText(this, getString(R.string.edit_book_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fillBookData() {
        etCode.setText(editingBook.getCode());
        etTitle.setText(editingBook.getTitle());
        etCategory.setText(editingBook.getCategory());
        etAuthor.setText(editingBook.getAuthor());
        etDate.setText(editingBook.getEntryDate());
        etPrice.setText(String.valueOf(editingBook.getPrice()));
        etImageUrl.setText(editingBook.getImageUrl());

        if (editingBook.getImageUrl() != null && !editingBook.getImageUrl().isEmpty()) {
            try {
                Uri imageUri;
                if (editingBook.getImageUrl().startsWith("/")) {

                    File imageFile = new File(editingBook.getImageUrl());
                    if (imageFile.exists()) {
                        imageUri = Uri.fromFile(imageFile);
                        ivBookImage.setImageURI(imageUri);
                        selectedImageUri = imageUri;
                    } else {
                        ivBookImage.setImageResource(R.drawable.ic_book_placeholder);
                    }
                } else {

                    imageUri = Uri.parse(editingBook.getImageUrl());
                    ivBookImage.setImageURI(imageUri);
                    selectedImageUri = imageUri;
                }
            } catch (Exception e) {
                ivBookImage.setImageResource(R.drawable.ic_book_placeholder);
            }
        } else {
            ivBookImage.setImageResource(R.drawable.ic_book_placeholder);
        }
    }

    private void setCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void setupEvents() {

        btnSave.setOnClickListener(v -> saveBook());


        btnCancel.setOnClickListener(v -> finish());


        etDate.setOnClickListener(v -> showDatePicker());


        btnPickImage.setOnClickListener(v -> openImagePicker());
        ivBookImage.setOnClickListener(v -> openImagePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etDate.setText(dateFormat.format(calendar.getTime()));
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
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh sách"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedUri = data.getData();
            try {

                String savedImagePath = copyImageToInternalStorage(selectedUri);
                if (savedImagePath != null) {
                    selectedImageUri = Uri.parse(savedImagePath);
                    ivBookImage.setImageURI(selectedImageUri);
                    etImageUrl.setText(savedImagePath);
                } else {
                    Toast.makeText(this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi khi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String copyImageToInternalStorage(Uri sourceUri) {
        try {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "book_image_" + timeStamp + ".jpg";


            File internalDir = new File(getFilesDir(), "book_images");
            if (!internalDir.exists()) {
                internalDir.mkdirs();
            }

            File destFile = new File(internalDir, fileName);


            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
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

        String code = etCode.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        double price = Double.parseDouble(etPrice.getText().toString().trim());
        String imageUrl = etImageUrl.getText().toString().trim();

        if (selectedImageUri != null) {
            imageUrl = selectedImageUri.toString();
        }

        // Chỉ xử lý edit mode
        editingBook.setCode(code);
        editingBook.setTitle(title);
        editingBook.setCategory(category);
        editingBook.setAuthor(author);
        editingBook.setEntryDate(date);
        editingBook.setPrice(price);
        editingBook.setImageUrl(imageUrl);

        int result = dbHelper.updateBook(editingBook);
        if (result > 0) {
            Toast.makeText(this, getString(R.string.update_book_success), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.update_book_error), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {

        if (TextUtils.isEmpty(etCode.getText().toString().trim())) {
            etCode.setError("Vui lòng nhập mã sách");
            etCode.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
            etTitle.setError("Vui lòng nhập tên sách");
            etTitle.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etCategory.getText().toString().trim())) {
            etCategory.setError("Vui lòng nhập thể loại");
            etCategory.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etAuthor.getText().toString().trim())) {
            etAuthor.setError("Vui lòng nhập tác giả");
            etAuthor.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etDate.getText().toString().trim())) {
            etDate.setError("Vui lòng chọn ngày nhập kho");
            etDate.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etPrice.getText().toString().trim())) {
            etPrice.setError("Vui lòng nhập giá");
            etPrice.requestFocus();
            return false;
        }

        try {
            double price = Double.parseDouble(etPrice.getText().toString().trim());
            if (price < 0) {
                etPrice.setError("Giá phải lớn hơn 0");
                etPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Giá không hợp lệ");
            etPrice.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}