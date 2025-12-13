package com.example.heartBuddy.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartBuddy.Data.DataRow;
import com.example.heartBuddy.Data.EditRow;
import com.example.heartBuddy.Data.LocalObject;
import com.example.heartBuddy.Data.Plotter;
import com.example.heartBuddy.Data.Series;
import com.example.heartBuddy.Data.TwoLineChart;
import com.example.heartBuddy.GlobalState;
import com.example.heartBuddy.R;
import com.example.heartBuddy.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ai.djl.nn.core.Linear;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private View root;
    private ViewGroup addRowContainer;
    private LineChart hrChart;
    private LineChart bpChart;
    private LinearLayout peekList;
    private Plotter plotter;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditRow.NewRow addRow;
    private List<Consumer<LocalObject<Series>>> seriesHooks =  new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        this.root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.addRowContainer = this.root.findViewById(R.id.addRowContainer);
        this.hrChart = this.root.findViewById(R.id.hrChart);
        this.bpChart = this.root.findViewById(R.id.bpChart);
        this.peekList = this.root.findViewById(R.id.peekList);
        this.plotter = new TwoLineChart(
                this.hrChart, this.bpChart,
                getString(R.string.hr_chart_title), getString(R.string.bp_chart_title),
                getString(R.string.diastolic_line_label), getString(R.string.systolic_line_label),
                R.color.hr_line, R.color.diastolic_line, R.color.systolic_line
        );
        this.datePicker = this.root.findViewById(R.id.homeDatePicker);
        this.timePicker = this.root.findViewById(R.id.homeTimePicker);
        this.addRow = new EditRow.NewRow(
                this,
                R.layout.row_add,
                GlobalState.series,
                this.datePicker,
                this.timePicker
        );
        View v = this.addRow.make();
        v.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addRowContainer.addView(v);
        Log.d("checknull", String.valueOf(GlobalState.series));

        Consumer<LocalObject<Series>> hook = obj -> this.refresh();
        GlobalState.series.addHook(hook);
        this.seriesHooks.add(hook);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.refresh();
    }
    public void refresh() {
        this.peekList.removeAllViews();
        Series series = GlobalState.series.get().orElse(new Series(new ArrayList<>()));
        LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        series.extract(dp -> new DataRow(
                        this,
                        R.layout.row_peek,
                        dp.getDateTime(),
                        dp.getHeartRate(),
                        dp.getDiastolic(),
                        dp.getSystolic()
                )).stream()
                .map(row -> row.make())
                .peek(row -> row.setLayoutParams(formParams))
                .forEach(row -> this.peekList.addView(row,0));
        this.plotter.unplot();
        this.plotter.plot(series);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        this.seriesHooks.forEach(hook -> GlobalState.series.removeHook(hook));
        this.seriesHooks.clear();
    }
}