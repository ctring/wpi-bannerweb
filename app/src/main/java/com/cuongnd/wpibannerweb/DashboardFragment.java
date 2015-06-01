package com.cuongnd.wpibannerweb;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.classes.ClassesActivity;
import com.cuongnd.wpibannerweb.classes.SelectTermActivity;
import com.cuongnd.wpibannerweb.grade.FinalGradeActivity;
import com.cuongnd.wpibannerweb.helper.SessionManager;
import com.cuongnd.wpibannerweb.simpleparser.AdvisorParser;
import com.cuongnd.wpibannerweb.simpleparser.CardBalanceParser;
import com.cuongnd.wpibannerweb.simpleparser.ContentFragment;
import com.cuongnd.wpibannerweb.simpleparser.MailboxParser;

/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class DashboardFragment extends Fragment {

    public static final String EXTRA_USERNAME = "username";
    private static final String DIALOG_CONTENT = "content";

    private ImageView imageProfile;

    private TextView textTest;

    public static DashboardFragment newInstance(String username) {
        Bundle args = new Bundle();
        args.putString(EXTRA_USERNAME, username);

        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sm = SessionManager.getInstance(getActivity().getApplicationContext());
        sm.checkStatus();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        imageProfile = (ImageView) v.findViewById(R.id.image_profile);

        TextView textName = (TextView) v.findViewById(R.id.text_name);
        textName.setText(SessionManager.getInstance(getActivity().getApplicationContext())
                .getUserName());

        TextView textWpiId = (TextView) v.findViewById(R.id.text_wpiid);

        ImageButton buttonBalance = (ImageButton) v.findViewById(R.id.button_balance);
        buttonBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentDialog(CardBalanceParser.PAGE_NAME);
            }
        });

        ImageButton buttonMailbox = (ImageButton) v.findViewById(R.id.button_mailbox);
        buttonMailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentDialog(MailboxParser.PAGE_NAME);
            }
        });

        ImageButton buttonAdvisor = (ImageButton) v.findViewById(R.id.button_advisor);
        buttonAdvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentDialog(AdvisorParser.PAGE_NAME);
            }
        });

        ImageButton buttonGrade = (ImageButton) v.findViewById(R.id.button_grade);
        buttonGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FinalGradeActivity.class);
                startActivity(i);
            }
        });

        ImageButton buttonClasses = (ImageButton) v.findViewById(R.id.button_classes);
        buttonClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SelectTermActivity.class);
                startActivity(i);
            }
        });


        // TODO: make width equal height
        TableLayout tableInfo = (TableLayout) v.findViewById(R.id.table_info);

        return v;
    }

    private void showContentDialog(String pageName) {
        FragmentManager fm = getActivity().getFragmentManager();
        ContentFragment fragment = ContentFragment.newInstance(pageName);
        fragment.show(fm, DIALOG_CONTENT);
    }

}
