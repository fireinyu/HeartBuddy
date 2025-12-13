package com.example.heartBuddy.Data;

import android.util.Pair;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TwoLineChart extends Plotter {
    private LineChart hrChart;
    private LineChart bpChart;
    private List<LineDataSet> plotted;
    private String diastolicLabel;
    private String systolicLabel;

    private int hrColor;
    private int diastolicColor;
    private int systolicColor;

    public TwoLineChart(
            LineChart hrChart, LineChart bpChart,
            String hrTitle, String bpTitle,
            String diastolicLabel, String systolicLabel,
            int hrColor, int diastolicColor, int systolicColor
    ) {
        this.hrChart = hrChart; this.bpChart = bpChart;
        this.diastolicLabel = diastolicLabel; this.systolicLabel  =systolicLabel;
        this.hrColor = hrColor; this.diastolicColor = diastolicColor; this.systolicColor = systolicColor;
        this.hrChart.setData(Optional.ofNullable(this.hrChart.getLineData()).orElse(new LineData()));
        this.bpChart.setData(Optional.ofNullable(this.bpChart.getLineData()).orElse(new LineData()));
        this.hrChart.getDescription().setText(hrTitle);
        this.bpChart.getDescription().setText(bpTitle);
        this.plotted = new ArrayList<>();
    }

    @Override
    public void unplot() {
        for (LineDataSet dataSet : this.plotted) {
            this.hrChart.getLineData().removeDataSet(dataSet);
            this.bpChart.getLineData().removeDataSet(dataSet);
        }
        this.hrChart.notifyDataSetChanged();
        this.bpChart.notifyDataSetChanged();
        this.plotted = new ArrayList<>();
    }

    @Override
    protected void plotHeartRates(List<Pair<ZonedDateTime, Double>> data) {
        List<Entry> entries = data.stream()
                .map(pair -> new Entry(pair.first.toEpochSecond(), pair.second.floatValue()))
                .collect(Collectors.toList());
        LineDataSet dataSet = new LineDataSet(entries, null);
        dataSet.setColor(this.hrColor);
//        dataSet.setDrawCircles(false);
//        dataSet.setDrawCircleHole(false);
        this.hrChart.getLineData().addDataSet(dataSet);
        this.plotted.add(dataSet);
    }

    @Override
    protected void plotDiastolic(List<Pair<ZonedDateTime, Double>> data) {
        List<Entry> entries = data.stream()
                .map(pair -> new Entry(pair.first.toEpochSecond(), pair.second.floatValue()))
                .collect(Collectors.toList());
        LineDataSet dataSet = new LineDataSet(entries, this.diastolicLabel);
        dataSet.setColor(this.diastolicColor);
//        dataSet.setDrawCircles(false);
//        dataSet.setDrawCircleHole(false);
        this.bpChart.getLineData().addDataSet(dataSet);
        this.plotted.add(dataSet);
    }

    @Override
    protected void plotSystolic(List<Pair<ZonedDateTime, Double>> data) {
        List<Entry> entries = data.stream()
                .map(pair -> new Entry(pair.first.toEpochSecond(), pair.second.floatValue()))
                .collect(Collectors.toList());
        LineDataSet dataSet = new LineDataSet(entries, this.systolicLabel);
        dataSet.setColor(this.systolicColor);
//        dataSet.setDrawCircles(false);
//        dataSet.setDrawCircleHole(false);
        this.bpChart.getLineData().addDataSet(dataSet);
        this.plotted.add(dataSet);
    }

    @Override
    public void plot(Series data) {
        if (data.size() == 0) {
            return;
        }
        ZonedDateTime start = data.getDateTimes().get(0);
        ValueFormatter dateTimeFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                ZonedDateTime dt = start.plus(Duration.ofSeconds((long) value));
                return String.format(
                        "%d/%d",
                        dt.getDayOfMonth(),
                        dt.getMonth().getValue()
                );
            }
        };
        this.hrChart.getXAxis().setValueFormatter(dateTimeFormatter);
        this.bpChart.getXAxis().setValueFormatter(dateTimeFormatter);
        this.hrChart.invalidate();
        this.bpChart.invalidate();
        super.plot(data.map(dt -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(dt.toEpochSecond() - start.toEpochSecond()), ZoneId.systemDefault()), x -> x, x -> x, x -> x));
        this.hrChart.notifyDataSetChanged();
        this.bpChart.notifyDataSetChanged();

    }

}