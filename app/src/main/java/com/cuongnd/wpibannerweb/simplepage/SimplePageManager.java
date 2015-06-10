package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.JSONSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * SimplePageManager is a singleton that manages all of the simple pages. It performs networking
 * and data saving tasks for the simple pages.
 *
 * @author Cuong Nguyen
 */
public class SimplePageManager {

    private static final String TAG = SimplePageManager.class.getSimpleName();

    private static SimplePageManager manager;

    public static SimplePageManager getInstance(Context context) {
        if (manager == null) {
            manager = new SimplePageManager(context);
        }
        return manager;
    }

    private final SimplePage[] mParsers;
    private Context mContext;

    /**
     * Constructs a new simple page manager. All the simple pages will be loaded with offline data,
     * if any.
     * @param context the application context that hold the offline data
     */
    private SimplePageManager(Context context) {
        mContext = context;

        AdvisorPage advisorPage = new AdvisorPage();
        try {
            advisorPage.setData(JSONSerializer.loadJSONFromFile(context,
                    AdvisorPage.PAGE_NAME + ".json"));
        } catch (IOException | JSONException e) {
            advisorPage.setData(new JSONObject());
            Log.e(TAG, "Cannot find offline data for AdvisorPage", e);
        }

        CardBalancePage cardBalancePage = new CardBalancePage();
        try {
            cardBalancePage.setData(JSONSerializer.loadJSONFromFile(context,
                    CardBalancePage.PAGE_NAME + ".json"));
        } catch (IOException | JSONException e) {
            cardBalancePage.setData(new JSONObject());
            Log.e(TAG, "Cannot find offline data for CardBalancePage", e);
        }

        MailboxPage mailboxPage = new MailboxPage();
        try {
            mailboxPage.setData(JSONSerializer.loadJSONFromFile(context,
                    MailboxPage.PAGE_NAME + ".json"));
        } catch (IOException | JSONException e) {
            mailboxPage.setData(new JSONObject());
            Log.e(TAG, "Cannot find offline data for MailboxPage", e);
        }

        IDImagePage idImagePage = new IDImagePage(mContext);

        mParsers = new SimplePage[]{advisorPage, cardBalancePage, mailboxPage, idImagePage};
    }

    /**
     * Reloads data for a page with specified name then saves the data locally.
     *
     * @param name name of the page to be reload
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    public void reloadPage(String name) throws IOException {
        SimplePage page = getPageParserByName(name);
        if (page == null) return;

        if (page.dataLoaded()) return;
        String html = ConnectionManager.getInstance().getPage(page.getUrl());
        try {
            page.parse(html);
            if (!(page instanceof IDImagePage)) {
                    JSONSerializer.saveJSONToFile(mContext,
                            page.getName() + ".json", page.getData());

            }
        } catch (IOException e) {
            Log.e(TAG, "Cannot save offline data!", e);
        } catch (NullPointerException e) {
            Log.e(TAG, "Error in parsing html of page " + name, e);
        }
    }

    /**
     * Update the view hierarchy with the data of the specified page.
     *
     * @param name name of the page
     * @param view the view hierarchy to be updated
     */
    public void updateView(String name, View view) {
        SimplePage page = getPageParserByName(name);
        if (page != null) {
            page.updateView(mContext, view);
        }
    }

    private SimplePage getPageParserByName(String name) {
        for (SimplePage page : mParsers) {
            if (page.getName().equals(name))
                return page;
        }
        return null;
    }

}
