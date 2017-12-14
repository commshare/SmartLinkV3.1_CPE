package com.alcatel.wifilink.rx.helper.base;

import android.app.Activity;
import android.util.Log;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.connection.ConnectionSettings;
import com.alcatel.wifilink.network.NetworkRegisterState;
import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.utils.ToastUtil_m;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;

/**
 * Created by qianli.ma on 2017/11/25 0025.
 */

public class ConnectSettingHelper {

    public ConnectSettingHelper() {

    }

    /**
     * 获取漫游时是否允许连接的状态
     */
    public void getConnWhenRoam() {
        API.get().getConnectionSettings(new MySubscriber<ConnectionSettings>() {
            @Override
            protected void onSuccess(ConnectionSettings result) {
                int roamingConnect = result.getRoamingConnect();
                switch (roamingConnect) {
                    case Cons.WHEN_ROAM_NOT_CONNECT:
                        roamNotConnNext(result);
                        break;
                    case Cons.WHEN_ROAM_CAN_CONNECT:
                        roamConnNext(result);
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

    private OnRoamNotConnListener onRoamNotConnListener;

    // 接口OnRoamNotConnListener
    public interface OnRoamNotConnListener {
        void roamNotConn(ConnectionSettings attr);
    }

    // 对外方式setOnRoamNotConnListener
    public void setOnRoamNotConnListener(OnRoamNotConnListener onRoamNotConnListener) {
        this.onRoamNotConnListener = onRoamNotConnListener;
    }

    // 封装方法roamNotConnNext
    private void roamNotConnNext(ConnectionSettings attr) {
        if (onRoamNotConnListener != null) {
            onRoamNotConnListener.roamNotConn(attr);
        }
    }

    private OnRoamConnListener onRoamConnListener;

    // 接口OnRoamConnListener
    public interface OnRoamConnListener {
        void roamConn(ConnectionSettings attr);
    }

    // 对外方式setOnRoamConnListener
    public void setOnRoamConnListener(OnRoamConnListener onRoamConnListener) {
        this.onRoamConnListener = onRoamConnListener;
    }

    // 封装方法roamConnNext
    private void roamConnNext(ConnectionSettings attr) {
        if (onRoamConnListener != null) {
            onRoamConnListener.roamConn(attr);
        }
    }

    /**
     * 切断连接
     */
    public void toDisConnect() {
        API.get().getNetworkRegisterState(new MySubscriber<NetworkRegisterState>() {
            @Override
            protected void onSuccess(NetworkRegisterState result) {
                int state = result.getRegist_state();
                switch (state) {
                    case Cons.REGISTER_SUCCESSFUL:
                        connectOrDisconnect(false);
                        break;
                    case Cons.REGISTTING:
                        registingNext(state);
                        break;
                    case Cons.REGISTRATION_FAILED:
                        registerFailedNext(state);
                        break;
                    case Cons.NOT_REGISETER:
                        notRegisterNext(state);
                        break;
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                registerResultErrorNext(error);
            }

            @Override
            public void onError(Throwable e) {
                registerErrorNext(e);
            }
        });
    }

    /**
     * 连接
     */
    public void toConnect() {
        // 1.检测sim卡的注册状态
        API.get().getNetworkRegisterState(new MySubscriber<NetworkRegisterState>() {
            @Override
            protected void onSuccess(NetworkRegisterState result) {
                int state = result.getRegist_state();
                switch (state) {
                    case Cons.REGISTER_SUCCESSFUL:
                        connectOrDisconnect(true);
                        break;
                    case Cons.REGISTTING:
                        registingNext(state);
                        break;
                    case Cons.REGISTRATION_FAILED:
                        registerFailedNext(state);
                        break;
                    case Cons.NOT_REGISETER:
                        notRegisterNext(state);
                        break;
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                registerResultErrorNext(error);
            }

            @Override
            public void onError(Throwable e) {
                registerErrorNext(e);
            }
        });
    }

    private OnNotRegisterListener onNotRegisterListener;

    // 接口OnNotRegisterListener
    public interface OnNotRegisterListener {
        void notRegister(int attr);
    }

    // 对外方式setOnNotRegisterListener
    public void setOnNotRegisterListener(OnNotRegisterListener onNotRegisterListener) {
        this.onNotRegisterListener = onNotRegisterListener;
    }

    // 封装方法notRegisterNext
    private void notRegisterNext(int attr) {
        if (onNotRegisterListener != null) {
            onNotRegisterListener.notRegister(attr);
        }
    }

    private OnRegisteFailedListener onRegisteFailedListener;

    // 接口OnRegisteFailedListener
    public interface OnRegisteFailedListener {
        void registerFailed(int attr);
    }

    // 对外方式setOnRegisteFailedListener
    public void setOnRegisteFailedListener(OnRegisteFailedListener onRegisteFailedListener) {
        this.onRegisteFailedListener = onRegisteFailedListener;
    }

    // 封装方法registerFailedNext
    private void registerFailedNext(int attr) {
        if (onRegisteFailedListener != null) {
            onRegisteFailedListener.registerFailed(attr);
        }
    }

    private OnRegistingListener onRegistingListener;

    // 接口OnRegistingListener
    public interface OnRegistingListener {
        void registing(int attr);
    }

    // 对外方式setOnRegistingListener
    public void setOnRegistingListener(OnRegistingListener onRegistingListener) {
        this.onRegistingListener = onRegistingListener;
    }

    // 封装方法registingNext
    private void registingNext(int attr) {
        if (onRegistingListener != null) {
            onRegistingListener.registing(attr);
        }
    }

    private OnRegisterResultErrorListener onRegisterResultErrorListener;

    // 接口OnRegisterResultErrorListener
    public interface OnRegisterResultErrorListener {
        void registerResultError(ResponseBody.Error attr);
    }

    // 对外方式setOnRegisterResultErrorListener
    public void setOnRegisterResultErrorListener(OnRegisterResultErrorListener onRegisterResultErrorListener) {
        this.onRegisterResultErrorListener = onRegisterResultErrorListener;
    }

    // 封装方法registerResultErrorNext
    private void registerResultErrorNext(ResponseBody.Error attr) {
        if (onRegisterResultErrorListener != null) {
            onRegisterResultErrorListener.registerResultError(attr);
        }
    }

    private OnRegisterErrorListener onRegisterErrorListener;

    // 接口OnRegisterErrorListener
    public interface OnRegisterErrorListener {
        void registerError(Throwable attr);
    }

    // 对外方式setOnRegisterErrorListener
    public void setOnRegisterErrorListener(OnRegisterErrorListener onRegisterErrorListener) {
        this.onRegisterErrorListener = onRegisterErrorListener;
    }

    // 封装方法registerErrorNext
    private void registerErrorNext(Throwable attr) {
        if (onRegisterErrorListener != null) {
            onRegisterErrorListener.registerError(attr);
        }
    }

    /**
     * 开启连接 | 断开连接
     *
     * @param needConn 是否需要连接 true:执行连接操作
     */
    private void connectOrDisconnect(boolean needConn) {
        if (needConn) {
            API.get().connect(new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {
                    connSuccessNext(result);
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
        } else {
            API.get().disConnect(new MySubscriber() {
                @Override
                protected void onSuccess(Object result) {
                    disconnSuccessNext(result);
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
    }

    private OnDisConnSuccessListener onDisConnSuccessListener;

    // 接口OnDisConnSuccessListener
    public interface OnDisConnSuccessListener {
        void disconnSuccess(Object attr);
    }

    // 对外方式setOnDisConnSuccessListener
    public void setOnDisConnSuccessListener(OnDisConnSuccessListener onDisConnSuccessListener) {
        this.onDisConnSuccessListener = onDisConnSuccessListener;
    }

    // 封装方法disconnSuccessNext
    private void disconnSuccessNext(Object attr) {
        if (onDisConnSuccessListener != null) {
            onDisConnSuccessListener.disconnSuccess(attr);
        }
    }

    private OnConnSuccessListener onConnSuccessListener;

    // 接口OnConnSuccessListener
    public interface OnConnSuccessListener {
        void connSuccess(Object attr);
    }

    // 对外方式setOnConnSuccessListener
    public void setOnConnSuccessListener(OnConnSuccessListener onConnSuccessListener) {
        this.onConnSuccessListener = onConnSuccessListener;
    }

    // 封装方法connSuccessNext
    private void connSuccessNext(Object attr) {
        if (onConnSuccessListener != null) {
            onConnSuccessListener.connSuccess(attr);
        }
    }

    /**
     * 获取连接状态
     */
    public void getConnSettingStatus() {
        API.get().getConnectionSettings(new MySubscriber<ConnectionSettings>() {
            @Override
            protected void onSuccess(ConnectionSettings result) {
                connResultNext(result);
                switch (result.getConnectMode()) {
                    case Cons.AUTO:
                        connAutoNext(result);
                        break;
                    case Cons.MANUAL:
                        connManualNext(result);
                        break;
                }
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

    private OnNormalConnResultListener onNormalConnResultListener;

    // 接口OnNormalConnResultListener
    public interface OnNormalConnResultListener {
        void connResult(ConnectionSettings attr);
    }

    // 对外方式setOnNormalConnResultListener
    public void setOnNormalConnResultListener(OnNormalConnResultListener onNormalConnResultListener) {
        this.onNormalConnResultListener = onNormalConnResultListener;
    }

    // 封装方法connResultNext
    private void connResultNext(ConnectionSettings attr) {
        if (onNormalConnResultListener != null) {
            onNormalConnResultListener.connResult(attr);
        }
    }

    private OnConnManualListener onConnManualListener;

    // 接口OnConnManualListener
    public interface OnConnManualListener {
        void connManual(ConnectionSettings attr);
    }

    // 对外方式setOnConnManualListener
    public void setOnConnManualListener(OnConnManualListener onConnManualListener) {
        this.onConnManualListener = onConnManualListener;
    }

    // 封装方法connManualNext
    private void connManualNext(ConnectionSettings attr) {
        if (onConnManualListener != null) {
            onConnManualListener.connManual(attr);
        }
    }

    private OnConnAutoListener onConnAutoListener;

    // 接口OnConnAutoListener
    public interface OnConnAutoListener {
        void connAuto(ConnectionSettings attr);
    }

    // 对外方式setOnConnAutoListener
    public void setOnConnAutoListener(OnConnAutoListener onConnAutoListener) {
        this.onConnAutoListener = onConnAutoListener;
    }

    // 封装方法connAutoNext
    private void connAutoNext(ConnectionSettings attr) {
        if (onConnAutoListener != null) {
            onConnAutoListener.connAuto(attr);
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
