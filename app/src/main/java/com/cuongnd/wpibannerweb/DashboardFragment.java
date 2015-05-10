package com.cuongnd.wpibannerweb;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class DashboardFragment extends Fragment {

    private ConnectionManager mConn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sm = SessionManager.getInstance(getActivity().getApplicationContext());
        sm.checkStatus();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return v;
    }
}
