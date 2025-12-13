package com.example.heartBuddy;

import android.view.View;
import android.view.ViewGroup;

public class Util {
    public static void set_enabled(View v, boolean enabled) {
        v.setEnabled(enabled);
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                Util.set_enabled(group.getChildAt(idx), enabled);
            }
        }
    }
}
