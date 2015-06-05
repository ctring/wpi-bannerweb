package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.JSONSerializer;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class SimplePageManager {

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
        advisorPage.setData(JSONSerializer.loadJSONFromFile(context,
                AdvisorPage.PAGE_NAME + ".json"));

        CardBalancePage cardBalancePage = new CardBalancePage();
        cardBalancePage.setData(JSONSerializer.loadJSONFromFile(context,
                CardBalancePage.PAGE_NAME + ".json"));

        MailboxPage mailboxPage = new MailboxPage();
        mailboxPage.setData(JSONSerializer.loadJSONFromFile(context,
                MailboxPage.PAGE_NAME + ".json"));

        mParsers = new SimplePage[]{advisorPage, cardBalancePage, mailboxPage};
    }

    public boolean refreshPage(String name) {
        SimplePage page = getPageParserByName(name);
        if (page != null) {
            String html = ConnectionManager.getInstance().getPage(page.getUri());
            if (html == null)
                return false;
            page.parse(html);
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
