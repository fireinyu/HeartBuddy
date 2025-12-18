package com.example.heartBuddy.Data;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.heartBuddy.Util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;


public class DataRow {
    private int templateId;
    private Fragment context;
    LocalDate date;
    LocalTime time;
    private double heartRate;
    private double diastolic;
    private double systolic;

    public DataRow(
            Fragment context,
            int templateId,
            ZonedDateTime dateTime,
            double heartRate,
            double diastolic,
            double systolic
    ){
        this.templateId = templateId;
        this.context = context;
        this.date = dateTime.toLocalDate();
        this.time = dateTime.toLocalTime();
        this.heartRate = heartRate;
        this.diastolic = diastolic;
        this.systolic = systolic;
    }

    public ViewGroup make() {
        ViewGroup template = (ViewGroup) context.getLayoutInflater().inflate(templateId, null, false);
        ((TextView) template.findViewWithTag("date")).setText(Util.format_date(date));
        ((TextView) template.findViewWithTag("time")).setText(Util.format_time(time));
        ((TextView) template.findViewWithTag("heartRate")).setText(String.valueOf(Math.round(this.heartRate)));
        ((TextView) template.findViewWithTag("diastolic")).setText(String.valueOf(Math.round(this.diastolic)));
        ((TextView) template.findViewWithTag("systolic")).setText(String.valueOf(Math.round(this.systolic)));
        Util.for_each(template, view -> view.setOnFocusChangeListener(Util::toggleKeyboard));
        return template;
    }

}
