package com.cuongnd.wpibannerweb.parser;

import android.view.View;

import org.json.JSONObject;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public abstract class PageParser {
    private JSONObject mData;

    public abstract boolean parse(String html);

    public abstract View getView();

    public abstract String getName();

    public JSONObject toJSON() {
        return mData;
    }

}
