package com.cuongnd.wpibannerweb.helper;

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

    public static final String BASE_URI = "https://bannerweb.wpi.edu/pls/prod/";

    private static final String HOME =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_WWWLogin";
    private static final String MAIN_MENU =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu";
    private static final String LOGIN =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_ValLogin";
    private static final String LOGOUT =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_Logout";

    private final static String PARAM_SID = "sid";
    private final static String PARAM_PIN = "PIN";
    private final static String CHARSET = "UTF-8";
    private final static String PARAM_REFERRER = "Referer";

    private String mUsername;
    private String mPin;

    HashMap<String, String> mCookies;

    private ConnectionManager() {

        // Set cookie manager VM-wide. This will not be effective so there is need of
        // manually managing cookie session later on.
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        mCookies = new HashMap<>();

    }

    void setUsernameAndPin(String username, String pin) {
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
            HttpURLConnection init = makeConnection(HOME, null, null);
            init.connect();

            HttpURLConnection conn = makeConnection(LOGIN, HOME,
                    String.format("%s=%s&%s=%s", PARAM_SID, mUsername, PARAM_PIN, mPin));

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                String data = inputStreamToString(is);
                is.close();
                // Log in successfully
                if (data.contains("refresh")) {
                    return true;
                }
                else {
                    Log.d(TAG, "Log in failed.");
                }
            } else {
                Log.d(TAG, "Connection failed. Response code: " + responseCode);
                return false;
            }

        } catch (IOException e) {
            // TODO: Handle exception carefully
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Log out of the BannerWeb
     */
    public void logOut() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(LOGOUT).openConnection();

            conn.setRequestProperty(PARAM_REFERRER, MAIN_MENU);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Log.d(TAG, "Log Out Successfully.");
            } else {
                Log.d(TAG, "Log Out failed. Response code: " + responseCode);
            }

        } catch (IOException e) {
            // TODO: Handle exception carefully
            e.printStackTrace();
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
     * @return The HTML string of the page. Return null if error occurs.
     */
    public String getPage(String url, String referrer) {
        return getPage(url, referrer, null);
    }

    /**
     * Get a page from a specifed url that requires a referrer and data.
     *
     * @param url      Url to get page from.
     * @param referrer Referrer to the page.
     * @param postData Data for the post method
     * @return The HTML string of the page. Return null if error occurs.
     */
    public String getPage(String url, String referrer, String postData) {
        try {
            HttpURLConnection conn = makeConnection(url, referrer, postData);

            InputStream is = conn.getInputStream();
            String data = inputStreamToString(is);
            is.close();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200 || isUserLoginPage(data)) {
                if (logIn()) {
                    conn = makeConnection(url, referrer, postData);
                    is = conn.getInputStream();
                    data = inputStreamToString(is);
                    is.close();
                }
                else {
                    return null;
                }
            }
            return data;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection makeConnection(String url, String referrer, String data) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        if (referrer != null) {
            conn.setRequestProperty(PARAM_REFERRER, referrer);
        }
        if (data != null) {
            conn.setDoOutput(true); // Request method is set to "POST" automatically
            BufferedWriter wr = new BufferedWriter(
                    new OutputStreamWriter(conn.getOutputStream(), CHARSET));
            wr.write(data);
            wr.close();
        }
        return conn;
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

    /**
     * Check if a page is the home
     * @param data
     * @return
     */
    private boolean isUserLoginPage(String data) {
        return (data.contains("<TITLE>User Login</TITLE>")); // User Login page signature
    }

}

/**
 * I don't need these two methods anymore but I still keep it just in case
 * Update the cookies from a connection.
 * @param conn Connection holding the cookies to be updated.
 */
    /*private void updateCookies(HttpURLConnection conn) {
        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
        for (String cookie : cookies) {
            String first = cookie.split(";", 2)[0];
            String[] pair = first.split("=", 2);
            mCookies.put(pair[0], pair[1]);
        }
    }

     *
     * Set the current cookies to a connection.
     * @param conn Connection to be set cookies.
     *
    private void setCookies(HttpURLConnection conn) {
        String cookies = "";
        Iterator<Map.Entry<String, String>> it = mCookies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = it.next();
            cookies += pair.getKey() + "=" + pair.getValue();
            if (it.hasNext())
                cookies += ";";
        }
        conn.addRequestProperty("Cookie", cookies);
    }*/
