package com.alcatel.wifilink.ui.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.alcatel.wifilink.utils.PreferenceUtil;

import java.util.Locale;

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
    protected void onResume() {
        super.onResume();
        // 初始化PreferenceUtil
        PreferenceUtil.init(this);
        // 根据上次的语言设置，重新设置语言
        switchLanguage(PreferenceUtil.getString("language", "en"));
    }

    /**
     * <切换语言>
     *
     * @param language
     * @see [类、类#方法、类#成员]
     */
    protected void switchLanguage(String language) {
        // 设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else if (language.equals("ar")) {
            // 阿拉伯语
            config.locale = new Locale("ar");
        } else if (language.equals("de")) {
            // 德语
            config.locale = Locale.GERMANY;
        } else if (language.equals("es")) {
            // 西班牙语
            config.locale = new Locale("es");
        } else if (language.equals("it")) {
            // 意大利语
            config.locale = Locale.ITALIAN;
        } else if (language.equals("fr")) {
            // 法语
            config.locale = Locale.FRENCH;
        }
        resources.updateConfiguration(config, dm);

        // 保存设置语言的类型
        PreferenceUtil.commitString("language", language);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }
}
