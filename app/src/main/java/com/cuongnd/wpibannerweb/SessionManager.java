package com.cuongnd.wpibannerweb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class SessionManager {
    private static final String TAG = "SessionManager";
    private static SessionManager sessionManager;

    public static SessionManager getInstance(Context context) {
        if (sessionManager == null)
            sessionManager = new SessionManager(context);
        return sessionManager;
    }

    private static final String PREF_SID = "username";
    private static final String PREF_PIN = "password";

    private Context mContext;
    private SharedPreferences pref;
    private ConnectionManager connectionManager;

    private SessionManager(Context context) {
        mContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        connectionManager = ConnectionManager.getInstance();
    }

    public void createSession(String username, String pin) {
        pref.edit()
            .putString(PREF_SID, username)
            .putString(PREF_PIN, pin)
            .apply();
    }

    public void checkStatus() {
        String username = pref.getString(PREF_SID, null);
        String password = pref.getString(PREF_PIN, null);
        if (username == null || password == null) {
            startLoginActivity();
        } else {
            connectionManager.setUsernameAndPin(username, password);
        }
    }

    public void deactivate() {
        pref.edit().clear().apply();
        connectionManager.logOut();
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent i = new Intent(mContext, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }
}
