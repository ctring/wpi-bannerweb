package com.cuongnd.wpibannerweb;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Cuong Nguyen on 5/8/2015.
 */
public class ConnectionManager {
    private static final String TAG = "ConnectionManager";

    private static ConnectionManager connectionManager;

    public static ConnectionManager getInstance() {
        if (connectionManager == null) {
            connectionManager = new ConnectionManager();
        }
        return connectionManager;
    }

    private static final String HOME = "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_WWWLogin";
    private static final String LOGIN_PATH = "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_ValLogin";
    private final String PARAM_SID = "sid";
    private final String PARAM_PIN = "PIN";
    private final String CHARSET = "UTF-8";
    private final String PARAM_REFERRER = "Referer";

    private String mUsername;
    private String mPin;

    HashMap<String, String> mCookies;

    private ConnectionManager() {

        // Set cookie manager VM-wide. This will not be effective so there is need of
        // manually managing cookie session later on.
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        mCookies = new HashMap<String, String>();

    }

    public void setUsernameAndPin(String username, String pin) {
        mUsername = username;
        mPin = pin;
    }

    /**
     * Log into the BannerWeb with stored username and password, then save the cookie session
     * @return True of login successfully, false otherwise.
     */
    public boolean logIn() {
        if (mUsername == null || mPin == null) {
            throw new RuntimeException("Username and password is null.");
        }
        try {
            // Load the homepage to get test cookies. Without the test cookies, BannerWeb will
            // assume that cookies are not enabled
            HttpURLConnection init = (HttpURLConnection) new URL(HOME).openConnection();
            updateCookies(init);

            HttpURLConnection conn = (HttpURLConnection) new URL(LOGIN_PATH).openConnection();
            setCookies(conn);

            conn.setDoOutput(true); // Request method is set to "POST" automatically
            conn.setRequestProperty(PARAM_REFERRER, HOME);

            BufferedWriter wr = new BufferedWriter(
                    new OutputStreamWriter(conn.getOutputStream(), CHARSET));
            wr.write(String.format("%s=%s&%s=%s", PARAM_SID, mUsername, PARAM_PIN, mPin));
            wr.close();

            int response = conn.getResponseCode();
            if (response == 200) {
                String data = inputStreamToString(conn.getInputStream());
                // Log in successfully
                if (data.contains("refresh")) {
                    updateCookies(conn);
                    return true;
                }
                else {
                    Log.d(TAG, "Log in failed.");
                }
            } else {
                Log.d(TAG, "Connection failed. Response code: " + response);
                return false;
            }

        } catch (IOException e) {
            // TODO: Handle exception carefully
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update the cookies from a connection.
     * @param conn Connection holding the cookies to be updated.
     */
    private void updateCookies(HttpURLConnection conn) {
        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
        for (String cookie : cookies) {
            String first = cookie.split(";", 2)[0];
            String[] pair = first.split("=", 2);
            mCookies.put(pair[0], pair[1]);
        }
    }

    /**
     * Set the current cookies to a connection.
     * @param conn Connection to be set cookies.
     */
    private void setCookies(HttpURLConnection conn) {
        Iterator it = mCookies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
            conn.setRequestProperty("Cookie", pair.getKey() + "=" + pair.getValue());
        }
    }


    /**
     * Get a page from a specifed url.
     * @param url Url to get page from.
     * @return The HTML string of the page. Return null if error occurs.
     */
    public String getPage(String url) {
        return getPage(url, null);
    }

    /**
     * Get a page from a specifed url that requires a referrer.
     * @param url Url to get page from.
     * @param referrer Referrer to the page.
     * @return The HTML string of the page. Return null if error occurs
     */
    public String getPage(String url, String referrer) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            setCookies(conn);
            if (referrer != null) {
                conn.setRequestProperty(PARAM_REFERRER, referrer);
            }

            String data = inputStreamToString(conn.getInputStream());
            return data;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert an input stream to string .
     * @param is Input stream to be converted.
     * @return A string read from the input stream.
     */
    private String inputStreamToString(InputStream is) {
        String str = new Scanner(is, CHARSET).useDelimiter("\\A").next();
        return str;
    }


}
