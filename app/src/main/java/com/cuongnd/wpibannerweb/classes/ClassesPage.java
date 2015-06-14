package com.cuongnd.wpibannerweb.classes;

import android.content.Context;
import android.util.Log;

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
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Represents the Classes page.
 *
 * @author Cuong Nguyen
 */
public class ClassesPage {

    private static final String TAG = ClassesPage.class.getSimpleName();

    private static final String JSON_CLASSES = "classes";
    private static final String JSON_REMIND = "remind";
    private static final String JSON_TERM = "term";

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

    /**
     * Parses and returns a list of terms to select to view registered classes. After the list of
     * terms is loaded, it is synchronised with the offline data, such that any offline data of a
     * term that is no longer valid will be removed.
     *
     * @param context application context to sync offline data with
     * @return a list of WPI terms
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     * @throws NullPointerException
     */
    public static ArrayList<Utils.TermValue> getTerms(Context context) throws IOException {
        ConnectionManager cm = ConnectionManager.getInstance();
        ArrayList<Utils.TermValue> terms = new ArrayList<>();
        String html;
        html = cm.getPage(VIEW_TERM, REGISTRATION);
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

                    // Use for displaying which term is set with reminder
                    try {
                        JSONObject obj = JSONSerializer.loadJSONFromFile(context, dir, f.getName());
                        t.setMark(obj.getBoolean(JSON_REMIND));
                    } catch (IOException e) {
                        Log.e(TAG, "Error opening file!");
                    } catch (JSONException e) {
                        Log.e(TAG, "Error reading file!", e);
                    }

                    break;
                }
            }
            if (!exist)
                if (!f.delete()) Log.e(TAG, "Cannot delete file!");
        }
    }

    public static  ArrayList<Utils.TermValue> getOfflineTerms(Context context) {
        ArrayList<Utils.TermValue> terms = new ArrayList<>();
        File dir = context.getDir(CLASSES_PAGE_FOLDER, Context.MODE_PRIVATE);
        File[] files = dir.listFiles();
        for (File f : files) {
            try {
                JSONObject obj = JSONSerializer.loadJSONFromFile(context, dir, f.getName());
                Utils.TermValue term = Utils.TermValue.fromJSON(obj.getJSONObject(JSON_TERM));
                terms.add(term);
            } catch (IOException e) {
                Log.e(TAG, "Error opening file!");
            } catch (JSONException e) {
                Log.e(TAG, "Error reading file!", e);
            }
        }
        return terms;
    }

    private Context mContext;
    private Utils.TermValue mTerm;
    private File mClassesPageDir;

    private volatile ArrayList<WPIClass> mClasses;
    private volatile boolean mRemind;

    /**
     * Constructs a new class page model. The data is initially loaded from local, if any.
     *
     * @param context application context to load and save offline data
     * @param term the term to be represented
     */
    public ClassesPage(Context context, Utils.TermValue term) {
        mContext = context;
        mTerm = term;
        mClassesPageDir = context.getDir(CLASSES_PAGE_FOLDER, Context.MODE_PRIVATE);

        loadFromLocal(); // Always call this after mClassesPageDir is initiated
    }

    /**
     * Loads offline data, if any.
     */
    private void loadFromLocal() {
        mRemind = false;
        ArrayList<WPIClass> classes = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONSerializer
                    .loadJSONFromFile(mContext, mClassesPageDir,getFileName(mTerm.getValue()));

            JSONArray jsonArray = jsonObject.getJSONArray(JSON_CLASSES);
            for (int i = 0; i < jsonArray.length(); i++) {
                WPIClass c = WPIClass.fromJSON(jsonArray.getJSONObject(i));
                classes.add(c);
            }
            mRemind = jsonObject.getBoolean(JSON_REMIND);
            mTerm = Utils.TermValue.fromJSON(jsonObject.getJSONObject(JSON_TERM));
        } catch (IOException e) {
            Log.e(TAG, "Error opening file!");
        } catch (JSONException e) {
            Log.e(TAG, "Error reading file!", e);
        }
        mClasses = classes;
    }

    /**
     * Gets a list of WPI class object that this page is holding.
     *
     * @return a list of WPI class object
     */
    public ArrayList<WPIClass> getClasses() {
        return mClasses;
    }

    /**
     * Determines whether this class is set to be reminded or not.
     *
     * @return <code>true</code> if this class will be reminded
     */
    public boolean isReminded() {
        return mRemind;
    }

    /**
     * Sets if this class will be reminded in the future.
     *
     * @param remind <code>true</code> if this class will be reminded
     */
    public void setRemind(boolean remind) {
        mRemind = remind;
    }

    /**
     * Reloads data and saves locally when finishes.
     *
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out If connection timed out
     * @throws NullPointerException
     */
    public void reload() throws IOException {
        ArrayList<WPIClass> classes = new ArrayList<>();

        selectTerm();

        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_CLASSES, REGISTRATION);
        Document doc = Jsoup.parse(html);

        if (doc.title().contains("Select Term")) {
            throw new IOException("Select Term page is received instead!");
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
    }

    private void selectTerm() throws IOException {
        ConnectionManager cm = ConnectionManager.getInstance();
        cm.getPage(SELECT_TERM, VIEW_TERM, "term_in=" + mTerm.getValue());
    }

    private ArrayList<WPIClass.Schedule> parseSchedule(Table time) {
        SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a", Locale.getDefault());
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        ArrayList<WPIClass.Schedule> schedules = new ArrayList<>();

        for (int i = 1; i < time.size(); i++) {
            try {
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
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing schedule from table", e);
            }
        }

        return schedules;
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
            jsonObject.put(JSON_TERM, mTerm.toJSON());
            JSONSerializer
                    .saveJSONToFile(mContext, mClassesPageDir, getFileName(mTerm.getValue()), jsonObject);
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Cannot save offline data", e);
        }
    }

}
