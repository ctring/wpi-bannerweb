package com.cuongnd.wpibannerweb.simpleparser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuongnd.wpibannerweb.helper.ConnectionManager;

import org.json.JSONObject;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class ParserManager {

    private static ParserManager manager;

    public static ParserManager getInstance() {
        if (manager == null) {
            manager = new ParserManager();
        }
        return manager;
    }

    private final PageParser[] mParsers;

    private ParserManager() {
        mParsers = new PageParser[]{new AdvisorParser(),
                new CardBalanceParser(),
                new MailboxParser()};
    }

    public boolean refreshPage(String name) {
        PageParser page = getPageParserByName(name);
        if (page != null) {
            String html = ConnectionManager.getInstance().getPage(page.getUri());
            if (html == null)
                return false;
            page.parse(html);
            return true;
        }
        return false;
    }

    public View getView(String name, LayoutInflater inflater, ViewGroup container) {
        PageParser page = getPageParserByName(name);
        if (page != null) {
            return page.getView(inflater, container);
        }
        return null;
    }

    public void updateView(String name, Context context, View view) {
        PageParser page = getPageParserByName(name);
        if (page != null) {
            page.updateView(context, view);
        }
    }

    private PageParser getPageParserByName(String name) {
        for (PageParser page : mParsers) {
            if (page.getName().equals(name))
                return page;
        }
        return null;
    }

}
