package com.cuongnd.wpibannerweb.grade;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.Utils;
import com.cuongnd.wpibannerweb.helper.Table;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class FinalGradePage {
    private static final String TAG = "FinalGradePage";

    private static final String STUDENT_RECORDS =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_GenMenu?name=bmenu.P_AdminMnu";
    private static final String VIEW_TERM =
            "https://bannerweb.wpi.edu/pls/prod/bwskogrd.P_ViewTermGrde";
    private static final String VIEW_GRADE =
            "https://bannerweb.wpi.edu/pls/prod/bwskogrd.P_ViewGrde";

    public static final String JSON_COURSE = "course";
    public static final String JSON_SUMMARY = "summary";
    public static final String JSON_TERM = "term";

    public static ArrayList<Utils.TermValue> getTerms() {
        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_TERM, STUDENT_RECORDS);

        ArrayList<Utils.TermValue> terms = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element select = doc.getElementById("term_id");
        Elements options = select.getElementsByTag("option");
        for (Element option : options) {
            String value = option.attr("value");
            String text = option.text();
            Utils.TermValue term = new Utils.TermValue(value, text);
            terms.add(term);
        }
        return terms;
    }

    public static JSONObject load(String termid) {
        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_GRADE, VIEW_TERM, "term_in=" + termid);

        if (html == null) return null;

        Document doc = Jsoup.parse(html);
        Element tableCourse = doc.select("table:contains(Undergraduate Course work)").first();
        Element tableSummary = doc.select("table:contains(Undergraduate Summary").first();

        Table course = new Table(tableCourse);
        Table summary = new Table(tableSummary);

        try {
            JSONObject data = new JSONObject();
            data.put(JSON_TERM, termid);
            data.put(JSON_COURSE, course.toJSONArray());
            data.put(JSON_SUMMARY, summary.toJSONArray());
            return data;
        } catch (JSONException e) {
            Utils.logError(TAG, e);
        }

        return null;
    }


}
