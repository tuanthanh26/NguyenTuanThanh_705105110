package com.example.nguyentuanthanh_705105110;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileImageHelper {
    private static final String TAG = "ProfileImageHelper";
    private static final String PROFILE_IMAGES_DIR = "profile_images";

    public static String saveProfileImage(Context context, Uri imageUri, String userId) {
        try {
            // tạo mục lưu ảnh
            File profileDir = new File(context.getFilesDir(), PROFILE_IMAGES_DIR);
            if (!profileDir.exists()) {
                profileDir.mkdirs();
            }

            // Tạo file ảnh với tên là userId (dđể chọn ảnh theo userdđã caappj nhật )
            File imageFile = new File(profileDir, userId + ".jpg");

            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            OutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            Log.d(TAG, "Profile image saved: " + imageFile.getAbsolutePath());
            return imageFile.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "Error saving profile image", e);
            return null;
        }
    }

    public static Bitmap getProfileImage(Context context, String userId) {
        try {
            File profileDir = new File(context.getFilesDir(), PROFILE_IMAGES_DIR);
            File imageFile = new File(profileDir, userId + ".jpg");

            if (imageFile.exists()) {
                FileInputStream fis = new FileInputStream(imageFile);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
                return bitmap;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading profile image", e);
        }
        return null;
    }

 ///  chưa dùng tới ở đây

    public static boolean hasProfileImage(Context context, String userId) {
        File profileDir = new File(context.getFilesDir(), PROFILE_IMAGES_DIR);
        File imageFile = new File(profileDir, userId + ".jpg");
        return imageFile.exists();
    }

    public static boolean deleteProfileImage(Context context, String userId) {
        try {
            File profileDir = new File(context.getFilesDir(), PROFILE_IMAGES_DIR);
            File imageFile = new File(profileDir, userId + ".jpg");
            
            if (imageFile.exists()) {
                boolean deleted = imageFile.delete();
                Log.d(TAG, "Profile image deleted: " + deleted);
                return deleted;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting profile image", e);
        }
        return false;
    }


    public static String getProfileImagePath(Context context, String userId) {
        File profileDir = new File(context.getFilesDir(), PROFILE_IMAGES_DIR);
        File imageFile = new File(profileDir, userId + ".jpg");
        return imageFile.exists() ? imageFile.getAbsolutePath() : null;
    }
} 