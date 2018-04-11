package com.darin.mysterious.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.darin.mysterious.Mysterious;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class StoryData implements Comparable<StoryData> {

    private String fileName;
    private int dayOfYear;

    private Book book;
    private boolean isLoading;
    private int failure;

    public StoryData(String fileName, int dayOfYear) {
        this.fileName = fileName;
        this.dayOfYear = dayOfYear;
        isLoading = true;
    }

    /**
     * @return true if the book's release date was less than 21 days ago
     */
    public boolean isAvailable() {
        int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return currentDayOfYear >= dayOfYear && currentDayOfYear - dayOfYear <= 21;
    }

    public boolean isDownloaded(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && getFile().exists();
    }

    public String getDate() {
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        while (!calendar.before(now)) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
        }

        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
    }

    public int getDay() {
        return dayOfYear;
    }

    public long getRemainingMillis() {
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        while (!now.before(calendar)) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        }

        return calendar.getTime().getTime() - System.currentTimeMillis();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public Book getBook() {
        return book;
    }

    public int getFailure() {
        return failure;
    }

    /**
     * Loads the book.epub for this story. Is NOT thread safe.
     */
    public void loadBook(EpubReader reader) {
        try {
            book = reader.readEpub(new FileInputStream(getFile()));
        } catch (FileNotFoundException e) {
            failure = Mysterious.FAILURE_NOT_DOWNLOADED;
        } catch (IOException e) {
            e.printStackTrace();
            failure = Mysterious.FAILURE_REASON_UNKNOWN;
        } catch (SecurityException e) {
            e.printStackTrace();
            failure = Mysterious.FAILURE_REASON_PERMISSION_STORAGE;
        }

        isLoading = false;
    }

    public File getFile() {
        return new File(Environment.getExternalStorageDirectory() + "/mysterious/stories", fileName);
    }

    public String getUrl(String prefix) {
        return prefix + fileName;
    }

    @Override
    public int compareTo(@NonNull StoryData o) {
        return o.dayOfYear - dayOfYear;
    }

    @Override
    public String toString() {
        return "fileName=" + fileName + "\n"
                + "dayOfYear=" + dayOfYear + "\n"
                + "isAvailable=" + isAvailable() + "\n"
                + "isLoading=" + isLoading + "\n"
                + "failure=" + failure + "\n"
                + "remainingMillis=" + getRemainingMillis() + "\n";
    }
}
