package com.darin.mysterious.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.darin.mysterious.Mysterious;
import com.darin.mysterious.R;
import com.darin.mysterious.adapters.StoryAdapter;
import com.darin.mysterious.data.StoryData;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Mysterious.OnLoadListener {

    private static final int REQUEST_PERMISSIONS = 521;

    private Mysterious mysterious;
    private List<StoryData> stories;

    private Toolbar toolbar;
    private RecyclerView recycler;

    private StoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        recycler = findViewById(R.id.recycler);

        mysterious = (Mysterious) getApplicationContext();
        mysterious.addOnLoadListener(this);

        stories = mysterious.getStories();
        adapter = new StoryAdapter(stories);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mysterious.removeOnLoadListener(this);
    }

    @Override
    public void onLoad() {
        stories = mysterious.getStories();
        adapter = new StoryAdapter(stories);
        recycler.swapAdapter(adapter, true);
    }

    @Override
    public void onChange(StoryData story) {
        adapter.notifyItemChanged(stories.indexOf(story));
    }

    @Override
    public void onFailure(int reason) {
        if (reason == Mysterious.FAILURE_REASON_PERMISSION_STORAGE) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    return;
            }

            mysterious.getStories();
        }
    }
}
