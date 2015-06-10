package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * SimplePage is an abstract class for all simple pages. A simple page is defined as a page that can
 * be fetched directly from a url without posting any additional data. Data in a simple page is
 * contained in a {@link JSONObject} object.
 *
 * @author Cuong Nguyen
 */
public abstract class SimplePage {

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
