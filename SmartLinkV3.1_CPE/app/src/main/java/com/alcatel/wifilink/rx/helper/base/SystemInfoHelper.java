package com.alcatel.wifilink.rx.helper.base;

import com.alcatel.wifilink.model.system.SystemInfo;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.network.ResponseObject;
import com.orhanobut.logger.Logger;

/**
 * Created by qianli.ma on 2017/12/21 0021.
 */

public class SystemInfoHelper {
    public void get() {
        RX.getInstant().getSystemInfo(new ResponseObject<SystemInfo>() {
            @Override
            protected void onSuccess(SystemInfo result) {
                Logger.t("ma_upgrade").v("SystemInfoHelper success");
                getSuccessNext(result);
            }

            @Override
            public void onError(Throwable e) {
                Logger.t("ma_upgrade").e("SystemInfoHelper error");
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Logger.t("ma_upgrade").e("SystemInfoHelper onResultError");
                resultErrorNext(error);
            }
        });
    }

    private OnGetSystemInfoSuccessListener onGetSystemInfoSuccessListener;

    // 接口OnGetSystemInfoSuccessListener
    public interface OnGetSystemInfoSuccessListener {
        void getSuccess(SystemInfo attr);
    }

    // 对外方式setOnGetSystemInfoSuccessListener
    public void setOnGetSystemInfoSuccessListener(OnGetSystemInfoSuccessListener onGetSystemInfoSuccessListener) {
        this.onGetSystemInfoSuccessListener = onGetSystemInfoSuccessListener;
    }

    // 封装方法getSuccessNext
    private void getSuccessNext(SystemInfo attr) {
        if (onGetSystemInfoSuccessListener != null) {
            onGetSystemInfoSuccessListener.getSuccess(attr);
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
