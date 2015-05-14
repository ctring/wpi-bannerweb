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
    private ArrayList<String> mColHeaders;
    private ArrayList<String> mRowHeaders;

    public Table() {
        mRows = new ArrayList<>();
        mColHeaders = new ArrayList<>();
        mRowHeaders = new ArrayList<>();
    }

    public Table(Element doc, boolean hasColHeader, boolean hasRowHeader) throws IOException {
        this();
        parse(doc, hasColHeader, hasRowHeader);
    }

    public void parse(Element doc, boolean hasColHeader, boolean hasRowHeader) throws IOException {
        Elements eTables = doc.getElementsByTag("table");
        if (eTables.isEmpty()) {
            throw new NoTableException();
        }
        Element eTable = eTables.first();
        Elements rows = eTable.getElementsByTag("TR");

        // Assume that the header is always in the first row
        if (hasColHeader) {
            Elements headers = rows.first().getElementsByTag("TH");
            mColHeaders.clear();
            for (Element header : headers) {
                mColHeaders.add(header.text());
            }
            rows.remove(0);
        }

        mRowHeaders.clear();
        mRows.clear();
        for (Element row : rows) {
            // Assume that the header is always in the first column
            if (hasRowHeader) {
                Element header = row.getElementsByTag("TH").first();
                mRowHeaders.add(header.text());
            }
            Elements cells = row.getElementsByTag("TD");
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

    public ArrayList<String> getColHeaders() {
        return mColHeaders;
    }

    public ArrayList<String> getRowHeaders() {
        return mColHeaders;
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
