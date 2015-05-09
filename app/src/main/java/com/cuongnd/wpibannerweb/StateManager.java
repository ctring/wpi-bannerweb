package com.cuongnd.wpibannerweb;

import java.net.HttpURLConnection;

/**
 * Created by Cuong Nguyen on 5/8/2015.
 */
public class StateManager {

    private static final String REFERER = "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_WWWLogin";
    private static final String LOGIN_PATH = "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_ValLogin";

    private boolean loggedIn;
    private static StateManager stateManager;

    private StateManager() {

    }

    public static StateManager getInstance() {
        if (stateManager == null) {
            stateManager = new StateManager();
        }
        return stateManager;
    }

    public boolean LogIn(String username, String pin) {
        HttpURLConnection conn;

    }



}
