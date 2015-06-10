package com.cuongnd.wpibannerweb.grade;

import android.util.Log;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.Utils;
import com.cuongnd.wpibannerweb.helper.Table;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Represents the Final grade page model.
 *
 * @author Cuong Nguyen
 */
public class FinalGradePage {
    private static final String TAG = FinalGradePage.class.getSimpleName();

    private static final String STUDENT_RECORDS =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_GenMenu?name=bmenu.P_AdminMnu";
    private static final String VIEW_TERM =
            "https://bannerweb.wpi.edu/pls/prod/bwskogrd.P_ViewTermGrde";
    private static final String VIEW_GRADE =
            "https://bannerweb.wpi.edu/pls/prod/bwskogrd.P_ViewGrde";

    public static final String JSON_COURSE = "course";
    public static final String JSON_SUMMARY = "summary";
    public static final String JSON_TERM = "term";


    /**
     * Parses and returns a list of terms to select to view final grade.
     *
     * @return a list of WPI terms
     * @throws IOException If a connection problem occurred
     * @throws SocketTimeoutException If connection timed out
     * @throws NullPointerException
     */
    public static ArrayList<Utils.TermValue> getTerms() throws IOException {
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

    /**
     * Parses and returns a JSON object containing the final grade data of the specified term.
     *
     * @param termId the term to get final grade data in
     * @return a JSON object containing the final grade data
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     * @throws NullPointerException
     */
    public static JSONObject loadData(String termId) throws IOException {

        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_GRADE, VIEW_TERM, "term_in=" + termId);

        Document doc = Jsoup.parse(html);
        Element tableCourse = doc.select("table:contains(Undergraduate Course work)").first();
        Element tableSummary = doc.select("table:contains(Undergraduate Summary").first();

        Table course = new Table(tableCourse);
        Table summary = new Table(tableSummary);

        JSONObject data = new JSONObject();
        try {
            data.put(JSON_TERM, termId);
            data.put(JSON_COURSE, course.toJSONArray());
            data.put(JSON_SUMMARY, summary.toJSONArray());
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception occurred!", e);
        }
        return data;
    }


}
