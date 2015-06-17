package com.cuongnd.wpibannerweb.grade;

import android.support.v4.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * @author Cuong Nguyen
 */
public class GradeSelectTermActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new GradeSelectTermFragment();
    }
}
