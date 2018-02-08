package com.alcatel.wifilink.rx.ui;

import android.os.Bundle;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.system.SystemStates;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.rx.helper.base.LoginStateHelper;
import com.alcatel.wifilink.ui.activity.BaseActivityWithBack;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.Logs;
import com.alcatel.wifilink.utils.OtherUtils;
import com.alcatel.wifilink.utils.SP;
import com.alcatel.wifilink.utils.ToastUtil_m;

public class LoadingRxActivity extends BaseActivityWithBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_rx);
        stopAll();// 停止所有的定时器和context残余
        LoginStateHelper lsh = new LoginStateHelper(this);
        lsh.setOnErrorListener(attr -> to(RefreshWifiRxActivity.class, true, 2000));
        lsh.setOnResultErrorListener(attr -> to(RefreshWifiRxActivity.class, true, 2000));
        lsh.setOnNoWifiListener(attr -> to(RefreshWifiRxActivity.class, true, 2000));
        lsh.setOnYesWifiListener(attr -> Logs.t("ma_loading").vv("wifi is effect"));
        lsh.setOnLoginstateListener(attr -> nextSkip());
        lsh.get();
    }

    /**
     * 停止所有的定时器和context残余
     */
    private void stopAll() {
        OtherUtils.clearAllTimer();
        OtherUtils.stopHomeTimer();
        OtherUtils.clearContexts(getClass().getSimpleName());
    }

    /**
     * 下一步跳转
     */
    private void nextSkip() {
        boolean guideRx_flag = SP.getInstance(this).getBoolean(Cons.GUIDE_RX, false);
        if (guideRx_flag) {
            CA.toActivity(this, LoginRxActivity.class, false, true, false, 2000);
        } else {
            CA.toActivity(this, GuideRxActivity.class, false, true, false, 2000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void toast(int resId) {
        ToastUtil_m.show(this, resId);
    }

    private void toastLong(int resId) {
        ToastUtil_m.showLong(this, resId);
    }

    private void toast(String content) {
        ToastUtil_m.show(this, content);
    }

    private void to(Class ac, boolean isFinish) {
        CA.toActivity(this, ac, false, isFinish, false, 0);
    }

    private void to(Class ac, boolean isFinish, int delay) {
        CA.toActivity(this, ac, false, isFinish, false, delay);
    }
}
