package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.network.Network;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.base.NetworkSettingHelper;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;

/**
 * Created by qianli.ma on 2017/12/11 0011.
 */

public class ModeHelper {
    private Activity activity;

    public ModeHelper(Activity activity) {
        this.activity = activity;
    }

    public void transfer(int mode) {
        // 1.现获取当前的Mode对象
        NetworkSettingHelper nshelper = new NetworkSettingHelper();
        nshelper.setOnNormalNetworkListener(attr -> {
            attr.setNetworkMode(mode);// 2.修改为目标类型
            // 3.提交请求
            RX.getInstant().setNetworkSettings(attr, new ResponseObject() {
                @Override
                protected void onSuccess(Object result) {
                    // 4.再次获取
                    NetworkSettingHelper networkSettingHelper = new NetworkSettingHelper();
                    networkSettingHelper.setOnNormalNetworkListener(result1 -> modeSuccessNext(result1));
                    networkSettingHelper.getNetworkSetting();
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    toast(R.string.connect_failed);
                }

                @Override
                public void onError(Throwable e) {
                    toast(R.string.connect_failed);
                }
            });
        });
        nshelper.getNetworkSetting();
    }

    private OnModeSuccessListener onModeSuccessListener;

    // 接口OnModeSuccessListener
    public interface OnModeSuccessListener {
        void modeSuccess(Network attr);
    }

    // 对外方式setOnModeSuccessListener
    public void setOnModeSuccessListener(OnModeSuccessListener onModeSuccessListener) {
        this.onModeSuccessListener = onModeSuccessListener;
    }

    // 封装方法modeSuccessNext
    private void modeSuccessNext(Network attr) {
        if (onModeSuccessListener != null) {
            onModeSuccessListener.modeSuccess(attr);
        }
    }

    public void toast(int resId) {
        ToastUtil_m.show(activity, resId);
    }

    public void toastLong(int resId) {
        ToastUtil_m.showLong(activity, resId);
    }

    public void toast(String content) {
        ToastUtil_m.show(activity, content);
    }

    public void to(Class ac, boolean isFinish) {
        CA.toActivity(activity, ac, false, isFinish, false, 0);
    }

}
