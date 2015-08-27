package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.JSONSerializer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * SimplePageManager manages all of the simple pages. It performs networking
 * and data saving tasks for the simple pages.
 *
 * @author Cuong Nguyen
 */
public class SimplePageManager {

    private static final String TAG = SimplePageManager.class.getSimpleName();

    private final SimplePage[] mPages;
    private Context mContext;

    /**
     * Constructs a new simple page manager. All the simple pages will be loaded with offline data,
     * if any.
     * @param context the application context that hold the offline data
     */
    public SimplePageManager(Context context) {
        mContext = context;

        mPages = new SimplePage[]{  new TodayPage(),
                                    new CardBalancePage(),
                                    new AdvisorPage(),
                                    new MailboxPage(),
                                    new IDImagePage(mContext) };

        for (SimplePage page : mPages) {
            page.loadFromLocal(mContext);
        }
    }

    /**
     * Reloads data for a page with specified name then saves the data locally.
     *
     * @param name name of the page to be load
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    public void reloadPage(String name) throws IOException {
        SimplePage page = getPageParserByName(name);
        if (page != null) {
            page.load(mContext);
        }
    }

    /**
     * Create the view hierarchy of the pages.
     *
     * @return a list of cards representing pages
     */
    public ArrayList<View> createViews(ViewGroup container) {
        ArrayList<View> views = new ArrayList<>();
        for (SimplePage page : mPages) {
            View newView = page.createView(mContext, container);
            if (newView != null) {
                views.add(newView);
            }
        }

        return views;
    }

    /**
     * Update the view hierarchy with the data of the specified page. The page of the view is
     * identified using its tag.
     *
     * @param view the view hierarchy to be updated
     */
    public void updateView(View view) {
        Object tag = view.getTag();

        if (tag == null) {
            return;
        }

        SimplePage page = getPageParserByName(tag.toString());
        if (page != null) {
            page.updateView(mContext, view);
        }
    }

    /**
     * Update a list of view hierarchies.
     *
     * @param views the view group containing the view hierarchies to be updated
     */
    public void updateViews(ViewGroup views) {
        for (int i = 0; i < views.getChildCount(); i++) {
            updateView(views.getChildAt(i));
        }
    }

    private SimplePage getPageParserByName(String name) {
        for (SimplePage page : mPages) {
            if (page.getName().equals(name))
                return page;
        }
        return null;
    }

}
