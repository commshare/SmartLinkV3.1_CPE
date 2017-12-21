package com.alcatel.smartlinkv3.rx.helper;

import android.app.Activity;

import com.alcatel.smartlinkv3.rx.bean.WlanSettingForY900;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;

/**
 * Created by qianli.ma on 2017/12/19 0019.
 */

public class WlanSettingY900Helper {

    private Activity activity;

    public WlanSettingY900Helper(Activity activity) {
        this.activity = activity;
    }

    public void get() {
        API.get().getWlanSettingsForY900(new MySubscriber<WlanSettingForY900>() {
            @Override
            protected void onSuccess(WlanSettingForY900 result) {
                wlansettingNormalNext(result);
            }

            @Override
            public void onError(Throwable e) {
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                resultErrorNext(error);
            }
        });   
    }

    private OnWlanSettingNoramlListener onWlanSettingNoramlListener;

    // 接口OnWlanSettingNoramlListener
    public interface OnWlanSettingNoramlListener {
        void wlansettingNormal(WlanSettingForY900 attr);
    }

    // 对外方式setOnWlanSettingNoramlListener
    public void setOnWlanSettingNoramlListener(OnWlanSettingNoramlListener onWlanSettingNoramlListener) {
        this.onWlanSettingNoramlListener = onWlanSettingNoramlListener;
    }

    // 封装方法wlansettingNormalNext
    private void wlansettingNormalNext(WlanSettingForY900 attr) {
        if (onWlanSettingNoramlListener != null) {
            onWlanSettingNoramlListener.wlansettingNormal(attr);
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
