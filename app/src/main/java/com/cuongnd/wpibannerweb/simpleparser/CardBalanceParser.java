package com.cuongnd.wpibannerweb.simpleparser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class CardBalanceParser extends PageParser {
    public static final String PAGE_NAME = "CardBalanceParser";

    public static final String JSON_MEAL_PLAN = "mealplan";
    public static final String JSON_DATE_STAMP = "datestamp";
    public static final String JSON_TIME_STAMP = "timestamp";
    public static final String JSON_MEAL_TYPES = "mealtypes";

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    boolean parse(String html) {
        Document doc = Jsoup.parse(html, "https://bannerweb.wpi.edu/pls/prod/");
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
                    .put(JSON_MEAL_TYPES, mealTable);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_cardbalance, container, false);
    }

    @Override
    public String getUri() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwkcbrd.P_Display";
    }

    @Override
    public void updateView(Context context, View v) {
        TableLayout tableView = (TableLayout) v
                .findViewById(R.id.table_cardbalance);
        try {
            Table table = (Table) mData.get(JSON_MEAL_TYPES);

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
            // TODO: replace all the printStackTrace with something else more useful
            e.printStackTrace();
        }
    }

    private void addRow(Context context, TableLayout table) {
        TableRow row = new TableRow(context);
        for (int j = 0; j < 3; j++)
            row.addView(new TextView(context));
        table.addView(row);
    }
}
