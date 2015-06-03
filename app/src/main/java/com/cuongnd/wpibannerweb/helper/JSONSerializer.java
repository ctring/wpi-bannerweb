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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Cuong Nguyen on 6/2/2015.
 */
public class JSONSerializer {
    private static final String TAG = "JSONSerializer";

    public static JSONObject loadJSONFromFile(Context context, String fileName) {
        return loadJSONFromFile(context, null, fileName);
    }

    public static JSONObject loadJSONFromFile(Context context, File dir, String fileName) {
        BufferedReader reader = null;
        try {
            InputStream in;
            if (dir == null) {
                in = context.openFileInput(fileName);
            }
            else {
                in = new FileInputStream(new File(dir, fileName));
            }
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            return new JSONObject(jsonString.toString());
        } catch (IOException | JSONException e) {
            Utils.logError(TAG, e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                Utils.logError(TAG, e);
            }
        }
        return new JSONObject();
    }

    public static void saveJSONToFile(Context context, String fileName, JSONObject jsonObject) {
        saveJSONToFile(context, null, fileName, jsonObject);
    }

    public static void saveJSONToFile(Context context, File dir, String fileName,
                                      JSONObject jsonObject) {
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
        } catch (IOException e) {
            Utils.logError(TAG, e);
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                Utils.logError(TAG, e);
            }
        }
    }
}
