package com.darin.mysterious;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import com.darin.mysterious.data.StoryData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.epub.EpubReader;

public class Mysterious extends Application {

    public static final String URL_STORIES_BASE = "https://jfenn.me/Mysterious/stories/";

    public static final int FAILURE_REASON_NETWORK = 1;
    public static final int FAILURE_REASON_PERMISSION_STORAGE = 2;
    public static final int FAILURE_NOT_DOWNLOADED = 3;
    public static final int FAILURE_REASON_UNKNOWN = 4;

    private StoriesThread storiesThread;
    private BooksThread booksThread;
    private List<OnLoadListener> loadListeners;
    private boolean isLoaded;

    @Override
    public void onCreate() {
        super.onCreate();

        loadListeners = new ArrayList<>();
        reload();
    }

    public void reload() {
        if (storiesThread != null && storiesThread.isAlive())
            storiesThread.interrupt();
        storiesThread = new StoriesThread(this);
        storiesThread.start();
    }

    public List<StoryData> getStories() {
        List<StoryData> stories = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            for (StoryData story : storiesThread.stories) {
                if (story.isAvailable() && story.isDownloaded(this)) {
                    stories.add(story);
                }
            }
        } else {
            onFailure(FAILURE_REASON_PERMISSION_STORAGE);
            return stories;
        }

        if (booksThread != null && booksThread.isAlive())
            booksThread.interrupt();
        booksThread = new BooksThread(this, stories);
        booksThread.start();

        return stories;
    }

    public void addOnLoadListener(OnLoadListener listener) {
        loadListeners.add(listener);
    }

    public void removeOnLoadListener(OnLoadListener listener) {
        loadListeners.remove(listener);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    private void onLoad() {
        isLoaded = true;
        for (OnLoadListener listener : loadListeners) {
            listener.onLoad();
        }
    }

    private void onChange(StoryData story) {
        for (OnLoadListener listener : loadListeners) {
            listener.onChange(story);
        }
    }

    private void onFailure(int reason) {
        for (OnLoadListener listener : loadListeners) {
            listener.onFailure(reason);
        }
    }

    private static class StoriesThread extends Thread {

        private List<StoryData> stories;
        private Mysterious mysterious;

        public StoriesThread(Mysterious mysterious) {
            stories = new ArrayList<>();
            this.mysterious = mysterious;
        }

        @Override
        public void run() {
            HttpURLConnection request;

            try {
                request = (HttpURLConnection) new URL(URL_STORIES_BASE + "data.json").openConnection();
                request.connect();

                JsonObject object = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
                for (String url : object.keySet()) {
                    stories.add(new StoryData(url, object.get(url).getAsInt()));
                }

            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mysterious.onFailure(FAILURE_REASON_NETWORK);
                    }
                });
                return;
            }

            mysterious.onLoad();
            request.disconnect();
        }
    }

    private static class BooksThread extends Thread {

        private Mysterious mysterious;
        private List<StoryData> stories;

        public BooksThread(Mysterious mysterious, List<StoryData> stories) {
            this.mysterious = mysterious;
            this.stories = stories;
        }

        @Override
        public void run() {
            EpubReader reader = new EpubReader();
            for (StoryData story : stories) {
                story.loadBook(reader);
                new Handler(Looper.getMainLooper())
                        .post(new ChangeStoryRunnable(mysterious, story));
            }
        }

        private static class ChangeStoryRunnable implements Runnable {

            private Mysterious mysterious;
            private StoryData story;

            private ChangeStoryRunnable(Mysterious mysterious, StoryData story) {
                this.mysterious = mysterious;
                this.story = story;
            }

            @Override
            public void run() {
                mysterious.onChange(story);
            }
        }
    }

    public interface OnLoadListener {
        void onLoad();

        void onChange(StoryData story);

        void onFailure(int reason);
    }
}
