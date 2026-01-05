package com.example.heartBuddy.ui.home;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartBuddy.Data.DataRow;
import com.example.heartBuddy.Data.Datapoint;
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
    private Series series;
    private ViewGroup addRowContainer;
    private LineChart hrChart;
    private LineChart bpChart;
    private RecyclerView peekList;
    private Plotter plotter;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ViewGroup addRow;

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
        this.plotter = new TwoLineChart(
                this.hrChart, this.bpChart,
                getString(R.string.hr_chart_title), getString(R.string.bp_chart_title),
                getString(R.string.diastolic_line_label), getString(R.string.systolic_line_label),
                Util.get_color(R.attr.hrColor),
                Util.get_color(R.attr.diasColor),
                Util.get_color(R.attr.sysColor),
                Util.get_color(R.attr.defaultFontColor)
        );
        this.datePicker = this.root.findViewById(R.id.homeDatePicker);
        this.timePicker = this.root.findViewById(R.id.homeTimePicker);
        Util.for_each(root, v -> v.setClickable(true));
        Util.for_each(root, v -> v.setFocusableInTouchMode(true));
        Util.for_each(datePicker, v -> v.setFocusable(false));
        Util.for_each(timePicker, v -> v.setFocusable(false));
        Util.for_each(root, v -> v.setOnFocusChangeListener(Util::toggleKeyboard));
        addRow = EditRow.NewRow.make(getActivity(), R.layout.row_add, datePicker, timePicker);
        addRowContainer.addView(addRow);

        // why the other branch layoutparams are used when any branch runs ??
        ConstraintLayout constraintLayout = (ConstraintLayout) root;
        ConstraintSet constraintSet = new ConstraintSet();
        addRow.addOnLayoutChangeListener((vk, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            boolean isImeVisible = root.getRootWindowInsets() != null && root.getRootWindowInsets().isVisible(WindowInsets.Type.ime());

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

    }

    @Override
    public void onResume() {
        super.onResume();
        this.series = GlobalState.series.get().orElse(new Series(List.of()));
        plotter.unplot();
        plotter.plot(series);
        this.series.setOnSizeChangeHook(() -> {
            plotter.unplot();
            plotter.plot(series);
            this.peekList.getAdapter().notifyDataSetChanged();
        });
        new EditRow.NewRow(
                this.series,
                this.datePicker,
                this.timePicker
        ).populate(addRow);
        this.peekList = root.findViewById(R.id.peekList);
        this.peekList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.peekList.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new Util.BasicViewHolder(DataRow.make(getActivity(), R.layout.row_peek), 200);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Datapoint dp = series.get(series.size() -1 - position);
                new DataRow(
                        dp.getDateTime(),
                        dp.getHeartRate(),
                        dp.getDiastolic(),
                        dp.getSystolic()
                ).populate((ViewGroup) holder.itemView);
            }

            @Override
            public int getItemCount() {
                return series.size();
            }
        });
    }

    @Override
    public void onStop() {
        GlobalState.series.put(this.series);
        super.onStop();
    }
}