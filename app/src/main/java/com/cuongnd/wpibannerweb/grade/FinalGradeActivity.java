package com.cuongnd.wpibannerweb.grade;

import android.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class FinalGradeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        String termId = getIntent().getStringExtra(FinalGradeFragment.EXTRA_TERM_ID);
        return FinalGradeFragment.newInstance(termId);
    }
}
