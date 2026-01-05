package com.example.heartBuddy.Data;

import android.util.Pair;

import com.example.heartBuddy.R;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
public class Series implements Serializable {

    public static Series from(List<String[]>data) {
        return new Series(data.stream()
                .map(row -> Datapoint.from(row))
                .collect(Collectors.toList())
        );
    }
    private ArrayList<Datapoint> data;

    private transient Runnable onSizeChangeHook = () -> {};

    public Series (Datapoint[] datapoints) {
        this(Arrays.stream(datapoints).collect(Collectors.toList()));
    }

    public Series (List<Datapoint> datapoints) {
        this.data = new ArrayList<>(datapoints);
    }

    public void setOnSizeChangeHook(Runnable onSizeChangeHook) {
        this.onSizeChangeHook = onSizeChangeHook;
    }

    public void add(Datapoint entry) {
        int index = Math.abs(Collections.binarySearch(this.data, entry, (dp1, dp2) -> dp1.getDateTime().compareTo(dp2.getDateTime())) + 1);
        this.data.add(index, entry);
        onSizeChangeHook.run();
    }

    public Datapoint get(int index) {
        return this.data.get(index);
    }

    public void set(int index, Datapoint entry) {
        if (
                (index == 0 || this.data.get(index-1).getDateTime().compareTo(entry.getDateTime()) <= 0) &&
                (index+1 == this.data.size() || this.data.get(index+1).getDateTime().compareTo(entry.getDateTime()) >= 0)
        ) {
            this.data.set(index, entry);
        } else {
            int insertIndex = Math.abs(Collections.binarySearch(this.data, entry, (dp1, dp2) -> dp1.getDateTime().compareTo(dp2.getDateTime())) + 1);
            if (insertIndex > index) {
                insertIndex--;
            }
            this.data.remove(index);
            this.data.add(insertIndex, entry);
        }
    }
    public void remove(int index) {
        this.data.remove(index);
        onSizeChangeHook.run();
    }


    public List<ZonedDateTime> getDateTimes() {
        return this.data.stream()
                .map(datapoint -> datapoint.getDateTime())
                .collect(Collectors.toList());
    }

    public List<Pair<ZonedDateTime, Double>> getHeartRates() {
        return this.data.stream()
                .map(datapoint -> Pair.create(datapoint.getDateTime(), datapoint.getHeartRate()))
                .collect(Collectors.toList());
    }

    public List<Pair<ZonedDateTime, Double>> getDiastolic() {
        return this.data.stream()
                .map(datapoint -> Pair.create(datapoint.getDateTime(), datapoint.getDiastolic()))
                .collect(Collectors.toList());
    }

    public List<Pair<ZonedDateTime, Double>> getSystolic() {
        return this.data.stream()
                .map(datapoint -> Pair.create(datapoint.getDateTime(), datapoint.getSystolic()))
                .collect(Collectors.toList());
    }

    public int size () {
        return this.data.size();
    }

    public Series map(Function<ZonedDateTime, ZonedDateTime> dtMap, Function<Double, Double> hrMap, Function<Double, Double> diaMap, Function<Double, Double> sysMap) {
        return new Series((this.data.stream()
                .map(datapoint -> new Datapoint(
                        dtMap.apply(datapoint.getDateTime()),
                        hrMap.apply(datapoint.getHeartRate()),
                        diaMap.apply(datapoint.getDiastolic()),
                        sysMap.apply(datapoint.getSystolic())
                    )
                )
                .collect(Collectors.toList()))
        );
    }

    public <T> List<T> extract(Function<? super Datapoint, T> extractor){
        return this.data.stream()
                .map(dp -> extractor.apply(dp))
                .collect(Collectors.toList());
    }

    public List<String[]> export() {
        return this.data.stream()
                .map(dp -> dp.export())
                .collect(Collectors.toList());
    }
}
