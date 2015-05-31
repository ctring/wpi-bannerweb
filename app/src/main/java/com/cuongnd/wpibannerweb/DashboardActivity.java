package com.cuongnd.wpibannerweb;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cuongnd.wpibannerweb.helper.SessionManager;

/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class DashboardActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Intent i = getIntent();
        return DashboardFragment.newInstance(i.getStringExtra(DashboardFragment.EXTRA_USERNAME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log_out:
                SessionManager.getInstance(getApplicationContext()).deactivate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
