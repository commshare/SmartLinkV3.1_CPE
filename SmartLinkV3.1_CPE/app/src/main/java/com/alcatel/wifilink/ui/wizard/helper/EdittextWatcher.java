package com.alcatel.wifilink.ui.wizard.helper;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by qianli.ma on 2017/6/13.
 */

public abstract class EdittextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        textChange();
    }

    public abstract void textChange();
}
