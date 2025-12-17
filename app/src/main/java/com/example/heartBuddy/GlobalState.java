package com.example.heartBuddy;

import android.content.Context;
import android.util.Pair;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.heartBuddy.Data.LocalObject;
import com.example.heartBuddy.Data.Series;

public class GlobalState {
    /* TODO LIST
     * 1. Import and Export (.csv)
     * 2. auto size hack for edittexts
     * 3. remove hardcoding of chart label size
     * 5. Deploy?
     */
    static Path appStorageRoot;
    public static LocalObject<Series> series;

    public static void init (Context context) {
        GlobalState.appStorageRoot = context.getFilesDir().toPath();
        GlobalState.series = new LocalObject<>(GlobalState.appStorageRoot, "series");
    }

}
