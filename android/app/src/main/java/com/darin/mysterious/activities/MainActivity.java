package com.darin.mysterious.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.darin.mysterious.Mysterious;
import com.darin.mysterious.R;
import com.darin.mysterious.adapters.StoryAdapter;
import com.darin.mysterious.data.StoryData;
import com.darin.mysterious.utils.FormatUtils;

import java.util.List;

import me.jfenn.attribouter.Attribouter;

public class MainActivity extends AppCompatActivity implements Mysterious.OnLoadListener {

    private static final int REQUEST_PERMISSIONS = 521;

    private Mysterious mysterious;
    private List<StoryData> stories;

    private Handler handler;
    private Runnable runnable;

    private Toolbar toolbar;
    private RecyclerView recycler;
    private TextView countDownView;
    private View downloadLayout;
    private View downloadButton;

    private StoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        recycler = findViewById(R.id.recycler);
        countDownView = findViewById(R.id.countdown);
        downloadLayout = findViewById(R.id.download);
        downloadButton = findViewById(R.id.downloadOk);

        setSupportActionBar(toolbar);

        mysterious = (Mysterious) getApplicationContext();
        mysterious.addOnLoadListener(this);

        stories = mysterious.getStories();
        adapter = new StoryAdapter(stories);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        recycler.setAdapter(adapter);

        downloadLayout.setVisibility(mysterious.getUnDownloadedStories().size() > 0 ? View.VISIBLE : View.GONE);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mysterious.downloadStories(mysterious.getUnDownloadedStories());
                downloadLayout.setVisibility(View.GONE);
            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                countDownView.setText(FormatUtils.formatMillis(mysterious.millisUntilNextStory()));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mysterious.removeOnLoadListener(this);
        if (handler != null)
            handler.removeCallbacks(runnable);
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
            //TODO: show AlertDialog
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Attribouter.from(this)
                    .withFile(R.xml.attribouter)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}
