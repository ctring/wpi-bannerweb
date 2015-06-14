package com.cuongnd.wpibannerweb.grade;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.view.ListFragmentSwipeRefreshLayout;
import com.cuongnd.wpibannerweb.helper.Utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * @author Cuong Nguyen
 */
public class GradeSelectTermFragment extends ListFragment {

    private static final String TAG = GradeSelectTermFragment.class.getSimpleName();

    private GetTermsTask mGetTermsTask;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mFirstRun = true;
    private ArrayList<Utils.TermValue> mTermValues = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);

        mSwipeRefreshLayout = new SwipeRefreshLayout(getActivity());

        if (listFragmentView == null)
            return mSwipeRefreshLayout;

        mSwipeRefreshLayout.addView(listFragmentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeRefreshLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent_color, android.R.color.black);

        return mSwipeRefreshLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
        if (mFirstRun) {
            refresh();
        }
    }

    void updateView() {
        ArrayAdapter<Utils.TermValue> adapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, mTermValues);
        setListAdapter(adapter);
    }

    void refresh() {
        if (mGetTermsTask == null) {
            mGetTermsTask = new GetTermsTask();
            mGetTermsTask.execute();
        }
    }

    @Override
    public void onStop() {
        if (mGetTermsTask != null) {
            mGetTermsTask.cancel(false);
            mGetTermsTask = null;
        }
        super.onStop();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Utils.TermValue selectedTerm = (Utils.TermValue) getListAdapter().getItem(position);
        Intent i = new Intent(getActivity(), FinalGradeActivity.class);
        i.putExtra(FinalGradeFragment.EXTRA_TERM_ID, selectedTerm.getValue());
        i.putExtra(FinalGradeFragment.EXTRA_TERM_NAME, selectedTerm.toString());
        startActivity(i);
    }

    private class GetTermsTask extends AsyncTask<Void, Void, ArrayList<Utils.TermValue>> {

        @Override
        protected void onPreExecute() {
            Utils.startRefreshing(mSwipeRefreshLayout);
        }

        @Override
        protected ArrayList<Utils.TermValue> doInBackground(Void... params) {
            try {

                if (!isCancelled())
                    return FinalGradePage.getTerms();

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
        protected void onPostExecute(ArrayList<Utils.TermValue> termValues) {
            try {
                if (termValues != null && !isCancelled()) {
                    mTermValues = termValues;
                    updateView();
                    mFirstRun = false;
                }
            } finally {
                Utils.stopRefreshing(mSwipeRefreshLayout);
                mGetTermsTask = null;
            }
        }

        @Override
        protected void onCancelled() {
            Utils.stopRefreshing(mSwipeRefreshLayout);
            mGetTermsTask = null;
        }
    }
}
