package com.example.root.proto2.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.root.proto2.R;

import java.util.Calendar;

/**
 * Created by Sanket on 11/24/2017.
 */

public class DatePickerFrag extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c =Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int dayname=c.get(Calendar.DAY_OF_WEEK);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
        String month= Integer.toString(datePicker.getMonth());
        String year=Integer.toString(datePicker.getYear());
        String day=Integer.toString(datePicker.getDayOfMonth());
        String dayname= Integer.toString(Calendar.HOUR);
        TextView datetext=(TextView) getActivity().findViewById(R.id.date_view);
        datetext.setText(day+","+month+dayname);
    }
}
