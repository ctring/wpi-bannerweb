package com.cuongnd.wpibannerweb.grade;

import android.app.Fragment;

import com.cuongnd.wpibannerweb.SingleFragmentActivity;

/**
 * @author Cuong Nguyen
 */
public class FinalGradeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        String termId = getIntent().getStringExtra(FinalGradeFragment.EXTRA_TERM_ID);
        String termName = getIntent().getStringExtra(FinalGradeFragment.EXTRA_TERM_NAME);
        return FinalGradeFragment.newInstance(termId, termName);
    }
}
