package com.alcatel.smartlinkv3.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alcatel.smartlinkv3.R;

public class WlanAdvancedSettingsActivity extends AppCompatActivity {

    private static final String TAG = "WlanAdvancedSettingsAct";
    private String mWlanType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlan_advanced_settings);
        ActionBar baseActionBar = getSupportActionBar();
        if (baseActionBar != null) {
            baseActionBar.setDisplayHomeAsUpEnabled(true);
            baseActionBar.setElevation(0);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (intent != null) {
            mWlanType = intent.getStringExtra("wlan_type");
            Log.d(TAG, "onCreate, mWlanType:"+mWlanType);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }
}
