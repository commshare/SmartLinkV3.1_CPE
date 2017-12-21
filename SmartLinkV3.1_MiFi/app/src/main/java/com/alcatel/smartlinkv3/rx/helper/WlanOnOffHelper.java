package com.alcatel.smartlinkv3.rx.helper;

import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;

/**
 * Created by qianli.ma on 2017/12/21 0021.
 */

public class WlanOnOffHelper {

    public void on() {
        API.get().setWlanOn(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                setWlanOnSuccessNext(result);
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

    public void off() {
        API.get().setWlanOff(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                setWLanOffSuccessNext(result);
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

    private OnSetWlanOffSuccessListener onSetWlanOffSuccessListener;

    // 接口OnSetWlanOffSuccessListener
    public interface OnSetWlanOffSuccessListener {
        void setWLanOffSuccess(Object attr);
    }

    // 对外方式setOnSetWlanOffSuccessListener
    public void setOnSetWlanOffSuccessListener(OnSetWlanOffSuccessListener onSetWlanOffSuccessListener) {
        this.onSetWlanOffSuccessListener = onSetWlanOffSuccessListener;
    }

    // 封装方法setWLanOffSuccessNext
    private void setWLanOffSuccessNext(Object attr) {
        if (onSetWlanOffSuccessListener != null) {
            onSetWlanOffSuccessListener.setWLanOffSuccess(attr);
        }
    }

    private OnSetWlanOnSuccessListener onSetWlanOnSuccessListener;

    // 接口OnSetWlanOnSuccessListener
    public interface OnSetWlanOnSuccessListener {
        void setWlanOnSuccess(Object attr);
    }

    // 对外方式setOnSetWlanOnSuccessListener
    public void setOnSetWlanOnSuccessListener(OnSetWlanOnSuccessListener onSetWlanOnSuccessListener) {
        this.onSetWlanOnSuccessListener = onSetWlanOnSuccessListener;
    }

    // 封装方法setWlanOnSuccessNext
    private void setWlanOnSuccessNext(Object attr) {
        if (onSetWlanOnSuccessListener != null) {
            onSetWlanOnSuccessListener.setWlanOnSuccess(attr);
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
