package com.alcatel.wifilink.rx.helper;

import com.alcatel.wifilink.model.wlan.WlanSetting;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

/**
 * Created by qianli.ma on 2017/11/27 0027.
 */

public class WpsHelper {

    /**
     * 获取WPS状态
     */
    public void getWpsStatus() {

        API.get().getWlanSetting(new MySubscriber<WlanSetting>() {
            @Override
            protected void onSuccess(WlanSetting result) {
                WlanSetting.AP2GBean ap2G = result.getAP2G();
                WlanSetting.AP2GGuestBean ap2G_guest = result.getAP2G_guest();
                WlanSetting.AP5GBean ap5G = result.getAP5G();
                WlanSetting.AP5GGuestBean ap5G_guest = result.getAP5G_guest();
                boolean isWpsWork = false;

                // 检测2.4G下的WPS状态
                if (ap2G != null) {
                    int apStatus = ap2G.getApStatus();
                    if (apStatus == Cons.WPS) {
                        isWpsWork = true;
                    }
                }

                // 检测2.4G-guest下的WPS状态
                if (ap2G_guest != null) {
                    int apStatus = ap2G_guest.getApStatus();
                    if (apStatus == Cons.WPS) {
                        isWpsWork = true;
                    }
                }

                // 检测5G下的WPS状态
                if (ap5G != null) {
                    int apStatus = ap5G.getApStatus();
                    if (apStatus == Cons.WPS) {
                        isWpsWork = true;
                    }
                }

                // 检测5G-guest下的WPS状态
                if (ap5G_guest != null) {
                    int apStatus = ap5G_guest.getApStatus();
                    if (apStatus == Cons.WPS) {
                        isWpsWork = true;
                    }
                }

                // 对接
                getWpsNext(isWpsWork);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                resultErrorNext(error);
            }

            @Override
            public void onError(Throwable e) {
                errorNext(e);
            }
        });
    }

    /* -------------------------------------------- interface -------------------------------------------- */

    private OnWpsListener onWpsListener;

    // 接口OnWlansettingListener
    public interface OnWpsListener {
        void getWlansetting(boolean attr);
    }

    // 对外方式setOnWlansettingListener
    public void setOnWpsListener(OnWpsListener onWpsListener) {
        this.onWpsListener = onWpsListener;
    }

    // 封装方法getWlansettingNext
    private void getWpsNext(boolean attr) {
        if (onWpsListener != null) {
            onWpsListener.getWlansetting(attr);
        }
    }

    private OnResultErrorListener onResultErrorListener;

    // 接口OnResultErrorListener
    public interface OnResultErrorListener {
        void resultError(ResponseBody.Error attr);
    }

    // 对外方式setOnResultErrorListener
    public void setOnResultErrorListener(OnResultErrorListener onResultErrorListener) {
        this.onResultErrorListener = onResultErrorListener;
    }

    // 封装方法resultErrorNext
    private void resultErrorNext(ResponseBody.Error attr) {
        if (onResultErrorListener != null) {
            onResultErrorListener.resultError(attr);
        }
    }

    private OnErrorListener onErrorListener;

    // 接口OnErrorListener
    public interface OnErrorListener {
        void error(Throwable attr);
    }

    // 对外方式setOnErrorListener
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    // 封装方法errorNext
    private void errorNext(Throwable attr) {
        if (onErrorListener != null) {
            onErrorListener.error(attr);
        }
    }
}
