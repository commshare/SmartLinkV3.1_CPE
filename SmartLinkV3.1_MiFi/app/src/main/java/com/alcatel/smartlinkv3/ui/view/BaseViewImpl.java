package com.alcatel.smartlinkv3.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.alcatel.smartlinkv3.utils.ToastUtil_m;

import java.util.Arrays;
import java.util.List;

public abstract class BaseViewImpl {
    protected Context m_context;
    protected View m_view;
    
    public BaseViewImpl(Context context) {
        m_context = context;
    }

    protected void init() {
    }

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onDestroy();

    public View getView() {
        return m_view;
    }

    public void toast(int resid) {
        ToastUtil_m.show(m_context, resid);
    }

    public void toast(String content) {
        ToastUtil_m.show(m_context, content);
    }

    public void to(Class target, boolean isFinish) {
        Intent intent = new Intent(m_context, target);
        ((Activity)m_context).startActivity(intent);
        if (isFinish) {
            ((Activity)m_context).finish();
        }
    }

    public String[] getStringArr(@ArrayRes int arrayId) {
        return m_context.getResources().getStringArray(arrayId);
    }

    public List<String> getStringList(@ArrayRes int resId) {
        return Arrays.asList(getStringArr(resId));
    }

    public Drawable getDrawables(@DrawableRes int resId) {
        return m_context.getResources().getDrawable(resId);
    }
}
