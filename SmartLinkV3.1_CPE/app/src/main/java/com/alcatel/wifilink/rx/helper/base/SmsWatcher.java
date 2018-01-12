package com.alcatel.wifilink.rx.helper.base;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.utils.ToastUtil_m;

/**
 * Created by qianli.ma on 2018/1/5 0005.
 */

public class SmsWatcher implements TextWatcher {

    private int maxLength = 1530;
    private Context context;
    private EditText et;

    public SmsWatcher(Context context, EditText et) {
        this.context = context;
        this.et = et;
        et.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() >= maxLength) {
            String smsLength = context.getString(R.string.sms_max_length);
            String content = String.format(smsLength, maxLength);
            ToastUtil_m.show(context, content);
        }
    }
}
