package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.ConnectionManager;
import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Represents an Advisor page model.
 *
 * @author Cuong Nguyen
 */
public class AdvisorPage extends SimplePage {

    public static final String PAGE_NAME = AdvisorPage.class.getSimpleName();

    public static final String JSON_COUNT_ADVISOR = "count";
    public static final String JSON_ADVISOR = "advisor";
    public static final String JSON_EMAIL = "email";
    public static final String JSON_DEPARTMENT = "department";
    public static final String JSON_LOCATION = "location";
    public static final String JSON_ADVISOR_2 = "advisor2";
    public static final String JSON_EMAIL_2 = "email2";
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

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_dashboard_card_advisor;
    }

    @Override
    public String getUrl() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwksadv.P_Summary";
    }

    @Override
    public boolean dataLoaded() {
        return false;
    }

    /**
     * Parses a HTML string representing the Advisor page.
     *
     * @param html the HTML string to be parsed
     * @throws NullPointerException
     */
    @Override
    public void parse(String html) {
        Document doc = Jsoup.parse(html, ConnectionManager.BASE_URI);
        Element body = doc.body();

        Element nameE = body.getElementsContainingOwnText(JSOUP_ADVISOR).first();
        String name = nameE.nextSibling().toString().trim();
        Elements emailE = body.getElementsByAttributeValueContaining("href", "mailto");
        String email = emailE.first().text();
        Elements departmentE = body.getElementsContainingOwnText(JSOUP_DEPARTMENT);
        String department = departmentE.first().nextSibling().toString().trim();
        Elements locationE = body.getElementsContainingOwnText(JSOUP_LOCATION);
        String location = locationE.first().nextSibling().toString().trim();

        try {
            mData.put(JSON_ADVISOR, name)
                    .put(JSON_EMAIL, email)
                    .put(JSON_DEPARTMENT, department)
                    .put(JSON_LOCATION, location)
                    .put(JSON_COUNT_ADVISOR, 1);
        } catch (JSONException e) {
            Log.e(PAGE_NAME, "JSON exception occurred!", e);
        }

        nameE = body.getElementsContainingOwnText(JSOUP_2ND_ADVISOR).first();
        // If there are two advisors
        if (nameE != null) {
            name = nameE.nextSibling().toString().trim();
            email = emailE.last().text();
            department = departmentE.last().nextSibling().toString().trim();
            location = locationE.last().nextSibling().toString().trim();

            try {
                mData.put(JSON_ADVISOR_2, name)
                        .put(JSON_EMAIL_2, email)
                        .put(JSON_DEPARTMENT_2, department)
                        .put(JSON_LOCATION_2, location)
                        .put(JSON_COUNT_ADVISOR, 2);
            } catch (JSONException e) {
                Log.e(PAGE_NAME, "JSON exception occurred!", e);
            }
        }

    }

    /**
     * Updates the view hierarchy that displays the Advisor page.
     *
     * @param context the Context of the application
     * @param v the view hierarchy to be updated.
     */
    @Override
    public void updateView(Context context, View v) {
        try {
            TextView text = (TextView) v.findViewById(R.id.text_advisor);
            text.setText(mData.getString(JSON_ADVISOR));
            text = (TextView) v.findViewById(R.id.text_email);
            text.setText(mData.getString(JSON_EMAIL));
            text = (TextView) v.findViewById(R.id.text_department);
            text.setText(mData.getString(JSON_DEPARTMENT));
            text = (TextView) v.findViewById(R.id.text_office);
            text.setText(mData.getString(JSON_LOCATION));

            LinearLayout advisor2 = (LinearLayout) v.findViewById(R.id.advisor_2);
            if (mData.getInt(JSON_COUNT_ADVISOR) == 2) {
                advisor2.setVisibility(View.VISIBLE);
                text = (TextView) v.findViewById(R.id.text_advisor_2);
                text.setText(mData.getString(JSON_ADVISOR_2));
                text = (TextView) v.findViewById(R.id.text_email_2);
                text.setText(mData.getString(JSON_EMAIL_2));
                text = (TextView) v.findViewById(R.id.text_department_2);
                text.setText(mData.getString(JSON_DEPARTMENT_2));
                text = (TextView) v.findViewById(R.id.text_office_2);
                text.setText(mData.getString(JSON_LOCATION_2));
            } else {
                advisor2.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            Log.e(PAGE_NAME, "Cannot find data!");
        } catch (NullPointerException e) {
            Log.e(PAGE_NAME, "Cannot update view!", e);
        }
    }
}
