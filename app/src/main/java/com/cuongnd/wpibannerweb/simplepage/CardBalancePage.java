package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Table;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Card balance page model.
 *
 * @author Cuong Nguyen
 */
public class CardBalancePage extends SimplePage {

    public static final String PAGE_NAME = CardBalancePage.class.getSimpleName();

    public static final String JSON_MEAL_PLAN = "mealplan";
    public static final String JSON_DATE_STAMP = "datestamp";
    public static final String JSON_TIME_STAMP = "timestamp";
    public static final String JSON_MEAL_TYPES = "mealtypes";

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_dashboard_card_cardbalance;
    }

    @Override
    public String getUrl() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwkcbrd.P_Display";
    }

    @Override
    public boolean dataLoaded() {
        return false;
    }

    /**
     * Parses a HTML string representing the Card balance page.
     *
     * @param html the HTML string to be parsed
     * @throws NullPointerException
     */
    @Override
    public void parse(String html) {
        Document doc = Jsoup.parse(html, ConnectionManager.BASE_URI);
        Element body = doc.body();

        Element mealPlanE1 = body.getElementsContainingOwnText("Your current meal plan").first();
        Element mealPlanE2 = mealPlanE1.getElementsByTag("b").first();
        String mealPlan = mealPlanE2.text();

        Element timeStampE = body.getElementsContainingOwnText("Your balances as of").first();
        String temp = timeStampE.text();

        Pattern datePat = Pattern.compile("([0-9]{2})-\\w{3}-(19|20)\\d\\d");
        Pattern timePat = Pattern.compile("[0-9]?[0-9]:[0-9]{2} [AP]M");
        Matcher matcher = datePat.matcher(temp);
        String dateStamp = null;
        if (matcher.find()) dateStamp = matcher.group();
        matcher = timePat.matcher(temp);
        String timeStamp = null;
        if (matcher.find()) timeStamp = matcher.group();

        Element table = body.getElementsByClass("datadisplaytable").first();

        try {
            Table mealTable = new Table(table);
            mData.put(JSON_MEAL_PLAN, mealPlan)
                    .put(JSON_DATE_STAMP, dateStamp)
                    .put(JSON_TIME_STAMP, timeStamp)
                    .put(JSON_MEAL_TYPES, mealTable.toJSONArray());

        } catch (JSONException e) {
            Log.e(PAGE_NAME, "JSON exception occurred!", e);
        }

    }

    /**
     * Updates the view hierarchy that displays the Card balance page.
     *
     * @param context the Context of the application
     * @param v the view hierarchy to be updated.
     */
    @Override
    public void updateView(Context context, View v) {
        try {
            TextView textMealPlan = (TextView) v.findViewById(R.id.text_meal_plan);
            textMealPlan.setText(mData.getString(JSON_MEAL_PLAN));

            TableLayout tableView = (TableLayout) v
                    .findViewById(R.id.table_cardbalance);
            Table table = new Table(mData.getJSONArray(JSON_MEAL_TYPES));

            int diff = table.size() - tableView.getChildCount();
            if (diff > 0) {
                for (int i = 0; i < diff; i++)
                    addRow(context, tableView);
            }
            for (int i = 1; i < tableView.getChildCount(); i++) {
                TableRow row = (TableRow) tableView.getChildAt(i);
                for (int j = 0; j < row.getChildCount(); j++) {
                    TextView cell = (TextView) row.getChildAt(j);
                    cell.setText(table.get(i, j));
                }
            }
        } catch (JSONException e) {
            Log.e(PAGE_NAME, "Cannot find data!");
        } catch (NullPointerException e) {
            Log.e(PAGE_NAME, "Cannot update view!", e);
        }
    }

    private void addRow(Context context, TableLayout table) {
        TableRow row = new TableRow(context);

        TableRow.LayoutParams paramsAccount =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f);
        TextView textAccount = new TextView(context);
        textAccount.setLayoutParams(paramsAccount);

        TableRow.LayoutParams paramsBalance =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
        TextView textBalance = new TextView(context);
        textBalance.setLayoutParams(paramsBalance);
        textBalance.setGravity(Gravity.CENTER);

        TableRow.LayoutParams paramsDate =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
        TextView textDate = new TextView(context);
        textDate.setLayoutParams(paramsDate);
        textDate.setGravity(Gravity.CENTER);

        row.addView(textAccount);
        row.addView(textBalance);
        row.addView(textDate);

        table.addView(row);
    }
}
