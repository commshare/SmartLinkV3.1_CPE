package com.alcatel.wifilink.rx.helper.base;

import android.util.Log;

import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;
import com.alcatel.wifilink.ui.home.helper.temp.ConnectionStates;

/**
 * Created by qianli.ma on 2017/11/25 0025.
 */

public class ConnectStatusHelper {

    private OnResultError onResultError;
    private OnError onError;
    private OnConnected onConnected;
    private OnConnecting onConnecting;
    private OnDisConnected onDisConnected;
    private OnDisConnecting onDisConnecting;

    /**
     * 获取连接状态
     */
    public void getStatus() {
        API.get().getConnectionStates(new MySubscriber<ConnectionStates>() {
            @Override
            protected void onSuccess(ConnectionStates result) {
                Log.v("ma_simconnect", "result: " + result.getConnectionStatus());
                int status = result.getConnectionStatus();
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

    /* -------------------------------------------- INTERFACE -------------------------------------------- */
    public interface OnResultError {
        void resultError(ResponseBody.Error error);
    }

    public interface OnError {
        void error(Throwable e);
    }

    public interface OnConnected {
        void connected(ConnectionStates result);
    }

    public interface OnConnecting {
        void connecting(ConnectionStates result);
    }

    public interface OnDisConnected {
        void disConnected(ConnectionStates result);
    }

    public interface OnDisConnecting {
        void disconnecting(ConnectionStates result);
    }

    /* -------------------------------------------- method -------------------------------------------- */
    public void setOnResultError(OnResultError onResultError) {
        this.onResultError = onResultError;
    }

    public void setOnError(OnError onError) {
        this.onError = onError;
    }

    public void setOnConnected(OnConnected onConnected) {
        this.onConnected = onConnected;
    }

    public void setOnConnecting(OnConnecting onConnecting) {
        this.onConnecting = onConnecting;
    }

    public void setOnDisConnected(OnDisConnected onDisConnected) {
        this.onDisConnected = onDisConnected;
    }

    public void setOnDisConnecting(OnDisConnecting onDisConnecting) {
        this.onDisConnecting = onDisConnecting;
    }

    /* -------------------------------------------- method -------------------------------------------- */
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

    private void connectedNext(ConnectionStates result) {
        if (onConnected != null) {
            onConnected.connected(result);
        }
    }

    private void connectingNext(ConnectionStates result) {
        if (onConnecting != null) {
            onConnecting.connecting(result);
        }
    }

    private void disconnectedNext(ConnectionStates result) {
        if (onDisConnected != null) {
            onDisConnected.disConnected(result);
        }
    }

    private void disconnectingNext(ConnectionStates result) {
        if (onDisConnecting != null) {
            onDisConnecting.disconnecting(result);
        }
    }
}
