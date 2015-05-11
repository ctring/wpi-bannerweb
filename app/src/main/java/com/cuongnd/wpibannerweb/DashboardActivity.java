package com.cuongnd.wpibannerweb;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class DashboardActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Intent i = getIntent();
        return DashboardFragment.newInstance(i.getStringExtra(DashboardFragment.EXTRA_USERNAME));
    }
}
