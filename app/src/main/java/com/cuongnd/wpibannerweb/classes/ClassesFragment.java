package com.cuongnd.wpibannerweb.classes;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.util.TypedValue;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Cuong Nguyen
 */
public class ClassesFragment extends Fragment implements WeekView.MonthChangeListener {

    private static final String TAG = ClassesFragment.class.getSimpleName();

    public static final String EXTRA_TERM_ID = "TermId";
    public static final String EXTRA_TERM_NAME = "TermName";

    private static final int WEEK_VIEW_DAY_VIEW = 1;
    private static final int WEEK_VIEW_THREE_DAY_VIEW = 3;
    private static final int WEEK_VIEW_WEEK_VIEW = 7;

    public static ClassesFragment newInstance(String termId, String termName) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TERM_ID, termId);
        args.putString(EXTRA_TERM_NAME, termName);

        ClassesFragment fragment = new ClassesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private GetClassesTask mGetClassesTask;
    private ClassesPage mClassesPage;
    private boolean mFirstTime = true;
    private boolean mCalendarMode = false;
    private int mWeekViewView = WEEK_VIEW_THREE_DAY_VIEW;

    private ViewSwitcher mSwitcher;
    private RecyclerView mRecyclerClasses;
    private SwipeRefreshLayout mSwipeRefresh;
    private WeekView mWeekView;
    private Parcelable mRecyclerState;

    private Calendar mFirstVisibleDay;
    private double mFirstVisibleHour;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

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

        mSwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_classes);
        mSwipeRefresh.setColorSchemeResources(R.color.accent_color, android.R.color.black);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mWeekView = (WeekView) v.findViewById(R.id.weekView);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent weekViewEvent, RectF rectF) {
                int id = (int) weekViewEvent.getId();
                WPIClass c = mClassesPage.getClasses().get(id);
                startDetailActivity(c);
            }
        });

        mRecyclerClasses = (RecyclerView) v.findViewById(R.id.recycler_classes);
        mRecyclerClasses.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    private void startDetailActivity(WPIClass c)  {
        try {
            Intent i = new Intent(getActivity(), ClassesDetailActivity.class);
            JSONObject data = c.toJSON();
            i.putExtra(ClassesDetailFragment.EXTRA_WPI_CLASS, data.toString());
            startActivity(i);
        } catch (JSONException e) {
            Log.e(TAG, "Exception occurred", e);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String termId = getArguments().getString(EXTRA_TERM_ID);
        String termName = getArguments().getString(EXTRA_TERM_NAME);
        Utils.TermValue term = new Utils.TermValue(termId, termName);
        mClassesPage = new ClassesPage(getActivity(), term);

        if (getActivity() != null) {
            ActionBar ab = getActivity().getActionBar();
            if (ab != null) ab.setTitle(termName);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        switchToCalendar(mCalendarMode);

        setNumberOfVisibleDays(mWeekViewView);

        if (mFirstVisibleDay != null) {
            mWeekView.goToDate(mFirstVisibleDay);
            mWeekView.goToHour(mFirstVisibleHour);
        }

        updateView();
        if (mFirstTime) {
            refresh();
        }
    }

    private void updateView() {
        ArrayList<WPIClass> classes = mClassesPage.getClasses();
        if (classes == null)
            return;

        ClassesAdapter adapter = new ClassesAdapter(classes);
        mRecyclerClasses.setAdapter(adapter);
        if (mRecyclerState != null)
            mRecyclerClasses.getLayoutManager().onRestoreInstanceState(mRecyclerState);

        mWeekView.invalidate();
    }

    private void refresh() {
        if (mGetClassesTask == null) {
            mGetClassesTask = new GetClassesTask();
            mGetClassesTask.execute();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.classes_fragment_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.action_switch_to_calendar).setVisible(!mCalendarMode);

        menu.setGroupVisible(R.id.action_group_calendar_view, mCalendarMode);
        menu.findItem(R.id.action_switch_to_detail).setVisible(mCalendarMode);
        menu.findItem(R.id.action_go_to).setVisible(mCalendarMode);
        menu.findItem(R.id.action_go_to_today).setVisible(mCalendarMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_to_calendar:
                mCalendarMode = true;

                switchToCalendar(true);

                Utils.stopRefreshing(mSwipeRefresh);
                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.action_switch_to_detail:
                mCalendarMode = false;

                switchToCalendar(false);

                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.action_go_to_today:
                mWeekView.goToToday();
                return true;

            case R.id.action_go_to:
                DatePickerFragment.newInstance(mWeekView.getFirstVisibleDay().getTimeInMillis(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(year, monthOfYear, dayOfMonth, 6, 0);
                                mWeekView.goToDate(cal);
                            }
                        }).show(getActivity().getFragmentManager(), "DatePicker");
                return true;

            case R.id.action_day_view:
                if (mWeekViewView != WEEK_VIEW_DAY_VIEW) {
                    item.setChecked(true);
                    mWeekViewView = WEEK_VIEW_DAY_VIEW;

                    setNumberOfVisibleDays(WEEK_VIEW_DAY_VIEW);

                   changeWeekViewDimensions(12, 12);
                }
                return true;

            case R.id.action_three_day_view:
                if (mWeekViewView != WEEK_VIEW_THREE_DAY_VIEW) {
                    item.setChecked(true);
                    mWeekViewView = WEEK_VIEW_THREE_DAY_VIEW;

                    setNumberOfVisibleDays(WEEK_VIEW_THREE_DAY_VIEW);

                    changeWeekViewDimensions(12, 12);
                }
                return true;

            case R.id.action_week_view:
                if (mWeekViewView != WEEK_VIEW_WEEK_VIEW) {
                    item.setChecked(true);
                    mWeekViewView = WEEK_VIEW_WEEK_VIEW;

                    setNumberOfVisibleDays(WEEK_VIEW_WEEK_VIEW);

                    changeWeekViewDimensions(10, 10);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setNumberOfVisibleDays(int numberOfVisibleDays) {

        Calendar firstVisibleDay = mWeekView.getFirstVisibleDay();
        double firstVisibleHour = mWeekView.getFirstVisibleHour();

        mWeekView.setNumberOfVisibleDays(numberOfVisibleDays);
        if (numberOfVisibleDays > 5) {
            mWeekView.setDateTimeInterpreter(new ShortDateTimeInterpreter());
        } else {
            mWeekView.setDateTimeInterpreter(new LongDateTimeInterpreter());
        }

        if (firstVisibleDay != null) {
            mWeekView.goToDate(firstVisibleDay);
            mWeekView.goToHour(firstVisibleHour);
        }
    }

    private void switchToCalendar(boolean toCalendar) {
        View v = mSwitcher.getNextView();
        if (toCalendar) {
            if (v.getTag().equals("calendar")) {
                mSwitcher.showNext();
            }
        } else {
            if (v.getTag().equals("list"))
                mSwitcher.showNext();
        }
    }

    private void changeWeekViewDimensions(int textSize, int eventTextSize) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mWeekView.setTextSize(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, metrics));
        mWeekView.setEventTextSize(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, eventTextSize, metrics));
    }

    @Override
    public void onStop() {
        mRecyclerState = mRecyclerClasses.getLayoutManager().onSaveInstanceState();
        mFirstVisibleDay = mWeekView.getFirstVisibleDay();
        mFirstVisibleHour = mWeekView.getFirstVisibleHour();
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

        for (int id = 0; id < classes.size(); id++) {
            WPIClass c = classes.get(id);
            String name = c.getCode();
            ArrayList<WPIClass.Schedule> schedules = c.getSchedules();

            for (WPIClass.Schedule s : schedules) {
                String title = name + " - " + s.getType();
                boolean[] daysOfWeek = new boolean[8];
                for (int d : s.getDays()) daysOfWeek[d] = true;

                Calendar startDate = Calendar.getInstance();
                startDate.set(Calendar.YEAR, newYear);
                startDate.set(Calendar.MONTH, newMonth - 1);
                startDate.set(Calendar.DAY_OF_MONTH, 1);
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

                events.addAll(createWeeklyEvents(id, title, s.getStartTime(), s.getEndTime(),
                        startDate, endDate, daysOfWeek));
            }
        }

        return events;
    }

    private ArrayList<WeekViewEvent> createWeeklyEvents(long id, String title,
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
                WeekViewEvent event = new WeekViewEvent(id, title, start, end);
                event.setColor(getResources().getColor(R.color.event_color_03));
                events.add(event);
            }
            c.add(Calendar.DATE, 1);
        }
        return events;
    }

    private String interpretTimeHelper(int hour) {
        String amPm;
        if (hour >= 0 && hour < 12) amPm = "AM";
        else amPm = "PM";
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;
        return String.format("%02d %s", hour, amPm);
    }

    private class ShortDateTimeInterpreter implements DateTimeInterpreter {
        @Override
        public String interpretDate(Calendar date) {
            char dow = 'X';
            switch (date.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY: dow = 'M'; break;
                case Calendar.TUESDAY: dow = 'T'; break;
                case Calendar.WEDNESDAY: dow = 'W'; break;
                case Calendar.THURSDAY: dow = 'R'; break;
                case Calendar.FRIDAY: dow = 'F'; break;
                case Calendar.SATURDAY: dow = 'A'; break;
                case Calendar.SUNDAY: dow = 'S'; break;
            }
            return String.format("%c %d/%02d", dow,
                    date.get(Calendar.MONTH) + 1,
                    date.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public String interpretTime(int hour) {
            return interpretTimeHelper(hour);
        }
    }

    private class LongDateTimeInterpreter implements DateTimeInterpreter {
        @Override
        public String interpretDate(Calendar date) {
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("EEE", Locale.getDefault());
            String dayName = sdf.format(date.getTime()).toUpperCase();
            return String.format("%s %d/%02d", dayName,
                    date.get(Calendar.MONTH) + 1,
                    date.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public String interpretTime(int hour) {
            return interpretTimeHelper(hour);
        }
    }

    private class GetClassesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Utils.startRefreshing(mSwipeRefresh);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (!isCancelled())
                    mClassesPage.reload();
            } catch (SocketTimeoutException e) {
                Utils.showShortToast(getActivity(), getString(R.string.error_connection_timed_out));
                Log.e(TAG, getString(R.string.error_connection_timed_out), e);
            } catch (IOException e) {
                Utils.showShortToast(getActivity(),
                        getString(R.string.error_connection_problem_occurred));
                Log.e(TAG, getString(R.string.error_connection_problem_occurred), e);
            } catch (NullPointerException e) {
                Utils.showShortToast(getActivity(),
                        getString(R.string.error_no_data_received));
                Log.e(TAG, getString(R.string.error_no_data_received), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            try {
                if (!isCancelled()) {
                    updateView();
                    mFirstTime = false;
                }
            } finally {
                Utils.stopRefreshing(mSwipeRefresh);
                mGetClassesTask = null;
            }
        }

        @Override
        protected void onCancelled() {
            Utils.stopRefreshing(mSwipeRefresh);
            mGetClassesTask = null;
        }
    }

    private class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassesViewHolder> {

        class ClassesViewHolder extends RecyclerView.ViewHolder {

            TextView textClassName;
            TextView textInstructorSection;
            TextView textCrn;
            TextView textShowSchedule;

            public ClassesViewHolder(View itemView) {
                super(itemView);
                textClassName = (TextView) itemView.findViewById(R.id.text_class_name);
                textInstructorSection = (TextView) itemView.findViewById(R.id.text_instructor_section);
                textCrn = (TextView) itemView.findViewById(R.id.text_crn);
                textShowSchedule = (TextView) itemView.findViewById(R.id.text_show_schedule);
            }
        }

        private ArrayList<WPIClass> mClasses;

        public ClassesAdapter(ArrayList<WPIClass> classes) {
            mClasses = classes;
        }

        @Override
        public ClassesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_classes_card,
                    parent, false);
            return new ClassesViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ClassesViewHolder holder, int position) {
            final WPIClass c = mClasses.get(position);
            holder.textClassName.setText(String.format("%s - %s", c.getCode(), c.getName()));
            holder.textInstructorSection.setText(String.format("%s  |  %s", c.getInstructor(),
                    c.getSection()));
            holder.textCrn.setText(String.format("CRN %s", c.getCRN()));
            holder.textShowSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDetailActivity(c);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mClasses.size();
        }
    }
}
