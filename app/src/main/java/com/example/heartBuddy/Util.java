package com.example.heartBuddy;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Util {

    private static Context context;
    private static InputMethodManager imm;

    public static void init(Context context) {
        Util.context = context;
        imm = (InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    }

    public static String format_date(LocalDate date) {
        return String.format("%02d.%02d", date.getMonth().getValue(), date.getDayOfMonth());
    }

    public static String format_time(LocalTime time) {
        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }
    public static void set_enabled(View v, boolean enabled) {
        v.setEnabled(enabled);
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                Util.set_enabled(group.getChildAt(idx), enabled);
            }
        }
    }

    public static void for_each(View v, Consumer<View> consumer) {
        consumer.accept(v);
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                Util.for_each(group.getChildAt(idx), consumer);
            }
        }
    }

    public static int get_color(int attrId) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attrId, value, true);
        return value.data;
    }

    public static void toggleKeyboard(View view, boolean isFocused) {
        if (isFocused) {
            if (view instanceof EditText) {
                imm.showSoftInput(view, 0);
            } else {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static <T, U, R> Stream<R> zipWith(Stream<T> main, Stream<U> second, BiFunction<? super T, ? super U, ? extends R> combiner) {
        Iterator<U> secondIter = second.iterator();
        return main.map(
                item -> combiner.apply(item, secondIter.next())
        );
    }
}
