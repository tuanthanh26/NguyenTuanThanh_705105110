package com.example.nguyentuanthanh_705105110;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và cột
    private static final String TABLE_BOOKS = "books";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CODE = "code";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_ENTRY_DATE = "entry_date";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE_URL = "image_url";

    private static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + TABLE_BOOKS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CODE + " TEXT NOT NULL UNIQUE, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_CATEGORY + " TEXT NOT NULL, " +
            COLUMN_AUTHOR + " TEXT NOT NULL, " +
            COLUMN_ENTRY_DATE + " TEXT NOT NULL, " +
            COLUMN_PRICE + " REAL NOT NULL, " +
            COLUMN_IMAGE_URL + " TEXT" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKS);

        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        String[] sampleBooks = {
                "INSERT INTO " + TABLE_BOOKS + " VALUES (1, 'BK001', 'Lập trình Java cơ bản', 'Lập trình', 'Nguyễn Văn A', '15/07/2025', 350000, '')",
                "INSERT INTO " + TABLE_BOOKS + " VALUES (2, 'BK002', 'Android Development', 'Mobile', 'Trần Thị B', '14/07/2025', 450000, '')",
                "INSERT INTO " + TABLE_BOOKS + " VALUES (3, 'BK003', 'Cơ sở dữ liệu SQLite', 'Database', 'Lê Văn C', '13/07/2025', 280000, '')"
        };

        for (String sql : sampleBooks) {
            db.execSQL(sql);
        }
    }

    // Thêm sách mới
    public long addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CODE, book.getCode());
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_CATEGORY, book.getCategory());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_ENTRY_DATE, book.getEntryDate());
        values.put(COLUMN_PRICE, book.getPrice());
        values.put(COLUMN_IMAGE_URL, book.getImageUrl());

        long id = db.insert(TABLE_BOOKS, null, values);
        db.close();
        return id;
    }

    // Lấy tất cả sách
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_BOOKS + " ORDER BY " + COLUMN_ENTRY_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                );
                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return books;
    }

    // Tìm kiếm sách theo tên
    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_BOOKS +
                " WHERE " + COLUMN_TITLE + " LIKE ? OR " +
                COLUMN_AUTHOR + " LIKE ? OR " +
                COLUMN_CODE + " LIKE ? OR " +
                COLUMN_CATEGORY + " LIKE ?" +
                " ORDER BY " + COLUMN_ENTRY_DATE + " DESC";

        String searchPattern = "%" + query + "%";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{searchPattern, searchPattern, searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                );
                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return books;
    }

    // Cập nhật sách
    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CODE, book.getCode());
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_CATEGORY, book.getCategory());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_ENTRY_DATE, book.getEntryDate());
        values.put(COLUMN_PRICE, book.getPrice());
        values.put(COLUMN_IMAGE_URL, book.getImageUrl());

        int rowsAffected = db.update(TABLE_BOOKS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(book.getId())});
        db.close();
        return rowsAffected;
    }

    // Xóa sách
    public int deleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Lấy sách theo ID
    public Book getBookById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Book book = null;
        if (cursor.moveToFirst()) {
            book = new Book(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_DATE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
            );
        }

        cursor.close();
        db.close();
        return book;
    }

    // Lấy sách mới thêm ngày gần nhất
    public List<Book> getRecentBooks(int days) {
        List<Book> books = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        Date pastDate = calendar.getTime();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String pastDateStr = dateFormat.format(pastDate);
        
        String sql = "SELECT * FROM " + TABLE_BOOKS + 
                " WHERE " + COLUMN_ENTRY_DATE + " >= ?" +
                " ORDER BY " + COLUMN_ENTRY_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{pastDateStr});

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                );
                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return books;
    }
}