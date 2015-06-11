package com.cuongnd.wpibannerweb;

import android.app.Application;
import android.util.Log;

import java.io.File;

/**
 * @author Cuong Nguyen
 */
public class WPIBannerWebApplication extends Application {

    private static final String TAG = WPIBannerWebApplication.class.getSimpleName();
    private static WPIBannerWebApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static WPIBannerWebApplication getInstance() {
        return instance;
    }

    public void clearApplicationData() {

        File files = getFilesDir();
        File appDir = new File(files.getParent());

        if(appDir.exists()) {
            String[] children = appDir.list();
            for(String s : children) {
                if(!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static void deleteDir(File dir) {
        if (dir == null) return;
        if (dir.isDirectory()) {
            String[] children = dir.list();

            for(String child : children) {
                deleteDir(new File(dir, child));
            }
            if (!dir.delete()) {
                Log.e(TAG, "Cannot delete folder: " + dir.getName());
            }
        } else {
            if (!dir.delete()) {
                Log.e(TAG, "Cannot delete file: " + dir.getName());
            }
        }

    }

}
