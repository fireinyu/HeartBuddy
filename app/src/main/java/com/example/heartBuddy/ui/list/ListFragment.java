package com.example.heartBuddy.ui.list;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.heartBuddy.Data.EditRow;
import com.example.heartBuddy.Data.LocalObject;
import com.example.heartBuddy.Data.Series;
import com.example.heartBuddy.GlobalState;
import com.example.heartBuddy.R;
import com.example.heartBuddy.Util;
import com.example.heartBuddy.databinding.FragmentListBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private View root;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private LinearLayout modifyList;
    private List<Consumer<LocalObject<Series>>> hooks = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        this.root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.datePicker = root.findViewById(R.id.listDatePicker);
        this.timePicker = root.findViewById(R.id.listTimePicker);
        this.modifyList = root.findViewById(R.id.modifyList);
        Consumer<LocalObject<Series>> hook = obj -> this.refresh();
        GlobalState.series.addHook(hook);
        this.hooks.add(hook);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.refresh();
    }

    public void refresh() {
        this.modifyList.removeAllViews();
        Series series = GlobalState.series.get().orElse(new Series(new ArrayList<>()));
        LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
        EditRow.ModifyRow.resetIndex();
        Util.for_each(root, v -> v.setClickable(true));
        Util.for_each(root, v -> v.setFocusableInTouchMode(true));
        Util.for_each(root, v -> v.setOnFocusChangeListener(Util::toggleKeyboard));
        List<EditRow.ModifyRow> rows = series.extract(dp -> new EditRow.ModifyRow(
                        this,
                        R.layout.row_modify,
                        GlobalState.series,
                        EditRow.ModifyRow.nextIndex(),
                        dp.getDateTime(),
                        dp.getHeartRate(),
                        dp.getDiastolic(),
                        dp.getSystolic(),
                        this.datePicker, this.timePicker
                ));
        rows.stream()
                .map(row -> row.make())
                .peek(row -> row.setLayoutParams(formParams))
                .forEach(row -> this.modifyList.addView(row,0));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        this.hooks.forEach(hook->GlobalState.series.removeHook(hook));
        this.hooks.clear();
    }
}