package com.alcatel.wifilink.rx.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;

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
    private OnResultError onResultError;
    private OnError onError;
    private CheckBoard checkBoardClick;
    private CheckBoard checkBoardRoll;

    public BoardWanHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 点击事件调用此方法
     */
    public void boardNormal() {
        if (pgd == null) {
            pgd = OtherUtils.showProgressPop(activity);
        }
        if (!pgd.isShowing()) {
            pgd.show();
        }
        // 1.连接硬件
        if (checkBoardClick == null) {
            checkBoardClick = new CheckBoard() {
                @Override
                public void successful() {
                    // 2.登陆状态
                    API.get().getLoginState(new MySubscriber<LoginState>() {
                        @Override
                        protected void onSuccess(LoginState result) {
                            if (result.getState() == Cons.LOGOUT) {
                                to(LoginRxActivity.class);
                                return;
                            }
                            // 3.WAN状态
                            obtainWanStatus();
                        }

                        @Override
                        protected void onResultError(ResponseBody.Error error) {
                            Log.v("ma_couldn_connect", "boardWanHelper getLoginState error: " + error.getMessage());
                            OtherUtils.hideProgressPop(pgd);
                            toast(R.string.connect_failed);
                            to(RefreshWifiRxActivity.class);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.v("ma_couldn_connect", "boardWanHelper getLoginState error: " + e.getMessage());
                            OtherUtils.hideProgressPop(pgd);
                            toast(R.string.connect_failed);
                            to(RefreshWifiRxActivity.class);
                        }
                    });
                }
            };
        }
        checkBoardClick.checkBoard(activity, LoginRxActivity.class);
    }

    /**
     * 定时器调用此方法
     */
    public void boardTimer() {
        // 1.连接硬件
        if (checkBoardRoll == null) {
            checkBoardRoll = new CheckBoard() {
                @Override
                public void successful() {
                    // 2.登陆状态
                    API.get().getLoginState(new MySubscriber<LoginState>() {
                        @Override
                        protected void onSuccess(LoginState result) {
                            if (result.getState() == Cons.LOGOUT) {
                                to(LoginRxActivity.class);
                                return;
                            }
                            // 3.WAN状态
                            obtainWanStatusRoll();
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
            };
        }
        checkBoardRoll.checkBoard(activity, LoginRxActivity.class);
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
    private void obtainWanStatusRoll() {
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
                        break;
                    case Cons.DISCONNECTED:
                        disconnectedNext(result);
                        break;
                    case Cons.DISCONNECTING:
                        disconnectingNext(result);
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
                        Log.v("ma_boardwan", "wan connected");
                        connectedNext(result);
                        break;
                    case Cons.CONNECTING:
                        Log.v("ma_boardwan", "wan connecting");
                        connectingNext(result);
                        delayRepeatGetWanstatu();
                        break;
                    case Cons.DISCONNECTED:
                        Log.v("ma_boardwan", "wan disconnected");
                        disconnectedNext(result);
                        break;
                    case Cons.DISCONNECTING:
                        Log.v("ma_boardwan", "wan wan disconneting");
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
                Log.v("ma_couldn_connect", "boardWanHelper obtainWanStatus error: " + e.getMessage());
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
        handler.postDelayed(this::obtainWanStatus, 1000);
    }

    private void toast(int resId) {
        Log.v("ma_counld", getClass().getSimpleName());
        ToastUtil_m.show(activity, resId);
    }

    private void to(Class ac) {
        CA.toActivity(activity, ac, false, true, false, 0);
    }

    /* -------------------------------------------- INTERFACE -------------------------------------------- */

    public interface OnResultError {
        void resultError(ResponseBody.Error error);
    }

    public interface OnError {
        void error(Throwable e);
    }

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

    public void setOnResultError(OnResultError onResultError) {
        this.onResultError = onResultError;
    }

    public void setOnError(OnError onError) {
        this.onError = onError;
    }

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

    private void resultErrorNext(ResponseBody.Error error) {
        if (onResultError != null) {
            onResultError.resultError(error);
        }
    }

    private void errorNext(Throwable e) {
        if (onError != null) {
            onError.error(e);
        }
    }

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
