package com.example.heartBuddy.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;

import com.example.heartBuddy.Data.DataRow;
import com.example.heartBuddy.Data.EditRow;
import com.example.heartBuddy.Data.LocalObject;
import com.example.heartBuddy.Data.Plotter;
import com.example.heartBuddy.Data.Series;
import com.example.heartBuddy.Data.TwoLineChart;
import com.example.heartBuddy.GlobalState;
import com.example.heartBuddy.R;
import com.example.heartBuddy.Util;
import com.example.heartBuddy.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
                Util.get_color(R.attr.hrColor),
                Util.get_color(R.attr.diasColor),
                Util.get_color(R.attr.sysColor),
                Util.get_color(R.attr.defaultFontColor)
        );
        Log.d("colors", ""+R.attr.hrColor+" "+R.attr.diasColor+" "+R.attr.sysColor);
        this.datePicker = this.root.findViewById(R.id.homeDatePicker);
        this.timePicker = this.root.findViewById(R.id.homeTimePicker);
        this.addRow = new EditRow.NewRow(
                this,
                R.layout.row_add,
                GlobalState.series,
                this.datePicker,
                this.timePicker
        );
        Util.for_each(root, v -> v.setClickable(true));
        Util.for_each(root, v -> v.setFocusableInTouchMode(true));
        Util.for_each(root, v -> v.setOnFocusChangeListener(Util::toggleKeyboard));
        View v = this.addRow.make();
        this.addRowContainer.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // why the other branch layoutparams are used when any branch runs ??
        ConstraintLayout constraintLayout = (ConstraintLayout) root;
        ConstraintSet constraintSet = new ConstraintSet();
        v.addOnLayoutChangeListener((vk, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            boolean isImeVisible = root.getRootWindowInsets() != null && root.getRootWindowInsets().isVisible(WindowInsets.Type.ime());
            Log.d("debug_layout", "IME visible: " + isImeVisible);

            constraintSet.clone(constraintLayout);

            if (isImeVisible) {
                // Anchor the top of addRowContainer to the top of the guideline/view meant for when the keyboard is open
                // Assuming you have a view or guideline with this ID in your layout
                constraintSet.connect(R.id.addRowContainer, ConstraintSet.TOP, R.id.peekListTop, ConstraintSet.TOP);
            } else {
                // Anchor the top of addRowContainer to the bottom of the peek list when keyboard is closed
                constraintSet.connect(R.id.addRowContainer, ConstraintSet.TOP, R.id.peekListBottom, ConstraintSet.BOTTOM);
            }

            // Apply the modified constraints
            constraintSet.applyTo(constraintLayout);
        });
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
        LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);
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