package com.cuongnd.wpibannerweb;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.cuongnd.wpibannerweb.parser.AdvisorParser;
import com.cuongnd.wpibannerweb.parser.CardBalanceParser;
import com.cuongnd.wpibannerweb.parser.GradeParser;
import com.cuongnd.wpibannerweb.parser.MailboxParser;

/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class DashboardFragment extends Fragment {

    public static final String EXTRA_USERNAME = "username";
    private static final String DIALOG_CONTENT = "content";

    private ConnectionManager connectionManager;

    private ImageView imageProfile;
    private TextView textName;
    private TextView textWpiId;
    private ImageButton buttonBalance;
    private ImageButton buttonMailbox;
    private ImageButton buttonAdvisor;
    private ImageButton buttonGrade;
    private TableLayout tableInfo;

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

        connectionManager = ConnectionManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        imageProfile = (ImageView) v.findViewById(R.id.image_profile);

        textName = (TextView) v.findViewById(R.id.text_name);
        textName.setText(getArguments().getString(EXTRA_USERNAME));

        textWpiId = (TextView) v.findViewById(R.id.text_wpiid);

        buttonBalance = (ImageButton) v.findViewById(R.id.button_balance);
        buttonBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentDialog(CardBalanceParser.PAGE_NAME);
            }
        });

        buttonMailbox = (ImageButton) v.findViewById(R.id.button_mailbox);
        buttonMailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentDialog(MailboxParser.PAGE_NAME);
            }
        });

        buttonAdvisor = (ImageButton) v.findViewById(R.id.button_advisor);
        buttonAdvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentDialog(AdvisorParser.PAGE_NAME);
            }
        });

        buttonGrade = (ImageButton) v.findViewById(R.id.button_grade);
        buttonGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentDialog(GradeParser.PAGE_NAME);
            }
        });

        // TODO: make width equal height
        tableInfo = (TableLayout) v.findViewById(R.id.table_info);

        return v;
    }

    private void showContentDialog(String pageName) {
        FragmentManager fm = getActivity().getFragmentManager();
        ContentFragment fragment = ContentFragment.newInstance(pageName);
        fragment.show(fm, DIALOG_CONTENT);
    }

}
