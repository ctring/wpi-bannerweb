package com.cuongnd.wpibannerweb.classes;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Cuong Nguyen on 5/29/2015.
 */
public class ClassesFragment extends Fragment implements WeekView.MonthChangeListener {

    private static final String TAG = "ClassesFragment";

    public static final String EXTRA_TERM_ID = "TermId";

    public static ClassesFragment newInstance(String termId) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TERM_ID, termId);

        ClassesFragment fragment = new ClassesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private GetClassesTask mGetClassesTask;
    private ClassesPage mClassesPage;
    private boolean mFirstTime;

    private ViewSwitcher mSwitcher;
    private WeekView mWeekView;
    private TextView mTextClasses;
    private CheckBox mCheckRemind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        String termId = getArguments().getString(EXTRA_TERM_ID);
        mClassesPage = new ClassesPage(getActivity(), termId);
        mFirstTime = true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_classes, container, false);
        mSwitcher = (ViewSwitcher) v.findViewById(R.id.switcher);

        mTextClasses = (TextView) v.findViewById(R.id.text_classes);

        mWeekView = (WeekView) v.findViewById(R.id.weekView);
        mWeekView.setMonthChangeListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
        if (mFirstTime) {
            updateData();
            mFirstTime = false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.classes_fragment_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_view:
                mSwitcher.showNext();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        if (mGetClassesTask != null) {
            mGetClassesTask.cancel(false);
        }
        super.onStop();
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        ArrayList<WPIClass> classes = mClassesPage.getClasses();
        List<WeekViewEvent> events = new ArrayList<>();
        if (classes == null)
            return events;

        for (WPIClass c : classes) {
            String name = c.getCode() + c.getSection();
            ArrayList<WPIClass.Schedule> schedules = c.getSchedules();
            for (WPIClass.Schedule s : schedules) {
                String title = name + " - " + s.getType();
                boolean[] daysOfWeek = new boolean[8];
                for (int d : s.getDays()) daysOfWeek[d] = true;

                Calendar startDate = Calendar.getInstance();
                startDate.set(Calendar.YEAR, newYear);
                startDate.set(Calendar.MONTH, newMonth - 1);
                if (startDate.before(s.getStartDate())) {
                    startDate = s.getStartDate();
                }

                Calendar endDate = Calendar.getInstance();
                endDate.set(Calendar.YEAR, newYear);
                endDate.set(Calendar.MONTH, newMonth - 1);
                endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                if (endDate.after(s.getEndDate())) {
                    endDate = s.getEndDate();
                }

                events.addAll(createWeeklyEvents(title, s.getStartTime(), s.getEndTime(),
                        startDate, endDate, daysOfWeek));
            }
        }

        return events;
    }

    private ArrayList<WeekViewEvent> createWeeklyEvents(String title,
                                                        Calendar startTime, Calendar endTime,
                                                        Calendar startDate, Calendar endDate,
                                                        boolean[] daysOfWeek) {
        ArrayList<WeekViewEvent> events = new ArrayList<>();
        Calendar c = (Calendar) startDate.clone();
        while (!c.after(endDate)) {
            int currentDay = c.get(Calendar.DAY_OF_WEEK);
            if (daysOfWeek[currentDay]) {
                Calendar start = (Calendar) c.clone();
                start.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                start.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                Calendar end = (Calendar) c.clone();
                end.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
                end.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
                // TODO: deal with id so that it can be used in clicking event
                WeekViewEvent event = new WeekViewEvent(1, title, start, end);
                // TODO: choose color for event
                event.setColor(getResources().getColor(R.color.event_color_01));
                events.add(event);
            }
            c.add(Calendar.DATE, 1);
        }
        return events;
    }

    private void updateData() {
        if (mGetClassesTask == null) {
            mGetClassesTask = new GetClassesTask();
            mGetClassesTask.execute();
        }
    }

    private void updateView() {
        ArrayList<WPIClass> classes = mClassesPage.getClasses();
        if (classes == null)
            return;

        String text = "";
        for (WPIClass c : classes)
            text += c.toString() + "\n";
        mTextClasses.setText(text);
    }

    private class GetClassesTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return !isCancelled() && mClassesPage.refresh();
            } catch (IOException e) {
                Utils.logError(TAG, e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mGetClassesTask = null;
            if (!success) return;

            updateView();
        }

        @Override
        protected void onCancelled() {
            mGetClassesTask = null;
        }
    }
}
