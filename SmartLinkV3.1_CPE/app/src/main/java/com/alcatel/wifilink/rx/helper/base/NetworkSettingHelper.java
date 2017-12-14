package com.alcatel.wifilink.rx.helper.base;

import com.alcatel.wifilink.model.network.Network;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.NetworkRegisterState;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

/**
 * Created by qianli.ma on 2017/12/11 0011.
 */

public class NetworkSettingHelper {

    public NetworkSettingHelper() {

    }

    /**
     * 获取网络类型
     */
    public void getNetworkSetting() {
        // 先获取注册状态
        API.get().getNetworkRegisterState(new MySubscriber<NetworkRegisterState>() {
            @Override
            protected void onSuccess(NetworkRegisterState result) {
                if (result.getRegist_state() == Cons.REGISTER_SUCCESSFUL) {
                    // 再获取网络注册模式
                    getnetworkSetting();
                }
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });

    }

    private void getnetworkSetting() {
        API.get().getNetworkSettings(new MySubscriber<Network>() {
            @Override
            protected void onSuccess(Network result) {
                normalNetworkNext(result);
                int networkMode = result.getNetworkMode();
                switch (networkMode) {
                    // 自动模式
                    case Cons.AUTO_MODE:
                        autoNext(result);
                        break;

                    // 2G模式
                    case Cons.ONLY_2G:
                    case Cons.GSM_LTE:
                    case Cons.GSM_UMTS:
                        mode2GNext(result);
                        break;

                    // 3G模式
                    case Cons.ONLY_3G:
                    case Cons.UMTS_LTE:
                    case Cons.CDMA_EVDO:
                    case Cons.EVDO_ONLY:
                    case Cons.CDMA_EHRPD:
                    case Cons.CDMA_ONLY_1X_SPRINT:
                        mode3GNext(result);
                        break;

                    // 4G模式
                    case Cons.ONLY_LTE:
                    case Cons.LTE_CDMA_EVDO:
                        mode4GNext(result);
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

    private OnNormalNetworkListener onNormalNetworkListener;

    // 接口OnNormalNetworkListener
    public interface OnNormalNetworkListener {
        void normalNetwork(Network attr);
    }

    // 对外方式setOnNormalNetworkListener
    public void setOnNormalNetworkListener(OnNormalNetworkListener onNormalNetworkListener) {
        this.onNormalNetworkListener = onNormalNetworkListener;
    }

    // 封装方法normalNetworkNext
    private void normalNetworkNext(Network attr) {
        if (onNormalNetworkListener != null) {
            onNormalNetworkListener.normalNetwork(attr);
        }
    }

    private OnAutoListener onAutoListener;

    // 接口OnAutoListener
    public interface OnAutoListener {
        void auto(Network attr);
    }

    // 对外方式setOnAutoListener
    public void setOnAutoListener(OnAutoListener onAutoListener) {
        this.onAutoListener = onAutoListener;
    }

    // 封装方法autoNext
    private void autoNext(Network attr) {
        if (onAutoListener != null) {
            onAutoListener.auto(attr);
        }
    }

    private On4GListener on4GListener;

    // 接口On4GListener
    public interface On4GListener {
        void mode4G(Network attr);
    }

    // 对外方式setOn4GListener
    public void setOn4GListener(On4GListener on4GListener) {
        this.on4GListener = on4GListener;
    }

    // 封装方法mode4GNext
    private void mode4GNext(Network attr) {
        if (on4GListener != null) {
            on4GListener.mode4G(attr);
        }
    }

    private On3GListener on3GListener;

    // 接口On3GListener
    public interface On3GListener {
        void mode3G(Network attr);
    }

    // 对外方式setOn3GListener
    public void setOn3GListener(On3GListener on3GListener) {
        this.on3GListener = on3GListener;
    }

    // 封装方法mode3GNext
    private void mode3GNext(Network attr) {
        if (on3GListener != null) {
            on3GListener.mode3G(attr);
        }
    }

    private On2GListener on2GListener;

    // 接口On2GListener
    public interface On2GListener {
        void mode2G(Network attr);
    }

    // 对外方式setOn2GListener
    public void setOn2GListener(On2GListener on2GListener) {
        this.on2GListener = on2GListener;
    }

    // 封装方法mode2GNext
    private void mode2GNext(Network attr) {
        if (on2GListener != null) {
            on2GListener.mode2G(attr);
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
