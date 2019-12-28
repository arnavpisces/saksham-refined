package com.zuccessful.trueharmony.utilities;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AxisDateValueFormatter implements IAxisValueFormatter {

    private static final String TAG = AxisDateValueFormatter.class.getSimpleName();
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf2;
    private ArrayList<String> dates;

    public AxisDateValueFormatter(ArrayList<String> dates) {
        this.dates = dates;
        sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        sdf2 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
//        long d = Long.parseLong(dates.get((int) value));
        try {
            Date date = sdf.parse(String.valueOf(value));
            Log.d(TAG, "LOG: " + value + " : " + value + " : " + date.toString());
            return sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(value);
    }
}
