package com.cuongnd.wpibannerweb.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Contains miscellaneous utility classes and static methods.
 *
 * @author Cuong Nguyen
 */
public class Utils {

    public static void showLongToast(final Activity activity, final String toast) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showShortToast(final Activity activity, final String toast) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Data structure for holding name and id of a WPI term
     */
    public static class TermValue {
        private String mValue;
        private String mText;
        private boolean mMark;

        public TermValue(String value, String text) {
            mValue = value;
            mText = text;
            mMark = false;
        }

        public String getValue() {
            return mValue;
        }

        public String toString() {
            return mText;
        }

        public boolean isMark() { return mMark;}

        public void setMark(boolean mark) {
            mMark = mark;
        }
    }

    /**
     * Converts a string of days of week in WPI convention to an array of numbers representing the
     * corresponding days of week.
     *
     * @param days the string of days of week in WPI convention.
     * @return an array of numbers representing the corresponding days of week. The numbers in this
     * array follow the constants for days of week in the Calendar class
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
     * Converts an array of days of week into a string of days of week in WPI convention. The
     * numbering convention is:
     * <ul>
     *     <li>Monday = 2</li>
     *     <li>Tuesday = 3</li>
     *     <li>Wednesday = 4</li>
     *     <li>thuRsday = 5</li>
     *     <li>Friday = 6</li>
     * </ul>
     *
     * @param days an array contains the days of week
     * @return a string of days of week in WPI convention
     */
    public static String toWpiDays(int[] days) {
        char[] wpiDays = {'?', '?', 'M', 'T', 'W', 'R', 'F'};
        String ret = "";
        for (int c : days) ret += wpiDays[c];
        return ret;
    }

}
