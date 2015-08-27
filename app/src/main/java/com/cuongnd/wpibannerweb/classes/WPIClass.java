package com.cuongnd.wpibannerweb.classes;

import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Represents a class at WPI. The most crucial information for a WPI class is:
 * <ul>
 *     <li>Class name (for example: introduction to program design)</li>
 *     <li>Class code (for example: CS 1021)</li>
 *     <li>Section (for example: A01)</li>
 *     <li>Class CRN (for example: 12345) </li>
 *     <li>Instructor</li>
 *     <li>Schedules</li>
 * </ul>
 * <p>
 * Each class contains a list of schedules for lectures, labs or conferences. A schedule model
 * is represented by the class {@link com.cuongnd.wpibannerweb.classes.WPIClass.Schedule}.
 *
 * Two classes are compared by their starting date (assuming every schedule has the same starting date).
 *
 * @author Cuong Nguyen
 */
public class WPIClass implements Comparable<WPIClass> {

    public static final String JSON_NAME = "name";
    public static final String JSON_CODE = "code";
    public static final String JSON_SECTION = "section";
    public static final String JSON_INSTRUCTOR = "instructor";
    public static final String JSON_CRN = "crn";
    public static final String JSON_SCHEDULES = "schedules";

    private String mName;
    private String mCode;
    private String mSection;
    private String mInstructor;
    private String mCRN;
    private ArrayList<Schedule> mSchedules;

    public WPIClass(String name, String code, String section, String CRN,
                    String instructor, ArrayList<Schedule> schedules) {
        mName = name;
        mCode = code;
        mSection = section;
        mInstructor = instructor;
        mCRN = CRN;
        mSchedules = schedules;
    }

    @Override
    public int compareTo(WPIClass wpiClass) {
        if (mSchedules.isEmpty()) {
            if (wpiClass.mSchedules.isEmpty()) {
                return 0;
            }
            return 1;
        }
        if (wpiClass.mSchedules.isEmpty()) {
            return -1;
        }
        Schedule firstThisSchedule = mSchedules.get(0);
        Schedule firstOtherSchedule = wpiClass.mSchedules.get(0);
        //return firstOtherSchedule.getStartDate().compareTo(firstThisSchedule.getStartDate());
        return firstThisSchedule.getStartDate().compareTo(firstOtherSchedule.getStartDate());
    }

    public String toString() {
        String info = String.format("%s - %s - %s\n%s\n%s\n", mName, mCode, mSection, mCRN, mInstructor);
        String schedule = "";
        for (Schedule s : mSchedules) {
            schedule += s.toString() + "\n";
        }
        return info + schedule;
    }

    public static WPIClass fromJSON(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString(JSON_NAME);
        String code = jsonObject.getString(JSON_CODE);
        String section = jsonObject.getString(JSON_SECTION);
        String instructor = jsonObject.getString(JSON_INSTRUCTOR);
        String crn = jsonObject.getString(JSON_CRN);
        ArrayList<Schedule> schedules = new ArrayList<>();
        JSONArray jsonSchedules = jsonObject.getJSONArray(JSON_SCHEDULES);
        for (int i = 0; i < jsonSchedules.length(); i++) {
            schedules.add(Schedule.fromJSON(jsonSchedules.getJSONObject(i)));
        }

        return new WPIClass(name, code, section, crn, instructor, schedules);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray schedules = new JSONArray();
        for (Schedule s : mSchedules) {
            schedules.put(s.toJSON());
        }
        jsonObject.put(JSON_NAME, mName)
                .put(JSON_CODE, mCode)
                .put(JSON_CRN, mCRN)
                .put(JSON_INSTRUCTOR, mInstructor)
                .put(JSON_SECTION, mSection)
                .put(JSON_SCHEDULES, schedules);

        return jsonObject;
    }

    public String getName() {
        return mName;
    }

    public String getSection() {
        return mSection;
    }

    public String getCode() {
        return mCode;
    }

    public String getInstructor() {
        return mInstructor;
    }

    public String getCRN() {
        return mCRN;
    }

    public ArrayList<Schedule> getSchedules() {
        return mSchedules;
    }

    public static class Schedule implements Comparable<Schedule> {
        public static final String JSON_START_TIME = "startTime";
        public static final String JSON_END_TIME = "endTime";
        public static final String JSON_START_DATE = "startDate";
        public static final String JSON_END_DATE = "endDate";
        public static final String JSON_DAYS = "days";
        public static final String JSON_INSTRUCTOR = "instructor";
        public static final String JSON_TYPE = "type";
        public static final String JSON_LOCATION = "location";

        private Calendar mStartTime;
        private Calendar mEndTime;
        private Calendar mStartDate;
        private Calendar mEndDate;
        private int[] mDays;
        private String mInstructor;
        private String mType;
        private String mLocation;
        private Object mTag;

        public String toString() {
            SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a", Locale.getDefault());
            SimpleDateFormat formatDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return String.format("%s - %s | %s | %s | %s - %s | %s | %s",
                    formatTime.format(mStartTime.getTime()),
                    formatTime.format(mEndTime.getTime()),
                    Utils.toWpiDays(mDays),
                    mLocation,
                    formatDate.format(mStartDate.getTime()),
                    formatDate.format(mEndDate.getTime()),
                    mType, mInstructor);
        }

        @Override
        public int compareTo(Schedule schedule) {
            return mStartTime.compareTo(schedule.mStartTime);
        }

        public static Schedule fromJSON(JSONObject jsonObject) throws JSONException {
            Schedule schedule = new Schedule();

            Calendar startTime = Calendar.getInstance();
            startTime.setTimeInMillis(jsonObject.getLong(JSON_START_TIME));

            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(jsonObject.getLong(JSON_END_TIME));

            Calendar startDate = Calendar.getInstance();
            startDate.setTimeInMillis(jsonObject.getLong(JSON_START_DATE));

            Calendar endDate = Calendar.getInstance();
            endDate.setTimeInMillis(jsonObject.getLong(JSON_END_DATE));

            int[] days = Utils.fromWpiDays(jsonObject.getString(JSON_DAYS));

            String instructor = jsonObject.getString(JSON_INSTRUCTOR);

            String type = jsonObject.getString(JSON_TYPE);

            String location = jsonObject.getString(JSON_LOCATION);

            schedule.setStartTime(startTime)
                    .setEndTime(endTime)
                    .setDays(days)
                    .setLocation(location)
                    .setStartDate(startDate)
                    .setEndDate(endDate)
                    .setType(type)
                    .setInstructor(instructor);
            return schedule;
        }

        public JSONObject toJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_START_TIME, mStartTime.getTimeInMillis())
                    .put(JSON_END_TIME, mEndTime.getTimeInMillis())
                    .put(JSON_START_DATE, mStartDate.getTimeInMillis())
                    .put(JSON_END_DATE, mEndDate.getTimeInMillis())
                    .put(JSON_DAYS, Utils.toWpiDays(mDays))
                    .put(JSON_INSTRUCTOR, mInstructor)
                    .put(JSON_TYPE, mType)
                    .put(JSON_LOCATION, mLocation);

            return jsonObject;
        }

        public Schedule setStartTime(Calendar startTime) {
            this.mStartTime = startTime;
            return this;
        }

        public Schedule setEndTime(Calendar endTime) {
            this.mEndTime = endTime;
            return this;
        }

        public Schedule setStartDate(Calendar startDate) {
            this.mStartDate = startDate;
            return this;
        }

        public Schedule setEndDate(Calendar endDate) {
            this.mEndDate = endDate;
            return this;
        }

        public Schedule setDays(int[] dates) {
            this.mDays = dates;
            return this;
        }

        public Schedule setInstructor(String instructor) {
            this.mInstructor = instructor;
            return this;
        }

        public Schedule setType(String type) {
            this.mType = type;
            return this;
        }

        public Schedule setLocation(String location) {
            this.mLocation = location;
            return this;
        }

        public void setTag(Object tag) {
            this.mTag = tag;
        }

        public Calendar getStartTime() {
            return mStartTime;
        }

        public Calendar getEndTime() {
            return mEndTime;
        }

        public Calendar getStartDate() {
            return mStartDate;
        }

        public Calendar getEndDate() {
            return mEndDate;
        }

        public int[] getDays() {
            return mDays;
        }

        public String getInstructor() {
            return mInstructor;
        }

        public String getType() {
            return mType;
        }

        public String getLocation() {
            return mLocation;
        }

        public Object getTag() {
            return mTag;
        }
    }
}
