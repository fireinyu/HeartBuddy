package com.example.heartBuddy.Data;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.heartBuddy.Util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;


public class DataRow {

    public static ViewGroup make(Activity context, int layoutId) {
        ViewGroup row = (ViewGroup) context.getLayoutInflater().inflate(layoutId, null);
        row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return row;
    }
    LocalDate date;
    LocalTime time;
    private double heartRate;
    private double diastolic;
    private double systolic;

    public DataRow(
            ZonedDateTime dateTime,
            double heartRate,
            double diastolic,
            double systolic
    ){
        this.date = dateTime.toLocalDate();
        this.time = dateTime.toLocalTime();
        this.heartRate = heartRate;
        this.diastolic = diastolic;
        this.systolic = systolic;
    }

    public void populate(ViewGroup form) {
        ((TextView) form.findViewWithTag("date")).setText(Util.format_date(date));
        ((TextView) form.findViewWithTag("time")).setText(Util.format_time(time));
        ((TextView) form.findViewWithTag("heartRate")).setText(String.valueOf(Math.round(this.heartRate)));
        ((TextView) form.findViewWithTag("diastolic")).setText(String.valueOf(Math.round(this.diastolic)));
        ((TextView) form.findViewWithTag("systolic")).setText(String.valueOf(Math.round(this.systolic)));
        Util.for_each(form, view -> view.setOnFocusChangeListener(Util::toggleKeyboard));
    }

    public void resetDateTime() {
        this.date = ZonedDateTime.now().toLocalDate();
        this.time = ZonedDateTime.now().toLocalTime();
    }
}
