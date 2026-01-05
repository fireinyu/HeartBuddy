package com.example.heartBuddy.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartBuddy.Data.Datapoint;
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

public class ListFragment extends Fragment {

    private Series series;
    private FragmentListBinding binding;
    private View root;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private RecyclerView modifyList;
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
        Util.for_each(root, v -> v.setClickable(true));
        Util.for_each(root, v -> v.setFocusableInTouchMode(true));
        Util.for_each(datePicker, v -> v.setFocusable(false));
        Util.for_each(timePicker, v -> v.setFocusable(false));
        Util.for_each(root, v -> v.setOnFocusChangeListener(Util::toggleKeyboard));
    }

    @Override
    public void onResume() {
        super.onResume();
        this.series = GlobalState.series.get().orElse(new Series(List.of()));
        this.series.setOnSizeChangeHook(() -> this.modifyList.getAdapter().notifyDataSetChanged());

        this.modifyList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.modifyList.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               RecyclerView.ViewHolder holder = new Util.BasicViewHolder(EditRow.ModifyRow.make(getActivity(), R.layout.row_modify, datePicker, timePicker), 300);
               return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Datapoint dp = series.get(series.size() - 1 - position);
                new EditRow.ModifyRow(
                        series,
                        series.size() - 1 - position,
                        dp.getDateTime(),
                        dp.getHeartRate(),
                        dp.getDiastolic(),
                        dp.getSystolic(),
                        datePicker,
                        timePicker
                ).populate((ViewGroup)holder.itemView);

            }

            @Override
            public int getItemCount() {
                return series.size();
            }
        });
    }

//    public void refresh() {
//        this.modifyList.removeAllViews();
//        Series series = GlobalState.series.get().orElse(new Series(new ArrayList<>()));
//        LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
//        EditRow.ModifyRow.resetIndex();
//        Util.for_each(root, v -> v.setClickable(true));
//        Util.for_each(root, v -> v.setFocusableInTouchMode(true));
//        Util.for_each(datePicker, v -> v.setFocusable(false));
//        Util.for_each(timePicker, v -> v.setFocusable(false));
//        Util.for_each(root, v -> v.setOnFocusChangeListener(Util::toggleKeyboard));
//        List<EditRow.ModifyRow> rows = series.extract(dp -> new EditRow.ModifyRow(
//                        getActivity(),
//                        R.layout.row_modify,
//                        GlobalState.series,
//                        EditRow.ModifyRow.nextIndex(),
//                        dp.getDateTime(),
//                        dp.getHeartRate(),
//                        dp.getDiastolic(),
//                        dp.getSystolic(),
//                        this.datePicker, this.timePicker
//                ));
//        rows.stream()
//                .map(row -> row.make())
//                .peek(row -> row.setLayoutParams(formParams))
//                .forEach(row -> this.modifyList.addView(row,0));
//    }


    @Override
    public void onStop() {
        GlobalState.series.put(this.series);
        super.onStop();
    }
}