package com.cuongnd.wpibannerweb.parser;

import android.view.View;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class CardBalanceParser extends PageParser {
    public static final String PAGE_NAME = "CardBalanceParser";

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
        return "https://bannerweb.wpi.edu/pls/prod/hwwkcbrd.P_Display";
    }

    @Override
    public void updateView(View v) {

    }
}
