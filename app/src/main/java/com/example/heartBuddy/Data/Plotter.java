package com.example.heartBuddy.Data;

import android.util.Pair;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class Plotter {



    protected abstract void plotHeartRates (List<Pair<ZonedDateTime, Double>> data);

    protected abstract void plotDiastolic (List<Pair<ZonedDateTime, Double>> data);

    protected abstract void plotSystolic (List<Pair<ZonedDateTime, Double>> data);

    public void plot (Series data) {
        this.plotHeartRates(data.getHeartRates());
        this.plotDiastolic(data.getDiastolic());
        this.plotSystolic(data.getSystolic());
    }


    public abstract void unplot ();

}
