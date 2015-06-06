package com.cuongnd.wpibannerweb.grade;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cuongnd.wpibannerweb.helper.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Cuong Nguyen on 5/29/2015.
 */
public class GradeSelectTermFragment extends ListFragment {

    private static final String TAG = "GradeSelectTermFragment";

    private GetTermsTask mGetTermsTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGetTermsTask = new GetTermsTask();
        mGetTermsTask.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Utils.TermValue selectedTerm = (Utils.TermValue) getListAdapter().getItem(position);
        Intent i = new Intent(getActivity(), FinalGradeActivity.class);
        i.putExtra(FinalGradeFragment.EXTRA_TERM_ID, selectedTerm.getValue());
        startActivity(i);
    }

    @Override
    public void onStop() {
        if (mGetTermsTask != null) {
            mGetTermsTask.cancel(false);
            mGetTermsTask = null;
        }
        super.onStop();
    }

    private class GetTermsTask extends AsyncTask<Void, Void, ArrayList<Utils.TermValue>> {
        @Override
        protected ArrayList<Utils.TermValue> doInBackground(Void... params) {
            if (isCancelled())
                return null;
            try {
                return FinalGradePage.getTerms();
            } catch (IOException e) {
                Utils.logError(TAG, e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Utils.TermValue> termValues) {
            if (isCancelled() || termValues == null)
                return;
            ArrayAdapter<Utils.TermValue> adapter =
                    new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, termValues);
            GradeSelectTermFragment.this.setListAdapter(adapter);
        }
    }
}
