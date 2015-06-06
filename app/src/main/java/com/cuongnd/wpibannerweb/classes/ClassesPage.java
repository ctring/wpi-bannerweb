package com.cuongnd.wpibannerweb.classes;

import android.content.Context;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.helper.JSONSerializer;
import com.cuongnd.wpibannerweb.helper.Utils;
import com.cuongnd.wpibannerweb.helper.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
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

    private static final String JSON_CLASSES = "classes";
    private static final String JSON_REMIND = "remind";

    private static final String REGISTRATION =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_GenMenu?name=bmenu.P_RegMnu";
    private static final String SELECT_TERM =
            "https://bannerweb.wpi.edu/pls/prod/bwcklibs.P_StoreTerm";
    private static final String VIEW_TERM =
            "https://bannerweb.wpi.edu/pls/prod/bwskflib.P_SelDefTerm";
    private static final String VIEW_CLASSES =
            "https://bannerweb.wpi.edu/pls/prod/bwskfshd.P_CrseSchdDetl";

    private static final String CLASSES_PAGE_FOLDER = "classes_page";

    public static String getFileName(String termId) {
        return termId + ".json";
    }

    private Context mContext;
    private String mTermId;
    private volatile ArrayList<WPIClass> mClasses;
    private volatile boolean mRemind;

    private File mClassesPageDir;

    public ClassesPage(Context context, String termId) {
        mContext = context;
        mTermId = termId;
        mClassesPageDir = context.getDir(CLASSES_PAGE_FOLDER, Context.MODE_PRIVATE);

        loadFromLocal(); // Always call this after mClassesPageDir is initiated
    }

    public static ArrayList<Utils.TermValue> getTerms(Context context) throws IOException {
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

        syncOfflineData(context, terms);

        return terms;
    }

    private static void syncOfflineData(Context context, ArrayList<Utils.TermValue> terms) {
        File dir = context.getDir(CLASSES_PAGE_FOLDER, Context.MODE_PRIVATE);
        File[] files = dir.listFiles();
        for (File f : files) {
            boolean exist = false;
            for (Utils.TermValue t : terms) {
                if (f.getName().equals(t.getValue() + ".json")) {
                    exist = true;
                    try {
                        JSONObject obj = JSONSerializer.loadJSONFromFile(context, dir, f.getName());
                        t.setMark(obj.getBoolean(JSON_REMIND));
                    } catch (JSONException | IOException e) {
                        Utils.logError(TAG, e);
                    }
                    break;
                }
            }
            if (!exist)
                f.delete();
        }
    }

    public ArrayList<WPIClass> getClasses() {
        return mClasses;
    }

    public boolean isReminded() {
        return mRemind;
    }

    public void setRemind(boolean remind) {
        mRemind = remind;
    }

    private void loadFromLocal() {
        mRemind = false;
        ArrayList<WPIClass> classes = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONSerializer
                    .loadJSONFromFile(mContext, mClassesPageDir,getFileName(mTermId));
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_CLASSES);
            for (int i = 0; i < jsonArray.length(); i++) {
                WPIClass c = WPIClass.fromJSON(jsonArray.getJSONObject(i));
                classes.add(c);
            }
            mRemind = jsonObject.getBoolean(JSON_REMIND);
        } catch (JSONException | IOException e) {
            Utils.logError(TAG, e);
        }
        mClasses = classes;
    }

    private void saveToLocal() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (WPIClass c : mClasses) {
                jsonArray.put(c.toJSON());
            }
            jsonObject.put(JSON_CLASSES, jsonArray);
            jsonObject.put(JSON_REMIND, mRemind);
            JSONSerializer
                    .saveJSONToFile(mContext, mClassesPageDir, getFileName(mTermId), jsonObject);
        } catch (JSONException | IOException e) {
            Utils.logError(TAG, e);
        }
    }

    public boolean refresh() throws IOException {
        ArrayList<WPIClass> classes = new ArrayList<>();

        selectTerm();
        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_CLASSES, REGISTRATION);

        if (html == null) return false;

        Document doc = Jsoup.parse(html);

        if (doc.title().contains("Select Term")) {
            return false;
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

        mClasses = classes;

        saveToLocal();
        return true;
    }

    private void selectTerm() throws IOException {
        ConnectionManager cm = ConnectionManager.getInstance();
        cm.getPage(SELECT_TERM, VIEW_TERM, "term_in=" + mTermId);
    }

    private ArrayList<WPIClass.Schedule> parseSchedule(Table time) {
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

}
