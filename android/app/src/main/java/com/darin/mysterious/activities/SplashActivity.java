package com.darin.mysterious.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.darin.mysterious.Mysterious;
import com.darin.mysterious.R;
import com.darin.mysterious.data.StoryData;

public class SplashActivity extends AppCompatActivity implements Mysterious.OnLoadListener {

    private Mysterious mysterious;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mysterious = (Mysterious) getApplicationContext();

        if (mysterious.isLoaded())
            onLoad();
        else mysterious.addOnLoadListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mysterious.removeOnLoadListener(this);
    }

    @Override
    public void onLoad() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onLoad(StoryData story) {
    }

    @Override
    public void onFailure(int reason) {
        Toast.makeText(this, "Failed to do something.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
