package com.cuongnd.wpibannerweb.classes;

import android.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * @author Cuong Nguyen
 */
public class ClassesActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String termId = getIntent().getStringExtra(ClassesFragment.EXTRA_TERM_ID);
        String termName = getIntent().getStringExtra(ClassesFragment.EXTRA_TERM_NAME);
        return ClassesFragment.newInstance(termId, termName);
    }
}
