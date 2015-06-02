package com.cuongnd.wpibannerweb.helper;

import android.content.Context;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;

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

    /**
     * Data structure for holding name and id of a term
     */
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

    /**
     * Convert a string of days of week in WPI convention to an array of numbers representing the
     * corresponding days of week.
     * @param days The string of days of week in WPI convention.
     * @return An array of numbers representing the corresponding days of week. The numbers in this
     * array follow the constants for days of week in the Calendar class.
     */
    public static int[] fromWpiDays(String days) {
        int[] ret = new int[days.length()];
        for (int i = 0; i < days.length(); i++) {
            switch (days.charAt(i)) {
                case 'M': ret[i] = Calendar.MONDAY; break;
                case 'T': ret[i] = Calendar.TUESDAY; break;
                case 'W': ret[i] = Calendar.WEDNESDAY; break;
                case 'R': ret[i] = Calendar.THURSDAY; break;
                case 'F': ret[i] = Calendar.FRIDAY; break;
            }
        }
        return ret;
    }

    /**
     * Convert an array of days of week into a string of days of week in WPI convention.
     * @param days An array contains the days of week.
     * @return A string of days of week in WPI convention.
     */
    public static String toWpiDays(int[] days) {
        char[] wpiDays = {'?', '?', 'M', 'T', 'W', 'R', 'F'};
        String ret = "";
        for (int c : days) ret += wpiDays[c];
        return ret;
    }

}
