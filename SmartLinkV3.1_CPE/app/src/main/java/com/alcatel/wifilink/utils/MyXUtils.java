package com.alcatel.wifilink.utils;

import android.util.Log;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;


/**
 * Created by qianli.ma on 2017/8/2.
 */

public abstract class MyXUtils<T> {

    private String url;
    private Map<String, String> keyValues;

    public MyXUtils(String url, Map<String, String> keyValues) {
        this.url = url;
        this.keyValues = keyValues;
        request();
    }

    public abstract void success(T t);

    public abstract void failed();

    public void request() {
        RequestParams params = new RequestParams(url);
        Set<String> keys = keyValues.keySet();
        for (String key : keys) {
            params.addQueryStringParameter(key, keyValues.get(key));
        }

        x.http().get(params, new Callback.CommonCallback<T>() {
            @Override
            public void onSuccess(T t) {
                success(t);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("ma_wv", "ma_error: " + ex.getMessage().toString());
                failed();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d("ma_wv", "ma_cancel");
            }

            @Override
            public void onFinished() {
                Log.d("ma_wv", "ma_onFinished");
            }
        });
    }

}
