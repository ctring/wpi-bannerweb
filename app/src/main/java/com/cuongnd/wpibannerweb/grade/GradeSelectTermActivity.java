package com.cuongnd.wpibannerweb.grade;

import android.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * Created by Cuong Nguyen on 6/1/2015.
 */
public class GradeSelectTermActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new GradeSelectTermFragment();
    }
}
