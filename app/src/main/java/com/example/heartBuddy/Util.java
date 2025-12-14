package com.example.heartBuddy;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.compress.utils.Iterators;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class Util {

    private static Context context;

    public static void init(Context context) {
        Util.context = context;
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

    public static int get_color(int attrId) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attrId, value, true);
        return value.data;
    }

    public static <T, U, R> Stream<R> zipWith(Stream<T> main, Stream<U> second, BiFunction<? super T, ? super U, ? extends R> combiner) {
        Iterator<U> secondIter = second.iterator();
        return main.map(
                item -> combiner.apply(item, secondIter.next())
        );
    }
}
