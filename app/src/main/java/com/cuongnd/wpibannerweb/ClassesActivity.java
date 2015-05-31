package com.cuongnd.wpibannerweb;

import android.app.Fragment;

/**
 * Created by Cuong Nguyen on 5/29/2015.
 */
public class ClassesActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String termid = getIntent().getStringExtra(ClassesFragment.EXTRA_TERM_ID);
        return ClassesFragment.newInstance(termid);
    }
}
