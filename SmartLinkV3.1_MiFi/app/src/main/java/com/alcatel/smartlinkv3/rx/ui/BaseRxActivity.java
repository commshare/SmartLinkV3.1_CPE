package com.alcatel.smartlinkv3.rx.ui;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;

import java.util.Locale;

/**
 * Created by qianli.ma on 2017/9/27.
 */

public class BaseRxActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setLanguage();// 设置语言
    }

    private void setLanguage() {
        Resources resource = getResources();
        Configuration config = resource.getConfiguration();
        config.setLocale(Locale.getDefault());
        DisplayMetrics dm = getResources().getDisplayMetrics();
        getResources().updateConfiguration(config, dm);
    }
}
