package com.example.heartBuddy.Data;

import android.util.Log;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Datapoint implements Serializable {

    public static Datapoint from(String[] data) {
        Log.d("debug_import",data[0]);
        return new Datapoint(
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(data[0])), ZoneId.systemDefault()),
                Double.valueOf(data[3]),
                Double.valueOf(data[2]),
                Double.valueOf(data[1])
        );
    }
    private Long dateTime;

    private double heartRate;
    private double diastolic;
    private double systolic;

    public Datapoint (ZonedDateTime dateTime, double heartRate, double diastolic, double systolic) {
        this.dateTime = dateTime.toEpochSecond();
        this.heartRate = heartRate;
        this.diastolic = diastolic;
        this.systolic = systolic;
    }

    public ZonedDateTime getDateTime() {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(this.dateTime), ZoneId.systemDefault());
    }

    public double getHeartRate() {
        return this.heartRate;
    }

    public double getDiastolic() {
        return this.diastolic;
    }

    public double getSystolic() {
        return this.systolic;
    }

    public String[] export() {
        Log.d("debug_export","" + dateTime + " " + systolic + diastolic + heartRate);
        return Stream.of(dateTime, systolic, diastolic, heartRate)
                .map(val -> String.valueOf(val))
                .collect(Collectors.toList())
                .toArray(new String[]{});
    }
}
