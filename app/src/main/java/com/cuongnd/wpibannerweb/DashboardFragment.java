package com.cuongnd.wpibannerweb;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.TextView;

import com.cuongnd.wpibannerweb.classes.ClassesSelectTermActivity;
import com.cuongnd.wpibannerweb.grade.GradeSelectTermActivity;
import com.cuongnd.wpibannerweb.helper.Utils;
import com.cuongnd.wpibannerweb.simplepage.AdvisorPage;
import com.cuongnd.wpibannerweb.simplepage.CardBalancePage;
import com.cuongnd.wpibannerweb.simplepage.IDImagePage;
import com.cuongnd.wpibannerweb.simplepage.MailboxPage;
import com.cuongnd.wpibannerweb.simplepage.SimplePageManager;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;


/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    public static final String EXTRA_USERNAME = "username";

    private ImageView mIDImage;
    private CardView mCardAdvisor;
    private CardView mCardMailbox;
    private CardView mCardCardbalance;
    private SwipeRefreshLayout mSwipeRefresh ;

    private int mTaskCounter = 0;
    private SimplePageManager mSimplePageManager;
    private boolean mFirstRun = true;
    private boolean mTerminated;

    private GetContentTask mGetMailbox;
    private GetContentTask mGetCardBalance;
    private GetContentTask mGetAdvisor;
    private GetContentTask mGetIdImage;

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

        mTerminated = false;
        SessionManager sm = SessionManager.getInstance(getActivity().getApplicationContext());
        if (!sm.checkStatus()) {
            mTerminated = true;
        }

        setRetainInstance(true);

        mSimplePageManager = new SimplePageManager(getActivity());
        mTaskCounter = 0;
        mFirstRun = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mIDImage = (ImageView) v.findViewById(R.id.image_id);
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
                //Intent i = new Intent(getActivity(), ClassesSelectTermActivity.class);
                Intent i = new Intent(getActivity(), ClassesSelectTermActivity.class);
                startActivity(i);
            }
        });

        mSwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_dashboard);
        mSwipeRefresh.setColorSchemeResources(R.color.accent_color, android.R.color.black);
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
        if (mTerminated) return;
        mSimplePageManager.updateView(CardBalancePage.PAGE_NAME, mCardCardbalance);
        mSimplePageManager.updateView(AdvisorPage.PAGE_NAME, mCardAdvisor);
        mSimplePageManager.updateView(MailboxPage.PAGE_NAME, mCardMailbox);
        mSimplePageManager.updateView(IDImagePage.PAGE_NAME, mIDImage);
        if (mFirstRun) {
            mFirstRun = false;
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                }
            });
            refresh();
        }
    }

    private void refresh() {
        cancelTask(mGetCardBalance);
        mGetCardBalance = new GetContentTask(CardBalancePage.PAGE_NAME, mCardCardbalance);
        mGetCardBalance.execute();

        cancelTask(mGetAdvisor);
        mGetAdvisor = new GetContentTask(AdvisorPage.PAGE_NAME, mCardAdvisor);
        mGetAdvisor.execute();

        cancelTask(mGetMailbox);
        mGetMailbox = new GetContentTask(MailboxPage.PAGE_NAME, mCardMailbox);
        mGetMailbox.execute();

        cancelTask(mGetIdImage);
        mGetIdImage = new GetContentTask(IDImagePage.PAGE_NAME, mIDImage);
        mGetIdImage.execute();
    }

    @Override
    public void onStop() {
        cancelTask(mGetCardBalance);
        mGetCardBalance = null;

        cancelTask(mGetAdvisor);
        mGetAdvisor = null;

        cancelTask(mGetMailbox);
        mGetMailbox = null;
        super.onStop();
    }

    private void cancelTask(AsyncTask task) {
        if (task != null)
            task.cancel(false);
    }

    private class GetContentTask extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... params) {
            try {
                if (!isCancelled()) {
                    mSimplePageManager.reloadPage(mPageName);
                }
            } catch (SocketTimeoutException e) {
                Utils.showShortToast(getActivity(),
                        getString(R.string.error_connection_timed_out));
                Log.e(TAG, "Connection timed out", e);
            } catch (IOException e) {
                Utils.showShortToast(getActivity(),
                        getString(R.string.error_connection_problem_occurred));
                Log.e(TAG, "Connection problem occurred", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            if (!this.isCancelled()) {
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
