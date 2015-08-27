package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.JSONSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * SimplePage is an abstract class for all simple pages model. A simple page is defined as a page
 * that can be fetched directly from a url without posting any additional data. Data in a simple page
 * is contained in a {@link JSONObject} object.
 *
 * @author Cuong Nguyen
 */
public abstract class SimplePage {

    private static final String TAG = SimplePage.class.getSimpleName();

    /**
     * Data of a simple page.
     */
    protected JSONObject mData;

    /**
     * Constructs a simple page with empty data.
     */
    SimplePage() {
        mData = new JSONObject();
    }

    /**
     * Parses data from a HTML string and puts into the internal data variable of this page.
     *
     * @param html the HTML string to be parsed
     * @throws NullPointerException
     */
    public abstract void parse(String html);

    /**
     *  Reloads this page then saves the data locally.
     *
     * @param context Context for saving the data locally
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    public void load(Context context) throws IOException {
        String html = ConnectionManager.getInstance().getPage(getUrl());
        try {
            parse(html);
            JSONSerializer.saveJSONToFile(context, getName() + ".json", getData());
        } catch (IOException e) {
            Log.e(TAG, "Cannot save offline data!");
        } catch (NullPointerException e) {
            Log.e(TAG, "Error in parsing html of page " + getName(), e);
        }
    }

    /**
     * Loads offline data from JSON file if exists.
     *
     * @param context the Context of the application
     */
    public void loadFromLocal(Context context) {
        try {
            setData(JSONSerializer.loadJSONFromFile(context, this.getName() + ".json"));
        } catch (IOException | JSONException e) {
            setData(new JSONObject());
            Log.e(TAG, "Cannot find offline data for " + this.getName());
        }
    }

    /**
     * Creates and return a new view for this page. The name of the page is set as a tag of the
     * view to later identify what page the view belongs to.
     *
     * @param context the Context to get a layout inflater
     *
     * @return a new view associating with this page
     */
    public View createView(Context context, ViewGroup container) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(getLayoutResource(), container, false);
        v.setTag(getName());

        return v;
    }

    /**
     * Updates a view hierarchy with current data. If at least one view id needed for the concrete
     * implementation of a simple page is not found, nothing will happen.
     *
     * @param context the Context of the application
     * @param v the view hierarchy to be updated.
     */
    public abstract void updateView(Context context, View v);

    /**
     * Returns the name of the page.
     *
     * @return name of the page
     */
    public abstract String getName();

    public abstract int getLayoutResource();

    /**
     * Returns the main url of the page. In the case when multiple urls are needed to reach the content,
     * the main url is the one locates the page with this content.
     *
     * @return the main url of the page
     */
    public abstract String getUrl();

    /**
     * Checks whether data is loaded.
     *
     * @return <code>true</code> if data is loaded. And <code>false</code>, otherwise
     */
    public abstract boolean dataLoaded();

    /**
     * Sets data for this page. If offline data is found, it can be set by using the method.
     *
     * @param data the data to be set
     */
    public void setData(JSONObject data) {
        mData = data;
    }

    /**
     * Gets data of the page.
     *
     * @return a JSONObject object containing the data of the page
     */
    public JSONObject getData() {
        return mData;
    }

}
