package com.cuongnd.wpibannerweb.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class Table {
    public static final String JSON_COLUMN_HEADER = "column";
    public static final String JSON_ROW_HEADER = "row";
    public static final String JSON_TABLE = "table";

    private ArrayList<ArrayList<String>> mRows;
    private ArrayList<String> mColHeaders;
    private ArrayList<String> mRowHeaders;

    public Table() {
        mRows = new ArrayList<>();
        mColHeaders = new ArrayList<>();
        mRowHeaders = new ArrayList<>();
    }

    public Table(String html, boolean hasColHeader, boolean hasRowHeader) throws IOException {
        this();
        parse(html, hasColHeader, hasRowHeader);
    }

    public void parse(String html, boolean hasColHeader, boolean hasRowHeader) throws IOException {
        Document doc = Jsoup.parse(html);
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

    public JSONObject toJSON() {
        JSONArray table = new JSONArray();
        for (ArrayList<String> row : mRows) {
            JSONArray newRow = new JSONArray();
            for (String cell : row) {
                newRow.put(cell);
            }
            table.put(newRow);
        }

        JSONArray colHeaders = new JSONArray();
        for (String header : mColHeaders) {
            colHeaders.put(header);
        }
        JSONArray rowHeaders = new JSONArray();
        for (String header : mRowHeaders) {
            rowHeaders.put(header);
        }

        try {
            JSONObject jo = new JSONObject();
            jo.put(JSON_TABLE, table)
                    .put(JSON_COLUMN_HEADER, colHeaders)
                    .put(JSON_ROW_HEADER, rowHeaders);
            return jo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
