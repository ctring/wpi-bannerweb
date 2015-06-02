package com.cuongnd.wpibannerweb.classes;

import android.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * Created by Cuong Nguyen on 5/29/2015.
 */
public class ClassesActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String termId = getIntent().getStringExtra(ClassesFragment.EXTRA_TERM_ID);
        return ClassesFragment.newInstance(termId);
    }
}
