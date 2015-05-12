package com.cuongnd.wpibannerweb.parser;

import android.view.View;

import com.cuongnd.wpibannerweb.R;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class GradeParser extends PageParser {
    public static final String PAGE_NAME = "GradeParser";

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    public boolean parse(String html) {
        return false;
    }

    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public void updateView(View v) {

    }
}
