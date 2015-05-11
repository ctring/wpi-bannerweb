package com.cuongnd.wpibannerweb.parser;

import android.view.View;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class MailboxParser extends PageParser {
    @Override
    public String getName() {
        return "MailboxParser";
    }

    @Override
    public boolean parse(String html) {
        return false;
    }

    @Override
    public View getView() {
        return null;
    }
}
