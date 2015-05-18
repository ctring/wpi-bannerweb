package com.cuongnd.wpibannerweb.helper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class Table {

    private ArrayList<ArrayList<String>> mRows;

    public Table() {
        mRows = new ArrayList<>();
    }

    public Table(Element doc) throws IOException {
        this();
        parse(doc);
    }

    public void parse(Element doc) throws IOException {
        Elements eTables = doc.getElementsByTag("table");
        if (eTables.isEmpty()) {
            throw new NoTableException();
        }
        Element eTable = eTables.first();
        Elements rows = eTable.getElementsByTag("TR");

        mRows.clear();
        for (Element row : rows) {
            Elements cells = row.select("TD, TH");
            ArrayList<String> newRow = new ArrayList<>();
            for (Element cell : cells) {
                newRow.add(cell.text());
            }
            mRows.add(newRow);
        }

    }

    public ArrayList<ArrayList<String>> getTable() {
        return mRows;
    }

    public static class NoTableException extends IOException {
        private final String message = "Can't find a table.";
        @Override
        public String toString() {
            return message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

}
