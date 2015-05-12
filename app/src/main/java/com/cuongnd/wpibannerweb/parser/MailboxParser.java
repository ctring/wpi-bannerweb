package com.cuongnd.wpibannerweb.parser;

import android.view.View;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class MailboxParser extends PageParser {
    public static final String PAGE_NAME = "MailboxParser";

    @Override
    public String getName() {
        return getName();
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
        return "https://bannerweb.wpi.edu/pls/prod/hwwkboxs.P_ViewBoxs";
    }

    @Override
    public void updateView(View v) {

    }
}
