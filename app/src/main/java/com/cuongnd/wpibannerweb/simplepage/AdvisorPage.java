package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class AdvisorPage extends SimplePage {

    public static final String PAGE_NAME = "AdvisorPage";

    public static final String JSON_COUNT_ADVISOR = "count";
    public static final String JSON_ADVISOR = "advisor";
    public static final String JSON_EMAIL = "email";
    public static final String JSON_DEPARTMENT = "department";
    public static final String JSON_LOCATION = "location";
    public static final String JSON_ADVISOR_2 = "advisor2";
    public static final String JSON_EMAIL2 = "email2";
    public static final String JSON_DEPARTMENT_2 = "department2";
    public static final String JSON_LOCATION_2 = "location2";

    private static final String JSOUP_ADVISOR = "Primary Advisor";
    private static final String JSOUP_2ND_ADVISOR = "Advisor for 2nd Major";
    private static final String JSOUP_DEPARTMENT = "Advisor Department";
    private static final String JSOUP_LOCATION = "Office Location";

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    public String getUri() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwksadv.P_Summary";
    }

    @Override
    public boolean parse(String html) {
        Document doc = Jsoup.parse(html, ConnectionManager.BASE_URI);
        Element body = doc.body();

        Element nameE = body.getElementsContainingOwnText(JSOUP_ADVISOR).first();
        String name = nameE.nextSibling().toString().trim();
        Element emailE = body.getElementsByAttributeValueContaining("href", "mailto").first();
        String email = emailE.text().trim();
        Element departmentE = body.getElementsContainingOwnText(JSOUP_DEPARTMENT).first();
        String department = departmentE.nextSibling().toString().trim();
        Element locationE = body.getElementsContainingOwnText(JSOUP_LOCATION).first();
        String location = locationE.nextSibling().toString().trim();

        try {
            mData.put(JSON_ADVISOR, name)
                    .put(JSON_EMAIL, email)
                    .put(JSON_DEPARTMENT, department)
                    .put(JSON_LOCATION, location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO: when to return false?
        return true;
    }
    
    @Override
    public void updateView(Context context, View v) {
        if (mData == null)
            return;
        try {
            TextView text = (TextView) v.findViewById(R.id.text_advisor);
            text.setText(mData.getString(JSON_ADVISOR));
            text = (TextView) v.findViewById(R.id.text_email);
            text.setText(mData.getString(JSON_EMAIL));
            text = (TextView) v.findViewById(R.id.text_department);
            text.setText(mData.getString(JSON_DEPARTMENT));
            text = (TextView) v.findViewById(R.id.text_office);
            text.setText(mData.getString(JSON_LOCATION));
        } catch (NullPointerException | JSONException e) {
            Utils.logError(PAGE_NAME, e);
        }
    }
}
