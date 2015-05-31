package com.cuongnd.wpibannerweb;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.cuongnd.wpibannerweb.classes.ClassesPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cuong Nguyen on 5/29/2015.
 */
public class ClassesFragment extends Fragment {
    public static final String EXTRA_TERM_ID = "TermId";

    public static ClassesFragment newInstance(String termid) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TERM_ID, termid);

        ClassesFragment fragment = new ClassesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private ViewSwitcher mSwitcher;
    private WeekView mWeekView;

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

        mWeekView = (WeekView) v.findViewById(R.id.weekView);
        mWeekView.setMonthChangeListener(new WeekView.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int i, int i1) {
                return new ArrayList<WeekViewEvent>();
            }
        });
        return v;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
