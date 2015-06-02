package com.cuongnd.wpibannerweb.simpleparser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.JSONSerializer;

import org.json.JSONObject;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class ParserManager {

    private static ParserManager manager;

    public static ParserManager getInstance(Context context) {
        if (manager == null) {
            manager = new ParserManager(context);
        }
        return manager;
    }

    private final PageParser[] mParsers;
    private Context mContext;

    private ParserManager(Context context) {
        mContext = context;

        AdvisorParser advisorParser = new AdvisorParser();
        advisorParser.setData(JSONSerializer.loadJSONFromFile(context,
                AdvisorParser.PAGE_NAME + ".json"));

        CardBalanceParser cardBalanceParser = new CardBalanceParser();
        cardBalanceParser.setData(JSONSerializer.loadJSONFromFile(context,
                CardBalanceParser.PAGE_NAME + ".json"));

        MailboxParser mailboxParser = new MailboxParser();
        mailboxParser.setData(JSONSerializer.loadJSONFromFile(context,
                MailboxParser.PAGE_NAME + ".json"));

        mParsers = new PageParser[]{advisorParser, cardBalanceParser, mailboxParser};
    }

    public boolean refreshPage(String name) {
        PageParser page = getPageParserByName(name);
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
