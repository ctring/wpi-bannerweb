package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.JSONSerializer;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class SimplePageManager {
    private static final String TAG = "SimplePageManager";

    private static SimplePageManager manager;

    public static SimplePageManager getInstance(Context context) {
        if (manager == null) {
            manager = new SimplePageManager(context);
        }
        return manager;
    }

    private final SimplePage[] mParsers;
    private Context mContext;

    private SimplePageManager(Context context) {
        mContext = context;

        AdvisorPage advisorPage = new AdvisorPage();
        try {
            advisorPage.setData(JSONSerializer.loadJSONFromFile(context,
                    AdvisorPage.PAGE_NAME + ".json"));
        } catch (IOException | JSONException e) {
            advisorPage.setData(null);
        }

        CardBalancePage cardBalancePage = new CardBalancePage();
        try {
            cardBalancePage.setData(JSONSerializer.loadJSONFromFile(context,
                    CardBalancePage.PAGE_NAME + ".json"));
        } catch (IOException | JSONException e) {
            // TODO Log this exception somewhere
            cardBalancePage.setData(null);
        }

        MailboxPage mailboxPage = new MailboxPage();
        try {
            mailboxPage.setData(JSONSerializer.loadJSONFromFile(context,
                    MailboxPage.PAGE_NAME + ".json"));
        } catch (IOException | JSONException e) {
            mailboxPage.setData(null);
        }

        IDImagePage idImagePage = new IDImagePage(mContext);

        mParsers = new SimplePage[]{advisorPage, cardBalancePage, mailboxPage, idImagePage};
    }

    public boolean refreshPage(String name) throws IOException, JSONException {
        SimplePage page = getPageParserByName(name);
        if (page != null) {
            String html = ConnectionManager.getInstance().getPage(page.getUri());
            if (html == null)
                return false;
            page.parse(html);
            if (!(page instanceof IDImagePage))
                JSONSerializer.saveJSONToFile(mContext, page.getName() + ".json", page.getData());
            return true;
        }
        return false;
    }

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
