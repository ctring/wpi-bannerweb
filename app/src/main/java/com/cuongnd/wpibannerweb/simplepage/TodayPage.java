package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.classes.ClassesDetailActivity;
import com.cuongnd.wpibannerweb.classes.ClassesDetailFragment;
import com.cuongnd.wpibannerweb.classes.ClassesPage;
import com.cuongnd.wpibannerweb.classes.WPIClass;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

/**
 * Manages the today page.
 *
 * @author Cuong Nguyen
 */
public class TodayPage extends SimplePage {

    public static final String PAGE_NAME = TodayPage.class.getSimpleName();

    private ArrayList<WPIClass.Schedule> mTodayEvents = new ArrayList<>();

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_dashboard_card_today;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public boolean dataLoaded() {
        return true;
    }

    @Override
    public void parse(String html) {
    }

    /**
     * Reloads this page using local data of classes.
     *
     * @param context Context for loading the data locally
     */
    @Override
    public void load(Context context) throws IOException {
        ArrayList<Utils.TermValue> terms = ClassesPage.getTerms(context);
        ArrayList<WPIClass> classes = new ArrayList<>();
        for (Utils.TermValue term : terms) {
            ClassesPage newPage = new ClassesPage(context, term);
            newPage.reload();
            classes.addAll(newPage.getClasses());
        }

        findTodayClasses(classes);
    }

    /**
     * Loads offline classes data from JSON file if exists.
     *
     * @param context the Context of the application
     */
    @Override
    public void loadFromLocal(Context context) {
        ArrayList<Utils.TermValue> offlineTerms = ClassesPage.getOfflineTerms(context);
        ArrayList<WPIClass> offlineClasses = new ArrayList<>();
        for (Utils.TermValue term : offlineTerms) {
            ClassesPage newPage = new ClassesPage(context, term);
            offlineClasses.addAll(newPage.getClasses());
        }

        findTodayClasses(offlineClasses);
    }

    private void findTodayClasses(ArrayList<WPIClass> classes) {
        Calendar today = Calendar.getInstance();
        mTodayEvents.clear();
        for (WPIClass c : classes) {
            ArrayList<WPIClass.Schedule> schedules = c.getSchedules();
            if (schedules.isEmpty()) {
                continue;
            }
            Calendar startDate = schedules.get(0).getStartDate();

            Calendar endDate = schedules.get(0).getEndDate();
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);
            endDate.set(Calendar.MILLISECOND, 999);

            if ((today.after(startDate) || today.equals(startDate))
                    && (today.before(endDate) || today.equals(endDate))) {
                int todayOfWeek = today.get(Calendar.DAY_OF_WEEK);
                for (WPIClass.Schedule s : schedules) {
                    boolean isToday = false;
                    int[] days = s.getDays();
                    for (int d : days) {
                        if (d == todayOfWeek) {
                            isToday = true;
                            break;
                        }
                    }

                    if (isToday) {
                        try {
                            s.setTag(c.toJSON());
                            mTodayEvents.add(s);
                        } catch (JSONException e) {
                            Log.e(PAGE_NAME, "Exception occurred", e);
                        }
                    }
                }
            }
        }
        Collections.sort(mTodayEvents);
    }

    /**
     * Updates the view hierarchy that displays the Today page.
     *
     * @param context the Context of the application
     * @param v the view hierarchy to be updated.
     */
    @Override
    public void updateView(final Context context, View v) {
        try {
            TextView noClass = (TextView) v.findViewById(R.id.text_no_class);
            TableLayout tableView = (TableLayout) v
                    .findViewById(R.id.table_today);

            while (tableView.getChildCount() > 1) {
                tableView.removeViewAt(tableView.getChildCount() - 1);
            }

            if (mTodayEvents.isEmpty()) {
                noClass.setVisibility(View.VISIBLE);
                return;
            }
            noClass.setVisibility(View.GONE);

            for (int i = 0; i < mTodayEvents.size(); i++)
                addRow(context, tableView);

            for (int i = 0; i < mTodayEvents.size(); i++) {
                try {
                    TableRow row = (TableRow) tableView.getChildAt(i + 1);

                    TextView textName = (TextView) row.getChildAt(0);
                    TextView textTime = (TextView) row.getChildAt(1);
                    TextView textWhere = (TextView) row.getChildAt(2);

                    final WPIClass.Schedule s = mTodayEvents.get(i);

                    JSONObject temp = (JSONObject) s.getTag();
                    // String name = temp.getString(WPIClass.JSON_NAME);
                    String code = temp.getString(WPIClass.JSON_CODE);
                    textName.setText(code);

                    String time = String.format("%s - %s",
                            Utils.formatTime(s.getStartTime(), Utils.TIME_FORMAT),
                            Utils.formatTime(s.getEndTime(), Utils.TIME_FORMAT));
                    textTime.setText(time);

                    textWhere.setText(s.getLocation());

                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, ClassesDetailActivity.class);
                            JSONObject data = (JSONObject) s.getTag();
                            i.putExtra(ClassesDetailFragment.EXTRA_WPI_CLASS, data.toString());
                            context.startActivity(i);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(PAGE_NAME, "Error retrieving class ", e);
                }
            }
        } catch (NullPointerException e) {
            Log.e(PAGE_NAME, "Cannot update view!", e);
        }
    }

    private void addRow(Context context, TableLayout table) {
        TableRow row = new TableRow(context);

        TableRow.LayoutParams paramsClass =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.20f);
        paramsClass.setMargins(6, 6, 0, 6);
        TextView textClass = new TextView(context);
        textClass.setLayoutParams(paramsClass);

        TableRow.LayoutParams paramsTime =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.45f);
        paramsTime.setMargins(0, 6, 0, 6);
        TextView textTime = new TextView(context);
        textTime.setLayoutParams(paramsTime);
        textTime.setGravity(Gravity.CENTER);

        TableRow.LayoutParams paramsWhere =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.35f);
        paramsWhere.setMargins(12, 6, 0, 6);
        TextView textWhere = new TextView(context);
        textWhere.setLayoutParams(paramsWhere);

        row.addView(textClass);
        row.addView(textTime);
        row.addView(textWhere);

        table.addView(row);
    }
}
