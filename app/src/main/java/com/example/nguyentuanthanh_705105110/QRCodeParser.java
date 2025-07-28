package com.example.nguyentuanthanh_705105110;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class QRCodeParser {
    private static final String TAG = "QRCodeParser";
    
    public static class BookInfo {
        public String code;
        public String title;
        public String author;
        public String category;
        public String price;
        public String imageUrl;
        
        public BookInfo() {
            this.code = "";
            this.title = "";
            this.author = "";
            this.category = "";
            this.price = "";
            this.imageUrl = "";
        }
    }
    
    public static BookInfo parseQRData(String qrData) {
        BookInfo bookInfo = new BookInfo();
        
        if (qrData == null || qrData.trim().isEmpty()) {
            Log.w(TAG, "QR data is null or empty");
            return bookInfo;
        }
        
        // Try to parse as JSON first
        if (qrData.trim().startsWith("{")) {
            try {
                JSONObject json = new JSONObject(qrData);
                bookInfo.code = json.optString("code", "");
                bookInfo.title = json.optString("title", "");
                bookInfo.author = json.optString("author", "");
                bookInfo.category = json.optString("category", "");
                bookInfo.price = json.optString("price", "");
                bookInfo.imageUrl = json.optString("imageUrl", "");
                
                Log.d(TAG, "Parsed JSON QR data: " + bookInfo.title);
                return bookInfo;
            } catch (JSONException e) {
                Log.w(TAG, "Failed to parse QR data as JSON", e);
            }
        }
        
        // Try to parse as CSV format
        String[] parts = qrData.split(",");
        if (parts.length >= 4) {
            bookInfo.code = parts[0].trim();
            bookInfo.title = parts[1].trim();
            bookInfo.author = parts[2].trim();
            bookInfo.category = parts[3].trim();
            if (parts.length >= 5) {
                bookInfo.price = parts[4].trim();
            }
            if (parts.length >= 6) {
                bookInfo.imageUrl = parts[5].trim();
            }
            
            Log.d(TAG, "Parsed CSV QR data: " + bookInfo.title);
            return bookInfo;
        }
        
        // If it's just a single value, treat it as book code
        bookInfo.code = qrData.trim();
        Log.d(TAG, "Treating QR data as book code: " + bookInfo.code);
        
        return bookInfo;
    }
    
    public static boolean isValidBookInfo(BookInfo bookInfo) {
        return bookInfo != null && 
               !bookInfo.code.isEmpty() && 
               !bookInfo.title.isEmpty();
    }
    
    public static String generateSampleQRData() {
        // Generate sample QR data for testing
        JSONObject json = new JSONObject();
        try {
            json.put("code", "BK001");
            json.put("title", "Lập trình Android");
            json.put("author", "Nguyễn Văn A");
            json.put("category", "Công nghệ");
            json.put("price", "150000");
            json.put("imageUrl", "");
        } catch (JSONException e) {
            Log.e(TAG, "Error generating sample QR data", e);
        }
        return json.toString();
    }
} 