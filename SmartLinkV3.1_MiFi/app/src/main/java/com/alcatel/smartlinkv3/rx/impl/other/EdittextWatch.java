package com.alcatel.smartlinkv3.rx.impl.other;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by qianli.ma on 2017/11/3 0003.
 */

public abstract class EdittextWatch implements TextWatcher {

    public abstract void textchange(String s);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textchange(String.valueOf(s));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
