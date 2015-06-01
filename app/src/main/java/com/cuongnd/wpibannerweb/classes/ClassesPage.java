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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

    public static ArrayList<WPIClass> getClasses(String termid) {
        return loadFromHtml(termid);
    }

    public static ArrayList<WPIClass> loadFromHtml(String termid) {
        ArrayList<WPIClass> classes = new ArrayList<>();

        selectTerm(termid);
        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_CLASSES, REGISTRATION);

        if (html == null) return classes;

        Document doc = Jsoup.parse(html);

        // TODO: Notify properly when the select term page is up instead of the classes page
        if (doc.title().contains("Select Term")) {
            return classes;
        }

        Elements tables = doc.getElementsByClass("datadisplaytable");

        for (int i = 0; i < tables.size(); i+= 2) {
            Table info = new Table(tables.get(i));
            Table time = new Table(tables.get(i + 1));

            String fullname = tables.get(i).getElementsByTag("caption").first().text();
            String[] parts = fullname.split(" - ");
            String name = parts[0];
            String code = parts[1];
            String section = parts[2];
            String CRN = info.get(1, 1);
            String instructor = info.get(3, 1);

            WPIClass wpiclass = new WPIClass(name, code, section, CRN, instructor,
                    parseSchedule(time));
            classes.add(wpiclass);
        }

        return classes;
    }

    private static ArrayList<WPIClass.Schedule> parseSchedule(Table time) {
        SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a", Locale.US);
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        ArrayList<WPIClass.Schedule> schedules = new ArrayList<>();

        try {
            for (int i = 1; i < time.size(); i++) {
                String[] timeRange = time.get(i, 1).split(" - ");
                Calendar startTime = Calendar.getInstance();
                startTime.setTime(formatTime.parse(timeRange[0]));
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(formatTime.parse(timeRange[1]));

                String[] dateRange = time.get(i, 4).split(" - ");
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(formatDate.parse(dateRange[0]));
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(formatDate.parse(dateRange[1]));

                int[] days = Utils.fromWpiDays(time.get(i, 2));
                String location = time.get(i, 3);
                String type = time.get(i, 5);
                String instructor = time.get(i, 6);

                WPIClass.Schedule schedule = new WPIClass.Schedule();
                schedule.setStartTime(startTime)
                        .setEndTime(endTime)
                        .setDays(days)
                        .setLocation(location)
                        .setStartDate(startDate)
                        .setEndDate(endDate)
                        .setType(type)
                        .setInstructor(instructor);

                schedules.add(schedule);
            }
        } catch (ParseException e) {
            Utils.logError(TAG, e);
        }
        return schedules;
    }

    private static void selectTerm(String termid) {
        ConnectionManager cm = ConnectionManager.getInstance();
        cm.getPage(SELECT_TERM, VIEW_TERM, "term_in=" + termid);
    }

}
