package com.cuongnd.wpibannerweb.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cuongnd.wpibannerweb.ConnectionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Cuong Nguyen on 6/6/2015.
 */
public class PictureUtils {

    public static void downloadPictureAndSave(Context context, String url, String referer,
                                                String fileName, int quality) throws IOException {

        byte[] bitmapBytes = ConnectionManager.getInstance().getBytes(url, referer);
        final Bitmap bitmap = BitmapFactory
                .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
