package com.cuongnd.wpibannerweb.simpleparser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.R;
import com.cuongnd.wpibannerweb.helper.Utils;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Cuong Nguyen on 5/11/2015.
 */
public class MailboxParser extends PageParser {
    public static final String PAGE_NAME = "MailboxParser";

    public static final String JSON_BOX = "box";
    public static final String JSON_DIR1 = "dir1";
    public static final String JSON_NUM1 = "num1";
    public static final String JSON_DIR2 = "dir2";
    public static final String JSON_NUM2 = "num2";
    public static final String JSON_DIR3 = "dir3";
    public static final String JSON_NUM3 = "num3";
    public static final String JSON_DIR4 = "dir4";

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    public boolean parse(String html) {
        Document doc = Jsoup.parse(html, "https://bannerweb.wpi.edu/pls/prod/");
        Element body = doc.body();

        Element boxE1 = body.getElementsContainingOwnText("You have been assigned").first();
        Element boxE2 = boxE1.getElementsByTag("B").first();
        String box = boxE2.text().trim();

        Elements steps = body.getElementsContainingOwnText("Rotate the knob");

        Elements step1 = steps.get(0).getElementsByTag("B");
        String dir1 = step1.get(0).text().trim();
        String num1 = step1.get(1).text().trim();
        Elements step2 = steps.get(1).getElementsByTag("B");
        String dir2 = step2.get(0).text().trim();
        String num2 = step2.get(1).text().trim();
        Elements step3 = steps.get(2).getElementsByTag("B");
        String dir3 = step3.get(0).text().trim();
        String num3 = step3.get(1).text().trim();
        Elements step4 = steps.get(3).getElementsByTag("B");
        String dir4 = step4.get(0).text().trim();

        try {
            mData.put(JSON_BOX, box)
                    .put(JSON_DIR1, dir1)
                    .put(JSON_NUM1, num1)
                    .put(JSON_DIR2, dir2)
                    .put(JSON_NUM2, num2)
                    .put(JSON_DIR3, dir3)
                    .put(JSON_NUM3, num3)
                    .put(JSON_DIR4, dir4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO: when to return false?
        return true;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_mailbox, container, false);
    }

    @Override
    public String getUri() {
        return "https://bannerweb.wpi.edu/pls/prod/hwwkboxs.P_ViewBoxs";
    }

    @Override
    public void updateView(Context context, View v) {
        if (mData == null)
            return;
        try {
            TextView text = (TextView) v.findViewById(R.id.text_box);
            text.setText("Your have been assigned box #: " + mData.getString(JSON_BOX));
            text = (TextView) v.findViewById(R.id.text_step1);
            text.setText(String.format("Rotate the knob %s, at least 4 complete turns, to: %s",
                    mData.getString(JSON_DIR1), mData.getString(JSON_NUM1)));
            text = (TextView) v.findViewById(R.id.text_step2);
            text.setText(String.format("Rotate the knob %s, passing the 1st number one time, to: %s",
                    mData.getString(JSON_DIR2), mData.getString(JSON_NUM2)));
            text = (TextView) v.findViewById(R.id.text_step3);
            text.setText(String.format("Rotate the knob %s to: %s",
                    mData.getString(JSON_DIR3), mData.getString(JSON_NUM3)));
            text = (TextView) v.findViewById(R.id.text_step4);
            text.setText(String.format("Rotate the knob %s to open",
                    mData.getString(JSON_DIR4)));

        } catch (NullPointerException | JSONException e) {
            Utils.logError(PAGE_NAME, e);
        }
    }
}
