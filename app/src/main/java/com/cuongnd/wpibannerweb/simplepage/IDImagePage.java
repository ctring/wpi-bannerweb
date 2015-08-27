package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.PictureUtils;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 * Represents the ID image page model.
 *
 * @author Cuong Nguyen
 */
public class IDImagePage extends SimplePage {

    public static final String PAGE_NAME = IDImagePage.class.getSimpleName();

    public static final String IMAGE_NAME = "IdImage";

    private Context mContext;

    IDImagePage(Context context) {
        mContext = context;
    }

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    public int getLayoutResource() {
        return 0;
    }

    @Override
    public String getUrl() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwkimage.p_display";
    }

    @Override
    public boolean dataLoaded() {
        File file = new File(mContext.getFilesDir(), IMAGE_NAME);
        return file.exists();
    }

    /**
     * Parses a HTML string representing the ID image page.
     *
     * @param html the HTML string to be parsed
     * @throws NullPointerException
     */
    @Override
    public void parse(String html) {
        Document doc = Jsoup.parse(html);
        Element pic = doc.select("img[src*=photos]").first();
        String referrer = "https://bannerweb.wpi.edu" + pic.attr("src");
        try {
            PictureUtils.downloadPictureAndSave(mContext, referrer, getUrl(), IMAGE_NAME, 100);
        } catch (SocketTimeoutException e){
            Log.e(PAGE_NAME, "Connection timed out!");
        } catch (IOException e) {
            Log.e(PAGE_NAME, "Cannot download ID image!");
        }
    }

    /**
     * Reloads this page then saves the data locally.
     *
     * @param context Context for saving the data locally
     *
     * @throws IOException            If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    @Override
    public void load(Context context) throws IOException {
        if (dataLoaded()) {
            return;
        }
        String html = ConnectionManager.getInstance().getPage(getUrl());
        parse(html);
    }

    @Override
    public void loadFromLocal(Context context) {
        // Do nothing because this class behaves differently
    }

    @Override
    public View createView(Context context, ViewGroup container) {
        return null;
    }

    /**
     * Updates the ID image.
     *
     * @param context the Context of the application
     * @param v image view to hold the id image
     */
    @Override
    public void updateView(Context context, View v) {
        try {
            InputStream in = context.openFileInput(IMAGE_NAME);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            ImageView imageView = (ImageView) v.findViewById(R.id.image_id);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e(PAGE_NAME, "Cannot find ID image in local storage!");
        } catch (NullPointerException e) {
            Log.e(PAGE_NAME, "Cannot update view!", e);
        }
    }

}
