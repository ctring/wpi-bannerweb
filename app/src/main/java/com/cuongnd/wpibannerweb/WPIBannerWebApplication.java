package com.cuongnd.wpibannerweb;

import android.app.Application;

import java.io.File;

/**
 * Created by Cuong Nguyen on 6/2/2015.
 */
public class WPIBannerWebApplication extends Application {

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

    public static boolean deleteDir(File dir) {
        if (dir == null) return false;
        if (dir.isDirectory()) {
            String[] children = dir.list();

            for(String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if(!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

}
