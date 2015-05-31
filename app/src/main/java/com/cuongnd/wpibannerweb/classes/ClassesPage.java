package com.cuongnd.wpibannerweb.classes;

import com.cuongnd.wpibannerweb.helper.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.Utils;
import com.cuongnd.wpibannerweb.helper.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Cuong Nguyen on 5/28/2015.
 */
public class ClassesPage {
    private static final String TAG = "ClassesPage";

    private static final String REGISTRATION =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_GenMenu?name=bmenu.P_RegMnu";
    private static final String VIEW_TERM =
            "https://bannerweb.wpi.edu/pls/prod/bwskflib.P_SelDefTerm";
    private static final String SELECT_TERM =
            "https://bannerweb.wpi.edu/pls/prod/bwcklibs.P_StoreTerm";
    private static final String VIEW_CLASSES =
            "https://bannerweb.wpi.edu/pls/prod/bwskfshd.P_CrseSchdDetl";

    public static final String JSON_TERM = "term";
    public static final String JSON_CLASSES = "classes";
    public static final String JSON_CLASS_NAME = "classname";
    public static final String JSON_CLASS_INFO = "classinfo";
    public static final String JSON_CLASS_TIME = "classtime";

    public static ArrayList<Utils.TermValue> getTerms() {
        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_TERM, REGISTRATION);

        ArrayList<Utils.TermValue> terms = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element select = doc.getElementById("term_id");
        Elements options = select.getElementsByTag("option");
        for (Element option : options) {
            if (!option.text().contains("only")) {
                String value = option.attr("value");
                String text = option.text();
                Utils.TermValue term = new Utils.TermValue(value, text);
                terms.add(term);
            }
        }
        return terms;
    }

    public static JSONObject load(String termid) {

        selectTerm(termid);

        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_CLASSES, REGISTRATION);

        if (html == null) return null;

        Document doc = Jsoup.parse(html);

        // TODO: Notify properly when the select term page is up instead of the classes page
        if (doc.title().contains("Select Term")) return null;

        Elements tables = doc.getElementsByClass("datadisplaytable");

        try {

            JSONArray classes = new JSONArray();
            for (int i = 0; i < tables.size(); i+= 2) {
                Element info = tables.get(i);
                Element time = tables.get(i + 1);

                JSONObject aclass = new JSONObject();
                aclass.put(JSON_CLASS_NAME, info.getElementsByTag("caption").first().text());
                aclass.put(JSON_CLASS_INFO, new Table(info));
                aclass.put(JSON_CLASS_TIME, new Table(time));

                classes.put(aclass);
            }

            JSONObject data = new JSONObject();
            data.put(JSON_TERM, termid);
            data.put(JSON_CLASSES, classes);
            return data;
        } catch (JSONException e) {
            Utils.logError(TAG, e);
        }

        return null;
    }

    private static void selectTerm(String termid) {
        ConnectionManager cm = ConnectionManager.getInstance();
        cm.getPage(SELECT_TERM, VIEW_TERM, "term_in=" + termid);
    }

}
