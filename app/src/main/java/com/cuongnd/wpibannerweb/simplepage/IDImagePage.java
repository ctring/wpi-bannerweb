package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

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

/**
 * Created by Cuong Nguyen on 6/5/2015.
 */
public class IDImagePage extends SimplePage {
    public static final String PAGE_NAME = "IDImagePage";

    public static final String IMAGE_NAME = "IdImage";

    private Context mContext;

    IDImagePage(Context context) {
        mContext = context;
    }

    @Override
    public boolean dataLoaded() {
        File file = new File(mContext.getFilesDir(), IMAGE_NAME);
        return file.exists();
    }

    @Override
    public boolean parse(String html) {
        Document doc = Jsoup.parse(html);
        Element pic = doc.select("img[src*=photos]").first();
        if (pic == null)
            return false;
        // TODO: use url or uri consistently
        String uri = "https://bannerweb.wpi.edu" + pic.attr("src");
        try {
            PictureUtils.downloadPictureAndSave(mContext, uri, getUri(), IMAGE_NAME, 100);
        } catch (IOException e) {
            Utils.logError(PAGE_NAME, e);
        }

        return true;
    }

    @Override
    public void updateView(Context context, View v) {
        try {
            InputStream in = context.openFileInput(IMAGE_NAME);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            ImageView imageView = (ImageView) v.findViewById(R.id.image_id);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Utils.logError(PAGE_NAME, e);
        }
    }

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    public String getUri() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwkimage.p_display";
    }
}
