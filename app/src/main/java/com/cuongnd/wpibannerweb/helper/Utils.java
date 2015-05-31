package com.cuongnd.wpibannerweb.helper;

import android.content.Context;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Cuong Nguyen on 5/18/2015.
 */
public class Utils {
    public static void logError(String TAG, Exception e) {
        Log.e(TAG, "exception", e);
    }

    public static void addRow(TableLayout table, String... text) {
        Context context = table.getContext();
        TableRow newRow = new TableRow(context);
        for (String t : text) {
            TextView newCell = new TextView(context);
            newCell.setText(t);
            newRow.addView(newCell);
        }
        table.addView(newRow);
    }

    public static class TermValue {
        private String mValue;
        private String mText;

        public TermValue(String value, String text) {
            mValue = value;
            mText = text;
        }

        public String getValue() {
            return mValue;
        }

        public String toString() {
            return mText;
        }
    }

}
