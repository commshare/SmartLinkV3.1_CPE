package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.ToastUtil_m;

import org.w3c.dom.Text;

/**
 * Created by qianli.ma on 2017/12/13 0013.
 */

public class SetTimeLimitHelper {
    private Activity activity;

    public SetTimeLimitHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 增加ED监听器
     *
     * @param etHour
     * @param etMin
     */
    public static void addEdwatch(EditText etHour, EditText etMin) {
        // 修改过程中的限定
        etHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int hour = Integer.valueOf(TextUtils.isEmpty(OtherUtils.getEdContent(etHour)) ? "0" : OtherUtils.getEdContent(etHour));
                int min = Integer.valueOf(TextUtils.isEmpty(OtherUtils.getEdContent(etMin)) ? "0" : OtherUtils.getEdContent(etMin));
                if (hour >= 12 & min > 0) {
                    etMin.setText("0");
                    etHour.setText("12");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 得到焦点后的限定
        etHour.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                int hour = Integer.valueOf(TextUtils.isEmpty(OtherUtils.getEdContent(etHour)) ? "0" : OtherUtils.getEdContent(etHour));
                int min = Integer.valueOf(TextUtils.isEmpty(OtherUtils.getEdContent(etMin)) ? "0" : OtherUtils.getEdContent(etMin));
                if (hour >= 12 & min > 0) {
                    etMin.setText("0");
                    etHour.setText("12");
                }
            }
        });

        etMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int hour = Integer.valueOf(TextUtils.isEmpty(OtherUtils.getEdContent(etHour)) ? "0" : OtherUtils.getEdContent(etHour));
                int min = Integer.valueOf(TextUtils.isEmpty(OtherUtils.getEdContent(etMin)) ? "0" : OtherUtils.getEdContent(etMin));
                if (hour >= 12 & min != 0) {// 防止死循环必须把加入min!=0这个条件, 否则陷入onTextChanged死循环
                    etMin.setText("0");
                } else {
                    if (min > 59) {
                        etMin.setText("59");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etMin.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                int hour = Integer.valueOf(TextUtils.isEmpty(OtherUtils.getEdContent(etHour)) ? "0" : OtherUtils.getEdContent(etHour));
                int min = Integer.valueOf(TextUtils.isEmpty(OtherUtils.getEdContent(etMin)) ? "0" : OtherUtils.getEdContent(etMin));
                if (hour >= 12 & min != 0) {
                    etMin.setText("0");
                } else {
                    if (min > 59) {
                        etMin.setText("59");
                    }
                }
            }
        });
    }

    /**
     * 设置set time limit
     *
     * @param etHour
     * @param etMin
     */
    public void setTimelimit(EditText etHour, EditText etMin) {
        String hour = OtherUtils.getEdContent(etHour);
        String min = OtherUtils.getEdContent(etMin);
        if (TextUtils.isEmpty(hour) || TextUtils.isEmpty(min)) {
            toast(R.string.not_empty);
            return;
        }
        int hour_i = Integer.valueOf(hour);
        int min_i = Integer.valueOf(min);
        // TODO: 2017/12/13 0013
    }

    private void toast(int resId) {
        ToastUtil_m.show(activity, resId);
    }

    private void toastLong(int resId) {
        ToastUtil_m.showLong(activity, resId);
    }

    private void toast(String content) {
        ToastUtil_m.show(activity, content);
    }

    private void to(Class ac, boolean isFinish) {
        CA.toActivity(activity, ac, false, isFinish, false, 0);
    }
}
