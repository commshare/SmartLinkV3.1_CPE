package com.alcatel.wifilink.rx.helper.base;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by qianli.ma on 2018/2/8 0008.
 */

public abstract class FeedbackEnterWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // 当前字数
        getCurrentLength(s.toString().length());
    }

    public abstract void getCurrentLength(int length);
}
