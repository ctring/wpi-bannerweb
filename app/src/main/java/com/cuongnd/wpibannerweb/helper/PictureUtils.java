package com.cuongnd.wpibannerweb.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cuongnd.wpibannerweb.ConnectionManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;

/**
 * Contains utility methods for dealing with images.
 *
 * @author Cuong Nguyen
 */
public class PictureUtils {

    /**
     * Downloads a picture and save it locally. The picture is saved in the app data folder with
     * specified file name and quality.
     *
     * @param context application context to save the picture
     * @param url url to download the picture
     * @param referrer referrer to the url. May be null if not needed
     * @param fileName name of the picture when saved locally
     * @param quality hint to the compressor, range from 1 to 100
     * @throws IOException If a connection problem occurred
     * @throws SocketTimeoutException If connection timed out
     * @throws FileNotFoundException
     */
    public static void downloadPictureAndSave(Context context, String url, String referrer,
                                                String fileName, int quality) throws IOException {

        byte[] bitmapBytes = ConnectionManager.getInstance().getBytes(url, referrer);
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
