package com.alcatel.smartlinkv3.network;

import android.content.Context;
import android.util.Log;

import com.alcatel.smartlinkv3.common.ToastUtil;
import com.alcatel.smartlinkv3.ui.activity.SmartLinkV3App;

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
        Log.d(TAG, "onStart");
    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onResultError " + e);

        if (e instanceof SocketTimeoutException) {
            ToastUtil.showMessage(mAppContext, "Time out");
        } else if (e instanceof ConnectException) {
            ToastUtil.showMessage(mAppContext, "Couldn't connect");
        }else {
            ToastUtil.showMessage(mAppContext, "Failed");
            System.out.println("e: "+e.getMessage().toString());
        }
        onFailure();
    }

    @Override
    public void onNext(ResponseBody<T> responseBody) {
        Log.d(TAG, "onNext");

        if (responseBody.getError() != null) {
            ToastUtil.showMessage(mAppContext, responseBody.getError().message);
            onResultError(responseBody.getError());
        } else {
            onSuccess(responseBody.getResult());
        }
    }

    protected abstract void onSuccess(T result);
    protected void onResultError(ResponseBody.Error error){}
    protected void onFailure(){}
}
