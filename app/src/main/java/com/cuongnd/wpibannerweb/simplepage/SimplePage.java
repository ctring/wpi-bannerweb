package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public abstract class SimplePage {
    protected JSONObject mData;

    SimplePage() {
        mData = new JSONObject();
    }

    public abstract boolean parse(String html);

    public abstract View getView(LayoutInflater inflater, ViewGroup container);

    public abstract void updateView(Context context, View v);

    public abstract String getName();

    public abstract String getUri();

    public void setData(JSONObject data) {
        mData = data;
    }

    public JSONObject getData() {
        return mData;
    }

}
