package com.cuongnd.wpibannerweb.simplepage;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class ContentFragment extends DialogFragment {
    public static final String EXTRA_PAGE_NAME = "PageName";

    public static ContentFragment newInstance(String pageName) {
        Bundle args = new Bundle();
        args.putString(EXTRA_PAGE_NAME, pageName);

        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private String mPageName;
    private SimplePageManager mSimplePageManager;
    private GetContentTask mGetContentTask;
    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPageName = getArguments().getString(EXTRA_PAGE_NAME);
        mSimplePageManager = SimplePageManager.getInstance(getActivity());
        mView = mSimplePageManager.getView(mPageName, inflater, container);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
        refresh();
    }

    private void refresh() {
        mGetContentTask = new GetContentTask();
        mGetContentTask.execute();
    }

    @Override
    public void onStop() {
        if (mGetContentTask != null) {
            mGetContentTask.cancel(false);
            mGetContentTask = null;
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    private void updateView() {
        if (mView != null)
            mSimplePageManager.updateView(mPageName, getActivity(), mView);
    }

    private class GetContentTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return !isCancelled() && mSimplePageManager.refreshPage(mPageName);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!this.isCancelled() && success) {
                updateView();
            }
        }
    }


}
