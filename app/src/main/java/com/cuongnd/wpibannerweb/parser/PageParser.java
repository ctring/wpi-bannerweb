package com.cuongnd.wpibannerweb.parser;

import android.view.View;

import org.json.JSONObject;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public abstract class PageParser {
    protected JSONObject mData;

    protected PageParser() {
        mData = new JSONObject();
    }

    public abstract boolean parse(String html);

    public abstract int getLayoutResId();

    public abstract void updateView(View v);

    public abstract String getName();

    public abstract String getUri();

    public JSONObject toJSON() {
        return mData;
    }

}
