package com.cuongnd.wpibannerweb.classes;

import com.cuongnd.wpibannerweb.helper.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Cuong Nguyen on 6/1/2015.
 */
public class WPIClass {
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

    public String toString() {
        String info = String.format("%s - %s - %s\n%s\n%s\n", mName, mCode, mSection, mCRN, mInstructor);
        String schedule = "";
        for (Schedule s : mSchedules) {
            schedule += s.toString() + "\n";
        }
        return info + schedule;
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

    public static class Schedule {
        private Calendar mStartTime;
        private Calendar mEndTime;
        private Calendar mStartDate;
        private Calendar mEndDate;
        private int[] mDays;
        private String mInstructor;
        private String mType;
        private String mLocation;

        public String toString() {
            SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a", Locale.US);
            SimpleDateFormat formatDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            return String.format("%s - %s | %s | %s | %s - %s | %s | %s",
                    formatTime.format(mStartTime.getTime()),
                    formatTime.format(mEndTime.getTime()),
                    Utils.toWpiDays(mDays),
                    mLocation,
                    formatDate.format(mStartDate.getTime()),
                    formatDate.format(mEndDate.getTime()),
                    mType, mInstructor);
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
    }
}
