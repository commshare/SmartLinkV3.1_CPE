package com.alcatel.wifilink.rx.helper.business;

import android.app.Activity;
import android.content.Context;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.model.network.NetworkInfos;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.NetworkRegisterState;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

/**
 * Created by qianli.ma on 2017/11/24 0024.
 */

public class RoamingHelper {

    public RoamingHelper() {

    }

    /**
     * 获取漫游状态
     */
    public void getRoamingStatus() {
        API.get().getNetworkRegisterState(new MySubscriber<NetworkRegisterState>() {
            @Override
            protected void onSuccess(NetworkRegisterState result) {
                int regist_state = result.getRegist_state();
                switch (regist_state) {
                    case Cons.REGISTER_SUCCESSFUL:
                        registerSuccessNext(regist_state);
                        getNetworkInfo();
                        break;
                    case Cons.REGISTRATION_FAILED:
                        registerFailedNext(regist_state);
                        break;
                    case Cons.REGISTTING:
                        registingNext(regist_state);
                        break;
                    case Cons.NOT_REGISETER:
                        notRegisterNext(regist_state);
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

    /**
     * 获取漫游状态
     */
    private void getNetworkInfo() {
        API.get().getNetworkInfo(new MySubscriber<NetworkInfos>() {
            @Override
            protected void onSuccess(NetworkInfos result) {
                int roaming = result.getRoaming();
                switch (roaming) {
                    case Cons.ROAMING:
                        roamingNext(roaming);
                        break;
                    case Cons.NOROAMING:
                        noRoamingNext(roaming);
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

    private OnNoRoamingListener onNoRoamingListener;

    // 接口OnNoRoamingListener
    public interface OnNoRoamingListener {
        void noRoaming(int attr);
    }

    // 对外方式setOnNoRoamingListener
    public void setOnNoRoamingListener(OnNoRoamingListener onNoRoamingListener) {
        this.onNoRoamingListener = onNoRoamingListener;
    }

    // 封装方法noRoamingNext
    private void noRoamingNext(int attr) {
        if (onNoRoamingListener != null) {
            onNoRoamingListener.noRoaming(attr);
        }
    }

    private OnRoamingListener onRoamingListener;

    // 接口OnRoamingListener
    public interface OnRoamingListener {
        void roaming(int attr);
    }

    // 对外方式setOnRoamingListener
    public void setOnRoamingListener(OnRoamingListener onRoamingListener) {
        this.onRoamingListener = onRoamingListener;
    }

    // 封装方法roamingNext
    private void roamingNext(int attr) {
        if (onRoamingListener != null) {
            onRoamingListener.roaming(attr);
        }
    }

    private OnRegisterSuccessListener onRegisterSuccessListener;

    // 接口OnRegisterSuccessListener
    public interface OnRegisterSuccessListener {
        void registerSuccess(int attr);
    }

    // 对外方式setOnRegisterSuccessListener
    public void setOnRegisterSuccessListener(OnRegisterSuccessListener onRegisterSuccessListener) {
        this.onRegisterSuccessListener = onRegisterSuccessListener;
    }

    // 封装方法registerSuccessNext
    private void registerSuccessNext(int attr) {
        if (onRegisterSuccessListener != null) {
            onRegisterSuccessListener.registerSuccess(attr);
        }
    }

    private OnRegisterFailedListener onRegisterFailedListener;

    // 接口OnRegisterFailedListener
    public interface OnRegisterFailedListener {
        void registerFailed(int attr);
    }

    // 对外方式setOnRegisterFailedListener
    public void setOnRegisterFailedListener(OnRegisterFailedListener onRegisterFailedListener) {
        this.onRegisterFailedListener = onRegisterFailedListener;
    }

    // 封装方法registerFailedNext
    private void registerFailedNext(int attr) {
        if (onRegisterFailedListener != null) {
            onRegisterFailedListener.registerFailed(attr);
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
