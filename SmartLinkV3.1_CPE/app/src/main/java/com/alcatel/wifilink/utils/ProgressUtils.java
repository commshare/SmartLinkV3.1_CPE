package com.alcatel.wifilink.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

/**
 * Created by qianli.ma on 2017/7/31.
 */

public class ProgressUtils extends ProgressDialog {
    public ProgressUtils(Context context) {
        super(context);
    }

    public ProgressUtils(Context context, int theme) {
        super(context, theme);
    }

    public ProgressDialog getProgressPop(String msg) {
        setMessage(msg);
        show();
        return this;
    }

    public void dismissIt() {
        dismiss();
    }

}
