package com.cuongnd.wpibannerweb.parser;

import android.view.View;

import com.cuongnd.wpibannerweb.ConnectionManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class AdvisorParser extends PageParser {

    public static final String JSON_COUNT_ADVISOR = "count";
    public static final String JSON_ADVISOR = "advisor";
    public static final String JSON_DEPARTMENT = "department";
    public static final String JSON_LOCATION = "location";
    public static final String JSON_ADVISOR_2 = "advisor2";
    public static final String JSON_DEPARTMENT_2 = "department2";
    public static final String JSON_LOCATION_2 = "location2";

    private static final String JSOUP_ADVISOR = "Primary Advisor";
    private static final String JSOUP_2ND_ADVISOR = "Advisor for 2nd Major";
    private static final String JSOUP_DEPARTMENT = "Advisor Department";
    private static final String JSOUP_LOCATION = "Office Location";

    @Override
    public String getName() {
        return "AdvisorParser";
    }

    @Override
    public View getView() {
        return null;
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

        return true;
    }
}
