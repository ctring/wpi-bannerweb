package com.cuongnd.wpibannerweb.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: write documentation
/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class Table {

    private String[][] mData;

    public Table(JSONArray data) throws JSONException {
        mData = new String[data.length()][];
        for (int i = 0; i < data.length(); i++) {
            JSONArray row = data.getJSONArray(i);
            mData[i] = new String[row.length()];
            for (int j = 0; j < row.length(); j++) {
                mData[i][j] = row.getString(j);
            }
        }
    }

    public Table(Element doc) {
        parse(doc);
    }

    public void parse(Element doc) {
        Elements eTables = doc.getElementsByTag("table");
        Element eTable = eTables.first();
        Elements rows = eTable.getElementsByTag("TR");

        mData = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            Elements cells = rows.get(i).select("TD, TH");
            mData[i] = new String[cells.size()];
            for (int j = 0; j < cells.size(); j++)
                mData[i][j] = cells.get(j).text();
        }
    }

    public String get(int row, int col) {
        return mData[row][col];
    }

    public int size() {
        return mData.length;
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        if (mData != null)
            for (int i = 0; i < mData.length; i++) {
                JSONArray row = new JSONArray();
                for (int j = 0; j < mData[i].length; j++) {
                    row.put(mData[i][j]);
                }
                jsonArray.put(row);
            }
        return jsonArray;
    }

}
