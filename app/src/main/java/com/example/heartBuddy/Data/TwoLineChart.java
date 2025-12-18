package com.example.heartBuddy.Data;

import android.util.Pair;

import com.example.heartBuddy.Util;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private int axisColor;

    public TwoLineChart(
            LineChart hrChart, LineChart bpChart,
            String hrTitle, String bpTitle,
            String diastolicLabel, String systolicLabel,
            int hrColor, int diastolicColor, int systolicColor,
            int axisColor
    ) {
        this.hrChart = hrChart; this.bpChart = bpChart;
        this.diastolicLabel = diastolicLabel; this.systolicLabel  =systolicLabel;
        this.hrColor = hrColor; this.diastolicColor = diastolicColor; this.systolicColor = systolicColor;
        this.hrChart.setData(Optional.ofNullable(this.hrChart.getLineData()).orElse(new LineData()));
        this.bpChart.setData(Optional.ofNullable(this.bpChart.getLineData()).orElse(new LineData()));
        this.hrChart.getDescription().setText(hrTitle);
        this.bpChart.getDescription().setText(bpTitle);
        this.plotted = new ArrayList<>();
        this.axisColor = axisColor;
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
        dataSet.setDrawValues(false);
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
        dataSet.setDrawValues(false);
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
        dataSet.setDrawValues(false);
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
            public String getFormattedValue(float delta) {
                return Util.format_date(start.plus(Duration.ofSeconds((long)delta)).toLocalDate());
            }
        };
        float TEXTSIZE = 22;
        MarkerView marker = new ChartMarker(this.hrChart.getContext());
        this.hrChart.setGridBackgroundColor(0);
        this.hrChart.setMarker(marker);
        this.hrChart.getLegend().setEnabled(false);
        this.hrChart.getAxisLeft().setEnabled(false);
        this.hrChart.getXAxis().setValueFormatter(dateTimeFormatter);
        this.hrChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        this.hrChart.getAxisRight().setTextSize(TEXTSIZE);
        this.hrChart.getAxisRight().setLabelCount(6);
        this.hrChart.getAxisRight().setTextColor(this.axisColor);
        this.hrChart.getAxisRight().setAxisLineColor(this.axisColor);
        this.hrChart.getAxisRight().setGridColor(this.axisColor);
        this.hrChart.getXAxis().setGranularity(Duration.ofDays(1).getSeconds());
        this.hrChart.getXAxis().setTextSize(TEXTSIZE);
        this.hrChart.getXAxis().setLabelCount(4);
        this.hrChart.getXAxis().setTextColor(this.axisColor);
        this.hrChart.getXAxis().setAxisLineColor(this.axisColor);
        this.hrChart.getXAxis().setGridColor(this.axisColor);
        this.bpChart.setGridBackgroundColor(0);
        this.bpChart.setMarker(marker);
        this.bpChart.getLegend().setEnabled(false);
        this.bpChart.getAxisLeft().setEnabled(false);
        this.bpChart.getAxisRight().setTextSize(TEXTSIZE);
        this.bpChart.getXAxis().setValueFormatter(dateTimeFormatter);
        this.bpChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        this.bpChart.getAxisRight().setGridColor(this.axisColor);
        this.bpChart.getAxisRight().setTextColor(this.axisColor);
        this.bpChart.getAxisRight().setAxisLineColor(this.axisColor);
        this.bpChart.getAxisRight().setGridColor(this.axisColor);
        this.bpChart.getXAxis().setTextSize(TEXTSIZE);
        this.bpChart.getAxisRight().setLabelCount(6);
        this.bpChart.getXAxis().setGranularity(Duration.ofDays(1).getSeconds());
        this.bpChart.getXAxis().setLabelCount(4);
        this.bpChart.getXAxis().setGridColor(this.axisColor);
        this.bpChart.getXAxis().setTextColor(this.axisColor);
        this.bpChart.getXAxis().setAxisLineColor(this.axisColor);
        this.bpChart.getXAxis().setGridColor(this.axisColor);
        this.bpChart.invalidate();
        this.hrChart.invalidate();
        super.plot(data.map(dt -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(dt.toEpochSecond() - start.toEpochSecond()), ZoneId.systemDefault()), x -> x, x -> x, x -> x));
        this.hrChart.notifyDataSetChanged();
        this.bpChart.notifyDataSetChanged();

    }

}