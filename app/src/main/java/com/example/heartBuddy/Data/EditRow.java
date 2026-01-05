package com.example.heartBuddy.Data;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;


import com.example.heartBuddy.Util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.stream.Stream;

public abstract class EditRow extends DataRow{

    public static ViewGroup make(Activity context, int layoutId, DatePicker datePicker, TimePicker timePicker) {
        ViewGroup row = DataRow.make(context, layoutId);
        ToggleButton dateBtn = row.findViewWithTag("date");
        ToggleButton timeBtn = row.findViewWithTag("time");
        dateBtn.setTextOff(dateBtn.getText());
        dateBtn.setOnCheckedChangeListener((btn, checked) -> {
            row.requestFocus();
            if (checked) {
                Util.set_enabled(row, false);
                Util.set_enabled(btn, true);
                Util.set_enabled(timeBtn, true);
                timeBtn.setChecked(false);
                datePicker.setVisibility(View.VISIBLE);
            } else {
                datePicker.setVisibility(View.GONE);
                ((ToggleButton)btn).setTextOff(Util.format_date(LocalDate.of(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth())));
                Util.set_enabled(row, true);
                refreshSubmitButton(row);
                ToggleButton nextView = row.findViewWithTag("time");
                nextView.setChecked(true);
            }
        });
        timeBtn.setTextOff(timeBtn.getText());
        timeBtn.setOnCheckedChangeListener((btn, checked) -> {
            row.requestFocus();
            if (checked) {
                Util.set_enabled(row, false);
                Util.set_enabled(btn, true);
                Util.set_enabled(dateBtn, true);
                dateBtn.setChecked(false);
                timePicker.setVisibility(View.VISIBLE);
            } else {
                timePicker.setVisibility(View.GONE);
                ((ToggleButton)btn).setTextOff(Util.format_time(LocalTime.of(timePicker.getHour(), timePicker.getMinute())));
                Util.set_enabled(row, true);
                refreshSubmitButton(row);
            }
        });
        Iterator<TextView.OnEditorActionListener> doneListeners = Stream.<TextView.OnEditorActionListener>of(
                (v, i, e) -> {
                    View nextView = row.findViewWithTag("diastolic");
                    nextView.requestFocus();
                    return true;
                },
                (v, i, e) -> {
                    View nextView = row.findViewWithTag("heartRate");
                    nextView.requestFocus();
                    return true;
                },
                (v, i, e) -> {
                    View nextView = row.findViewWithTag("submit");
                    row.requestFocus();
                    if (nextView.isEnabled()) {
                        nextView.performClick();
                    }
                    return true;
                }
        ).iterator();
        EditText hrInput = row.findViewWithTag("systolic");
        EditText diasInput = row.findViewWithTag("diastolic");
        EditText sysInput = row.findViewWithTag("heartRate");
        TextView hrMask = row.findViewWithTag("hrMask");
        TextView diasMask = row.findViewWithTag("diasMask");
        TextView sysMask = row.findViewWithTag("sysMask");
        Iterator<TextWatcher> textListeners = Stream.of(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditText input = hrInput;
                        TextView mask = hrMask;
                        refreshTextSize(input, mask);
                        refreshSubmitButton(row);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }
                },
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditText input = diasInput;
                        TextView mask = diasMask;
                        refreshTextSize(input, mask);
                        refreshSubmitButton(row);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }
                },
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        EditText input = sysInput;
                        TextView mask = sysMask;
                        refreshTextSize(input, mask);
                        refreshSubmitButton(row);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }
                }
        ).iterator();
        Stream.of(hrInput, diasInput, sysInput)
                .peek(view -> view.setOnEditorActionListener(doneListeners.next()))
                .forEach(view -> view.addTextChangedListener(textListeners.next()));
        row.post(() -> refreshTextSize(row));
        return row;
    }
    private DatePicker datePicker;

    private TimePicker timePicker;

    public EditRow(
            ZonedDateTime dateTime,
            double heartRate,
            double diastolic,
            double systolic,
            DatePicker datePicker, TimePicker timePicker
    ){
        super(dateTime, heartRate, diastolic, systolic);
        this.setPickers(datePicker, timePicker);
    }

    @Override
    public void populate(ViewGroup row) {
        super.populate(row);
        this.resetDateTime(row);
    }

    public void resetDateTime(ViewGroup row) {
        row.<ToggleButton>findViewWithTag("date").setTextOff(Util.format_date(this.date));
        this.datePicker.updateDate(this.date.getYear(), this.date.getMonthValue()-1, this.date.getDayOfMonth());
        row.<ToggleButton>findViewWithTag("time").setTextOff(Util.format_time(this.time));
        this.timePicker.setHour(this.time.getHour());
        this.timePicker.setMinute(this.time.getMinute());
    }

    Datapoint submit(ViewGroup row) {
        return new Datapoint(
                ZonedDateTime.of(
                        LocalDate.of(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth()),
                        LocalTime.of(timePicker.getHour(), timePicker.getMinute()),
                        ZoneId.systemDefault()
                ),
                Double.parseDouble(row.<TextView>findViewWithTag("heartRate").getText().toString()),
                Double.parseDouble(row.<TextView>findViewWithTag("diastolic").getText().toString()),
                Double.parseDouble(row.<TextView>findViewWithTag("systolic").getText().toString())
        );
    }

    private static void refreshSubmitButton(ViewGroup row) {
        boolean enabled = Stream.<TextView>of(
                row.findViewWithTag("heartRate"),
                row.findViewWithTag("diastolic"),
                row.findViewWithTag("systolic")
        ).allMatch(view -> view.getText().length()>0);
        row.<Button>findViewWithTag("submit").setEnabled(enabled);
    }

    private static void refreshTextSize(EditText input, TextView mask) {

        Editable inputText = input.getText();
        if (inputText.length() == 0) {
            mask.setText(input.getHint());

        } else {
            mask.setText(inputText);
        }
        input.setTextSize(TypedValue.COMPLEX_UNIT_PX, mask.getTextSize());
    }
    public static void refreshTextSize(ViewGroup row) {
        Util.zipWith(
                Stream.<EditText>of(
                        row.findViewWithTag("heartRate"),
                        row.findViewWithTag("diastolic"),
                        row.findViewWithTag("systolic")
                ),
                Stream.<TextView>of(
                        row.findViewWithTag("hrMask"),
                        row.findViewWithTag("diasMask"),
                        row.findViewWithTag("sysMask")
                ),
                (input, mask) -> {refreshTextSize(input, mask); return true;})
                .forEach(input -> {});
    }

    public void setPickers(DatePicker datePicker, TimePicker timePicker) {
        this.datePicker = datePicker;
        this.timePicker = timePicker;
        if (datePicker != null) {
            this.datePicker.setVisibility(View.GONE);

        }
        if (timePicker != null) {
            this.timePicker.setVisibility(View.GONE);
        }
    }

    public static class NewRow extends EditRow{

        public static ViewGroup make(Activity context, int layoutId, DatePicker datePicker, TimePicker timePicker) {
            ViewGroup row = EditRow.make(context, layoutId, datePicker, timePicker);
            return row;
        }
        private Series series;

        public NewRow(
                Series series,
                DatePicker datePicker, TimePicker timePicker
        ){
            super(ZonedDateTime.now(), -1, -1, -1, datePicker, timePicker);
            this.series = series;

        }

        @Override
        public void populate(ViewGroup row) {
            super.populate(row);
            this.resetDateTime(row);
            row.<TextView>findViewWithTag("heartRate").setText("");
            row.<TextView>findViewWithTag("diastolic").setText("");
            row.<TextView>findViewWithTag("systolic").setText("");
            row.<Button>findViewWithTag("submit").setOnClickListener(btn -> {
                series.add(this.submit(row));
                row.<TextView>findViewWithTag("heartRate").setText("");
                row.<TextView>findViewWithTag("diastolic").setText("");
                row.<TextView>findViewWithTag("systolic").setText("");
                row.requestFocus();
            });
        }
    }

    public static class ModifyRow extends EditRow {
        public static ViewGroup make(Activity context, int layoutId, DatePicker datePicker, TimePicker timePicker) {
            ViewGroup row = EditRow.make(context, layoutId, datePicker, timePicker);
            View confirmRemove = row.findViewWithTag("confirmRemove");
            row.<ToggleButton>findViewWithTag("remove").setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    confirmRemove.setVisibility(View.VISIBLE);
                    confirmRemove.setEnabled(true);
                } else {
                    confirmRemove.setVisibility(View.GONE);
                    confirmRemove.setEnabled(false);
                }
            });
            return row;
        }
        private Series series;
        private int entryIndex;
        public ModifyRow(
                Series series,
                int entryIndex,
                ZonedDateTime dateTime,
                double heartRate,
                double diastolic,
                double systolic,
                DatePicker datePicker, TimePicker timePicker
        ){
            super(dateTime, heartRate, diastolic, systolic, datePicker, timePicker);
            this.series = series;
            this.entryIndex = entryIndex;
        }


        @Override
        public void populate(ViewGroup row) {
            super.populate(row);
            row.<ToggleButton>findViewWithTag("submit").setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    Util.set_enabled(row, true);
                    View nextView = row.findViewWithTag("systolic");
                    nextView.requestFocus();
                } else {
                    Util.set_enabled(row, false);
                    series.set(this.entryIndex, this.submit(row));
                    btn.setEnabled(true);
                    row.requestFocus();
                }
            });
            View confirmRemove = row.findViewWithTag("confirmRemove");
            confirmRemove.setOnClickListener(btn -> {
                series.remove(this.entryIndex);
                row.<ToggleButton>findViewWithTag("remove").setChecked(false);
            });

        }
    }

}
