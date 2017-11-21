package com.alcatel.wifilink.rx.ui;

import android.os.Bundle;
import android.view.Window;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.SP;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

public class LoadingRxActivity extends BaseActivityWithBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_rx);
        boolean guideRx_flag = SP.getInstance(this).getBoolean(Cons.GUIDE_RX, false);
        if (guideRx_flag) {
            CA.toActivity(this, LoginRxActivity.class, false, true, false, 2000);
        } else {
            CA.toActivity(this, GuideRxActivity.class, false, true, false, 2000);
        }
    }
}
