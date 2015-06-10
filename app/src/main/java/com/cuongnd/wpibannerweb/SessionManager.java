package com.cuongnd.wpibannerweb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * SessionManager is singleton for managing login sessions.
 *
 * @author Cuong Nguyen
 */
public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();
    private static final String PREF = "LoginInfo";
    private static final String PREF_SID = "username";
    private static final String PREF_PIN = "password";

    private static SessionManager sessionManager;

    public static SessionManager getInstance(Context context) {
        if (sessionManager == null)
            sessionManager = new SessionManager(context);
        return sessionManager;
    }

    private Context mContext;
    private SharedPreferences mPref;
    private ConnectionManager mConnectionManager;

    private SessionManager(Context context) {
        mContext = context;
        mPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        mConnectionManager = ConnectionManager.getInstance();
    }

    public void createSession(String username, String pin) {
        mPref.edit()
            .putString(PREF_SID, username)
            .putString(PREF_PIN, pin)
            .apply();
    }

    /**
     * Checks the current login status. If username and password were already saved locally,
     * they are set in the connection manager. Otherwise, starts the login activity.
     * @return <code>true</code> if logged in. And <code>false</code>, otherwise
     */
    public boolean checkStatus() {
        String username = mPref.getString(PREF_SID, null);
        String password = mPref.getString(PREF_PIN, null);
        if (username == null || password == null) {
            startLoginActivity();
            return false;
        }
        mConnectionManager.setUsernameAndPin(username, password);
        return true;
    }

    /**
     * Deactivates this session by clearing local username and password.
     */
    public void deactivate() {
        new Thread() {
            @Override
            public void run() {
                mConnectionManager.logOut();
            }
        }.start();
        mPref.edit().clear().apply();
        WPIBannerWebApplication.getInstance().clearApplicationData();
        startLoginActivity();
    }

    /**
     * Starts the login activity and clear all the task.
     */
    private void startLoginActivity() {
        Intent i = new Intent(mContext, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public String getUserName() {
        return mPref.getString(PREF_SID, null);
    }
}
