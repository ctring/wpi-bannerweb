package com.cuongnd.wpibannerweb;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.TextView;

import com.cuongnd.wpibannerweb.classes.ClassesSelectTermActivity;
import com.cuongnd.wpibannerweb.grade.GradeSelectTermActivity;
import com.cuongnd.wpibannerweb.simplepage.AdvisorPage;
import com.cuongnd.wpibannerweb.simplepage.CardBalancePage;
import com.cuongnd.wpibannerweb.simplepage.MailboxPage;
import com.cuongnd.wpibannerweb.simplepage.SimplePageManager;


/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class DashboardFragment extends Fragment {

    public static final String EXTRA_USERNAME = "username";

    private ImageView mImageProfile;
    private CardView mCardAdvisor;
    private CardView mCardMailbox;
    private CardView mCardCardbalance;
    private SwipeRefreshLayout mSwipeRefresh ;

    private volatile int mTaskCounter;
    private SimplePageManager mSimplePageManager;
    private boolean mFirstRun;

    private GetContentTask mGetMailbox;
    private GetContentTask mGetCardBalance;
    private GetContentTask mGetAdvisor;

    public static DashboardFragment newInstance(String username) {
        Bundle args = new Bundle();
        args.putString(EXTRA_USERNAME, username);

        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sm = SessionManager.getInstance(getActivity().getApplicationContext());
        sm.checkStatus();

        setRetainInstance(true);

        mSimplePageManager = SimplePageManager.getInstance(getActivity());
        mTaskCounter = 0;
        mFirstRun = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mImageProfile = (ImageView) v.findViewById(R.id.image_profile);
        mCardAdvisor = (CardView) v.findViewById(R.id.cv_advisor);
        mCardMailbox = (CardView) v.findViewById(R.id.cv_mailbox);
        mCardCardbalance = (CardView) v.findViewById(R.id.cv_cardbalance);

        TextView textName = (TextView) v.findViewById(R.id.text_name);
        textName.setText(SessionManager.getInstance(getActivity().getApplicationContext())
                .getUserName());

        Button buttonGrade = (Button) v.findViewById(R.id.button_grade);
        buttonGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GradeSelectTermActivity.class);
                startActivity(i);
            }
        });

        Button buttonClasses = (Button) v.findViewById(R.id.button_classes);
        buttonClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClassesSelectTermActivity.class);
                startActivity(i);
            }
        });

        mSwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mSimplePageManager.updateView(CardBalancePage.PAGE_NAME, mCardCardbalance);
        mSimplePageManager.updateView(AdvisorPage.PAGE_NAME, mCardAdvisor);
        mSimplePageManager.updateView(MailboxPage.PAGE_NAME, mCardMailbox);
        if (mFirstRun) {
            mFirstRun = false;
            refresh();
        }
    }

    private void refresh() {
        mGetCardBalance = new GetContentTask(CardBalancePage.PAGE_NAME, mCardCardbalance);
        mGetCardBalance.execute();
        mGetAdvisor = new GetContentTask(AdvisorPage.PAGE_NAME, mCardAdvisor);
        mGetAdvisor.execute();
        mGetMailbox = new GetContentTask(MailboxPage.PAGE_NAME, mCardMailbox);
        mGetMailbox.execute();
    }

    @Override
    public void onStop() {
        if (mGetCardBalance != null) {
            mGetCardBalance.cancel(false);
            mGetCardBalance = null;
        }
        if (mGetAdvisor != null) {
            mGetAdvisor.cancel(false);
            mGetAdvisor = null;
        }
        if (mGetMailbox != null) {
            mGetMailbox.cancel(false);
            mGetMailbox = null;
        }
        super.onStop();
    }

    private class GetContentTask extends AsyncTask<Void, Void, Boolean> {

        View mView;
        String mPageName;

        GetContentTask(String pageName, View view) {
            mView = view;
            mPageName = pageName;
        }

        @Override
        protected void onPreExecute() {
            mTaskCounter++;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return !isCancelled() && mSimplePageManager.refreshPage(mPageName);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!this.isCancelled() && success) {
                mSimplePageManager.updateView(mPageName, mView);
            }
            mTaskCounter--;
            if (mTaskCounter == 0)
                mSwipeRefresh.setRefreshing(false);
        }

        @Override
        protected void onCancelled() {
            mTaskCounter--;
            if (mTaskCounter == 0)
                mSwipeRefresh.setRefreshing(false);
        }
    }

}
