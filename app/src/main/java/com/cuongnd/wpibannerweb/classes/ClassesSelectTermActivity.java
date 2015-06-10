package com.cuongnd.wpibannerweb.classes;

import android.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * @author Cuong Nguyen
 */
public class ClassesSelectTermActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ClassesSelectTermFragment();
    }
}
