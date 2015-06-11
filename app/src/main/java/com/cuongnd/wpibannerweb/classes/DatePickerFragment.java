package com.cuongnd.wpibannerweb.classes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Cuong Nguyen
 */
public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_INITIAL_DATE = "InitialDate";

    public static DatePickerFragment newInstance(long initialTimeInMillis, DatePickerDialog.OnDateSetListener
                                          listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setOnDateSetListener(listener);

        Bundle args = new Bundle();
        args.putLong(EXTRA_INITIAL_DATE, initialTimeInMillis);

        fragment.setArguments(args);

        return fragment;
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getArguments().getLong(EXTRA_INITIAL_DATE));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
    }

    private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }

}
