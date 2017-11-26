package com.alcatel.wifilink.rx.helper;

import android.app.Activity;
import android.util.Log;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.connection.ConnectionSettings;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;

/**
 * Created by qianli.ma on 2017/11/25 0025.
 */

public class ConnectSettingHelper {

    /**
     * 连接
     */
    public static void toConnect(Activity activity) {
        Log.v("ma_clickConn", "begin");
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                int status = result.getConnectionStatus();
                Log.v("ma_clickConn", "conn status:" + status);
                if (status == Cons.DISCONNECTED | status == Cons.DISCONNECTING) {
                    API.get().connect(new MySubscriber() {
                        @Override
                        protected void onSuccess(Object result) {
                            Log.v("ma_clickConn", "success");
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            ToastUtil_m.showLong(activity, activity.getString(R.string.usage_limit_over_notification_content));
                            Log.v("ma_clickConn", "error:" + error.getMessage());
                            Log.v("ma_clickConn", "errorCode:" + error.getCode());
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtil_m.showLong(activity, activity.getString(R.string.usage_limit_over_notification_content));
                            Log.v("ma_clickConn", "e");
                        }
                    });
                }
            }
        });
    }

    private static void toast(Activity activity, int resId) {
        ToastUtil_m.show(activity, resId);
    }

    private static void toastLong(Activity activity, int resId) {
        ToastUtil_m.showLong(activity, resId);
    }

    private static void toast(Activity activity, String content) {
        ToastUtil_m.show(activity, content);
    }

    private static void to(Activity activity, Class ac, boolean isFinish) {
        CA.toActivity(activity, ac, false, isFinish, false, 0);
    }
}
