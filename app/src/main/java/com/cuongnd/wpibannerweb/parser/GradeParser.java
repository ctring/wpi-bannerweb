package com.cuongnd.wpibannerweb.parser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public void updateView(Context context, View v) {

    }
}
