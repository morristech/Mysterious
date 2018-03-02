package com.darin.mysterious;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

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

public class Mysterious extends Application {

    public static final String URL_STORIES_BASE = "https://jfenn.me/Mysterious/stories/";

    public static final int FAILURE_REASON_NETWORK = 0;
    public static final int FAILURE_REASON_PERMISSION_STORAGE = 1;

    private StoriesThread thread;
    private List<OnLoadListener> loadListeners;
    private boolean isLoaded;

    @Override
    public void onCreate() {
        super.onCreate();

        loadListeners = new ArrayList<>();
        reload();
    }

    public void reload() {
        thread = new StoriesThread(this);
        thread.start();
    }

    public List<StoryData> getStories() {
        List<StoryData> stories = new ArrayList<>();
        for (StoryData story : thread.stories) {
            if (story.isAvailable()) {
                stories.add(story);
            }
        }

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

    public interface OnLoadListener {
        void onLoad();

        void onLoad(StoryData story);

        void onFailure(int reason);
    }
}
