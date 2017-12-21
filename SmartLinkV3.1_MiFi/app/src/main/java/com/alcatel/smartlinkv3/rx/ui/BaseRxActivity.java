package com.alcatel.smartlinkv3.rx.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;

import com.alcatel.smartlinkv3.utils.ToastUtil_m;

import java.util.Arrays;
import java.util.List;
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

    public void toast(int resid) {
        ToastUtil_m.show(this, resid);
    }

    public void toast(String content) {
        ToastUtil_m.show(this, content);
    }

    public void to(Class target, boolean isFinish) {
        Intent intent = new Intent(this, target);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public String[] getStringArr(@ArrayRes int arrayId) {
        return getResources().getStringArray(arrayId);
    }

    public List<String> getStringList(@ArrayRes int resId) {
        return Arrays.asList(getStringArr(resId));
    }

    public Drawable getDrawables(@DrawableRes int resId) {
        return getResources().getDrawable(resId);
    }
}
