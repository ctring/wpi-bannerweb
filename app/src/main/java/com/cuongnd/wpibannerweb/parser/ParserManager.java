package com.cuongnd.wpibannerweb.parser;

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

    private List<PageParser> mParsers;

    private ParserManager() {
        mParsers = new ArrayList<>();

    }

}
