package com.example.heartBuddy.Data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.fragment.app.Fragment;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DataRow {
    private ViewGroup template;
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
        this.template = (ViewGroup) context.getLayoutInflater().inflate(templateId, null, false);
        this.date = dateTime.toLocalDate();
        this.time = dateTime.toLocalTime();
        this.heartRate = heartRate;
        this.diastolic = diastolic;
        this.systolic = systolic;
    }

    public ViewGroup make() {
        ((TextView) this.template.findViewWithTag("date")).setText(String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonth().getValue(), date.getYear()));
        ((TextView) this.template.findViewWithTag("time")).setText(String.format("%02d:%02d", time.getHour(), time.getMinute()));
        ((TextView) this.template.findViewWithTag("heartRate")).setText(String.valueOf(Math.round(this.heartRate)));
        ((TextView) this.template.findViewWithTag("diastolic")).setText(String.valueOf(Math.round(this.diastolic)));
        ((TextView) this.template.findViewWithTag("systolic")).setText(String.valueOf(Math.round(this.systolic)));

        return this.template;
    }

}
