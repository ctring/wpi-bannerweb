package com.cuongnd.wpibannerweb.simpleparser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    public boolean parse(String html) {
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
        Elements rows = table.getElementsByTag("TR");
        rows.remove(0);

        class MealType {
            public String name, balance, lastused;
            public MealType(String name, String balance, String lastused) {
                this.name = name; this.balance = balance; this.lastused = lastused;
            }
        };

        ArrayList<MealType> types = new ArrayList<>();

        for (Element row : rows) {
            Elements cols = row.getElementsByTag("TD");
            types.add(new MealType(cols.get(0).text(),
                    cols.get(1).text(),
                    cols.get(2).text()));
        }

        try {
            JSONArray mealTypes = new JSONArray();
            for (MealType type : types) {
                JSONArray mealType = new JSONArray();
                mealType.put(type.name)
                        .put(type.balance)
                        .put(type.lastused);
                mealTypes.put(mealType);
            }

            mData.put(JSON_MEAL_PLAN, mealPlan)
                    .put(JSON_DATE_STAMP, dateStamp)
                    .put(JSON_TIME_STAMP, timeStamp)
                    .put(JSON_MEAL_TYPES, mealTypes);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_cardbalance, container, false);
        TableLayout table = (TableLayout) v.findViewById(R.id.table_cardbalance);

        int count = 0;
        try {
            JSONArray meanTypes = mData.getJSONArray(JSON_MEAL_TYPES);
            count = meanTypes.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Context context = inflater.getContext();
        for (int i = 0; i < count; i++) {
            addRow(context, table);
        }

        return v;
    }

    @Override
    public String getUri() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwkcbrd.P_Display";
    }

    @Override
    public void updateView(Context context, View v) {
        TableLayout table = (TableLayout)v
                .findViewById(R.id.table_cardbalance);
        try {
            JSONArray mealTypes = mData.getJSONArray(JSON_MEAL_TYPES);
            int diff = mealTypes.length() - table.getChildCount();
            if (diff > 0) {
                for (int i = 0; i < diff; i++)
                    addRow(context, table);
            }
            for (int i = 1; i < table.getChildCount(); i++) {
                TableRow row = (TableRow) table.getChildAt(i);
                JSONArray mealType = mealTypes.getJSONArray(i - 1);
                for (int j = 0; j < row.getChildCount(); j++) {
                    TextView cell = (TextView) row.getChildAt(j);
                    cell.setText(mealType.getString(j));
                }
            }
        } catch (JSONException e) {
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
