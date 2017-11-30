package com.alcatel.wifilink.rx.helper;

import android.app.Activity;

import com.alcatel.wifilink.utils.CA;
import com.alcatel.wifilink.model.user.LoginState;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.utils.OtherUtils;

/**
 * Created by qianli.ma on 2017/11/15 0015.
 */

public abstract class CheckBoard {

    private OnBoardListener onBoardListener;
    private OnAllErrorListener onAllErrorListener;

    public CheckBoard() {
    }

    public CheckBoard(OnBoardListener onBoardListener) {
        this.onBoardListener = onBoardListener;
    }

    public CheckBoard(OnAllErrorListener onAllErrorListener) {
        this.onAllErrorListener = onAllErrorListener;
    }

    public abstract void successful();

    /**
     * 检测是否连接上硬件
     *
     * @param ori    调用的ac
     * @param target 出错时的目标ac
     */
    public void checkBoard(Activity ori, Class target) {
        // 检测wifi是否有连接
        if (OtherUtils.isWifiConnect(ori)) {
            // 请求接口前
            onPrepare();
            API.get().getLoginState(new MySubscriber<LoginState>() {
                @Override
                protected void onSuccess(LoginState result) {
                    onSuccessful();// 请求接口成功后
                    successful();
                }

                @Override
                protected void onResultError(ResponseBody.Error error) {
                    allError();
                    onResultErrors(error);// 请求接口中途错误
                    // ToastUtil_m.show(ori, ori.getString(R.string.connect_failed));
                    CA.toActivity(ori, target, false, true, false, 0);
                }

                @Override
                public void onError(Throwable e) {
                    allError();
                    onErrors(e);// 请求接口错误溢出
                    // ToastUtil_m.show(ori, ori.getString(R.string.connect_failed));
                    CA.toActivity(ori, target, false, true, false, 0);
                }
            });
        } else {
            // wifi掉线
            if (ori != null) {
                // ToastUtil_m.show(ori, ori.getString(R.string.connect_failed));
                CA.toActivity(ori, target, false, true, false, 0);
            }
        }
    }

    /**
     * 错误的普通做法(适合自定义)
     */
    private void allError() {
        if (onAllErrorListener != null) {
            onAllErrorListener.allError();
        }
    }

    /**
     * 请求接口前
     */
    private void onPrepare() {
        if (onBoardListener != null) {
            onBoardListener.onPrepare();
        }
    }

    /**
     * 请求接口成功后
     */
    private void onSuccessful() {
        if (onBoardListener != null) {
            onBoardListener.onSuccessful();
        }
    }

    /**
     * 请求接口中途错误
     */
    private void onResultErrors(ResponseBody.Error error) {
        if (onBoardListener != null) {
            onBoardListener.onResultErrors(error);
        }
    }

    /**
     * 请求接口错误溢出
     */
    private void onErrors(Throwable e) {
        if (onBoardListener != null) {
            onBoardListener.onErrors(e);
        }
    }

    public interface OnAllErrorListener {
        void allError();
    }


    public interface OnBoardListener {
        /**
         * 请求接口前
         */
        void onPrepare();

        /**
         * 请求成功
         */
        void onSuccessful();

        /**
         * 请求接口错误
         */
        void onResultErrors(ResponseBody.Error error);

        /**
         * 请求溢出
         */
        void onErrors(Throwable e);
    }
}
