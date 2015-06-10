package com.cuongnd.wpibannerweb.helper;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * JSONSerializer is a utility class for loading and saving JSON files.
 *
 * @author Cuong Nguyen
 */
public class JSONSerializer {

    /**
     * Loads a JSON object from an internal file locating in the app data folder.
     *
     * @param context the context application that hold the internal file
     * @param fileName name of the file
     * @return the JSON object read from the file
     * @throws JSONException
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static JSONObject loadJSONFromFile(Context context,
                                              String fileName) throws JSONException, IOException {
        return loadJSONFromFile(context, null, fileName);
    }

    /**
     * Loads a JSON object from an internal file locating in a specified folder.
     *
     * @param context the context application that hold the internal file
     * @param dir the folder that the file is locating. If set to null, the file will be sought in
     *            the app data folder
     * @param fileName name of the file
     * @return the JSON object read from the file
     * @throws FileNotFoundException
     * @throws JSONException
     * @throws IOException
     */
    public static JSONObject loadJSONFromFile
            (Context context, File dir, String fileName) throws JSONException, IOException {
        BufferedReader reader = null;
        try {
            InputStream in;
            if (dir == null) {
                in = context.openFileInput(fileName);
            } else {
                in = new FileInputStream(new File(dir, fileName));
            }
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            return new JSONObject(jsonString.toString());
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     *  Save a JSON object to a file locating in the app data folder.
     *
     * @param context he context application to be saved in
     * @param fileName name of the file to be saved
     * @param jsonObject the JSON object to be saved
     * @throws IOException
     */
    public static void saveJSONToFile(Context context,
                                      String fileName, JSONObject jsonObject) throws IOException {
        saveJSONToFile(context, null, fileName, jsonObject);
    }

    /**
     * Save a JSON object to a file locating in a specified folder.
     *
     * @param context the context application to be saved in
     * @param dir the folder that the file will be saved. If set to null, the file will be saved
     *            in the app data folder
     * @param fileName name of the file to be saved
     * @param jsonObject the JSON object to be saved
     * @throws IOException
     */
    public static void saveJSONToFile(Context context, File dir, String fileName,
                                      JSONObject jsonObject) throws IOException {
        Writer writer = null;
        try {
            OutputStream out;
            if (dir == null) {
                out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            } else {
                out = new FileOutputStream(new File(dir, fileName));
            }
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(jsonObject.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
