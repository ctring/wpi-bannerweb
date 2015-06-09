package com.cuongnd.wpibannerweb.grade;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.view.ListFragmentSwipeRefreshLayout;
import com.cuongnd.wpibannerweb.helper.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Cuong Nguyen on 5/29/2015.
 */
public class GradeSelectTermFragment extends ListFragment {

    private static final String TAG = "GradeSelectTermFragment";

    private GetTermsTask mGetTermsTask;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mFirstRun = true;
    private ArrayList<Utils.TermValue> mTermValues = new ArrayList<>();

    // TODO: think about empty list
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);

        mSwipeRefreshLayout = new SwipeRefreshLayout(getActivity());

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
            mFirstRun = false;
            refresh();
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
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected ArrayList<Utils.TermValue> doInBackground(Void... params) {
            if (isCancelled())
                return null;
            try {
                return FinalGradePage.getTerms();
            } catch (IOException e) {
                Utils.logError(TAG, e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Utils.TermValue> termValues) {
            try {
                if (termValues == null) {
                    // TODO: notify by toast
                    getActivity().finish();
                    return;
                }
                if (isCancelled()) return;
                mTermValues = termValues;
                updateView();
            } finally {
                mSwipeRefreshLayout.setRefreshing(false);
                mGetTermsTask = null;
            }
        }

        @Override
        protected void onCancelled() {
            mSwipeRefreshLayout.setRefreshing(false);
            mGetTermsTask = null;
        }
    }
}
