package com.cuongnd.wpibannerweb;

import android.app.Fragment;

/**
 * Created by Cuong Nguyen on 5/7/2015.
 */
public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }
}
