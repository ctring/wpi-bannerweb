package com.cuongnd.wpibannerweb.helper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

// TODO: write documentation
/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class Table {

    private String[][] mData;

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

    public int lengthRow(int row) {
        return mData[row].length;
    }
}
