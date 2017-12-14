package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.connection.ConnectionSettings;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.helper.base.ConnectSettingHelper;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;

/**
 * Created by qianli.ma on 2017/12/11 0011.
 */

public class DataRoamHelper {

    private Activity activity;

    public DataRoamHelper(Activity activity) {
        this.activity = activity;
    }

    public void transfer() {
        // 1.先获取当前的漫游允许连接状态
        ConnectSettingHelper cshelper = new ConnectSettingHelper();
        cshelper.setOnNormalConnResultListener(result -> {
            int roamingConnect = result.getRoamingConnect();
            int needRoam = roamingConnect == Cons.WHEN_ROAM_CAN_CONNECT ? Cons.WHEN_ROAM_NOT_CONNECT : Cons.WHEN_ROAM_CAN_CONNECT;
            result.setRoamingConnect(needRoam);// 2.切换连接状态
            // 3.提交请求
            API.get().setConnectionSettings(result, new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {
                    // 4.再次获取
                    API.get().getConnectionSettings(new MySubscriber<ConnectionSettings>() {
                        @Override
                        protected void onSuccess(ConnectionSettings result) {
                            roamConnSuccessNext(result);
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
        cshelper.getConnSettingStatus();
    }

    private OnRoamConnSuccessListener onRoamConnSuccessListener;

    // 接口OnRoamConnSuccessListener
    public interface OnRoamConnSuccessListener {
        void roamConnSuccess(ConnectionSettings attr);
    }

    // 对外方式setOnRoamConnSuccessListener
    public void setOnRoamConnSuccessListener(OnRoamConnSuccessListener onRoamConnSuccessListener) {
        this.onRoamConnSuccessListener = onRoamConnSuccessListener;
    }

    // 封装方法roamConnSuccessNext
    private void roamConnSuccessNext(ConnectionSettings attr) {
        if (onRoamConnSuccessListener != null) {
            onRoamConnSuccessListener.roamConnSuccess(attr);
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
