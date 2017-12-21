package com.alcatel.smartlinkv3.rx.helper;

import com.alcatel.smartlinkv3.rx.model.WlanState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.Cons;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;

/**
 * Created by qianli.ma on 2017/12/21 0021.
 */

public class WlanStateHelper {
    public void get() {
        API.get().getWlanState(new MySubscriber<WlanState>() {
            @Override
            protected void onSuccess(WlanState result) {
                switch (result.getWlanState()) {
                    case Cons.OFF:
                        offNext(result);
                        break;
                    case Cons.ON:
                        onStateNext(result);
                        break;
                    case Cons.WPS:
                        wpsNext(result);
                        break;
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

    private OnOFFListener onOFFListener;

    // 接口OnOFFListener
    public interface OnOFFListener {
        void off(WlanState attr);
    }

    // 对外方式setOnOFFListener
    public void setOnOFFListener(OnOFFListener onOFFListener) {
        this.onOFFListener = onOFFListener;
    }

    // 封装方法offNext
    private void offNext(WlanState attr) {
        if (onOFFListener != null) {
            onOFFListener.off(attr);
        }
    }

    private OnONStateListener onONStateListener;

    // 接口OnONStateListener
    public interface OnONStateListener {
        void onState(WlanState attr);
    }

    // 对外方式setOnONStateListener
    public void setOnONStateListener(OnONStateListener onONStateListener) {
        this.onONStateListener = onONStateListener;
    }

    // 封装方法onStateNext
    private void onStateNext(WlanState attr) {
        if (onONStateListener != null) {
            onONStateListener.onState(attr);
        }
    }

    private OnWPSListener onWPSListener;

    // 接口OnWPSListener
    public interface OnWPSListener {
        void wps(WlanState attr);
    }

    // 对外方式setOnWPSListener
    public void setOnWPSListener(OnWPSListener onWPSListener) {
        this.onWPSListener = onWPSListener;
    }

    // 封装方法wpsNext
    private void wpsNext(WlanState attr) {
        if (onWPSListener != null) {
            onWPSListener.wps(attr);
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
