package com.cuongnd.wpibannerweb.simpleparser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public abstract class PageParser {
    protected JSONObject mData;

    PageParser() {
        mData = new JSONObject();
    }

    PageParser(JSONObject data) {
        mData = data;
    }

    abstract boolean parse(String html);

    public abstract View getView(LayoutInflater inflater, ViewGroup container);

    public abstract void updateView(Context context, View v);

    public abstract String getName();

    public abstract String getUri();

    public JSONObject toJSON() {
        return mData;
    }

}
