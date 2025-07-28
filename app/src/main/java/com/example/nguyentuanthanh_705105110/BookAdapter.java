package com.example.nguyentuanthanh_705105110;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import java.io.File;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> books;
    private DatabaseHelper dbHelper;
    private NumberFormat currencyFormat;

    public BookAdapter(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.books = new ArrayList<>();
        this.dbHelper = dbHelper;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);

        holder.tvCode.setText("Mã: " + book.getCode());
        holder.tvTitle.setText(book.getTitle());
        holder.tvCategory.setText("Thể loại: " + book.getCategory());
        holder.tvAuthor.setText("Tác giả: " + book.getAuthor());
        holder.tvDate.setText("Ngày nhập: " + book.getEntryDate());
        holder.tvPrice.setText("Giá: " + currencyFormat.format(book.getPrice()));
        // Hiển thị ảnh sách
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            try {
                Uri imageUri;
                if (book.getImageUrl().startsWith("file://")) {
                    imageUri = Uri.parse(book.getImageUrl());
                } else if (book.getImageUrl().startsWith("/")) {
                    File imageFile = new File(book.getImageUrl());
                    if (imageFile.exists()) {
                        imageUri = Uri.fromFile(imageFile);
                    } else {
                        imageUri = null;
                    }
                } else {
                    imageUri = Uri.parse(book.getImageUrl());
                }

                if (imageUri != null) {
                    holder.ivBookImage.setImageURI(imageUri);
                } else {
                    holder.ivBookImage.setImageResource(R.drawable.ic_book_placeholder);
                }
            } catch (Exception e) {
                holder.ivBookImage.setImageResource(R.drawable.ic_book_placeholder);
            }
        } else {
            holder.ivBookImage.setImageResource(R.drawable.ic_book_placeholder);
        }

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditBookActivity.class);
            intent.putExtra("book_id", book.getId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmDialog(book, position);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void updateBooks(List<Book> newBooks) {
        this.books.clear();
        this.books.addAll(newBooks);
        notifyDataSetChanged();
    }

    private void showDeleteConfirmDialog(Book book, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sách \"" + book.getTitle() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int result = dbHelper.deleteBook(book.getId());
                    if (result > 0) {
                        books.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, books.size());
                        Toast.makeText(context, "Đã xóa sách thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi khi xóa sách", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvTitle, tvCategory, tvAuthor, tvDate, tvPrice;
        Button btnEdit, btnDelete;
        ImageView ivBookImage;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
        }
    }
}