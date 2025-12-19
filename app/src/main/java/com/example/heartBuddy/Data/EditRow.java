package com.example.heartBuddy.Data;

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

import androidx.fragment.app.Fragment;

import com.example.heartBuddy.Util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public abstract class EditRow extends DataRow{

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Fragment context;
    private ViewGroup row;

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
        row = super.make();
        ToggleButton dateBtn = row.findViewWithTag("date");
        ToggleButton timeBtn = row.findViewWithTag("time");
        dateBtn.setTextOff(dateBtn.getText());
        dateBtn.setOnCheckedChangeListener((btn, checked) -> {
            this.row.requestFocus();
            if (checked) {
                Util.set_enabled(row, false);
                Util.set_enabled(btn, true);
                Util.set_enabled(timeBtn, true);
                timeBtn.setChecked(false);
                this.datePicker.setVisibility(View.VISIBLE);
            } else {
                this.datePicker.setVisibility(View.GONE);
                this.date = LocalDate.of(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth());
                ((ToggleButton)btn).setTextOff(Util.format_date(this.date));
                Util.set_enabled(row, true);
                this.refreshSubmitButton(row);
                ToggleButton nextView = row.findViewWithTag("time");
                nextView.setChecked(true);
            }
        });
        timeBtn.setTextOff(timeBtn.getText());
        timeBtn.setOnCheckedChangeListener((btn, checked) -> {
            this.row.requestFocus();
            if (checked) {
                Util.set_enabled(row, false);
                Util.set_enabled(btn, true);
                Util.set_enabled(dateBtn, true);
                dateBtn.setChecked(false);
                this.timePicker.setVisibility(View.VISIBLE);
            } else {
                this.timePicker.setVisibility(View.GONE);
                this.time = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                ((ToggleButton)btn).setTextOff(Util.format_time(this.time));
                Util.set_enabled(row, true);
                this.refreshSubmitButton(row);
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

        //goddamn
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
                        EditRow.this.refreshSubmitButton(row);
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
                        EditRow.this.refreshSubmitButton(row);
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
                        EditRow.this.refreshSubmitButton(row);
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
        row.post(() -> this.refreshTextSize());
        return row;
    }

    @Override
    public void resetDateTime() {
        super.resetDateTime();
        this.row.<TextView>findViewWithTag("date").setText(Util.format_date(this.date));
        this.datePicker.updateDate(this.date.getYear(), this.date.getMonthValue()-1, this.date.getDayOfMonth());
        this.row.<TextView>findViewWithTag("time").setText(Util.format_time(this.time));
        this.timePicker.setHour(this.time.getHour());
        this.timePicker.setMinute(this.time.getMinute());

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

    private void refreshTextSize(EditText input, TextView mask) {

        Editable inputText = input.getText();
        if (inputText.length() == 0) {
            mask.setText(input.getHint());

        } else {
            mask.setText(inputText);
        }
        input.setTextSize(TypedValue.COMPLEX_UNIT_PX, mask.getTextSize());
    }
    public void refreshTextSize() {
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
                row.requestFocus();
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
                    View nextView = row.findViewWithTag("systolic");
                    nextView.requestFocus();
                } else {
                    Util.set_enabled(row, false);
                    Series series = this.series.get().orElse(new Series(new ArrayList<>()));
                    series.set(this.entryIndex, this.submit(row));
                    this.series.put(series);
                    btn.setEnabled(true);
                    row.requestFocus();
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
            return row;
        }
    }

}
