package com.cuongnd.wpibannerweb.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Contains miscellaneous utility classes and static methods.
 *
 * @author Cuong Nguyen
 */
public class Utils {

    public static final String CLASS_FULL_TITLE = "%s - %s";
    public static final String TIME_FORMAT = "h:mm a";
    public static final String DATE_FORMAT = "MMM dd, yyyy";

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

    public static void startRefreshing(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    public static void stopRefreshing(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 200);
    }

    /**
     * Data structure for holding name and id of a WPI term
     */
    public static class TermValue {
        public static final String JSON_TERM_VALUE = "TermValue";
        public static final String JSON_TERM_NAME = "TermName";
        public static final String JSON_MARK = "Mark";

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

        public JSONObject toJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_TERM_VALUE, mValue);
            jsonObject.put(JSON_TERM_NAME, mText);
            jsonObject.put(JSON_MARK, mMark);
            return jsonObject;
        }

        public static TermValue fromJSON(JSONObject jsonObject) throws JSONException {
            String termValue = jsonObject.getString(JSON_TERM_VALUE);
            String termName = jsonObject.getString(JSON_TERM_NAME);
            boolean termMark = jsonObject.getBoolean(JSON_MARK);
            TermValue term = new TermValue(termValue, termName);
            term.setMark(termMark);
            return term;
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
        char[] wpiDays = {'?', '?', 'M', 'T', 'W', 'R', 'F', '?'};
        String ret = "";
        for (int c : days) ret += wpiDays[c];
        return ret;
    }

    /**
     * Converts a Calendar object into a string using provided format.
     * @param time Calendar object to be converted
     * @param format format of the result
     * @return a string representing the date and time of the given Calendar object in provided format
     */
    public static String formatTime(Calendar time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(time.getTime());
    }

}
