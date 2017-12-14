package com.alcatel.wifilink.rx.helper.business;

import android.text.TextUtils;

import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;

/**
 * Created by qianli.ma on 2017/12/11 0011.
 */

public class SimNumImsiHelper {
    public SimNumImsiHelper() {
    }

    /**
     * 获取IMSI号以及SIM号
     */
    public void getSimNumAndImsi() {
        API.get().getSystemInfo(new MySubscriber<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
                String msisdn = result.getMSISDN();
                String imsi = result.getIMSI();
                if (!TextUtils.isEmpty(msisdn)) {
                    simNumberNext(msisdn);
                }
                if (!TextUtils.isEmpty(imsi)) {
                    imsiNext(imsi);
                }
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

    private OnImsiListener onImsiListener;

    // 接口OnImsiListener
    public interface OnImsiListener {
        void imsi(String imsi);
    }

    // 对外方式setOnImsiListener
    public void setOnImsiListener(OnImsiListener onImsiListener) {
        this.onImsiListener = onImsiListener;
    }

    // 封装方法imsiNext
    private void imsiNext(String imsi) {
        if (onImsiListener != null) {
            onImsiListener.imsi(imsi);
        }
    }

    private OnSimNumberListener onSimNumberListener;

    // 接口OnSimNumberListener
    public interface OnSimNumberListener {
        void simNumber(String simNum);
    }

    // 封装方法simNumberNext
    private void simNumberNext(String simNum) {
        if (onSimNumberListener != null) {
            onSimNumberListener.simNumber(simNum);
        }
    }

    // 对外方式setOnSimNumberListener
    public void setOnSimNumberListener(OnSimNumberListener onSimNumberListener) {
        this.onSimNumberListener = onSimNumberListener;
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
