package com.cuongnd.wpibannerweb.parser;

import android.view.View;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class GradeParser extends PageParser {

    @Override
    public String getName() {
        return "GradeParser";
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public boolean parse(String html) {
        return false;
    }
}
