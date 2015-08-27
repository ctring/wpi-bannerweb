package com.cuongnd.wpibannerweb.classes;

import android.animation.Animator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Cuong Nguyen
 */
public class ClassesDetailFragment extends Fragment {

    private static final String TAG = ClassesDetailFragment.class.getSimpleName();
    public static final String EXTRA_WPI_CLASS = "WpiClass";

    public static ClassesDetailFragment newInstance(String wpiClass) {
        Bundle args = new Bundle();
        args.putString(EXTRA_WPI_CLASS, wpiClass);
        ClassesDetailFragment fragment = new ClassesDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTextClassName;
    private TextView mTextInstructor;
    private TextView mTextSection;
    private TextView mTextCrn;
    private LinearLayout mListSchedule;

    private WPIClass mWPIClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        try {
            JSONObject jsonObj = new JSONObject(getArguments().getString(EXTRA_WPI_CLASS));
            mWPIClass = WPIClass.fromJSON(jsonObj);
        } catch (JSONException e) {
            Utils.showLongToast(getActivity(), getString(R.string.error_occurred));
            Log.e(TAG, "Cannot parse WPI class string!", e);
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_classes_detail, container, false);

        mTextClassName = (TextView) v.findViewById(R.id.text_class_name);
        mTextInstructor = (TextView) v.findViewById(R.id.text_instructor);
        mTextSection = (TextView) v.findViewById(R.id.text_section);
        mTextCrn = (TextView) v.findViewById(R.id.text_crn);
        mListSchedule = (LinearLayout) v.findViewById(R.id.list_schedule);

        if (mWPIClass != null) {
            mTextClassName.setText(String.format(Utils.CLASS_FULL_TITLE,
                    mWPIClass.getCode(),
                    mWPIClass.getName()));
            mTextInstructor.setText(mWPIClass.getInstructor());
            mTextSection.setText(mWPIClass.getSection());
            mTextCrn.setText(mWPIClass.getCRN());

            for (WPIClass.Schedule schedule : mWPIClass.getSchedules()) {
                addScheduleView(inflater, mListSchedule, schedule);
            }
        }

        return v;
    }

    private void addScheduleView(LayoutInflater inflater, ViewGroup container, WPIClass.Schedule schedule) {
        View v = inflater.inflate(R.layout.fragment_classes_detail_schedule, container, false);

        TextView textType = (TextView) v.findViewById(R.id.text_type);
        TextView textTime = (TextView) v.findViewById(R.id.text_time);
        TextView textDays = (TextView) v.findViewById(R.id.text_days);
        TextView textLocation = (TextView) v.findViewById(R.id.text_location);
        TextView textInstructor = (TextView) v.findViewById(R.id.text_instructor);
        TextView textDateRange = (TextView) v.findViewById(R.id.text_date_range);

        textType.setText(schedule.getType());
        textTime.setText(String.format(Utils.CLASS_FULL_TITLE,
                Utils.formatTime(schedule.getStartTime(), Utils.TIME_FORMAT),
                Utils.formatTime(schedule.getEndTime(), Utils.TIME_FORMAT)));
        textDays.setText(formatDays(schedule.getDays()));
        textLocation.setText(schedule.getLocation());
        textInstructor.setText(schedule.getInstructor());
        textDateRange.setText(String.format(Utils.CLASS_FULL_TITLE,
                Utils.formatTime(schedule.getStartDate(), Utils.DATE_FORMAT),
                Utils.formatTime(schedule.getEndDate(), Utils.DATE_FORMAT)));

        container.addView(v);
    }



    private static String formatDays(int[] days) {
        String[] wpiDays = {"?", "?", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String strDays = "";
        for (int i = 0; i < days.length - 1; i++) {
            strDays += wpiDays[days[i]] + "  |  ";
        }
        strDays += wpiDays[days[days.length - 1]];
        return strDays;
    }


}
