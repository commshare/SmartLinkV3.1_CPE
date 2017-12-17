package com.alcatel.wifilink.rx.helper.base;

import android.content.Context;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.Usage.UsageSettings;
import com.alcatel.wifilink.network.RX;
import com.alcatel.wifilink.network.ResponseObject;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.orhanobut.logger.Logger;

/**
 * Created by qianli.ma on 2017/12/13 0013.
 */

public class UsageSettingHelper {

    private Context context;

    public UsageSettingHelper(Context context) {
        this.context = context;
    }

    /**
     * 获取流量设置
     */
    public void getUsageSetting() {
        RX.getInstant().getUsageSettings(new ResponseObject<UsageSettings>() {
            @Override
            protected void onSuccess(UsageSettings result) {
                getSuccessNext(result);
            }

            @Override
            public void onError(Throwable e) {
                toast(R.string.connect_failed);
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                toast(R.string.connect_failed);
                resultErrorNext(error);
            }
        });
    }

    private OngetSuccessListener ongetSuccessListener;

    // 接口OngetSuccessListener
    public interface OngetSuccessListener {
        void getSuccess(UsageSettings attr);
    }

    // 对外方式setOngetSuccessListener
    public void setOngetSuccessListener(OngetSuccessListener ongetSuccessListener) {
        this.ongetSuccessListener = ongetSuccessListener;
    }

    // 封装方法getSuccessNext
    private void getSuccessNext(UsageSettings attr) {
        if (ongetSuccessListener != null) {
            ongetSuccessListener.getSuccess(attr);
        }
    }

    /**
     * 提交流量设置
     *
     * @param us
     */
    public void setUsageSetting(UsageSettings us) {
        RX.getInstant().setUsageSettings(us, new ResponseObject() {
            @Override
            protected void onSuccess(Object result) {
                RX.getInstant().getUsageSettings(new ResponseObject<UsageSettings>() {
                    @Override
                    protected void onSuccess(UsageSettings result) {
                        setSuccessNext(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.t("ma_setusagesetting").v(e.getMessage());
                        toast(R.string.connect_failed);
                        errorNext(e);
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        Logger.t("ma_setusagesetting").v(error.getMessage());
                        toast(R.string.connect_failed);
                        resultErrorNext(error);
                    }
                });

            }

            @Override
            public void onError(Throwable e) {
                Logger.t("ma_setusagesetting").v(e.getMessage());
                toast(R.string.connect_failed);
                errorNext(e);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                Logger.t("ma_setusagesetting").v(error.getMessage());
                resultErrorNext(error);
            }
        });
    }

    private OnSetSuccessListener onSetSuccessListener;

    // 接口OnSetSuccessListener
    public interface OnSetSuccessListener {
        void setSuccess(UsageSettings attr);
    }

    // 对外方式setOnSetSuccessListener
    public void setOnSetSuccessListener(OnSetSuccessListener onSetSuccessListener) {
        this.onSetSuccessListener = onSetSuccessListener;
    }

    // 封装方法setSuccessNext
    private void setSuccessNext(UsageSettings attr) {
        if (onSetSuccessListener != null) {
            onSetSuccessListener.setSuccess(attr);
        }
    }

    private OnResutlErrorListener onResutlErrorListener;

    // 接口OnResutlErrorListener
    public interface OnResutlErrorListener {
        void resultError(ResponseBody.Error attr);
    }

    // 对外方式setOnResutlErrorListener
    public void setOnResutlErrorListener(OnResutlErrorListener onResutlErrorListener) {
        this.onResutlErrorListener = onResutlErrorListener;
    }

    // 封装方法resultErrorNext
    private void resultErrorNext(ResponseBody.Error attr) {
        if (onResutlErrorListener != null) {
            onResutlErrorListener.resultError(attr);
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

    private void toast(int resId) {
        ToastUtil_m.show(context, resId);
    }

    private void toastLong(int resId) {
        ToastUtil_m.showLong(context, resId);
    }

    private void toast(String content) {
        ToastUtil_m.show(context, content);
    }

    private void to(Class ac, boolean isFinish) {
        CA.toActivity(context, ac, false, isFinish, false, 0);
    }

}
