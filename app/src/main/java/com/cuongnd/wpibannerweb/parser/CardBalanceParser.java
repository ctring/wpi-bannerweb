package com.cuongnd.wpibannerweb.parser;

import android.view.View;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class CardBalanceParser extends PageParser {

    @Override
    public String getName() {
        return "CardBalanceParser";
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
