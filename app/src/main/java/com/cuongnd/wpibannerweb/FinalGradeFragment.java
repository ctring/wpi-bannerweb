package com.cuongnd.wpibannerweb;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.grade.FinalGradePage;
import com.cuongnd.wpibannerweb.helper.Helper;
import com.cuongnd.wpibannerweb.helper.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class FinalGradeFragment extends Fragment {

    private static final String TAG = "FinalGradeFragment";

    private Spinner mSpinnerTerm;
    private ScrollView mScrollGrade;
    private TableLayout mTableCourse;
    private TextView mTextCurrentTerm;
    private TextView mTextCumulative;
    private TextView mTextTransfer;
    private TextView mTextOverall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_finalgrade, container, false);

        mSpinnerTerm = (Spinner) v.findViewById(R.id.spinner_term);
        mScrollGrade = (ScrollView) v.findViewById(R.id.scroll_grade);
        mTableCourse = (TableLayout) v.findViewById(R.id.table_course);
        mTextCurrentTerm = (TextView) v.findViewById(R.id.text_current_term);
        mTextCumulative = (TextView) v.findViewById(R.id.text_cumulative);
        mTextTransfer = (TextView) v.findViewById(R.id.text_transfer);
        mTextOverall = (TextView) v.findViewById(R.id.text_overall);

        Button mButtonGetGrade = (Button) v.findViewById(R.id.button_getGrade);
        mButtonGetGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinalGradePage.TermValue term =
                        (FinalGradePage.TermValue) mSpinnerTerm.getSelectedItem();
                new GetGradeTask().execute(term.getValue());
            }
        });

        new GetTermsTask().execute();

        return v;
    }

    private class GetTermsTask extends AsyncTask<Void, Void, ArrayList<FinalGradePage.TermValue>> {
        @Override
        protected ArrayList<FinalGradePage.TermValue> doInBackground(Void... params) {
            return FinalGradePage.getTerms();
        }

        @Override
        protected void onPostExecute(ArrayList<FinalGradePage.TermValue> termValues) {
            ArrayAdapter<FinalGradePage.TermValue> adapter =
                    new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, termValues);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerTerm.setAdapter(adapter);
        }
    }

    private class GetGradeTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            return FinalGradePage.load(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mScrollGrade.setVisibility(View.VISIBLE);
            try {
                Table course = (Table) result.get(FinalGradePage.JSON_COURSE);
                Table summary = (Table) result.get(FinalGradePage.JSON_SUMMARY);

                mTableCourse.removeAllViews();
                for (int i = 1; i < course.size(); i++) {
                    String courseTitle = String.format("%s (%s %s)",
                            course.get(i, 4), course.get(i, 1), course.get(i, 2));
                    Helper.addRow(mTableCourse, courseTitle, course.get(i, 6));
                }

                mTextCurrentTerm.setText(summary.get(1, 5));
                mTextCumulative.setText(summary.get(2, 5));
                mTextTransfer.setText(summary.get(3, 5));
                mTextOverall.setText(summary.get(4, 5));

            } catch (JSONException e) {
                Helper.logError(TAG, e);
            }
        }


    }

}
