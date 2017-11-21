package com.alcatel.wifilink.rx.ui;

import android.os.Bundle;
import android.view.Window;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;

public class HomeRxActivity extends BaseActivityWithBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_rx);
    }
}
