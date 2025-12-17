package com.example.heartBuddy.Data;

import android.content.Context;
import android.widget.TextView;

import com.example.heartBuddy.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class ChartMarker extends MarkerView {
    private TextView tvContent;

    public ChartMarker(Context context) {
        super(context, R.layout.chart_marker);
        // this markerview only displays a textview
        tvContent = (TextView) findViewWithTag("chartMarkerText");
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText("" + e.getY()); // set the entry-value as the display text
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 4), -getHeight());
    }
}
