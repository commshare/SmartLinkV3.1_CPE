package com.alcatel.wifilink.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alcatel.wifilink.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ma_load", "TestActivity1");
        setContentView(R.layout.activity_test);
        Log.d("ma_load", "TestActivity2");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ma_load", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ma_load", "onStart");
    }
}
