package com.alcatel.smartlinkv3.rx.tools;

import android.content.Context;

import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by tao.j on 2017/6/15.
 */

public abstract class MySubscriber<T> extends Subscriber<ResponseBody<T>> {

    private static final String TAG = "tao";
    private Context mAppContext;


    public MySubscriber() {
        mAppContext = SmartLinkV3App.getInstance().getApplicationContext();
    }


    @Override
    public void onStart() {
        Logs.d(TAG, "onStart");
    }

    @Override
    public void onCompleted() {
        Logs.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Logs.d(TAG, "onError: " + e.getMessage().toString());
        Logs.d("ma_api", "onError: " + e.getMessage().toString());
        if (e instanceof SocketTimeoutException) {
            Logs.e("ma_error", "error: SocketTimeoutException" + e.getMessage());
        } else if (e instanceof ConnectException) {
            Logs.e("ma_error", "error: ConnectException" + e.getMessage());
        }

        onFailure();
    }

    @Override
    public void onNext(ResponseBody<T> responseBody) {
        Logs.d(TAG, "onNext");

        if (responseBody.getError() != null) {
            onResultError(responseBody.getError());
        } else {
            onSuccess(responseBody.getResult());
        }
    }

    protected abstract void onSuccess(T result);

    protected void onResultError(ResponseBody.Error error) {
        Logs.d(TAG, "onResultError: " + error.getMessage().toString());
        ToastUtil_m.show(mAppContext, error.message);
    }

    protected void onFailure() {

    }
}
