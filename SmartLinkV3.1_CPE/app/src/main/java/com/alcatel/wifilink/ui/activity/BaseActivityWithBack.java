package com.alcatel.wifilink.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class BaseActivityWithBack extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar baseActionBar = getSupportActionBar();
        if (baseActionBar != null) {
            baseActionBar.setHomeButtonEnabled(true);
            baseActionBar.setDisplayHomeAsUpEnabled(true);
            baseActionBar.setElevation(0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }
}
