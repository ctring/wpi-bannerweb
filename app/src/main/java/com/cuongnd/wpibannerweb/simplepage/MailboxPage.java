package com.cuongnd.wpibannerweb.simplepage;

import android.content.Context;
import android.util.Log;
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
import org.jsoup.select.Elements;

/**
 * Represents the Mailbox page model.
 *
 * @author Cuong Nguyen
 */
public class MailboxPage extends SimplePage {

    public static final String PAGE_NAME = MailboxPage.class.getSimpleName();

    public static final String JSON_BOX = "box";
    public static final String JSON_NUM1 = "num1";
    public static final String JSON_NUM2 = "num2";
    public static final String JSON_NUM3 = "num3";

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_dashboard_card_mailbox;
    }

    @Override
    public String getUrl() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwkboxs.P_ViewBoxs";
    }

    @Override
    public boolean dataLoaded() {
        return mData.has(JSON_BOX);
    }

    /**
     * Parses a HTML string representing the Mailbox page.
     *
     * @param html the HTML string to be parsed
     * @throws NullPointerException
     */
    @Override
    public void parse(String html) {

        Document doc = Jsoup.parse(html, ConnectionManager.BASE_URI);
        Element body = doc.body();

        Element boxE1 = body.getElementsContainingOwnText("You have been assigned").first();
        Element boxE2 = boxE1.getElementsByTag("B").first();
        String box = boxE2.text();

        Elements steps = body.getElementsContainingOwnText("Rotate the knob");

        String num1 = "";
        String num2 = "";
        String num3 = "";
        if (steps != null && steps.size() >= 3) {
            Elements step1 = steps.get(0).getElementsByTag("B");
            num1 = step1.get(1).text();
            Elements step2 = steps.get(1).getElementsByTag("B");
            num2 = step2.get(1).text();
            Elements step3 = steps.get(2).getElementsByTag("B");
            num3 = step3.get(1).text();
        }

        try {
            mData.put(JSON_BOX, box)
                    .put(JSON_NUM1, num1)
                    .put(JSON_NUM2, num2)
                    .put(JSON_NUM3, num3);
        } catch (JSONException e) {
            Log.e(PAGE_NAME, "JSON exception occurred!", e);
        }

    }

    /**
     * Updates the view hierarchy that displays the Mailbox page.
     *
     * @param context the Context of the application
     * @param v the view hierarchy to be updated.
     */
    @Override
    public void updateView(Context context, View v) {
        try {
            TextView text = (TextView) v.findViewById(R.id.text_box);
            text.setText(mData.getString(JSON_BOX));
            text = (TextView) v.findViewById(R.id.text_step1);
            text.setText(mData.getString(JSON_NUM1));
            text = (TextView) v.findViewById(R.id.text_step2);
            text.setText(mData.getString(JSON_NUM2));
            text = (TextView) v.findViewById(R.id.text_step3);
            text.setText(mData.getString(JSON_NUM3));

        } catch (JSONException e) {
            Log.e(PAGE_NAME, "Cannot find data!");
        } catch (NullPointerException e) {
            Log.e(PAGE_NAME, "Cannot update view!", e);
        }
    }
}
