package com.cuongnd.wpibannerweb.grade;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Utils;
import com.cuongnd.wpibannerweb.helper.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * @author Cuong Nguyen
 */
public class FinalGradeFragment extends Fragment {

    private static final String TAG = FinalGradeFragment.class.getSimpleName();

    public static final String EXTRA_TERM_ID = "termId";
    public static final String EXTRA_TERM_NAME = "termName";

    public static FinalGradeFragment newInstance(String termId, String termName) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TERM_ID, termId);
        args.putString(EXTRA_TERM_NAME, termName);

        FinalGradeFragment fragment = new FinalGradeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private GetGradeTask mGetGradeTask;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TableLayout mTableCourse;
    private TextView mTextCurrentTerm;
    private TextView mTextCumulative;
    private TextView mTextTransfer;
    private TextView mTextOverall;

    private String mTermId;
    private JSONObject mResult;
    private boolean mFirstRun;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFirstRun = true;
        mResult = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_finalgrade, container, false);

        mTableCourse = (TableLayout) v.findViewById(R.id.finalgrade_table_grades);
        mTextCurrentTerm = (TextView) v.findViewById(R.id.text_current_term);
        mTextCumulative = (TextView) v.findViewById(R.id.text_cumulative);
        mTextTransfer = (TextView) v.findViewById(R.id.text_transfer);
        mTextOverall = (TextView) v.findViewById(R.id.text_overall);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_grade);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent_color, android.R.color.black);

        mTermId = getArguments().getString(EXTRA_TERM_ID);

        String termName = getArguments().getString(EXTRA_TERM_NAME);
        if (getActivity() != null) {
            ActionBar ab = getActivity().getActionBar();
            if (ab != null) ab.setTitle("Final grade - " + termName);
        }

        updateView();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFirstRun) {
            refresh();
        }
    }

    private void refresh() {
        if (mGetGradeTask == null) {
            mGetGradeTask = new GetGradeTask();
            mGetGradeTask.execute(mTermId);
        }
    }

    @Override
    public void onDestroy() {
        if (mGetGradeTask != null) {
            mGetGradeTask.cancel(false);
            mGetGradeTask = null;
        }
        super.onDestroy();
    }

    private void updateView() {
        if (mResult == null) return;
        try {
            Table course = new Table(mResult.getJSONArray(FinalGradePage.JSON_COURSE));
            Table summary = new Table(mResult.getJSONArray(FinalGradePage.JSON_SUMMARY));

            mTableCourse.removeAllViews();

            LayoutInflater inflater = getActivity().getLayoutInflater();

            for (int i = 1; i < course.size(); i++) {
                String courseTitle = String.format("%s %s - %s",
                        course.get(i, 1), course.get(i, 2), course.get(i, 4));
                String grade = course.get(i, 6);

                TableRow newRow = (TableRow) inflater.inflate(R.layout.fragment_finalgrade_row,
                        mTableCourse, false);
                TextView textCourseName = (TextView) newRow.findViewById(R.id.finalgrade_course_name);
                TextView textGrade = (TextView) newRow.findViewById(R.id.finalgrade_grade);

                textCourseName.setText(courseTitle);
                textGrade.setText(grade);
                textGrade.setBackgroundResource(chooseColorByGrade(grade));

                mTableCourse.addView(newRow);
            }

            mTextCurrentTerm.setText(summary.get(1, 5));
            mTextCumulative.setText(summary.get(2, 5));
            mTextTransfer.setText(summary.get(3, 5));
            mTextOverall.setText(summary.get(4, 5));

        } catch (JSONException | NullPointerException e) {
            Log.e(TAG, "Cannot read data from result!");
        }
    }

    private int chooseColorByGrade(String grade) {
        switch (grade) {
            case "A": return R.color.grade_A_color;
            case "B": return R.color.grade_B_color;
            case "C": return R.color.grade_C_color;
            default:
                return R.color.grade_A_color;
        }
    }

    private class GetGradeTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            Utils.startRefreshing(mSwipeRefreshLayout);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            if (isCancelled())
                return null;
            try {
                return FinalGradePage.loadData(params[0]);
            } catch (SocketTimeoutException e) {
                Utils.showShortToast(getActivity(),
                      getString(R.string.error_connection_timed_out));
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
        protected void onPostExecute(JSONObject result) {
            try {
                if (result != null && !isCancelled()) {
                    mResult = result;
                    updateView();
                    mFirstRun = false;
                }
            } finally {
                Utils.stopRefreshing(mSwipeRefreshLayout);
                mGetGradeTask = null;
            }
        }

        @Override
        protected void onCancelled() {
            Utils.stopRefreshing(mSwipeRefreshLayout);
            mGetGradeTask = null;
        }
    }

}
