package com.cuongnd.wpibannerweb;

import android.app.Fragment;
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

/**
 * Created by Cuong Nguyen on 5/10/2015.
 */
public class DashboardFragment extends Fragment {

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
        textWpiId = (TextView) v.findViewById(R.id.text_wpiid);

        textTest = (TextView) v.findViewById(R.id.text_test);

        buttonBalance = (ImageButton) v.findViewById(R.id.button_balance);
        buttonBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPageTask getTask = new GetPageTask();
                getTask.execute("https://bannerweb.wpi.edu/pls/prod/hwwkcbrd.P_Display");
            }
        });
        buttonMailbox = (ImageButton) v.findViewById(R.id.button_mailbox);
        buttonAdvisor = (ImageButton) v.findViewById(R.id.button_advisor);
        buttonGrade = (ImageButton) v.findViewById(R.id.button_grade);

        // TODO: make width equal height
        tableInfo = (TableLayout) v.findViewById(R.id.table_info);

        return v;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return connectionManager.getPage(params[0]);
        }

        @Override
        protected void onPostExecute(final String data) {
            textTest.setText(data);
        }

        @Override
        protected void onCancelled() {
        }
    }
}
