package com.darin.mysterious.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.Calendar;

public class StoryData {

    private String fileName;
    private int dayOfYear;

    public StoryData(String fileName, int dayOfYear) {
        this.fileName = fileName;
        this.dayOfYear = dayOfYear;
    }

    public boolean isAvailable() {
        int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return currentDayOfYear >= dayOfYear && currentDayOfYear - dayOfYear <= 21;
    }

    public boolean isDownloaded(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && getFile().exists();
    }

    public File getFile() {
        return new File(Environment.getExternalStorageDirectory() + "/mysterious/stories", fileName);
    }

}
