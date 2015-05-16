package com.cuongnd.wpibannerweb.grade;

import android.content.Context;

import com.cuongnd.wpibannerweb.helper.ConnectionManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Cuong Nguyen on 5/13/2015.
 */
public class FinalGradePage {

    private static final String PREF = "Term";

    private static final String STUDENT_RECORDS =
            "https://bannerweb.wpi.edu/pls/prod/twbkwbis.P_GenMenu?name=bmenu.P_AdminMnu";
    private static final String VIEW_TERM =
            "https://bannerweb.wpi.edu/pls/prod/bwskogrd.P_ViewTermGrde";
    private static final String VIEW_GRADE =
            "https://bannerweb.wpi.edu/pls/prod/bwskogrd.P_ViewGrde";

    Context mContext;

    public FinalGradePage(Context context) {
        mContext = context;
    }

    public ArrayList<TermValue> getTerms() {
        ConnectionManager cm = ConnectionManager.getInstance();
        String html = cm.getPage(VIEW_TERM, STUDENT_RECORDS);

        ArrayList<TermValue> terms = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element select = doc.getElementById("term_id");
        Elements options = select.getElementsByTag("option");
        for (Element option : options) {
            String value = option.attr("value");
            String text = option.text();
            TermValue term = new TermValue(value, text);
            terms.add(term);
        }
        return terms;
    }

    public static class TermValue {
        private String mValue;
        private String mText;

        public TermValue(String value, String text) {
            mValue = value;
            mText = text;
        }

        public String getValue() {
            return mValue;
        }

        public String getText() {
            return mText;
        }
    }
}
