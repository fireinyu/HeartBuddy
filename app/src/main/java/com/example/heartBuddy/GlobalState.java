package com.example.heartBuddy;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;


import java.nio.file.Path;

import com.example.heartBuddy.Data.LocalObject;
import com.example.heartBuddy.Data.Series;
import com.example.heartBuddy.ui.list.ListFragment;

public class GlobalState {
    /* TODO LIST
     * 2. auto size hack for edittexts (export page)
     * 3. remove hardcoding of chart label size
     * 5. Deploy?
     */
    static Path appStorageRoot;
    public static Path exportRoot;
    public static LocalObject<Series> series;
    public static String importValidateKey;
    public static void init (Activity context) {

        GlobalState.appStorageRoot = context.getFilesDir().toPath();
        GlobalState.exportRoot =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toPath();
        GlobalState.series = new LocalObject<>(GlobalState.appStorageRoot, "series");
        GlobalState.importValidateKey = context.getString(R.string.import_verify);
    }



}
