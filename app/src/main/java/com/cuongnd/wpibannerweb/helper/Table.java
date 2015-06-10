package com.cuongnd.wpibannerweb.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a table of string that is parsed from HTML.
 *
 * @author Cuong Nguyen
 */
public class Table {

    private String[][] mData;

    /**
     * Constructs a new table from given JSON data. The data is stored in a primitive
     * two-dimensional array.
     *
     * @param data the JSON data to be converted to table
     * @throws JSONException
     */
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

    /**
     * Constructs a new table from given Jsoup element. The data is stored in a primitive
     * two-dimensional array.
     *
     * @param doc the Jsoup element to be converted to table
     * @throws NullPointerException
     */
    public Table(Element doc) {
        parse(doc);
    }

    /**
     * Parses a Jsoup element into a table structure.
     *
     * @param doc the Jsoup element to be parsed
     * @throws NullPointerException
     */
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

    /**
     * Gets the string in the specified row and column. Both row and column are zero-based.
     *
     * @param row the row that the string is in
     * @param col the column that the string is in
     * @return the string in the specified row and column
     * @throws ArrayIndexOutOfBoundsException
     */
    public String get(int row, int col) {
        return mData[row][col];
    }

    /**
     * Returns the number of row in this table.
     *
     * @return number of row
     * @throws NullPointerException
     */
    public int size() {
        return mData.length;
    }

    /**
     * Converts this table to a two-dimensional JSON array.
     *
     * @return the result JSON array
     */
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
