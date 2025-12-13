package com.example.heartBuddy.Data;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.example.heartBuddy.Util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class EditRow extends DataRow{

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Fragment context;


    public EditRow(
            Fragment context,
            int templateId,
            ZonedDateTime dateTime,
            double heartRate,
            double diastolic,
            double systolic,
            DatePicker datePicker, TimePicker timePicker
    ){
        super(context, templateId, dateTime, heartRate, diastolic, systolic);
        this.context = context;
        this.datePicker = datePicker;
        this.timePicker = timePicker;
        this.datePicker.setVisibility(View.GONE);
        this.timePicker.setVisibility(View.GONE);
    }

    @Override
    public ViewGroup make() {
        ViewGroup row = super.make();
        ToggleButton dateBtn = row.findViewWithTag("date");
        ToggleButton timeBtn = row.findViewWithTag("time");
        dateBtn.setTextOff(dateBtn.getText());
        dateBtn.setOnCheckedChangeListener((btn, checked) -> {
            Optional.ofNullable(this.context.getActivity().getCurrentFocus()).ifPresent(view -> view.clearFocus());
            InputMethodManager imm = (InputMethodManager) this.context.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btn.getWindowToken(), 0);
            if (checked) {
                Util.set_enabled(row, false);
                Util.set_enabled(btn, true);
                this.datePicker.setVisibility(View.VISIBLE);
                timeBtn.setChecked(false);
            } else {
                Util.set_enabled(row, true);
                this.refreshSubmitButton(row);
                this.datePicker.setVisibility(View.GONE);
                this.date = LocalDate.of(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth());
                ((ToggleButton)btn).setTextOff(String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonth().getValue(), date.getYear()));
            }
        });
        timeBtn.setTextOff(timeBtn.getText());
        timeBtn.setOnCheckedChangeListener((btn, checked) -> {
            Optional.ofNullable(this.context.getActivity().getCurrentFocus()).ifPresent(view -> view.clearFocus());
            InputMethodManager imm = (InputMethodManager) this.context.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btn.getWindowToken(), 0);
            if (checked) {
                Util.set_enabled(row, false);
                Util.set_enabled(btn, true);
                this.timePicker.setVisibility(View.VISIBLE);
                dateBtn.setChecked(false);
            } else {
                Util.set_enabled(row, true);
                this.refreshSubmitButton(row);
                this.timePicker.setVisibility(View.GONE);
                this.time = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                ((ToggleButton)btn).setTextOff(String.format("%02d:%02d", time.getHour(), time.getMinute()));
            }
        });
        TextWatcher listener = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                return;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               EditRow.this.refreshSubmitButton(row);
            }

        };
        Stream.<TextView>of(
                        row.findViewWithTag("heartRate"),
                        row.findViewWithTag("diastolic"),
                        row.findViewWithTag("systolic")
                )
                .filter(view -> view instanceof EditText)
                .map(view -> (EditText)view)
                .forEach(view -> view.addTextChangedListener(listener));

        return row;
    }

    Datapoint submit(ViewGroup row) {
        return new Datapoint(
                ZonedDateTime.of(this.date, this.time, ZoneId.systemDefault()),
                Double.parseDouble(row.<TextView>findViewWithTag("heartRate").getText().toString()),
                Double.parseDouble(row.<TextView>findViewWithTag("diastolic").getText().toString()),
                Double.parseDouble(row.<TextView>findViewWithTag("systolic").getText().toString())
        );
    }

    private void refreshSubmitButton(ViewGroup row) {
        boolean enabled = Stream.<TextView>of(
                row.findViewWithTag("heartRate"),
                row.findViewWithTag("diastolic"),
                row.findViewWithTag("systolic")
        ).allMatch(view -> view.getText().length()>0);
        row.<Button>findViewWithTag("submit").setEnabled(enabled);
    }
    public static class NewRow extends EditRow{
        private LocalObject<Series> series;
        public NewRow(
                Fragment context,
                int templateId,
                LocalObject<Series> series,
                DatePicker datePicker, TimePicker timePicker
        ){
            super(context, templateId, ZonedDateTime.now(), -1, -1, -1, datePicker, timePicker);
            this.series = series;
        }

        @Override
        public ViewGroup make() {
            ViewGroup row = super.make();
            row.<TextView>findViewWithTag("heartRate").setText("");
            row.<TextView>findViewWithTag("diastolic").setText("");
            row.<TextView>findViewWithTag("systolic").setText("");
            row.<Button>findViewWithTag("submit").setOnClickListener(btn -> {
                Series series = this.series.get().orElse(new Series(new ArrayList<>()));
                series.add(this.submit(row));
                this.series.put(series);
                row.<TextView>findViewWithTag("heartRate").setText("");
                row.<TextView>findViewWithTag("diastolic").setText("");
                row.<TextView>findViewWithTag("systolic").setText("");
            });
            return row;
        }
    }
    public static class ModifyRow extends EditRow {

        private static int head = -1;
        public static int nextIndex() {
            return ++ModifyRow.head;
        }
        public static void resetIndex() {
            ModifyRow.head = -1;
        }
        private LocalObject<Series> series;
        private int entryIndex;
        public ModifyRow(
                Fragment context,
                int templateId,
                LocalObject<Series> series,
                int entryIndex,
                ZonedDateTime dateTime,
                double heartRate,
                double diastolic,
                double systolic,
                DatePicker datePicker, TimePicker timePicker
        ){
            super(context, templateId, dateTime, heartRate, diastolic, systolic, datePicker, timePicker);
            this.series = series;
            this.entryIndex = entryIndex;
        }

        @Override
        public ViewGroup make() {
            ViewGroup row = super.make();
            row.<ToggleButton>findViewWithTag("submit").setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    Util.set_enabled(row, true);
                } else {
                    Util.set_enabled(row, false);
                    Series series = this.series.get().orElse(new Series(new ArrayList<>()));
                    series.set(this.entryIndex, this.submit(row));
                    this.series.put(series);
                    btn.setEnabled(true);
                }
            });
            View confirmRemove = row.findViewWithTag("confirmRemove");
            confirmRemove.setOnClickListener(btn -> {
                Series series = this.series.get().orElse(new Series(new ArrayList<>()));
                series.remove(this.entryIndex);
                this.series.put(series);
            });
            row.<ToggleButton>findViewWithTag("remove").setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    confirmRemove.setVisibility(View.VISIBLE);
                    confirmRemove.setEnabled(true);
                } else {
                    confirmRemove.setVisibility(View.GONE);
                    confirmRemove.setEnabled(false);
                }
            });
            Log.d("check_refresh",String.valueOf(((TextView) row.findViewWithTag("heartRate")).getText()));
            return row;
        }
    }

}
