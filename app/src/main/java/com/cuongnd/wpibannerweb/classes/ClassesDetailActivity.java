package com.cuongnd.wpibannerweb.classes;

import android.support.v4.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * @author Cuong Nguyen
 */
public class ClassesDetailActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String wpiClass = getIntent().getStringExtra(ClassesDetailFragment.EXTRA_WPI_CLASS);
        return ClassesDetailFragment.newInstance(wpiClass);
    }
}
