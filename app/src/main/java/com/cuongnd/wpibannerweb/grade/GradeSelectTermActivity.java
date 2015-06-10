package com.cuongnd.wpibannerweb.grade;

import android.app.Fragment;

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
