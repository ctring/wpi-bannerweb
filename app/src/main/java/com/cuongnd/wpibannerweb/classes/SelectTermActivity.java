package com.cuongnd.wpibannerweb.classes;

import android.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * Created by Cuong Nguyen on 6/1/2015.
 */
public class SelectTermActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SelectTermFragment();
    }
}
