package com.zybooks.inventoryapp.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class Helper {
    // Convert Bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    // Convert byte array to Bitmap
    public static Bitmap getBitmapFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // Notify the user
    public static void ToastNotify(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void SnackbarNotify(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }




}
