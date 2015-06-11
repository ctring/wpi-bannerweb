package com.cuongnd.wpibannerweb;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

/**
 * ConnectionManager is singleton class for performing networking task.
 *
 * @author Cuong Nguyen
 */
public class ConnectionManager {

    private static final String TAG = ConnectionManager.class.getSimpleName();

    private static ConnectionManager connectionManager;

    public static ConnectionManager getInstance() {
        if (connectionManager == null) {
            connectionManager = new ConnectionManager();
        }
        return connectionManager;
    }

    public static final int CONNECTION_TIME_OUT = 10000;

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

    /**
     * Constructs a new ConnectionManager object. A {@link CookieManager} object is also
     * created and set as default.
     */
    private ConnectionManager() {

        // Set cookie manager VM-wide.
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    /**
     * Sets username and password for log in task.
     *
     * @param username WPI username
     * @param pin WPI password
     */
    public void setUsernameAndPin(String username, String pin) {
        mUsername = username;
        mPin = pin;
    }

    /**
     * Logs into the WPI BannerWeb with stored username and password. If username and password
     * are not set with the {@link #setUsernameAndPin(String, String) setUsernameAndPin} method,
     * login will fail.
     *
     * @return <code>true</code> if logged in successfully, <code>false</code> otherwise
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    public boolean logIn() throws IOException {

        if (mUsername == null || mPin == null) {
            Log.w(TAG, "Username and Pin are not set yet!");
            return false;
        }

        // Load the homepage to get test cookies. Without the test cookies, BannerWeb will
        // assume that cookies are not enabled.
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
            } else {
                Log.e(TAG, "Log in failed.");
            }
        } else {
            Log.e(TAG, "Connection failed. Response code: " + responseCode);
            return false;
        }
        conn.disconnect();
        return false;
    }

    public void logOut() {
        mUsername = null; mPin = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(LOGOUT).openConnection();

            conn.setRequestProperty(PARAM_REFERRER, MAIN_MENU);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Log.i(TAG, "Log out successfully.");
            }
            conn.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "Connection error!", e);
        }
    }

    /**
     * Gets a HTML page from a specified url.
     *
     * @param url url to get page from
     * @return the HTML string representing the page
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    public String getPage(String url) throws IOException {
        return getPage(url, null);
    }

    /**
     * Gets a page from a specified url that requires a referrer.
     *
     * @param url url to get page from
     * @param referrer referrer to the page
     * @return the HTML string representing the page
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    public String getPage(String url, @Nullable String referrer) throws IOException {
        return getPage(url, referrer, null);
    }

    /**
     * Gets a page from a specified url. If the post data is not null, the HTTP method will be set to
     * POST and the post data is posted to the url. A referrer may also be passed if needed.
     *
     * @param url      url to get the page from
     * @param referrer referrer to the page
     * @param postData data for the post method
     * @return the HTML string representing the page
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    public String getPage(String url, @Nullable String referrer, @Nullable String postData)
            throws IOException {

        HttpURLConnection conn = null;
        try {
            conn = makeConnection(url, referrer, postData);
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
                    throw new IOException("Log in failed");
                }
            }
            return data;
        } finally {
            if (conn != null)
                conn.disconnect();
        }

    }

    /**
     * Gets an array of bytes from a url. A referrer may also be passed if needed.
     *
     * @param url url to get bytes from
     * @param referrer referrer to the page
     * @return an array of bytes
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    public byte[] getBytes(String url, String referrer) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = makeConnection(url, referrer, null);

            conn.connect();
            if (conn.getResponseCode()  != 200) {
                if (logIn()) {
                    conn = makeConnection(url, referrer, null);
                }
                else {
                    return null;
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = conn.getInputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    /**
     * Creates a {@link HttpURLConnection} with specified url, referrer or data. If the post data is
     * set, the HTTP method will be set to POST.
     *
     * @param url url of the connection.
     * @param referrer referrer of the connection. May be <code>null</code>
     * @param data data of the connection. If set, the method of the connection will be set to POST
     * @return a HttpURLConnection object containing the provided parameters
     * @throws IOException If a connection error occurred
     * @throws SocketTimeoutException If connection timed out
     */
    private HttpURLConnection makeConnection(String url, String referrer, String data)
            throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(CONNECTION_TIME_OUT);

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

    private String inputStreamToString(InputStream is) {
        return new Scanner(is, CHARSET).useDelimiter("\\A").next();
    }

    private boolean isUserLoginPage(String html) {
        return (html.contains("<TITLE>User Login</TITLE>")); // User Login page signature
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
