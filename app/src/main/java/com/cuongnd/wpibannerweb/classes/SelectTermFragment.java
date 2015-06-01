package com.cuongnd.wpibannerweb.classes;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cuongnd.wpibannerweb.helper.Utils;

import java.util.ArrayList;

/**
 * Created by Cuong Nguyen on 5/29/2015.
 */
public class SelectTermFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetTermsTask().execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Utils.TermValue selectedTerm = (Utils.TermValue) getListAdapter().getItem(position);
        Intent i = new Intent(getActivity(), ClassesActivity.class);
        i.putExtra(ClassesFragment.EXTRA_TERM_ID, selectedTerm.getValue());
        startActivity(i);
    }

    private class GetTermsTask extends AsyncTask<Void, Void, ArrayList<Utils.TermValue>> {
        @Override
        protected ArrayList<Utils.TermValue> doInBackground(Void... params) {
            return ClassesPage.getTerms();
        }

        @Override
        protected void onPostExecute(ArrayList<Utils.TermValue> termValues) {
            ArrayAdapter<Utils.TermValue> adapter =
                    new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, termValues);
            SelectTermFragment.this.setListAdapter(adapter);
        }
    }
}
