package com.example.heartBuddy.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Datapoint implements Serializable {

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

}
