package com.cuongnd.wpibannerweb.parser;

import android.view.View;

import com.cuongnd.wpibannerweb.ConnectionManager;

import java.util.ArrayList;
import java.util.List;

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
        mParsers = new PageParser[]{new AdvisorParser(), new CardBalanceParser(),
                new GradeParser(), new MailboxParser()};
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

    public int getLayoutResId(String name) {
        PageParser page = getPageParserByName(name);
        if (page != null) {
            return page.getLayoutResId();
        }
        return 0;
    }

    public void updateView(String name, View view) {
        PageParser page = getPageParserByName(name);
        if (page != null) {
            page.updateView(view);
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
