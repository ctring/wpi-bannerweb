package com.cuongnd.wpibannerweb;

import android.app.Fragment;

/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class FinalGradeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new FinalGradeFragment();
    }
}
