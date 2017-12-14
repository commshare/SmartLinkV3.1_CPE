package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.rx.helper.base.UsageSettingHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;

/**
 * Created by qianli.ma on 2017/12/13 0013.
 */

public class BillingHelper {
    private Activity activity;

    public BillingHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置结算日
     *
     * @param day
     */
    public void setBillingDay(int day) {
        // 1. 先获取当前的流量对象
        UsageSettingHelper ush_g = new UsageSettingHelper(activity);
        ush_g.setOngetSuccessListener(attr -> {
            // 2.修改结算日
            attr.setBillingDay(day);
            // 3.提交最新请求
            UsageSettingHelper ush_s = new UsageSettingHelper(activity);
            ush_s.setOnSetSuccessListener(attr1 -> {
                toast(R.string.success);
            });
            ush_s.setUsageSetting(attr);
        });
        ush_g.getUsageSetting();
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
