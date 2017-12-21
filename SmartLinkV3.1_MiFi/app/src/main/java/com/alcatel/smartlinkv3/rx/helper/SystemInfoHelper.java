package com.alcatel.smartlinkv3.rx.helper;

import android.app.Activity;
import android.content.Context;

import com.alcatel.smartlinkv3.business.system.SystemInfoForNew;
import com.alcatel.smartlinkv3.business.system.SystemInfoForY900;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.Logs;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.utils.VersionUtils;

/**
 * Created by qianli.ma on 2017/12/18 0018.
 */

public class SystemInfoHelper {
    private Activity activity;
    private Context context;

    public SystemInfoHelper(Activity activity) {
        this.activity = activity;
    }

    public SystemInfoHelper(Context context) {
        this.context = context;
    }

    public void get() {
        API.get().getSystemInfoForY900(new MySubscriber<SystemInfoForY900>() {
            @Override
            protected void onSuccess(SystemInfoForY900 y900) {
                normalNext(y900);
                if (VersionUtils.isOldVersion(y900.getDeviceName())) {
                    oldVersionNext(y900);
                } else {
                    newVersionNext(null);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                normalNext(error);
                resultErrorNext(error);
                Logs.v("ma_rx", "getSystemInfoForY900 result error");
            }

            @Override
            public void onError(Throwable e) {
                normalNext(e);
                errorNext(e);
                Logs.v("ma_rx", "getSystemInfoForY900 error:" + e.getMessage());
            }
        });
    }

    private OnNormalListener onNormalListener;

    // 接口OnNormalListener
    public interface OnNormalListener {
        void normal(Object attr);
    }

    // 对外方式setOnNormalListener
    public void setOnNormalListener(OnNormalListener onNormalListener) {
        this.onNormalListener = onNormalListener;
    }

    // 封装方法normalNext
    private void normalNext(Object attr) {
        if (onNormalListener != null) {
            onNormalListener.normal(attr);
        }
    }

    private OnNewVersionListener onNewVersionListener;

    // 接口OnNewVersionListener
    public interface OnNewVersionListener {
        void newVersion(SystemInfoForNew attr);
    }

    // 对外方式setOnNewVersionListener
    public void setOnNewVersionListener(OnNewVersionListener onNewVersionListener) {
        this.onNewVersionListener = onNewVersionListener;
    }

    // 封装方法newVersionNext
    private void newVersionNext(SystemInfoForNew attr) {
        if (onNewVersionListener != null) {
            onNewVersionListener.newVersion(attr);
        }
    }

    private OnOldVersionListener onOldVersionListener;

    // 接口OnOldVersionListener
    public interface OnOldVersionListener {
        void oldVersion(SystemInfoForY900 attr);
    }

    // 对外方式setOnOldVersionListener
    public void setOnOldVersionListener(OnOldVersionListener onOldVersionListener) {
        this.onOldVersionListener = onOldVersionListener;
    }

    // 封装方法oldVersionNext
    private void oldVersionNext(SystemInfoForY900 attr) {
        if (onOldVersionListener != null) {
            onOldVersionListener.oldVersion(attr);
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
