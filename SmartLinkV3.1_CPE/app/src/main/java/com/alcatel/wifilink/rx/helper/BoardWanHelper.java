package com.alcatel.wifilink.rx.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.model.wan.WanSettingsParams;
import com.alcatel.wifilink.model.wan.WanSettingsResult;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.rx.ui.LoginRxActivity;
import com.alcatel.wifilink.rx.ui.RefreshWifiRxActivity;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.utils.OtherUtils;

/**
 * Created by qianli.ma on 2017/11/16 0016.
 */

public class BoardWanHelper {


    private Activity activity;
    private ProgressDialog pgd;
    private Handler handler;
    private OnNormalNextListener onNormalNextListener;
    private OnConnetedNextListener onConnetedNextListener;
    private OnDisConnetedNextListener onDisConnetedNextListener;
    private OnConnetingNextListener onConnetingNextListener;
    private OnDisconnetingNextListener onDisconnetingNextListener;
    private OnSendRequestSuccess onSendRequestSuccess;
    private OnSendRequestFailed onSendRequestFailed;

    public BoardWanHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 调用此方法
     */
    public void boardNormal() {
        if (pgd == null) {
            pgd = OtherUtils.showProgressPop(activity);
        }
        // 1.连接硬件
        new CheckBoard() {
            @Override
            public void successful() {
                // 2.登陆状态
                API.get().getLoginState(new MySubscriber<LoginState>() {
                    @Override
                    protected void onSuccess(LoginState result) {
                        if (result.getState() == Cons.LOGOUT) {
                            toast(R.string.login_kickoff_logout_successful);
                            to(LoginRxActivity.class);
                            return;
                        }
                        // 3.WAN状态
                        obtainWanStatus();
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        OtherUtils.hideProgressPop(pgd);
                        toast(R.string.connect_failed);
                        to(RefreshWifiRxActivity.class);
                    }

                    @Override
                    public void onError(Throwable e) {
                        OtherUtils.hideProgressPop(pgd);
                        toast(R.string.connect_failed);
                        to(RefreshWifiRxActivity.class);
                    }
                });
            }
        }.checkBoard(activity, RefreshWifiRxActivity.class);
    }

    /**
     * 发送设置wan口请求
     *
     * @param wsp
     */
    public void sendWanRequest(WanSettingsParams wsp) {
        API.get().setWanSettings(wsp, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                reGetWanStatus();// 重复获取WAN口状态
            }

            /**
             * 重复获取WAN口状态
             */
            private void reGetWanStatus() {
                API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
                    @Override
                    protected void onSuccess(WanSettingsResult result) {
                        int status = result.getStatus();
                        if (status == Cons.CONNECTED) {
                            sendSuccessNext();
                        } else if (status == Cons.CONNECTING) {
                            reGetWanStatus();
                        } else {
                            sendFailedNext();
                        }
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        sendFailedNext();
                    }

                    @Override
                    public void onError(Throwable e) {
                        sendFailedNext();
                    }
                });
            }


            @Override
            protected void onResultError(ResponseBody.Error error) {
                sendFailedNext();
            }

            @Override
            public void onError(Throwable e) {
                sendFailedNext();
            }
        });
    }

    /**
     * 获取wan状态
     */
    private void obtainWanStatus() {
        API.get().getWanSettings(new MySubscriber<WanSettingsResult>() {
            @Override
            protected void onSuccess(WanSettingsResult result) {
                normalNext(result);
                int status = result.getStatus();
                switch (status) {
                    case Cons.CONNECTED:
                        connectedNext(result);
                        break;
                    case Cons.CONNECTING:
                        connectingNext(result);
                        delayRepeatGetWanstatu();
                        break;
                    case Cons.DISCONNECTED:
                        disconnectedNext(result);
                        break;
                    case Cons.DISCONNECTING:
                        disconnectingNext(result);
                        break;
                }
                if (status != Cons.CONNECTING) {
                    OtherUtils.hideProgressPop(pgd);
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                OtherUtils.hideProgressPop(pgd);
                toast(R.string.check_your_wan_cabling);
                to(RefreshWifiRxActivity.class);
            }

            @Override
            public void onError(Throwable e) {
                OtherUtils.hideProgressPop(pgd);
                toast(R.string.connect_failed);
                to(RefreshWifiRxActivity.class);
            }
        });
    }

    /**
     * 延迟迭代请求
     */
    private void delayRepeatGetWanstatu() {
        handler = new Handler();
        handler.postDelayed(this::obtainWanStatus, 2500);
    }

    private void toast(int resId) {
        ToastUtil_m.show(activity, resId);
    }

    private void to(Class ac) {
        CA.toActivity(activity, ac, false, true, false, 0);
    }

    /* -------------------------------------------- INTERFACE -------------------------------------------- */

    public interface OnSendRequestSuccess {
        void sendSuccess();
    }

    public interface OnSendRequestFailed {
        void sendFailed();
    }

    public interface OnNormalNextListener {
        void normalNext(WanSettingsResult wanResult);
    }

    public interface OnConnetedNextListener {
        void connectedNext(WanSettingsResult wanResult);
    }

    public interface OnDisConnetedNextListener {
        void disConnectedNext(WanSettingsResult wanResult);
    }

    public interface OnConnetingNextListener {
        void connectingNext(WanSettingsResult wanResult);
    }

    public interface OnDisconnetingNextListener {
        void disConnectingNext(WanSettingsResult wanResult);
    }

    /* -------------------------------------------- METHOD -------------------------------------------- */

    public void setOnSendRequestSuccess(OnSendRequestSuccess onSendRequestSuccess) {
        this.onSendRequestSuccess = onSendRequestSuccess;
    }

    public void setOnSendRequestFailed(OnSendRequestFailed onSendRequestFailed) {
        this.onSendRequestFailed = onSendRequestFailed;
    }

    public void setOnNormalNextListener(OnNormalNextListener onNormalNextListener) {
        this.onNormalNextListener = onNormalNextListener;
    }

    public void setOnConnetedNextListener(OnConnetedNextListener onConnetedNextListener) {
        this.onConnetedNextListener = onConnetedNextListener;
    }

    public void setOnDisConnetedNextListener(OnDisConnetedNextListener onDisConnetedNextListener) {
        this.onDisConnetedNextListener = onDisConnetedNextListener;
    }

    public void setOnConnetingNextListener(OnConnetingNextListener onConnetingNextListener) {
        this.onConnetingNextListener = onConnetingNextListener;
    }

    public void setOnDisconnetingNextListener(OnDisconnetingNextListener onDisconnetingNextListener) {
        this.onDisconnetingNextListener = onDisconnetingNextListener;
    }

    /* -------------------------------------------- USE -------------------------------------------- */

    private void sendSuccessNext() {
        if (onSendRequestSuccess != null) {
            onSendRequestSuccess.sendSuccess();
        }
    }

    private void sendFailedNext() {
        if (onSendRequestFailed != null) {
            onSendRequestFailed.sendFailed();
        }
    }

    private void normalNext(WanSettingsResult result) {
        if (onNormalNextListener != null) {
            onNormalNextListener.normalNext(result);
        }
    }

    private void connectedNext(WanSettingsResult result) {
        if (onConnetedNextListener != null) {
            onConnetedNextListener.connectedNext(result);
        }
    }

    private void disconnectedNext(WanSettingsResult result) {
        if (onDisConnetedNextListener != null) {
            onDisConnetedNextListener.disConnectedNext(result);
        }
    }

    private void connectingNext(WanSettingsResult result) {
        if (onConnetingNextListener != null) {
            onConnetingNextListener.connectingNext(result);
        }
    }

    private void disconnectingNext(WanSettingsResult result) {
        if (onDisconnetingNextListener != null) {
            onDisconnetingNextListener.disConnectingNext(result);
        }
    }
}
